package com.github.byw.formula;

/**
 * 参数条件
 *
 * @author byw
 * @date 2022/11/23
 */
public interface FormulaConditions {

	/**
	 * 得到开始执行条件(什么情况下公式才执行)
	 * 返回 null 或空集合代表没有限制，公式总会执行。
	 * <p>
	 * 参数应该是可以得出 boolean 值的公式
	 *
	 * @return {@link String}
	 */
	default String getStartConditions() {
		return null;
	}

	/**
	 * 得到停止执行条件(什么情况下公式才结束，有些情况需要公式不断迭代)
	 * 返回 null 代表公式只会执行一次。
	 * <p>
	 * 参数应该是一个会得出 boolean 值的公式
	 *
	 * @return {@link String}
	 */
	default String getStopConditions() {
		return null;
	}

	/**
	 * 保留几位小数，每个公式可以自定义配置，当统一的配置不能满足需求的时候使用。
	 * 例如，其他公式都保留4位小数，但是偏偏这个公式的结果就只能保留2位小数才能满足要求的时候使用。
	 *
	 * @return boolean
	 */
	default Integer retainDecimal() {
		return null;
	}
}
