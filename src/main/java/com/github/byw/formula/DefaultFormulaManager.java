package com.github.byw.formula;

import com.github.byw.exception.CalculateException;
import com.github.byw.helper.StringFormatter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 公式
 *
 * @author byw
 * @date 2022/11/23
 */
public class DefaultFormulaManager implements FormulaManager{

	/**
	 * 公式列表
	 */
	private final List<Formula> formulaList = new ArrayList<>();

	public DefaultFormulaManager() {

	}

	/**
	 * 添加
	 *
	 * @param formula    公式
	 * @param conditions 条件
	 * @return {@link DefaultFormulaManager}
	 */
	@Override
	public DefaultFormulaManager add(String formula, FormulaConditions... conditions) {
		if (StringUtils.isBlank(formula)) {
			throw new CalculateException("公式不能为空");
		}
		Formula formulaInstance = new Formula(formula, conditions.length == 0 ? null : conditions[0]);
		formulaList.add(formulaInstance);
		return this;
	}

	/**
	 * 添加
	 *
	 * @param template   模板
	 * @param arg        模板所需参数
	 * @param conditions 条件
	 * @return {@link DefaultFormulaManager}
	 */
	@Override
	public DefaultFormulaManager add(String template, String arg, FormulaConditions... conditions) {
		return this.add(template, Lists.newArrayList(arg), conditions);
	}

	/**
	 * 添加
	 *
	 * @param template   模板
	 * @param args       模板所需参数
	 * @param conditions 条件
	 * @return {@link DefaultFormulaManager}
	 */
	@Override
	public DefaultFormulaManager add(String template, List<String> args, FormulaConditions... conditions) {
		String format = StringFormatter.format(template, args.toArray(new String[0]));
		return this.add(format, conditions);
	}

	/**
	 * 添加公式列表
	 * ps:此方法是为了多个公式可以复用同一个 FormulaConditions , 如果没有这个需求请使用 add 方法。
	 *
	 * @param formulaList 公式列表
	 * @param conditions  条件
	 * @return {@link DefaultFormulaManager}
	 */
	@Override
	public DefaultFormulaManager addList(List<String> formulaList, FormulaConditions... conditions) {
		if (CollectionUtils.isEmpty(formulaList)) {
			throw new CalculateException("公式不能为空");
		}
		Formula formulaInstance = new Formula(formulaList, conditions.length == 0 ? null : conditions[0]);
		this.formulaList.add(formulaInstance);
		return this;
	}

	@Override
	public List<Formula> getFormulaList() {
		return formulaList;
	}
}
