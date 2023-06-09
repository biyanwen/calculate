# Calculate

English | [中文](README_ZH.md)

Calculate is a tool implemented in Java, it can improve the flexibility, readability, and maintainability of
mathematical calculation code.

## Problems Of traditional programming methods

### Poor code readability

There is a problem with the lack of restraint for programmers when using traditional programming methods. So some
programmers would mingle the data query code with mathematical calculation code, which can make the code difficult to
read. But the 'Calculate' could reform this problem. Because you should declare arguments before perform calculations,
which can make the code to split with a data query part and a mathematical calculation part. This could help to improve
the code readability.

### The comments and the code have a different cycle of maintenance

For code that contains many formulas, good comments can be very helpful in quickly understanding the code. This can
enable us to find the target code without having to read all the related code.But with the continually changing demands
of code. Some programmers change the code without changing the comments along with it. This can misguide us in
understanding code. So this tool combines code with comments, which can oblige programmers to change the code and
comments together.

### High cost of troubleshooting problems

Traditional programming methods are like black box mode, do not allow us to check the status of calculation in real
time. When result of calculation is an error, we need to debug the code and check every argument. This would let us take
a lot of time when there are a lot of formulas. This tool will output procedure logs of calculation. Programmers can
find the bug quickly by logs of procedure.This is very helpful in reducing the time required to solve problems.

## Quick start

### Add Maven dependence

占位 占位

### Calculate of single value

This tool support calculate of single value and many(list) value. We first take a look at the calculation of single
value.

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// 1. Create the instance of factory. 
		CalculateFactory factory = CalculateFactory.createFactory();
		// 2. Create three instances by the factory instance. 
		// 2.1.'ParamContext param' is a context of arguments. We must register the value of arguments in the 'ParamContext' before calculate.Once the calculation is complete, the result of calculation will be automatically registered in 'ParamContext'.
		ParamContext param = factory.createParam();
		// 2.2.'FormulaManager formula' is a formula manager. We can register formulas in the 'FormulaManager',and then the formulas will be sequentially executed.
		FormulaManager formula = factory.createFormulaManager();
		// 2.3.'CalculateExecutor executor' is an executor for calculating formulas.
		CalculateExecutor executor = factory.createExecutor();

		param.addNumber("小明的数学成绩", 90);
		param.addNumber("小明的语文成绩", 80);
		param.addNumber("小明的化学成绩", 70);

		formula.add("小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的化学成绩)/3");
		formula.add("小明平均成绩的2倍 = 小明的平均成绩 * 2");
		// Invoking the method of execution. 
		ResultManager resultManager = executor.exec(param, formula);
		// Using the 'ResultManager' to get the result of calculation in the context of arguments.
		BigDecimal avgGrade = resultManager.getNumResult("小明的平均成绩");
		BigDecimal avgDouble = resultManager.getNumResult("小明平均成绩的2倍");
	}
}

~~~

You can receive logs of calculation at a file of log when formulas are executing. Like this:

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的语文成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的化学成绩 值：70
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的化学成绩)/3；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的平均成绩 值：80.0000000000
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明平均成绩的2倍 = 小明的平均成绩 * 2；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明平均成绩的2倍 值：160.0000000000
~~~

Congratulations! You have been mastering this tool.But you may have some questions. For example, you have many arguments
whose names need to be confirmed at runtime, so you cannot hardcode them in the code. So we provide **the template of
string** to solve this problem.

#### Dynamically setting names of arguments

We extend the example base on previous code. How to dynamically set the names of students when we calculate the average
score of every student?

~~~java
class CalculateExecutorTest {
	@Test
	public void test_for_many_students_single() {
		CalculateFactory factory = CalculateFactory.createFactory();
		ParamContext param = factory.createParam();
		FormulaManager formula = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		// We need to traverse every student. 
		for (StudentMessage studentMessage : createStudentMessageList()) {
			//'{}' is a placeholder of string template which has formal parameter features. Then the placeholder will be replaced with the value of 'studentMessage.getName() + "的数学成绩"' at runtime.
			// ps: It is better to write a function that returns 'studentMessage.getName() + "的数学成绩"' so that we can reuse it in any context.
			param.addNumber("{某同学的数学成绩}", studentMessage.getName() + "的数学成绩", studentMessage.getMathScore());
			param.addNumber("{某同学的语文成绩}", studentMessage.getName() + "的语文成绩", studentMessage.getChineseScore());
			param.addNumber("{某同学的化学成绩}", studentMessage.getName() + "的化学成绩", studentMessage.getChemistrySore());
			// We use 'Lists.newArrayList' to pass in the actual parameters, because there are multiple placeholder to be replaced.
			formula.add("{某同学的平均成绩} = ({某同学的数学成绩} + {某同学的语文成绩} + {某同学的化学成绩})/3", Lists.newArrayList(studentMessage.getName() + "的平均成绩"
					, studentMessage.getName() + "的数学成绩", studentMessage.getName() + "的语文成绩", studentMessage.getName() + "的化学成绩"));
		}
		// Executing formulas 
		ResultManager resultManager = executor.exec(param, formula);
		assertEquals(BigDecimal.valueOf(80), resultManager.getNumResult("小明的平均成绩", 0));
		assertEquals(BigDecimal.valueOf(80), resultManager.getNumResult("小红的平均成绩", 0));
		assertEquals(BigDecimal.valueOf(87), resultManager.getNumResult("小李的平均成绩", 0));
	}

