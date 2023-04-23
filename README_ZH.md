# Calculate

Calculate 是一款用 Java 实现的，可以增加数学计算相关代码的灵活性，可读性和可维护性的工具。

## 传统开发方式存在的问题

### 代码可读性差

传统开发方式对代码的抒写方式缺少约束力，所以经常会将计算相关的代码与数据查询相关的代码混在一起，可读性较差。而此工具的使用方式必须先声明参与运算的参数，之后才能进行计算，所以在一定程度上将代码分割为两个部分，一个是数据处理和准备部分，另一个是进行计算的部分。增加了代码的可读性。

### 注释和代码的维护周期不同

对于有大量计算公式的代码来说，良好的注释能让我们快速的熟悉代码，甚至不用完全理解所有代码就能根据注释找到我们需要的那一行代码。但是随着需求的不断变更，有一些研发人员只是更改对应的代码，却没有维护代码对应的注释，这就导致在我们阅读代码的时候注释不仅不能为我们提供帮助，甚至会误导我们。而此工具将注释与代码结合到一起，强迫研发人员在修改代码的同时也要修改对应的注释。

### 排查问题成本较高

传统开发方式相当于黑盒模式，对于研发人员来说公式的计算过程也无法实时查看，当计算结果有问题时只能在代码中打断点查看每个计算参数是否正确，当代码包含大量计算公式时排查链路较长，排查成本较高。本工具会输出计算的流程，以及计算中使用的参数，可以直接通过输出的日志来排查问题，快速定位到最开始发生问题的公式，降低排查成本。

## 快速开始

### 添加 Maven 依赖

占位 占位

### 单值计算

工具支持单值计算和多值（列表）计算，我们先来看下单值计算。

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// 1. 首先创建工厂类实例
		CalculateFactory factory = CalculateFactory.createFactory();
		// 2. 通过工厂创建三种计算所需类的实例
		// 2.1. ParamContext param 是参数上下文，我们计算之前要把将要使用的参数注册到上下文中。同时公式的计算结果也会自动注册到参数上下文中，
		ParamContext param = factory.createParam();
		// 2.2. FormulaManager formula 是公式管理器，将要计算的公式需要注册到这里，会按照顺序执行对应的公式。
		FormulaManager formula = factory.createFormulaManager();
		// 2.3. CalculateExecutor executor 是公式执行器，是真正用来执行公式的。
		CalculateExecutor executor = factory.createExecutor();

		param.addNumber("小明的数学成绩", 90);
		param.addNumber("小明的语文成绩", 80);
		param.addNumber("小明的化学成绩", 70);

		formula.add("小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的化学成绩)/3");
		formula.add("小明平均成绩的2倍 = 小明的平均成绩 * 2");
		// 调用 exce 方法来执行公式
		ResultManager resultManager = executor.exec(param, formula);
		// 使用 ResultManager 实例可以获取上下文中的参数数值。
		BigDecimal avgGrade = resultManager.getNumResult("小明的平均成绩");
		BigDecimal avgDouble = resultManager.getNumResult("小明平均成绩的2倍");
	}
}

~~~

在计算的过程中会输出以下日志信息：

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的数学成绩 值：90
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的语文成绩 值：80
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的化学成绩 值：70
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明的平均成绩 = (小明的数学成绩 + 小明的语文成绩 + 小明的化学成绩)/3；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明的平均成绩 值：80.0000000000
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：小明平均成绩的2倍 = 小明的平均成绩 * 2；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：小明平均成绩的2倍 值：160.0000000000
~~~

恭喜你！已经掌握了本工具的使用方法。

不过你可能还有一些疑问，比如说参数名需要在运行时才能确定，没有办法像上面的例子中写死，如何解决这个问题呢？本工具提供了**字符串模板**来解决这个问题。

#### 动态设置参数名

还是基于上面的例子来进行拓展，假设我们现在有很多同学，而不是只有一个小明，我们如何来计算每个同学的平均成绩呢？代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void test_for_many_students_single() {
		CalculateFactory factory = CalculateFactory.createFactory();
		ParamContext param = factory.createParam();
		FormulaManager formula = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		// 因为有多个同学，所以要遍历
		for (StudentMessage studentMessage : createStudentMessageList()) {
			//{} 是字符串模板占位符的标志相当于形参，后续会用[studentMessage.getName() + "的数学成绩"]这个实参替换掉。实参可以写成一个方法方便复用，这里示例比较简单，所以没有写成一个方法。
			param.addNumber("{某同学的数学成绩}", studentMessage.getName() + "的数学成绩", studentMessage.getMathScore());
			param.addNumber("{某同学的语文成绩}", studentMessage.getName() + "的语文成绩", studentMessage.getChineseScore());
			param.addNumber("{某同学的化学成绩}", studentMessage.getName() + "的化学成绩", studentMessage.getChemistrySore());
			// 这里也是字符串模板，只不过有很多个形参需要替换，所以用 Lists.newArrayList 将实参传入进去
			formula.add("{某同学的平均成绩} = ({某同学的数学成绩} + {某同学的语文成绩} + {某同学的化学成绩})/3", Lists.newArrayList(studentMessage.getName() + "的平均成绩"
					, studentMessage.getName() + "的数学成绩", studentMessage.getName() + "的语文成绩", studentMessage.getName() + "的化学成绩"));
		}
		// 执行公式
		ResultManager resultManager = executor.exec(param, formula);
		assertEquals(BigDecimal.valueOf(80), resultManager.getNumResult("小明的平均成绩", 0));
		assertEquals(BigDecimal.valueOf(80), resultManager.getNumResult("小红的平均成绩", 0));
		assertEquals(BigDecimal.valueOf(87), resultManager.getNumResult("小李的平均成绩", 0));
	}

	// 创建测试所需数据
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

