package com.github.byw.exec;

import com.github.byw.exception.CalculateException;
import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.exec.exector.Executor;
import com.github.byw.exec.exector.ListDataExecutor;
import com.github.byw.exec.exector.SingleDataExecutor;
import com.github.byw.formula.Formula;
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
			Executor executor = ExecutorManage.getInstance().get(formulaInstance);
			executor.exec(formulaInstance, param, config);
		}
		return new DefaultResultManager(paramContext);
	}

	public static ResultManager getResultManager(Param param) {
		return new DefaultResultManager(param.getParamContext());
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
