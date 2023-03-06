package com.github.byw.exec.operator;

import com.github.byw.exception.CalculateException;
import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 获得列表加和值
 *
 * @author byw
 * @date 2022/11/24
 */
@SuppressWarnings("unchecked")
public class ListSum extends Operator {
	@Override
	public Object executeInner(Object[] list) throws Exception {
		if (list.length == 0) {
			return null;
		}
		if (list[0] instanceof Collection) {
			return ((Collection<?>) list[0]).stream().map(t -> {
				if (t instanceof BigDecimal) {
					return (BigDecimal) t;
				} else {
					return new BigDecimal(t.toString());
				}
			}).reduce(BigDecimal::add).get();
		}
		throw new CalculateException("listSum 函数接受的参数必须实现 Collection 接口");
	}
}
