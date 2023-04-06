package com.github.byw.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印日志操作
 * 会将计算过程中产生的日志以 INFO 级别的日志输出
 *
 * @author byw
 * @date 2023/04/03
 */
public class PrintLogOperator implements LogOperator {
	protected static final Logger LOGGER = LoggerFactory.getLogger(LogOperator.class);

	@Override
	public void operate(String log) {
		LOGGER.info(log);
	}
}
