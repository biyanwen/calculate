package com.github.byw.param;

import com.github.byw.exec.config.CalculateConfig;

import java.util.Collection;
import java.util.List;

/**
 * 参数上下文
 *
 * @author byw
 * @date 2023/04/06
 */
public interface ParamContext {

	/**
	 * 添加数值参数
	 *
	 * @param paramName 参数名称
	 * @param number    数值
	 * @return {@link ParamContext}
	 */
	<T extends Number> ParamContext addNumber(String paramName, T number);

	/**
	 * 添加数值参数
	 *
	 * @param template 模板
	 * @param arg      参数值
	 * @param number   数量
	 * @return {@link ParamContext}
	 */
	<T extends Number> ParamContext addNumber(String template, String arg, T number);

	/**
	 * 添加数值参数
	 *
	 * @param template 模板
	 * @param args     参数值
	 * @param number   数量
	 * @return {@link ParamContext}
	 */
	<T extends Number> ParamContext addNumber(String template, List<String> args, T number);

	/**
	 * 添加数组参数
	 *
	 * @param paramName 参数名称
	 * @param array     数组
	 * @return {@link ParamContext}
	 */
	<R extends Number, T extends List<R>> ParamContext addArray(String paramName, T array);

	/**
	 * 添加数组数组
	 *
	 * @param array    数组
	 * @param arg      参数
	 * @param template 模板
	 * @return {@link ParamContext}
	 */
	<R extends Number, T extends List<List<R>>> ParamContext addArrayArray(String template, String arg, T array);

	/**
	 * 添加数组
	 *
	 * @param template 模板
	 * @param arg      模板所需参数
	 * @param array    数组
	 * @return {@link ParamContext}
	 */
	<R extends Number, T extends Collection<R>> ParamContext addArray(String template, String arg, T array);

	/**
	 * 添加数组
	 *
	 * @param template 模板
	 * @param args     模板所需参数
	 * @param array    数组
	 * @return {@link ParamContext}
	 */
	<R extends Number, T extends Collection<R>> ParamContext addArray(String template, List<String> args, T array);

	/**
	 * 得到参数上下文
	 *
	 * @return {@link ParamManager}
	 */
	ParamManager getParamContext();

	/**
	 * 获取配置（每个参数都有自己的配置信息，后续计算的时候会使用）
	 *
	 * @param paramName 参数名称
	 * @return {@link ParamConfig}
	 */
	ParamConfig getParamConfig(String paramName);

	/**
	 * 设置计算配置
	 *
	 * @param config 配置
	 */
	void setCalculateConfig(CalculateConfig config);

}
