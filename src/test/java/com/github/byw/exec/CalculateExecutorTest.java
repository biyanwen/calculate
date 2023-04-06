package com.github.byw.exec;

import com.github.byw.exception.CalculateException;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.exec.config.FunctionConfig;
import com.github.byw.factory.CalculateFactory;
import com.github.byw.formula.FormulaConditions;
import com.github.byw.formula.FormulaManager;
import com.github.byw.param.Param;
import com.github.byw.result.ResultManager;
import com.google.common.collect.Lists;
import com.ql.util.express.Operator;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculateExecutorTest {

	/**
	 * 常见计算 （加减乘除）
	 */
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		// 单值计算
		CalculateExecutor executor = factory.createExecutor();
		Param param = factory.createParam();
		param.addNumber("小明的数学成绩", 90);
		param.addNumber("小明的语文成绩", 80);
		param.addNumber("小明的数学成绩", 70);

		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的数学成绩)/3");
		formulaManager.add("小明平均成绩的2倍 = 小明的平均成绩 * 2");
		ResultManager resultManager = executor.exec(param, formulaManager);
		BigDecimal avgGrade = resultManager.getNumResult("小明的平均成绩");
		BigDecimal avgDouble = resultManager.getNumResult("小明平均成绩的2倍");

		BigDecimal 小明平均成绩的2倍 = CalculateExecutor.getResultManager(param).getNumResult("小明平均成绩的2倍");
		assertEquals(avgDouble, 小明平均成绩的2倍);
		assertEquals(new BigDecimal("73.3333333333"), avgGrade);
		assertEquals(new BigDecimal("146.6666666666"), avgDouble);

		//多值计算
		Param param_many = factory.createParam();
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));
		param_many.addArray("小红3次考试的数学成绩", Lists.newArrayList(90, 90, 70));

		FormulaManager formulaManager_many = factory.createFormulaManager();
		formulaManager_many.add("每次考试小红比小明多几分_index = 小红3次考试的数学成绩_index - 小明3次考试的数学成绩_index");
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		List<BigDecimal> 每次考试小红比小明多几分 = resultManager_mangy.getNumResultList("每次考试小红比小明多几分");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(10), BigDecimal.valueOf(0), BigDecimal.valueOf(-20))
				, 每次考试小红比小明多几分);
		assertTrue(equalCollection);
	}

	/**
	 * 测试一些内置的函数
	 */
	@Test
	public void operator_test() {
		CalculateFactory factory = CalculateFactory.createFactory();
		Param param_many = factory.createParam();
		CalculateExecutor executor = factory.createExecutor();
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 70));

		FormulaManager formulaManager_many = factory.createFormulaManager();
		// 列表内的数值加和
		formulaManager_many.add("加和结果 = listSum(小明3次考试的数学成绩)");
		// 列表内的数值最大值
		formulaManager_many.add("最大值 = listMax(小明3次考试的数学成绩)");
		// 列表内的数值最小值
		formulaManager_many.add("最小值 = listMin(小明3次考试的数学成绩)");
		// 默认值，当使用的参数在上下文中无法找到时可以设置默认值
		formulaManager_many.add("默认加5分_index = 小明3次考试的数学成绩_index + default(额外加分项,5)");
		// 默认值，直接赋值 0
		formulaManager_many.add("默认加0分_index = 小明3次考试的数学成绩_index + defaultZero(额外加分项)");

		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		BigDecimal 加和结果 = resultManager_mangy.getNumResult("加和结果");
		assertEquals(BigDecimal.valueOf(240), 加和结果);

		BigDecimal 最大值 = resultManager_mangy.getNumResult("最大值");
		assertEquals(BigDecimal.valueOf(90), 最大值);

		BigDecimal 最小值 = resultManager_mangy.getNumResult("最小值");
		assertEquals(BigDecimal.valueOf(70), 最小值);

		List<BigDecimal> 默认加5分 = resultManager_mangy.getNumResultList("默认加5分");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(85), BigDecimal.valueOf(95), BigDecimal.valueOf(75))
				, 默认加5分);
		assertTrue(equalCollection);

		List<BigDecimal> 默认加0分 = resultManager_mangy.getNumResultList("默认加0分");
		boolean equalCollection2 = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(80), BigDecimal.valueOf(90), BigDecimal.valueOf(70))
				, 默认加0分);
		assertTrue(equalCollection2);
	}

	/**
	 * 开始停止逻辑
	 */
	@Test
	public void start_stop() {
		//单值计算
		//单值计算 同时设置开始和结束条件
		CalculateFactory factory = CalculateFactory.createFactory();
		CalculateExecutor executor = factory.createExecutor();
		Param param = factory.createParam();
		param.addNumber("小明的数学成绩", 90);

		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("小明的数学成绩 = 小明的数学成绩 + 1", new FormulaConditions() {
			@Override
			public String getStartConditions() {
				return "小明的数学成绩 == 90";
			}

			@Override
			public String getStopConditions() {
				return "小明的数学成绩 == 100";
			}
		});
		ResultManager resultManager = executor.exec(param, formulaManager);
		assertEquals(new BigDecimal("100"), resultManager.getNumResult("小明的数学成绩"));

		//单值计算 只设置结束条件
		Param param2 = factory.createParam();
		param2.addNumber("小红的数学成绩", 90);

		FormulaManager formulaManager2 = factory.createFormulaManager();
		formulaManager2.add("小红的数学成绩 = 小红的数学成绩 + 1", new FormulaConditions() {
			@Override
			public String getStopConditions() {
				return "小红的数学成绩 == 100";
			}
		});
		BigDecimal 小红的数学成绩 = executor.exec(param2, formulaManager2).getNumResult("小红的数学成绩");
		assertEquals(new BigDecimal("100"), 小红的数学成绩);

		//多值计算
		//单值计算 同时设置开始和结束条件
		//如果当前成绩低于88就加1，直到成绩不小于90停止
		Param param_many = factory.createParam();
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));

		FormulaManager formulaManager_many = factory.createFormulaManager();
		formulaManager_many.add("小明3次考试的数学成绩_index = 小明3次考试的数学成绩_index + 1", new FormulaConditions() {
			@Override
			public String getStartConditions() {
				return "小明3次考试的数学成绩_index < 88";
			}

			@Override
			public String getStopConditions() {
				return "小明3次考试的数学成绩_index >= 90";
			}
		});
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		List<BigDecimal> 小明3次考试的数学成绩 = resultManager_mangy.getNumResultList("小明3次考试的数学成绩");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(90), BigDecimal.valueOf(90), BigDecimal.valueOf(90))
				, 小明3次考试的数学成绩);
		assertTrue(equalCollection);
	}

	/**
	 * 测试四舍五入
	 */
	@Test
	public void round_test() {
		CalculateFactory factory = CalculateFactory.createFactory();
		// 单值计算
		CalculateExecutor executor = factory.createExecutor();
		Param instance = factory.createParam();
		instance.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = 圆周率 + 1", new FormulaConditions() {
			@Override
			public Integer retainDecimal() {
				return 3;
			}
		});
		BigDecimal 结果 = executor.exec(instance, formulaManager).getNumResult("结果");
		assertEquals(BigDecimal.valueOf(4.142), 结果);
		// 通过配置设置
		CalculateConfig config = new CalculateConfig();
		config.setRetainDecimal(1);
		CalculateFactory factorySingle = CalculateFactory.createFactory(config);
		BigDecimal 结果1 = factorySingle.createExecutor().exec(instance, formulaManager).getNumResult("结果");
		assertEquals(BigDecimal.valueOf(4.1), 结果1);

		//多值计算
		Param param_many = factory.createParam();
		param_many.addArray("小明最近三年身高", Lists.newArrayList(168.55, 169.55, 170.55));
		FormulaManager formula_many = factory.createFormulaManager();
		formula_many.add("结果_index = 小明最近三年身高_index + 1", new FormulaConditions() {
			@Override
			public Integer retainDecimal() {
				return 1;
			}
		});
		List<BigDecimal> 结果数组 = executor.exec(param_many, formula_many).getNumResultList("结果");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(169.6), BigDecimal.valueOf(170.6),
				BigDecimal.valueOf(171.6)), 结果数组);
		assertTrue(equalCollection);
		// 通过配置设置
		CalculateConfig config2 = new CalculateConfig();
		config2.setRetainDecimal(0);
		CalculateFactory factoryMany = CalculateFactory.createFactory(config2);
		List<BigDecimal> 结果数组2 = factoryMany.createExecutor().exec(param_many, formula_many).getNumResultList("结果");
		boolean equalCollection2 = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(170.0), BigDecimal.valueOf(171.0),
				BigDecimal.valueOf(172.0)), 结果数组2);
		assertTrue(equalCollection2);
	}

	/**
	 * 异常测试
	 */
	@Test
	public void exception() {
		CalculateFactory factory = CalculateFactory.createFactory();
		CalculateExecutor executor = factory.createExecutor();
		// Param 为必传
		assertThrows(CalculateException.class, () -> {
			FormulaManager formulaManager = factory.createFormulaManager();
			formulaManager.add("小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的数学成绩)/3");
			executor.exec(null, formulaManager);
		});
		// 多值计算的公式中，变量和结果名称必须在结尾标注了 _index
		assertThrows(CalculateException.class, () -> {
			Param param_many = factory.createParam();
			param_many.addArray("小明最近三年身高", Lists.newArrayList(168.55, 169.55, 170.55));
			FormulaManager formula_many = factory.createFormulaManager();
			formula_many.add("结果 = 小明最近三年身高_index + 1", new FormulaConditions() {
				@Override
				public Integer retainDecimal() {
					return 1;
				}
			});
			executor.exec(param_many, formula_many);
		});
		// 多值计算无法获取参数长度
		assertThrows(CalculateException.class, () -> {
			Param param_many = factory.createParam();
			FormulaManager formula_many = factory.createFormulaManager();
			formula_many.add("结果_index = 小明最近三年身高_index + 1", new FormulaConditions() {
				@Override
				public Integer retainDecimal() {
					return 1;
				}
			});
			executor.exec(param_many, formula_many);
		});
	}

	/**
	 * 测试注册自定义函数
	 */
	@Test
	public void register_function() {
		CalculateConfig calculateConfig = new CalculateConfig();
		calculateConfig.setFunctionConfig(new FunctionConfig().addFunction("testFunction", new TestFunction()));

		CalculateFactory factory = CalculateFactory.createFactory(calculateConfig);
		CalculateExecutor executor = factory.createExecutor();

		Param instance = factory.createParam();
		instance.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = testFunction(圆周率) + 1");
		BigDecimal 结果 = executor.exec(instance, formulaManager).getNumResult("结果");
		assertEquals(BigDecimal.valueOf(10087), 结果);
	}

	@Test
	public void log_operate() {
		CalculateConfig calculateConfig = new CalculateConfig();
		calculateConfig.setLogOperator(System.out::println);
		CalculateFactory factory = CalculateFactory.createFactory(calculateConfig);
		CalculateExecutor executor = factory.createExecutor();

		Param instance = factory.createParam();
		instance.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = 圆周率 + 1", new FormulaConditions() {
			@Override
			public Integer retainDecimal() {
				return 3;
			}
		});
		BigDecimal 结果 = executor.exec(instance, formulaManager).getNumResult("结果");
		assertEquals(BigDecimal.valueOf(4.142), 结果);
	}

	/**
	 * 测试函数
	 * 无论传入什么参数都返回 10086
	 *
	 * @author byw
	 * @date 2023/04/03
	 */
	public static class TestFunction extends Operator {

		@Override
		public Object executeInner(Object[] list) throws Exception {
			return BigDecimal.valueOf(10086);
		}
	}
}