	// Create the data of test.
	private List<StudentMessage> createStudentMessageList() {
		return Lists.newArrayList(new StudentMessage("小明", BigDecimal.valueOf(80), BigDecimal.valueOf(80), BigDecimal.valueOf(80))
				, new StudentMessage("小红", BigDecimal.valueOf(80), BigDecimal.valueOf(90), BigDecimal.valueOf(70))
				, new StudentMessage("小李", BigDecimal.valueOf(100), BigDecimal.valueOf(90), BigDecimal.valueOf(70)));
	}

	private static class StudentMessage {
		private String name;

		private BigDecimal mathScore;

		private BigDecimal chineseScore;

		private BigDecimal chemistrySore;

		public StudentMessage(String name, BigDecimal mathScore, BigDecimal chineseScore, BigDecimal chemistrySore) {
			this.name = name;
			this.mathScore = mathScore;
			this.chineseScore = chineseScore;
			this.chemistrySore = chemistrySore;
		}

		public String getName() {
			return name;
		}

		public BigDecimal getMathScore() {
			return mathScore;
		}

		public BigDecimal getChineseScore() {
			return chineseScore;
		}

		public BigDecimal getChemistrySore() {
			return chemistrySore;
		}
	}
}
~~~

The output log is as follows:

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的语文成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的化学成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红的数学成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红的语文成绩 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红的化学成绩 值：70
 INFO [main] (PrintLogOperator.java:18) - 参数名：小李的数学成绩 值：100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小李的语文成绩 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小李的化学成绩 值：70
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的化学成绩)/3；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的平均成绩 值：80.0000000000
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小红的平均成绩 = (小红的数学成绩 + 小红的语文成绩 + 小红的化学成绩)/3；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红的平均成绩 值：80.0000000000
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小李的平均成绩 = (小李的数学成绩 + 小李的语文成绩 + 小李的化学成绩)/3；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小李的平均成绩 值：86.6666666667
~~~

The question of dynamically setting name have be resolved. But there is a question for calculation of many values. It is
very trouble if we calculate one by one. Resolving this problem, we can use arrays to perform calculation. For example,
three math examination has been done. Now we want to know the difference between the scores of two students in each
exam. We create two array to contain the three scores for each student, then we subtract one array from the other.

### Calculate of many values (List)

Now Xiao Ming and Xiao Hong have taken three math exam. We want to know the difference between the scores of two
students in each exam. The code is as follows:

~~~java

class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param_many = factory.createParam();
		// Creating an array to contain arguments, and then register it in the context of arguments 
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));
		param_many.addArray("小红3次考试的数学成绩", Lists.newArrayList(90, 90, 70));

		// The arguments, which type is array, need to add a suffix of '_index'. 
		formulaManager_many.add("每次考试小红比小明多几分_index = 小红3次考试的数学成绩_index - 小明3次考试的数学成绩_index");
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		// Retrieve the calculated result with the specified argument name.
		// ps: At this time, you do not need to add the suffix of '_index'.
		List<BigDecimal> 每次考试小红比小明多几分 = resultManager_mangy.getNumResultList("每次考试小红比小明多几分");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(10), BigDecimal.valueOf(0), BigDecimal.valueOf(-20))
				, 每次考试小红比小明多几分);
		assertTrue(equalCollection);
	}
}

~~~

