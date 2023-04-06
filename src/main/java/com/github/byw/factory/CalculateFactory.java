package com.github.byw.factory;

import com.github.byw.exec.CalculateExecutor;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.param.Param;
import lombok.Data;

/**
 * 工厂
 *
 * @author byw
 * @date 2023/04/03
 */
@Data
public class CalculateFactory {

	private CalculateConfig config = new CalculateConfig();

	private CalculateFactory() {
	}

	/**
	 * 创建工厂 - 不使用配置
	 *
	 * @return {@link CalculateFactory}
	 */
	public static CalculateFactory createFactory() {
		return new CalculateFactory();
	}

	/**
	 * 创建工厂 - 使用配置
	 *
	 * @param config 配置
	 * @return {@link CalculateFactory}
	 */
	public static CalculateFactory createFactory(CalculateConfig config) {
		CalculateFactory calculateFactory = new CalculateFactory();
		calculateFactory.setConfig(config);
		return calculateFactory;
	}

	public Param createParam() {
		return Param.getInstance(config);
	}

	public CalculateExecutor createExecutor() {
		return CalculateExecutor.getInstance(config);
	}
}
