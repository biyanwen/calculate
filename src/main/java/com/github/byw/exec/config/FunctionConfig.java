package com.github.byw.exec.config;

import com.github.byw.exec.operator.ListSum;
import com.ql.util.express.Operator;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 函数配置，主要提供向计算器添加自定义函数的功能。
 * ps：自定义函数如何定义可以查看{@link ListSum}
 *
 * @author byw
 * @date 2023/04/03
 */
@Data
public class FunctionConfig {

	private Map<String, Operator> operatorMap = new HashMap<>();

	public FunctionConfig addFunction(String name, Operator operator) {
		operatorMap.put(name, operator);
		return this;
	}
}
