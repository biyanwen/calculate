package com.github.byw.result;

import java.math.BigDecimal;
import java.util.List;

/**
 * 结果管理
 *
 * @author byw
 * @date 2022/11/24
 */
public interface ResultManager {

	/**
	 * 得到数字类结果
	 *
	 * @param paramName 参数名称
	 * @return {@link BigDecimal}
	 */
	BigDecimal getNumResult(String paramName);

	/**
	 * 得到数字类结果 列表
	 *
	 * @param paramName 参数名称
	 * @return {@link List}<{@link BigDecimal}>
	 */
	List<BigDecimal> getNumResultList(String paramName);
	/**
	 * 得到数字类结果
	 *
	 * @param paramName 参数名称
	 * @param scale     精度
	 * @return {@link BigDecimal}
	 */
	BigDecimal getNumResult(String paramName, int scale);

	/**
	 * 得到bool结果
	 *
	 * @param paramName 参数名称
	 * @return {@link Boolean}
	 */
	Boolean getBoolResult(String paramName);
}
