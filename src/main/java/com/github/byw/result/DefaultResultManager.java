package com.github.byw.result;

import com.github.byw.exception.CalculateException;
import com.github.byw.param.ParamManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认结果管理
 *
 * @author byw
 * @date 2022/11/24
 */
public class DefaultResultManager implements ResultManager {
	/**
	 * 参数上下文
	 */
	private final ParamManager paramContext;

	public DefaultResultManager(ParamManager paramContext) {
		this.paramContext = paramContext;
	}

	@Override
	public BigDecimal getNumResult(String paramName) {
		return getOriginalNumResult(paramName);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BigDecimal> getNumResultList(String paramName) {
		Object result = paramContext.get(paramName);
		if (result == null) {
			return null;
		}
		if (!(result instanceof List)) {
			throw new CalculateException("结果类型不是 List");
		}
		List resultList = (List) result;

		resultList = (List) resultList.stream().map(t -> {
			if (!(t instanceof BigDecimal)) {
				return new BigDecimal(t.toString());
			}
			return t;
		}).collect(Collectors.toList());
		return (List<BigDecimal>) resultList;
	}

	@Override
	public BigDecimal getNumResult(String paramName, int scale) {
		BigDecimal result = this.getOriginalNumResult(paramName);
		if (result == null) {
			return null;
		}
		return result.setScale(scale, RoundingMode.HALF_UP);
	}

	@Override
	public Boolean getBoolResult(String paramName) {
		Object result = paramContext.get(paramName);
		if (result == null) {
			return null;
		}
		if (result instanceof Boolean) {
			return (Boolean) result;
		}

		throw new CalculateException("paramName 不是 Boolean 类型");
	}

	/**
	 * 得到原始num结果
	 *
	 * @param paramName 参数名称
	 */
	private BigDecimal getOriginalNumResult(String paramName) {
		Object result = paramContext.get(paramName);
		if (result == null) {
			return null;
		}
		if (result instanceof BigDecimal) {
			return (BigDecimal) result;
		}
		BigDecimal bigDecimal;
		try {
			bigDecimal = new BigDecimal(result.toString());
		} catch (NumberFormatException e) {
			throw new CalculateException("参数 " + paramName + " 不是数值" + " 真实值：" + result);
		}
		return bigDecimal;
	}
}