输出日志如下：

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

动态设置参数名的问题解决了，但是还有新的问题，就是在某些场景下一个值一个值去计算会很麻烦，比如现在完成了三次数学考试，现在想知道两位同学每次考试成绩的差值。比较方便的做法就是直接将包含多个数值的数组当做参数参与计算，下面我们来看下应该怎么做。

### 多值（列表）计算

问题：现在小明和小红都参与了三次数学竞赛，现在想知道每次考试小红比小明的成绩高多少分。代码如下：

~~~java

class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param_many = factory.createParam();
		// 将参数以集合的方式存储到参数上下文中
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));
		param_many.addArray("小红3次考试的数学成绩", Lists.newArrayList(90, 90, 70));

		// 公式中集合类型的参数需要在名称后面标注 '_index'
		formulaManager_many.add("每次考试小红比小明多几分_index = 小红3次考试的数学成绩_index - 小明3次考试的数学成绩_index");
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		// 通过结果参数名称获取对应的参数值，这里不需要加 _index 。计算的结果会自动将 _index 去掉。
		List<BigDecimal> 每次考试小红比小明多几分 = resultManager_mangy.getNumResultList("每次考试小红比小明多几分");
		boolean equalCollection = CollectionUtils.isEqualCollection(Lists.newArrayList(BigDecimal.valueOf(10), BigDecimal.valueOf(0), BigDecimal.valueOf(-20))
				, 每次考试小红比小明多几分);
		assertTrue(equalCollection);
	}
}

~~~

日志输出如下： 从日志可以看出公式在计算的时候会**按照索引对参数和公式进行拆分并进行对应的计算**，之前在公式上配置的 `_index` 后缀在运行时被替换成了 '_索引XXX'。

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

现在我们已经解决了多值（列表）计算的问题，但是有些场景并不是每个参数都是以数组的形式给出的，比如现在想要将小明每次考试的成绩乘以系数 0.8 那应该怎么做呢？代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		ParamContext param_many = factory.createParam();
		// 将参数以集合的方式存储到参数上下文中
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 90));
		// 公式中集合类型的参数需要在名称后面标注 '_index'
		formulaManager_many.add("小明最终的数学成绩_index = 小明3次考试的数学成绩_index * 0.8");
		ResultManager resultManager_mangy = executor.exec(param_many, formulaManager_many);
		// 通过结果参数名称获取对应的参数值，这里不需要加 _index 。计算的结果会自动将 _index 去掉。
		List<BigDecimal> 小明最终的数学成绩 = resultManager_mangy.getNumResultList("小明最终的数学成绩");
	}
}
~~~

日志如下：

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

掌握了以上内容就可以使用此工具进行开发了，更多功能可以参考进阶文档。

## 进阶文档

### 设置保留小数位数

有两种方式进行设置。

1. 全局设置。 可以通过 `CalculateConfig#setRetainDecimal` 进行全局设置。使用方式如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		//这里保留一位小数,配置需要在构造工厂实例的时候传入。
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

日志如下：从最后两行可以看出该工具在 4.14159 的基础上进行了四舍五入。

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：圆周率 值：3.14159
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：结果 = 圆周率 + 1；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.14159
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.1
~~~

2. 为某个公式单独配置，使用方式如下： FormulaManager#add 方法可以接受一个 FormulaConditions 类型的参数，实现 retainDecimal() 方法就可以实现保留小数的效果。

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
			//这里设置保留一位小数
			@Override
			public Integer retainDecimal() {
				return 1;
			}
		});
		BigDecimal 结果 = executor.exec(param, formulaManager).getNumResult("结果");
	}
}
~~~

日志如下：从最后两行可以看出该工具在 4.14159 的基础上进行了四舍五入。

~~~text
 INFO [main] (PrintLogOperator.java:18) - 参数名：圆周率 值：3.14159
 INFO [main] (PrintLogOperator.java:18) - 正在执行公式：结果 = 圆周率 + 1；开始执行条件：无约束；结束执行条件：无约束
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.14159
 INFO [main] (PrintLogOperator.java:18) - 参数名：结果 值：4.1
~~~

### 设置公式的开始执行和结束执行条件

