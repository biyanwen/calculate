package com.github.byw.factory;

import com.github.byw.exec.CalculateExecutor;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.formula.DefaultFormulaManager;
import com.github.byw.formula.FormulaManager;
import com.github.byw.param.Param;
import lombok.Data;
import lombok.SneakyThrows;

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

	/**
	 * 创建参数上下文
	 *
	 * @return {@link Param}
	 */
	public Param createParam() {
		return Param.getInstance(config);
	}

	/**
	 * 创建公式执行器
	 *
	 * @return {@link CalculateExecutor}
	 */
	public CalculateExecutor createExecutor() {
		return CalculateExecutor.getInstance(config);
	}

	/**
	 * 创建公式管理器
	 *
	 * @return {@link FormulaManager}
	 */
	@SneakyThrows
	public FormulaManager createFormulaManager() {
		return (FormulaManager) config.getFormulaManagerClass().newInstance();
	}
}
