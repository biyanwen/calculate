package com.github.byw.exec.exector;


import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.formula.Formula;
import com.github.byw.param.Param;

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
	 *
	 * @param formulaInstance 公式实例
	 * @param param           参数
	 * @param config          配置
	 */
	void exec(Formula formulaInstance, Param param, CalculateConfig config);
}
