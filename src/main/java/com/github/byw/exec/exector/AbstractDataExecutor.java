package com.github.byw.exec.exector;

import com.github.byw.exec.operator.*;
import com.github.byw.param.Param;
import com.ql.util.express.ExpressRunner;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象数据执行程序
 *
 * @author byw
 * @date 2023/02/01
 */
public abstract class AbstractDataExecutor implements Executor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataExecutor.class);

	private final static ExpressRunner RUNNER = new ExpressRunner(true, false);

	protected static final char EQUAL = '=';

	static {
		RUNNER.addFunction("listMax", new ListMax());
		RUNNER.addFunction("listMin", new ListMin());
		RUNNER.addFunction("listSum", new ListSum());
		RUNNER.addFunction("defaultZero", new DefaultZero());
		RUNNER.addFunction("default", new Default());
	}

	/**
	 * 执行公式，并获取 bool 类型的结果
	 *
	 * @param formula 公式
	 * @param param   参数
	 * @return {@link Boolean}
	 */
	@SneakyThrows
	protected Boolean executeForBool(String formula, Param param) {
		return (Boolean) RUNNER.execute(formula, param.getParamContext(), null, true, false);
	}

	/**
	 * 执行
	 *
	 * @param formula 公式
	 * @param param   参数
	 */
	@SneakyThrows
	protected void execute(String formula, Param param) {
		RUNNER.execute(formula, param.getParamContext(), null, true, false);
	}


	/**
	 * 修改公式
	 * 主要是处理用户自定义的四舍五入
	 *
	 * @param toBeExecutedFormula 要的执行公式
	 * @param integer
	 * @return {@link String}
	 */
	protected String modificationFormula(String toBeExecutedFormula, Integer integer) {
		String toBeExecutedFormulaResult = toBeExecutedFormula;
		if (toBeExecutedFormula.indexOf(EQUAL) == -1) {
			return toBeExecutedFormulaResult;
		}
		int EQUAL_INDEX = toBeExecutedFormula.indexOf(EQUAL);
		// 排除使用了 == 的情况
		if (toBeExecutedFormula.charAt(EQUAL_INDEX + 1) == EQUAL) {
			return toBeExecutedFormulaResult;
		}
		String resultMark = toBeExecutedFormula.split(String.valueOf(EQUAL))[0];
		toBeExecutedFormulaResult = resultMark + " = " + " round(" + toBeExecutedFormula + "," + integer + ")";
		return toBeExecutedFormulaResult;
	}

	/**
	 * 打印日志
	 *
	 * @param toBeExecutedFormula 要执行公式
	 * @param startCondition      开始条件
	 * @param stopCondition       停止条件
	 */
	protected void printLog(String toBeExecutedFormula, String startCondition, String stopCondition) {
		StringBuilder builder = new StringBuilder("正在执行公式：");
		String logMessage = builder.append(toBeExecutedFormula)
				.append("；开始执行条件：")
				.append(StringUtils.isBlank(startCondition) ? "无约束" : startCondition)
				.append("；结束执行条件：")
				.append(StringUtils.isBlank(stopCondition) ? "无约束" : stopCondition)
				.toString();
		LOGGER.info(logMessage);
	}

}
