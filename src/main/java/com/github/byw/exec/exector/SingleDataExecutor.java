package com.github.byw.exec.exector;

import com.github.byw.exec.config.CalculateConfig;
import com.github.byw.formula.Formula;
import com.github.byw.formula.FormulaConditions;
import com.github.byw.param.Param;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 用于计算参数是单个数值的公式
 * 此类作为默认执行器，所以 canHandle 返回 true，不过此类优先级最低，只有所有
 * Executor都没有返回 true 的情况下才会轮到此类
 *
 * @author byw
 * @date 2023/02/01
 */
public class SingleDataExecutor extends AbstractDataExecutor {

	@Override
	public boolean canHandle(Formula formulaInstance) {
		return true;
	}

	@SneakyThrows
	@Override
	protected void doExec(Formula formulaInstance) {

		FormulaConditions formulaConditions = formulaInstance.getConditions();
		if (formulaConditions == null) {
			doExec(formulaInstance, config);
			return;
		}
		//停止条件
		String stopConditions = Optional.ofNullable(formulaConditions.getStopConditions()).orElse("1==1");

		Boolean startConditionsResult = judgeWhetherStart(formulaConditions.getStartConditions());

		Boolean stopConditionsResult = executeForBool(stopConditions, param);
		if (startConditionsResult) {
			doExec(formulaInstance, config);
			while (!stopConditionsResult && !Thread.currentThread().isInterrupted()) {
				doExec(formulaInstance, config);
				stopConditionsResult = executeForBool(stopConditions, param);
			}
		}
	}

	@SneakyThrows
	private void doExec(Formula formulaInstance, CalculateConfig config) {
		Integer getInteger = null;
		List<String> toBeExecutedFormulaList = formulaInstance.getFormulaList();
		FormulaConditions conditions = formulaInstance.getConditions();
		if (conditions != null) {
			getInteger = conditions.retainDecimal();
		}
		for (String toBeExecutedFormula : toBeExecutedFormulaList) {
			String startCondition = "";
			String stopCondition = "";
			if (conditions != null) {
				startCondition = conditions.getStartConditions() == null ? "" : String.valueOf(conditions.getStartConditions());
				stopCondition = conditions.getStopConditions() == null ? "" : String.valueOf(conditions.getStopConditions());
			}
			printFormulaLog(toBeExecutedFormula, startCondition, stopCondition);
			if (config.getRetainDecimal() != null) {
				toBeExecutedFormula = modificationFormula(toBeExecutedFormula, config.getRetainDecimal());
			}
			if (getInteger != null) {
				toBeExecutedFormula = modificationFormula(toBeExecutedFormula, getInteger);
			}
			execute(toBeExecutedFormula, param);
		}
	}

	private Boolean judgeWhetherStart(String startCondition) throws Exception {
		if (StringUtils.isBlank(startCondition)) {
			return true;
		}
		return executeForBool(startCondition, param);
	}
}
