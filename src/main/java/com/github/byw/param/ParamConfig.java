package com.github.byw.param;

/**
 * 参数配置
 *
 * @author byw
 * @date 2022/11/23
 */
public class ParamConfig {
	private final boolean isArray;

	private final int size;

	private final Class<?> paramClass;

	public ParamConfig(boolean isArray, int size, Class<?> paramClass) {
		this.isArray = isArray;
		this.size = size;
		this.paramClass = paramClass;
	}

	public boolean isArray() {
		return isArray;
	}

	public int getSize() {
		return size;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}
}