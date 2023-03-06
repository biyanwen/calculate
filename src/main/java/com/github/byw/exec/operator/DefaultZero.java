package com.github.byw.exec.operator;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 如果参数不存在，提供默认参数0
 *
 * @author byw
 * @date 2022/12/09
 */
public class DefaultZero extends Operator {
	@Override
	public Object executeInner(Object[] list) throws Exception {
		Object obj = list[0];
		return Optional.ofNullable(obj).orElse(BigDecimal.ZERO);
	}
}
