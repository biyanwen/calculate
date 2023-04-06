package com.github.byw.log;

/**
 * 处理计算过程中产生的日志
 *
 * @author byw
 * @date 2023/04/03
 */
public interface LogOperator {

	/**
	 * 操作
	 *
	 * @param log 日志
	 */
	void operate(String log);
}
