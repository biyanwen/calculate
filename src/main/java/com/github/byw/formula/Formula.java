package com.github.byw.formula;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 公式
 *
 * @author byw
 * @date 2022/12/01
 */
@Data
public class Formula {

	/**
	 * 公式
	 */
	private List<String> formulaList;

	/**
	 * 条件
	 */
	private FormulaConditions conditions;

	public Formula(String formula, FormulaConditions conditions) {
		this.formulaList = Lists.newArrayList(formula);
		this.conditions = conditions;
	}

	public Formula(List<String> formulaList, FormulaConditions conditions) {
		this.formulaList = formulaList;
		this.conditions = conditions;
	}

	@Override
	public String toString() {
		return "Formula{" +
				"formulaList=" + formulaList +
				", conditions=" + conditions +
				'}';
	}
}
