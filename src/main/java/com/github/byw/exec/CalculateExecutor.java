package com.github.byw.exec;

import com.github.byw.exception.CalculateException;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.exec.exector.Executor;
import com.github.byw.exec.exector.ListDataExecutor;
import com.github.byw.exec.exector.SingleDataExecutor;
import com.github.byw.formula.Formula;
import com.github.byw.formula.DefaultFormulaManager;
import com.github.byw.formula.FormulaManager;
import com.github.byw.param.Param;
import com.github.byw.result.DefaultResultManager;
import com.github.byw.result.ResultManager;
import com.ql.util.express.IExpressContext;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * 计算执行者
 *
 * @author byw
 * @date 2022/11/24
 */
public class CalculateExecutor {

	public static ResultManager getResultManager(Param param) {
		return new DefaultResultManager(param.getParamContext());
	}

	private CalculateExecutor(CalculateConfig config) {
		this.config = config;
	}

	private CalculateConfig config = new CalculateConfig();

	public static CalculateExecutor getInstance(CalculateConfig config) {
		return new CalculateExecutor(config);
	}

	public ResultManager exec(Param param, FormulaManager formulaManager) {
		return CalculateExecutorBean.exec(param, formulaManager, config);
	}

	private static class CalculateExecutorBean {

		@SneakyThrows
		public static ResultManager exec(Param param, FormulaManager formulaManager, CalculateConfig... configs) {
			if (param == null) {
				throw new CalculateException("param 参数不能为 null ");
			}
			CalculateConfig config = new CalculateConfig();
			if (configs.length > 0) {
				config = configs[0];
			}
			IExpressContext<String, Object> paramContext = param.getParamContext();
			for (Formula formulaInstance : formulaManager.getFormulaList()) {
				Executor executor = CalculateExecutor.ExecutorManage.getInstance().get(formulaInstance);
				if (config.getFunctionConfig() != null) {
					config.getFunctionConfig().getOperatorMap().forEach(executor::registerFunction);
				}
				executor.exec(formulaInstance, param, config);
			}
			return new DefaultResultManager(paramContext);
		}
	}

	private static class ExecutorManage {

		private final List<Executor> executorList = new ArrayList<>();

		{
			executorList.add(new ListDataExecutor());
			executorList.add(new SingleDataExecutor());
		}

		public Executor get(Formula formulaInstance) {
			for (Executor executor : executorList) {
				if (executor.canHandle(formulaInstance)) {
					return executor;
				}
			}
			throw new RuntimeException("无法处理公式：" + formulaInstance);
		}

		public static ExecutorManage getInstance() {
			return new ExecutorManage();
		}
	}
}
