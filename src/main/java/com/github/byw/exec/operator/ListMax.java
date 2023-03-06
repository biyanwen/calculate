package com.github.byw.exec.operator;

import com.github.byw.exception.CalculateException;
import com.ql.util.express.Operator;

import java.util.Collection;
import java.util.Comparator;

/**
 * 获得列表最大值
 *
 * @author byw
 * @date 2022/11/24
 */
@SuppressWarnings("unchecked")
public class ListMax extends Operator {
	@Override
	public Object executeInner(Object[] list) throws Exception {
		if (list.length == 0) {
			return null;
		}
		if (list[0] instanceof Collection) {
			return ((Collection<?>) list[0]).stream().map(t -> (Comparable<Object>) t).max(Comparator.naturalOrder()).get();
		}
		throw new CalculateException("listMax 函数接受的参数必须实现 Collection 接口");
	}
}
