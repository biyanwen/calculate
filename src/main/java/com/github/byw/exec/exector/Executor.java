package com.github.byw.exec.exector;


import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.formula.Formula;
import com.github.byw.param.Param;
import com.ql.util.express.Operator;

/**
 * 公式执行器
 *
 * @author byw
 * @date 2023/02/01
 */
public interface Executor {

	/**
	 * 判断当前公式是否可以由此执行器处理
	 *
	 * @param formulaInstance 公式实例
	 * @return boolean
	 */
	boolean canHandle(Formula formulaInstance);

	/**
	 * 执行公式
	 * 这里不会返回任何数据，公式执行输出的结果会存储到上下文中
	 *
	 * @param formulaInstance 公式实例
	 * @param param           参数
	 * @param config          配置
	 */
	void exec(Formula formulaInstance, Param param, CalculateConfig config);


	/**
	 * 注册函数
	 *
	 * @param name     名字
	 * @param operator 函数
	 */
	void registerFunction(String name, Operator operator);
}
