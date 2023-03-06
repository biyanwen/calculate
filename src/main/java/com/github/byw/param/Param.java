package com.github.byw.param;

import com.github.byw.exception.CalculateException;
import com.github.byw.helper.StringFormatter;
import com.google.common.collect.Lists;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.IExpressContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数
 *
 * @author byw
 * @date 2022/11/23
 */
public class Param {
	private static final Logger LOGGER = LoggerFactory.getLogger(Param.class);

	/**
	 * 参数上下文
	 * key：参数名称 value：参数值
	 */
	private final IExpressContext<String, Object> paramContext = new LogContext();

	/**
	 * 配置上下文
	 * key：参数名称 value：参数配置
	 */
	private final Map<String, ParamConfig> configContext = new HashMap<>();

	private Param() {
	}

	public static Param getInstance() {
		return new Param();
	}

	/**
	 * 添加数值
	 *
	 * @param paramName 参数名称
	 * @param number    数值
	 * @return {@link Param}
	 */
	public <T extends Number> Param addNumber(String paramName, T number) {
		add(paramName, number, false);
		return this;
	}

	/**
	 * 添加数值
	 *
	 * @param template 模板
	 * @param arg      参数值
	 * @param number   数量
	 * @return {@link Param}
	 */
	public <T extends Number> Param addNumber(String template, String arg, T number) {
		return this.addNumber(template, Lists.newArrayList(arg), number);
	}

	/**
	 * 添加数值
	 *
	 * @param template 模板
	 * @param args     参数值
	 * @param number   数量
	 * @return {@link Param}
	 */
	public <T extends Number> Param addNumber(String template, List<String> args, T number) {
		String format = StringFormatter.format(template, args.toArray(new String[0]));
		this.add(format, number, false);
		return this;
	}

	/**
	 * 添加数组
	 *
	 * @param paramName 参数名称
	 * @param array     数组
	 * @return {@link Param}
	 */
	public <R extends Number, T extends List<R>> Param addArray(String paramName, T array) {
		add(paramName, array, true);
		return this;
	}

	/**
	 * 添加数组数组
	 *
	 * @param array    数组
	 * @param arg      参数
	 * @param template 模板
	 * @return {@link Param}
	 */
	public <R extends Number, T extends List<List<R>>> Param addArrayArray(String template, String arg, T array) {
		String paramName = StringFormatter.format(template, arg);
		add(paramName, array, true);
		return this;
	}


	/**
	 * 添加数组
	 *
	 * @param template 模板
	 * @param arg      模板所需参数
	 * @param array    数组
	 * @return {@link Param}
	 */
	public <R extends Number, T extends Collection<R>> Param addArray(String template, String arg, T array) {
		return this.addArray(template, Lists.newArrayList(arg), array);
	}

	/**
	 * 添加数组
	 *
	 * @param template 模板
	 * @param args     模板所需参数
	 * @param array    数组
	 * @return {@link Param}
	 */
	public <R extends Number, T extends Collection<R>> Param addArray(String template, List<String> args, T array) {
		String format = StringFormatter.format(template, args.toArray(new String[0]));
		add(format, array, true);
		return this;
	}


	/**
	 * 得到参数上下文
	 *
	 * @return {@link IExpressContext}<{@link String}, {@link Object}>
	 */
	public IExpressContext<String, Object> getParamContext() {
		return paramContext;
	}

	/**
	 * 获取配置
	 *
	 * @param paramName 参数名称
	 * @return {@link ParamConfig}
	 */
	public ParamConfig getConfig(String paramName) {
		return configContext.get(paramName);
	}

	private void add(String paramName, Object value, boolean isArray) {
		if (value == null) {
			throw new CalculateException(paramName + " 参数为空，请检查数据");
		}
		paramContext.put(paramName, value);
	}

	private class LogContext extends DefaultContext<String, Object> {

		@Override
		public Object put(String key, Object value) {
			LOGGER.info("参数名：" + key + " 值：" + value);

			boolean isArray = value instanceof Collection;
			int size = 1;
			if (isArray) {
				size = ((List) value).size();
			}
			configContext.put(key, new ParamConfig(isArray, size, value.getClass()));
			return super.put(key, value);
		}

		@Override
		public Object get(Object key) {
			Object obj = super.get(key);
			if (obj == null) {
				LOGGER.info("参数值为空：" + key);
			}
			return obj;
		}
	}
}
