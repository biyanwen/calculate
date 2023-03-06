package com.github.byw.exception;

import com.github.byw.helper.StringFormatter;

/**
 * 计算异常
 *
 * @author byw
 * @date 2023/02/22
 */
public class CalculateException extends RuntimeException {
	public CalculateException(String s) {
		super(s);
	}

	public CalculateException(String template, String... args) {
		super(StringFormatter.format(template, args));
	}
}