The output log is as follows: At runtime, this tool splits the formula and argument through the index of array, and then
execute it. We can see the `_index` has been replaced with '_索引XXX'.

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩 值：[80, 90, 90]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红3次考试的数学成绩 值：[90, 90, 70]
 INFO [main] (PrintLogOperator.java:18) - 上下文中不存在此参数：每次考试小红比小明多几分
 INFO [main] (ListDataExecutor.java:133) - 初始化结果参数：每次考试小红比小明多几分
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分 值：[0, 0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红3次考试的数学成绩_索引0 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引0 值：80
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：每次考试小红比小明多几分_索引0 = 小红3次考试的数学成绩_索引0 - 小明3次考试的数学成绩_索引0；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分_索引0 值：10
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分 值：[10, 0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红3次考试的数学成绩_索引1 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引1 值：90
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：每次考试小红比小明多几分_索引1 = 小红3次考试的数学成绩_索引1 - 小明3次考试的数学成绩_索引1；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分_索引1 值：0
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分 值：[10, 0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小红3次考试的数学成绩_索引2 值：70
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引2 值：90
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：每次考试小红比小明多几分_索引2 = 小红3次考试的数学成绩_索引2 - 小明3次考试的数学成绩_索引2；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分_索引2 值：-20
 INFO [main] (PrintLogOperator.java:18) - 参数名：每次考试小红比小明多几分 值：[10, 0, -20]
~~~

The question of many values has been resolved. Meanwhile, we can calculate with an array and single value. For example,
we want to multiply with an array argument and 0.8. The code is as follows

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param_many = factory.createParam();

		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));
		formulaManager_many.add("小明最终的数学成绩_index = 小明3次考试的数学成绩_index * 0.8");
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);

		List<BigDecimal> 小明最终的数学成绩 = resultManager_mangy.getNumResultList("小明最终的数学成绩");
	}
}
~~~

The output log is as follows:

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩 值：[80, 90, 90]
 INFO [main] (PrintLogOperator.java:18) - 上下文中不存在此参数：小明最终的数学成绩
 INFO [main] (ListDataExecutor.java:133) - 初始化结果参数：小明最终的数学成绩
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩 值：[0, 0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引0 值：80
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明最终的数学成绩_索引0 = 小明3次考试的数学成绩_索引0 * 0.8；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩_索引0 值：64.0
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩 值：[64.0, 0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引1 值：90
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明最终的数学成绩_索引1 = 小明3次考试的数学成绩_索引1 * 0.8；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩_索引1 值：72.0
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩 值：[64.0, 72.0, 0]
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明3次考试的数学成绩_索引2 值：90
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明最终的数学成绩_索引2 = 小明3次考试的数学成绩_索引2 * 0.8；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩_索引2 值：72.0
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明最终的数学成绩 值：[64.0, 72.0, 72.0]
~~~

If you have mastered the above content, you can now develop the application using this tool. More functions about this
tool please read the documents of advanced.

## The documents of advanced

### The configuration for maintaining decimal precision

We have two ways to achieve this goal.

1. global configuration. You can set the global configuration using 'CalculateConfig#setRetainDecimal'. Using the method
   as follows:

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// The configuration of keeping one decimal place, which needs to be passed in the factory instance at creating the factory instance.
		CalculateFactory factory = CalculateFactory.createFactory(new CalculateConfig().setRetainDecimal(1));
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param = factory.createParam();
		param.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = 圆周率 + 1");
		BigDecimal 结果 = executor.exec(param, formulaManager).getNumResult("结果");
	}
}
~~~

The output logs is as follows: In the last two lines, we can see the operation of keeping one decimal place.

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：圆周率 值：3.14159
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：结果 = 圆周率 + 1；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.14159
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.1
~~~

2. We can configure the maintaining decimal precision for a single formula. The 'FormulaManager#add' can receive a
   parameter of 'FormulaConditions', you can maintain decimal precision by implementing the 'retainDecimal()' method
   of 'FormulaConditions'

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param = factory.createParam();
		param.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = 圆周率 + 1", new FormulaConditions() {
			//We can configure the maintaining decimal precision for this formula. 
			@Override
			public Integer retainDecimal() {
				return 1;
			}
		});
		BigDecimal 结果 = executor.exec(param, formulaManager).getNumResult("结果");
	}
}
~~~

The output logs is as follows:

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：圆周率 值：3.14159
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：结果 = 圆周率 + 1；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.14159
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.1
~~~

### Set conditions of starting and stopping for a formula.

In certain circumstances, special conditions must be met when starting or stopping a formula. We can achieve this goal
using the 'FormulaConditions#getStartConditions' and 'FormulaConditions#getStopConditions' methods.

The code is as follows:

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		CalculateExecutor executor = factory.createExecutor();
		FormulaManager formulaManager = factory.createFormulaManager();
		ParamContext param = factory.createParam();
		param.addNumber("小明的数学成绩", 90);

		formulaManager.add("小明的数学成绩 = 小明的数学成绩 + 1", new FormulaConditions() {
			// Set the starting condition.
			@Override
			public String getStartConditions() {
				return "小明的数学成绩 == 90";
			}

			// Set the stoping condition.
			@Override
			public String getStopConditions() {
				return "小明的数学成绩 == 100";
			}
		});
		ResultManager resultManager = executor.exec(param, formulaManager);
		assertEquals(new BigDecimal("100"), resultManager.getNumResult("小明的数学成绩"));
	}
}
~~~

