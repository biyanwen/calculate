package com.github.byw.exec.config;

import lombok.Data;

/**
 * 计算配置
 *
 * @author byw
 * @date 2023/02/07
 */
@Data
public class CalculateConfig {

	/**
	 * 保留几位小数。默认不进行四舍五入。
	 */
	private Integer retainDecimal;

	/**
	 * 此属性只在参数是列表类型的时候生效。
	 * <p>
	 * param总大小。本工具在列表计算的时候需要知道参数的总长度才行帮忙进行循环计算。
	 * 当有些特殊情况导致本工具无法获取参数的总长度时可以从外部传入总长度。
	 * <p>
	 * 特殊情况举例：
	 * 例子1：a_index = defaultZero(b_index) + defaultZero(c_index); 当 b 和 c 都是空的时候就会导致无法获取参数长度的问题。
	 * 例子2：a_index = 0 。这种右侧只有一个常数的情况会导致无法获取参数长度的问题。
	 */
	private Integer paramTotalSize;

	/**
	 * 此属性只在参数是列表类型的时候生效。
	 * <p>
	 * 当前用参数列表中指定索引对应的数值进行计算
	 * <p>
	 * 例如当 currentIndex = 1 的时候，如果外界没有更改这个参数的数值，那么就永远只使用索引 1 的数值进行计算。
	 * 极特殊的场景会使用到。
	 */
	private Integer currentIndex;

	public CalculateConfig() {
	}
}
