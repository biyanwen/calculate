package com.github.byw.exec.exector;

import com.github.byw.exception.CalculateException;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.formula.Formula;
import com.github.byw.formula.FormulaConditions;
import com.github.byw.param.ParamConfig;
import com.google.common.collect.Lists;
import com.ql.util.express.IExpressContext;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 列表数据执行程序
 *
 * @author byw
 * @date 2023/02/01
 */
public class ListDataExecutor extends AbstractDataExecutor {
	/**
	 * 索引标志
	 */
	private final String indexMark = "_index";

	/**
	 * 关键字
	 * <p>
	 * 主要用来判断参数的截止位置
	 */
	private static final List<Character> keywords = Lists.newArrayList('=', '|', '&', '+', '-', '*', '/', ',', ')', ' ', ':');
	private static final char LEFT_BRACKET = '(';

	/**
	 * 循环是否由外部参数控制
	 */
	private boolean demise = false;

	/**
	 * 当前索引
	 */
	private Integer currentIndex = null;

	/**
	 * param总长度
	 */
	private Integer paramTotalSize = null;

	/**
	 * 判断条件，如果有所公式都包含 _index 就返回 true
	 *
	 * @param formulaInstance 公式实例
	 * @return boolean
	 */
	@Override
	public boolean canHandle(Formula formulaInstance) {
		boolean result = true;
		for (String formula : formulaInstance.getFormulaList()) {
			if (!formula.contains(indexMark)) {
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	protected void doExec(Formula formulaInstance) {
		doDetailExec(formulaInstance, config);
	}

	private void doDetailExec(Formula formulaInstance, CalculateConfig config) {
		demise = config.getCurrentIndex() != null;
		currentIndex = config.getCurrentIndex();
		paramTotalSize = config.getParamTotalSize();
		List<FormulaMessage> formulaMessages = formulaInstance.getFormulaList().stream().map(t -> extractFormulaMessage(t, false)).collect(Collectors.toList());
		FormulaConditions conditions = Optional.ofNullable(formulaInstance.getConditions()).orElse(new FormulaConditions() {
		});
		//是否取整
		Integer integer = conditions.retainDecimal();
		FormulaMessage startConditionMessage = extractFormulaMessage(conditions.getStartConditions(), true);
		FormulaMessage stopConditionMessage = extractFormulaMessage(conditions.getStopConditions(), true);

		List<FormulaMessage> checklist = new ArrayList<>(formulaMessages);
		checklist.add(startConditionMessage);
		checklist.add(stopConditionMessage);
		checkParamSize(checklist);

		List<FormulaMessage> messageList = checklist.stream().filter(t -> t != null && t.getSize() != -1).collect(Collectors.toList());
		if (messageList.isEmpty() && paramTotalSize == null) {
			throw new CalculateException(formulaInstance.getFormulaList() + " 公式有误！无法获取参数长度，请检查参数是否有误。也可以通过配置 " + CalculateConfig.class.getName() + "#paramTotalSize 来解决此问题");
		}
		int size = messageList.isEmpty() ? paramTotalSize : messageList.stream().findAny().get().getSize();
		int i = Optional.ofNullable(config.getCurrentIndex()).orElse(0);
		if (i >= size) {
			LOGGER.warn("警告！当前公式执行索引为 " + i + " 从公式获取的参数总长度为 " + size + " 索引大于等于参数总长度，跳出循环！");
		}
		for (; i < size; i++) {
			ConditionResultBean startConditionsResult = judgeWhetherStartOrStop(startConditionMessage, i);

			ConditionResultBean stopConditionsResult = judgeWhetherStartOrStop(stopConditionMessage, i);
			if (startConditionsResult.result) {
				initResultList(formulaMessages, size);
				executiveFormula(formulaMessages, integer, i, startConditionsResult.conditionFormula, stopConditionsResult.conditionFormula, config);
				while (!stopConditionsResult.result && !Thread.currentThread().isInterrupted()) {
					executiveFormula(formulaMessages, integer, i, startConditionsResult.conditionFormula, stopConditionsResult.conditionFormula, config);
					stopConditionsResult = judgeWhetherStartOrStop(stopConditionMessage, i);
				}
			}
			// 把执行权让渡给使用者
			if (demise) {
				break;
			}
		}
	}
	/**
	 * 初始化结果列表
	 * <p>
	 * 之所以初始化时因为避免列表类公式的结果错位和缺失，例如当前结果列表的长度应该为3，但是在计算第二个结果的时候因为没有满足运算条件所以没有输出结果，但是第三个结果成功输出了，
	 * 这就导致结果列表的长度变成了2，同时第二个结果应该是第三个结果(因为中间有个公式没有满足执行条件，没有执行)。
	 *
	 * @param formulaMessages 公式信息
	 * @param size            参数长度
	 */
	private void initResultList(List<FormulaMessage> formulaMessages, int size) {
		formulaMessages.forEach(formulaMessage -> {
			if (param.getParamContext().get(formulaMessage.getOriginalResultName()) == null) {
				LOGGER.info("初始化" + formulaMessage.getOriginalResultName());
				param.addArray(formulaMessage.getOriginalResultName(), new ArrayList<>(Collections.nCopies(size, BigDecimal.ZERO)));
			}
		});
	}


	private void executiveFormula(List<FormulaMessage> formulaMessages, Integer integer, int i, String startCondition, String stopCondition, CalculateConfig config) {
		for (FormulaMessage formulaMessage : formulaMessages) {

			execute(formulaMessage, i, oldFormula -> {
				printFormulaLog(oldFormula, startCondition, stopCondition);
				if (config.getRetainDecimal() != null) {
					return modificationFormula(oldFormula, config.getRetainDecimal());
				}
				if (integer != null) {
					return modificationFormula(oldFormula, integer);
				}
				return oldFormula;
			});
		}
	}

	/**
	 * 判断是否启动/停止
	 *
	 * @param formulaMessage 公式信息列表
	 * @param index          指数
	 * @return {@link ConditionResultBean}
	 */
	private ConditionResultBean judgeWhetherStartOrStop(FormulaMessage formulaMessage, int index) {
		if (formulaMessage == null) {
			return new ConditionResultBean("无约束", true);
		}
		StringBuilder builder = new StringBuilder("");

		Object execute = execute(formulaMessage, index, formula -> {
			builder.append(formula);
			return formula;
		});
		if (!(execute instanceof Boolean)) {
			throw new CalculateException(formulaMessage.getOriginalFormula() + " 有误，结果应该是 Boolean 类型");
		}
		if (!(Boolean) execute) {
			return new ConditionResultBean(builder.toString(), false);
		}
		return new ConditionResultBean(builder.toString(), true);
	}

	/**
	 * 执行
	 * 按照索引对参数进行拆分，并且组装新的公式
	 *
	 * @param formulaMessage            公式信息
	 * @param index                     索引
	 * @param changeTheFormulaFunctions 改变函数公式（执行前会回调，如果需要更改公式可以在这里更改）
	 * @return boolean
	 */
	private Object execute(FormulaMessage formulaMessage, int index, Function<String, String>... changeTheFormulaFunctions) {
		IExpressContext<String, Object> paramContext = param.getParamContext();
		String originalFormula = formulaMessage.getOriginalFormula();
		List<String> nameToBeReplacedList = formulaMessage.getNameToBeReplacedList();
		List<String> parameterOriginalNameList = formulaMessage.getParameterOriginalNameList();
		String resultIndexName = null;
		//当前公式是否为对空进行判断的公式，
		boolean isNullJudge = formulaMessage.isNullJudge();
		for (int i = 0; i < parameterOriginalNameList.size(); i++) {
			String nameToBeReplaced = nameToBeReplacedList.get(i);
			String parameterOriginalName = parameterOriginalNameList.get(i);
			ParamConfig config = param.getParamConfig(parameterOriginalName);
			// 第一个是结果参数名称，不用校验。如果是空检查语句也不用校验
			if (i > 0 && !isNullJudge) {
				if (config == null) {
					boolean isDefaultFun = checkDefaultFun(formulaMessage.getOriginalFormula(), parameterOriginalName);
					if (isDefaultFun) {
						continue;
					}
					throw new CalculateException(formulaMessage.getOriginalFormula() + " 公式数据有误 " + parameterOriginalName + " 参数缺失");
				}
				if (!config.isArray()) {
					throw new CalculateException(parameterOriginalName + " 参数期望是 List.class 类型，实际是 " + config.getParamClass());
				}
			}
			// 如果循环的索引时外部传递进来的，那么只要参数的最大索引大于当前正在执行的索引就通过检测
			if (i > 0 && demise) {
				boolean isDefaultFun = checkDefaultFun(formulaMessage.getOriginalFormula(), nameToBeReplaced);
				// 如果使用了提供默认值的函数或者是空值检验公式 则跳过检验
				if (config.getSize() <= currentIndex && !isDefaultFun && !isNullJudge) {
					throw new CalculateException("公式有误！" + formulaMessage.getOriginalFormula() + " 参数 " + parameterOriginalName + " 有误！最大索引应该大于等于 " + currentIndex);
				}
			}
			String newParameterOriginalName = parameterOriginalName + "_" + "索引" + index;
			if (i == 0) {
				resultIndexName = newParameterOriginalName;
			}
			// 对公式中的参数名称进行替换
			if (originalFormula.contains(nameToBeReplaced)) {
				originalFormula = originalFormula.replace(nameToBeReplaced, newParameterOriginalName);
			}
			parameterResolution(formulaMessage, parameterOriginalName, isNullJudge, paramContext, i, index, newParameterOriginalName);
		}
		if (changeTheFormulaFunctions.length != 0) {
			originalFormula = changeTheFormulaFunctions[0].apply(originalFormula);
		}
		execute(originalFormula, param);
		String originalResultName = parameterOriginalNameList.get(0);
		//执行完成之后需要将结果放入到 List 中，并保存到上下文中。
		Object result = paramContext.get(originalResultName);
		Object resultForIndex = paramContext.get(resultIndexName);
		if (resultForIndex == null) {
			throw new CalculateException("数据异常，" + resultIndexName + " 结果数据缺失");
		}
		if (result != null && !(result instanceof List)) {
			throw new CalculateException("数据异常，" + originalResultName + " 类型不是 " + List.class);
		}
		List<Object> resultList;
		if (result == null) {
			resultList = new ArrayList<>();
		} else {
			resultList = (List<Object>) result;
		}
		if (resultList.size() > index) {
			resultList.set(index, resultForIndex);
		} else {
			resultList.add(resultForIndex);
		}
		paramContext.put(originalResultName, resultList);
		return resultForIndex;
	}

	/**
	 * 参数拆分
	 *
	 * @param formulaMessage           公式元信息
	 * @param parameterOriginalName    参数原名称
	 * @param isNullJudge              是否为空判断公式
	 * @param paramContext             参数上下文
	 * @param i                        当前正在处理参数的索引
	 * @param index                    公式正在使用参数值的索引
	 * @param newParameterOriginalName 新的参数名称
	 */
	private void parameterResolution(FormulaMessage formulaMessage, String parameterOriginalName, boolean isNullJudge, IExpressContext<String, Object> paramContext, int i, int index, String newParameterOriginalName) {
		// 对每个参数按照索引进行拆解，用于后续计算
		// 第一个是结果参数的名称或者是空判断公式，不需要对参数的数值进行更改
		// 如果新参数名称已经在上下文中有对应的数值了就不进行替换
		if (paramContext.get(newParameterOriginalName) != null) {
			return;
		}
		boolean isDefaultFun = checkDefaultFun(formulaMessage.getOriginalFormula(), parameterOriginalName);
		if (i != 0 && !isNullJudge) {
			Object o = paramContext.get(parameterOriginalName);
			if (o == null) {
				if (isDefaultFun) {
					return;
				}
				throw new CalculateException(formulaMessage.getOriginalFormula() + " 公式执行有误！ " + parameterOriginalName + " 参数未定义，请在计算前定义");
			}
			List<Object> paramList = (List<Object>) o;
			// 如果是默认赋值函数，那么参数必须有值才进行拆分
			if (!(isDefaultFun && paramList.size() <= index)) {
				paramContext.put(newParameterOriginalName, paramList.get(index));
			}
		}
	}


	private void checkParamSize(List<FormulaMessage> checklist) {
		checklist = checklist.stream().filter(Objects::nonNull).collect(Collectors.toList());
		Integer markSize = null;
		String markFormula = null;
		for (FormulaMessage formulaMessage : checklist) {
			if (formulaMessage.getSize() == -1) {
				continue;
			}
			if (markSize == null) {
				markSize = formulaMessage.getSize();
				markFormula = formulaMessage.getOriginalFormula();
			}
			// 如果循环的索引时外部传递进来的，那么只要参数的最大索引大于当前正在执行的索引就通过检测
			if (demise) {
				if (markSize <= currentIndex && !formulaMessage.isNullJudge()) {
					throw new CalculateException(markFormula + " 有误！参数的最大索引应该大于等于 " + currentIndex);
				}
			} else {
				if (markSize != formulaMessage.getSize()) {
					throw new CalculateException("公式 {0} 和 {1} 的参数长度不同，分别为 {2}，{3}", markFormula, formulaMessage.getOriginalFormula(),
							markSize.toString(), String.valueOf(formulaMessage.getSize()));
				}
			}
			markSize = formulaMessage.getSize();
			markFormula = formulaMessage.getOriginalFormula();
		}

		if (markSize == null && paramTotalSize == null) {
			List<String> formulaList = checklist.stream().map(FormulaMessage::getOriginalFormula).collect(Collectors.toList());
			throw new CalculateException(formulaList + " 公式有误！无法获取参数长度，请检查参数是否有误。也可以通过配置 " + CalculateConfig.class.getName() + "#paramTotalSize 来解决此问题");
		}
	}


	/**
	 * 参数检查
	 * 必须都是 List 类型，并且长度要相等
	 *
	 * @param formulaMessage 公式信息
	 */
	private void paramCheck(FormulaMessage formulaMessage) {
		List<String> parameterOriginalNameList = formulaMessage.getParameterOriginalNameList();
		if (CollectionUtils.isEmpty(parameterOriginalNameList)) {
			throw new CalculateException("公式有误！" + formulaMessage.getOriginalFormula() + " 需要至少有一个计算参数包含 _index");
		}
		// 说明是赋值语句 例如 a_index = 1;
		if (parameterOriginalNameList.size() == 1) {
			formulaMessage.setSize(-1);
			return;
		}
		boolean isNullJudge = formulaMessage.getOriginalFormula().contains("null");
		formulaMessage.setNullJudge(isNullJudge);
		Integer markSize = null;
		String markName = null;
		// 第一个是结果名称不用检查
		for (int i = 1; i < parameterOriginalNameList.size(); i++) {
			String name = parameterOriginalNameList.get(i);
			ParamConfig config = param.getParamConfig(name);
			if (config == null) {
				continue;
			}
			if (markSize == null) {
				markSize = config.getSize();
				markName = name;
			}
			if (!demise && markSize != config.getSize()) {
				throw new CalculateException("公式有误！" + formulaMessage.getOriginalFormula() + " 参数 {0} 和 {1} 的长度不同，分别为 {2}，{3}", markName, name, markSize.toString(), String.valueOf(config.getSize()));
			}
			markSize = config.getSize();
			markName = name;
		}
		// 有可能是多条公式一起执行，前一个公式还没有执行，所以当前公式无法获取参数数值。多条公式一起执行的情况下，只要能从一个公式中获取参数长度就可以。
		if (markSize == null && paramTotalSize == null) {
			formulaMessage.setSize(-1);
			return;
		}
		formulaMessage.setSize(Optional.ofNullable(markSize).orElse(paramTotalSize));
	}

	/**
	 * 检查是否是默认赋值函数
	 *
	 * @param originalFormula 原来公式
	 * @param paramName       参数名称
	 * @return boolean
	 */
	private boolean checkDefaultFun(String originalFormula, String paramName) {
		return originalFormula.contains("default(" + paramName) || originalFormula.contains("defaultZero(" + paramName);
	}

	/**
	 * 提取公式信息
	 *
	 * @param formula   公式
	 * @param condition 条件 是否为条件公式，条件公式，条件公式没有结果名称，需要系统自己加上
	 * @return {@link FormulaMessage}
	 */
	private FormulaMessage extractFormulaMessage(String formula, boolean condition) {
		if (StringUtils.isBlank(formula)) {
			return null;
		}
		if (condition) {
			formula = "条件公式_" + Thread.currentThread().getId() + indexMark + " = " + formula + " ";
		}
		FormulaMessage formulaMessage = new FormulaMessage(formula);
		checkResultName(formula);
		char[] indexMarkChars = indexMark.toCharArray();
		char[] formulaChars = formula.toCharArray();
		boolean skip = false;
		//保存命中的字符
		List<Character> hitCharList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();
		for (int i = 0; i < formulaChars.length; i++) {
			char formulaChar = formulaChars[i];
			if (keywords.contains(formulaChar)) {
				hitCharList.clear();
				continue;
			}
			//左括号说明之前是函数名称
			if (formulaChar == LEFT_BRACKET) {
				hitCharList.clear();
				continue;
			}
			if (formulaChar == indexMarkChars[0]) {
				//j = 1 是因为第一个字符已经匹配过了
				int a = i;
				for (int j = 1; j < indexMarkChars.length; j++) {
					a++;
					//说明已经对比完公式最后一个字符了，所以不满足需求
					if (a == formulaChars.length) {
						hitCharList.clear();
						break;
					}
					if (formulaChars[a] != indexMarkChars[j]) {
						break;
					}
					if (j + 1 == indexMarkChars.length && (a + 1 == formulaChars.length || keywords.contains(formulaChars[a + 1]))) {
						StringBuilder stringBuffer = new StringBuilder();
						hitCharList.forEach(character -> stringBuffer.append(character.toString()));
						nameList.add(stringBuffer.toString());
						skip = true;
						i = a;
						hitCharList.clear();
					}
				}
			}
			if (!skip) {
				hitCharList.add(formulaChar);
			}
			skip = false;
		}
		formulaMessage.setParameterOriginalNameList(nameList);
		formulaMessage.setNameToBeReplacedList(nameList.stream().map(name -> name + indexMark).collect(Collectors.toList()));

		paramCheck(formulaMessage);
		return formulaMessage;
	}

	/**
	 * 检验结果名字
	 *
	 * @param formula 公式
	 */
	private void checkResultName(String formula) {
		if (formula.indexOf(EQUAL) == -1) {
			throw new RuntimeException(formula + " 不是等式");
		}
		int EQUAL_INDEX = formula.indexOf(EQUAL);
		// 排除使用了 == 的情况
		if (formula.charAt(EQUAL_INDEX + 1) == EQUAL) {
			throw new RuntimeException(formula + " 不是等式");
		}
		String resultName = formula.split(String.valueOf(EQUAL))[0];
		if (!resultName.contains(indexMark)) {
			throw new CalculateException(resultName + " 必须包含字符串：" + indexMark);
		}
	}

	/**
	 * 存储条件计算结果
	 *
	 * @author byw
	 * @date 2023/02/06
	 */
	private static class ConditionResultBean {

		/**
		 * 条件公式
		 */
		private String conditionFormula;

		/**
		 * 结果
		 */
		private boolean result;

		public ConditionResultBean(String conditionFormula, boolean result) {
			this.conditionFormula = conditionFormula;
			this.result = result;
		}
	}

	@Data
	private static class FormulaMessage {

		/**
		 * 原始公式
		 */
		private String originalFormula;

		/**
		 * 待替换参数名称
		 * 包括结果参数名称
		 */
		private List<String> nameToBeReplacedList = new ArrayList<>();

		/**
		 * 参数原始名称
		 */
		private List<String> parameterOriginalNameList = new ArrayList<>();

		/**
		 * 参数长度 ；
		 * ps: 如果是简单的赋值语句那么 size = -1. 例如 a_index = 1;
		 */
		private int size;

		/**
		 * 是默认赋值公式
		 */
		private boolean defaultFun = false;
		/**
		 * 是空值判断公式
		 */
		private boolean nullJudge = false;

		public FormulaMessage(String originalFormula) {
			this.originalFormula = originalFormula;
		}

		public String getOriginalResultName() {
			return parameterOriginalNameList.get(0);
		}
	}
}