在某些特殊的情况下，我们的公式需要在满足某些特定条件时才开始执行/停止。我们可以使用 `FormulaConditions#getStartConditions`
和 `FormulaConditions#getStopConditions`
来实现。代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		//单值计算
		//单值计算 同时设置开始和结束条件
		CalculateFactory factory = CalculateFactory.createFactory();
		CalculateExecutor executor = factory.createExecutor();
		FormulaManager formulaManager = factory.createFormulaManager();
		ParamContext param = factory.createParam();
		param.addNumber("小明的数学成绩", 90);

		formulaManager.add("小明的数学成绩 = 小明的数学成绩 + 1", new FormulaConditions() {
			// 设置公式开始执行的条件
			@Override
			public String getStartConditions() {
				return "小明的数学成绩 == 90";
			}

			// 设置公式结束执行的条件
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

日志如下：

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

1. 如果开始/结束条件由多个公式组成，可以使用 `&&` 或者 `||` 相连接。
2. 如果条件的参数是一个数组那么需要在名称后面添加 `_index` 后缀。

### 函数

#### 内置函数

本工具内置了很多有用的函数，这些函数可以直接在公式中使用。代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		CalculateFactory factory = CalculateFactory.createFactory();
		ParamContext param_many = factory.createParam();
		FormulaManager formulaManager_many = factory.createFormulaManager();
		CalculateExecutor executor = factory.createExecutor();
		param_many.addArray("小明3次考试的数学成绩", Lists.newArrayList(80, 90, 70));

		// listSum 函数，可以将列表内的数值加和。
		formulaManager_many.add("加和结果 = listSum(小明3次考试的数学成绩)");
		// listMax 函数，可以给出列表内数值的最大值
		formulaManager_many.add("最大值 = listMax(小明3次考试的数学成绩)");
		// listMin 函数，可以给出列表内数值的最小值
		formulaManager_many.add("最小值 = listMin(小明3次考试的数学成绩)");
		// default 函数，当使用的参数在上下文中无法找到时可以设置默认值
		formulaManager_many.add("默认加5分_index = 小明3次考试的数学成绩_index + default(额外加分项,5)");
		// defaultZero 函数 设置默认值，当使用的参数在上下文中无法找到时直接赋值 0
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

下面详细列出所有内置函数：

| 函数名称    | 作用                                           | 所需参数                           |
| ----------- | ---------------------------------------------- | ---------------------------------- |
| listSum     | 可以将列表内的数值加和                         | 只接受一个数组类型的参数           |
| listMax     | 可以给出列表内数值的最大值                     | 只接受一个数组类型的参数           |
| listMin     | 可以给出列表内数值的最小值                     | 只接受一个数组类型的参数           |
| default     | 当使用的参数在上下文中无法找到时可以设置默认值 | 接受两个参数，第二个参数是默认值。 |
| defaultZero | 当使用的参数在上下文中无法找到时可以设置默认值 | 只接受一个参数                     |
| max         | 给出传入参数的最大值                           | 接受多个非数组类型的参数           |
| min         | 给出传入参数的最小值                           | 接受多个非数组类型的参数           |

#### 自定义函数

尽管本工具已经内置了很多比较实用的函数，但是你仍然可以定义你自己的函数，这需要使用 `CalculateConfig#setFunctionConfig` 配置，需要注意的是函数的名字必须是唯一的。代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// 这里注册名字是“testFunction”的自定义函数
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
	 * 所有自定义函数都需要继承 com.ql.util.express.Operator
	 *
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
~~~

ps: 所有自定义函数都需要继承 com.ql.util.express.Operator。

### 拓展接口

#### 日志

默认情况下计算信息都是使用 `info`  级别的日志输出到日志文件中的。但是实际情况往往更为复杂，比如我们想要将日志保存到数据库中应该怎么做呢？

可以使用 `CalculateConfig#setLogOperatorClass` 方法将日志处理类替换成使用者自己的实现类，所有实现类都要实现 `LogOperator` 接口。代码如下：

~~~java
class CalculateExecutorTest {
	@Test
	public void common_compute() {
		// 这里注册了 MyLogOperator 日志处理实现类
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
			// 这里可以写保存日志的方法
		}
	}
}
~~~

#### 其他

工具的核心类 `公式管理器` 和 `参数上下文` 也支持用户拓展，需要使用 `CalculateConfig#setFormulaManagerClass` 和 `CalculateConfig#setParamContextClass`
方法来替换。

## 最佳实践

- CalculateExecutor 可以缓存起来不用每次都创建，只要配置不变就不用重新创建。
- ParamContext 每次计算开始前创建一个实例，在整个计算流程中都共享这一个实例即可。
- FormulaManager 建议每次调用 CalculateExecutor#exec 时创建一个实例。因为复用 FormulaManager 实例会导致公式被重复计算浪费资源，同时也可能会影响计算结果。
- 如果数值与时间是强相关的可以将参数转化为数组，并用 index 来代替时间进行运算。例如1月1日，1月2日进行了两次考试，那么就可以创建一个包含两次考试成绩的数组用 index 0 代替 1月1日，index 1 代替 1月2日。







