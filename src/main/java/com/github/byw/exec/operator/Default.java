package com.github.byw.exec.operator;

import com.github.byw.exception.CalculateException;
import com.ql.util.express.Operator;

import java.util.Optional;

public class Default extends Operator {
	@Override
	public Object executeInner(Object[] list) throws Exception {
		if (list.length == 1) {
			throw new CalculateException("该操作需要提供两个参数，目前只有一个");
		}
		return Optional.ofNullable(list[0]).orElse(list[1]);
	}
}