The output logs is as follows:

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：90
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：91
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：92
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：93
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：94
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：95
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：96
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：97
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：98
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：99
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的数学成绩 = 小明的数学成绩 + 1；开始执行条件：小明的数学成绩 == 90；结束执行条件：小明的数学成绩 == 100
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：100
~~~

ps：

1. If you have multiple formulas of starting or stopping that you can use `&&` or `||` to connect them.
2. If the parameter of the formula is an array that you should add the suffix of '_index' for it.

### Function

#### Built-in functions

Many functions have been built-in this tool which can be used in formulas directly. The code is as follows:

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		ParamContext param_many = factory.createParam();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 70));

		// listSum function. It can sum up all elements of an array.
		formulaManager_many.add("加和结果 = listSum(小明3次考试的数学成绩)");
		// listMax function. It can get the max element of an array.
		formulaManager_many.add("最大值 = listMax(小明3次考试的数学成绩)");
		// listMin function. It can get the min element of an array.
		formulaManager_many.add("最小值 = listMin(小明3次考试的数学成绩)");
		// default function. It can give a custom value when unable to find the parameter in context. 
		formulaManager_many.add("默认加5分_index = 小明3次考试的数学成绩_index + default(额外加分项,5)");
		// defaultZero function. It can give a zero value when unable to find the parameter in context. 
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
}
~~~

The detail message of the built-in functions is as follows:

| name        | feature                                                                 | text                                                                                          |
|-------------|-------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| listSum     | It can sum up all elements of an array                                  | Only receive one parameter of the array type                                                  |
| listMax     | It can get the max element of an array                                  | Only receive one parameter of the array type                                                  |
| listMin     | It can get the min element of an array                                  | Only receive one parameter of the array type                                                  |
| default     | It can give a custom value when unable to find the parameter in context | Receive two parameters, the  second  one  is  a  custom  value  that  needs  to  be  returned |
| defaultZero | It can give a zero value when unable to find the parameter in context   | Only receive one parameter                                                                    |
| max         | It can get the max value of parameters                                  | Receive multiple parameters which is non-array                                                |
| min         | It can get the min value of parameters                                  | Receive multiple parameters which is non-array                                                |

#### Custom functions

You can define a custom function using 'CalculateConfig#setFunctionConfig'. ps: The name of the custom function must be
unique. The code is as follows:

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// Register a custom function that name is 'testFunction'.
		CalculateConfig calculateConfig =
				new CalculateConfig().setFunctionConfig(new FunctionConfig().addFunction("testFunction", new TestFunction()));

		CalculateFactory factory = CalculateFactory.createFactory(calculateConfig);
		CalculateExecutor executor = factory.createExecutor();

		ParamContext instance = factory.createParam();
		instance.addNumber("圆周率", 3.14159);
		FormulaManager formulaManager = factory.createFormulaManager();
		formulaManager.add("结果 = testFunction(圆周率) + 1");
		BigDecimal 结果 = executor.exec(instance, formulaManager).getNumResult("结果");
		assertEquals(BigDecimal.valueOf(10087), 结果);
	}

	/**
	 * The implementation of a custom function must extend 'com.ql.util.express.Operator'.
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
~~~

ps: The implementation of a custom function must extend 'com.ql.util.express.Operator'.

### Expansion points

#### log

By default, the logs are output in a log file through the info level. But in fact, we could want to save the logs in the
database.By using the configuration of the 'CalculateConfig#setLogOperatorClass', you can pass in a custom implement
of 'LogOperator' interface , and then you can achieve the goal.

The code is as follows:

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// The 'MyLogOperator' class is my class for custom implementation. 
		CalculateFactory factory = CalculateFactory.createFactory(new CalculateConfig().setLogOperator(MyLogOperator.class));
		CalculateExecutor executor = factory.createExecutor();

		ParamContext instance = factory.createParam();
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

	public static class MyLogOperator implements LogOperator {

		@Override
		public void operate(String log) {
			// You can write the logic of saving logs in a database at this.
		}
	}
}
~~~

#### Other

The core classes 'FormulaManager' and 'ParamContext' are supporting to extending. You can use '
CalculateConfig#setFormulaManagerClass' and 'CalculateConfig#setParamContextClass' to achieve the goal.

## Best Practices

- The 'CalculateExecutor' can be cached and used anywhere when the configuration is not changing.
- The 'ParamContext' needs to be created at every time of calculation.
- The 'FormulaManager' needs to be created every time invoking 'CalculateExecutor#exec', because reuses it will lead to
  the formula to be repetition execution.
- If the numerical value is strongly correlated with time, the parameter can be converted into an array, and an index
  can be used instead of time for calculation. For example, if two exams were taken on January 1st and January 2nd,
  respectively, an array containing the scores of the two exams can be created, using index 0 instead of January 1st and
  index 1 instead of January 2nd.









