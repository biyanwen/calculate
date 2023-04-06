package com.github.byw.formula;

import java.util.List;

/**
 * 公式管理器
 *
 * @author byw
 * @date 2023/04/06
 */
public interface FormulaManager {

	/**
	 * 添加公式
	 *
	 * @param formula    公式
	 * @param conditions 条件
	 * @return {@link FormulaManager}
	 */
	FormulaManager add(String formula, FormulaConditions... conditions);

	/**
	 * 添加
	 *
	 * @param template   模板
	 * @param arg        模板所需参数
	 * @param conditions 条件
	 * @return {@link FormulaManager}
	 */
	FormulaManager add(String template, String arg, FormulaConditions... conditions);

	/**
	 * 添加
	 *
	 * @param template   模板
	 * @param args       模板所需参数
	 * @param conditions 条件
	 * @return {@link FormulaManager}
	 */
	FormulaManager add(String template, List<String> args, FormulaConditions... conditions);

	/**
	 * 添加公式列表
	 * ps:此方法是为了多个公式可以复用同一个 FormulaConditions , 如果没有这个需求请使用 add 方法。
	 *
	 * @param formulaList 公式列表
	 * @param conditions  条件
	 * @return {@link FormulaManager}
	 */
	FormulaManager addList(List<String> formulaList, FormulaConditions... conditions);

	/**
	 * 得到公式列表
	 *
	 * @return {@link List}<{@link Formula}>
	 */
	List<Formula> getFormulaList();
}
