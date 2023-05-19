package com.github.byw.helper;

import com.github.byw.exception.CalculateException;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.*;

/**
 * 字符串格式化程序
 *
 * @author byw
 * @date 2022/11/29
 */
public class StringFormatter {
	private static final char LEFT_BRACKET = '{';
	private static final char RIGHT_BRACKET = '}';

	/**
	 * 用 {} 占位，括号里面可以填充参数的名称
	 * 之后会根据 参数的位置去替换，名称只是为了让模板更易读。
	 *
	 * @param template 模板
	 * @param args     参数
	 * @return {@link String}
	 */
	public static String format(String template, String... args) {
		List<String> placeholderNameList = getPlaceholderNameList(template);
		if (placeholderNameList.size() != args.length) {
			throw new CalculateException(template + " 公式有误！模板占位符和参数的数量不一致");
		}
		Map<String, String> paramMap = new HashMap<>();
		for (int i = 0; i < placeholderNameList.size(); i++) {
			String key = placeholderNameList.get(i);
			paramMap.put(key, args[i]);
		}
		return StrSubstitutor.replace(template, paramMap, "{", "}");
	}

	private static List<String> getPlaceholderNameList(String template) {
		List<String> keyList = new ArrayList<>();
		Stack<Character> stack = new Stack<>();
		for (int i = 0; i < template.toCharArray().length; i++) {
			char[] chars = template.toCharArray();
			if (chars[i] == LEFT_BRACKET) {
				while (i < chars.length) {
					stack.push(chars[i]);
					if (chars[i] == RIGHT_BRACKET) {
						char[] keyCharList = new char[stack.size()];
						int index = 0;
						while (!stack.isEmpty()) {
							keyCharList[index] = stack.pop();
							index++;
						}
						if (keyCharList.length > 0) {
							keyCharList = reverse(keyCharList);
							String key = getKey(keyCharList);
							keyList.add(key);
						}
						break;
					}
					i++;
				}
			}
		}
		if (!stack.isEmpty()) {
			throw new CalculateException("字符串格式化失败,参数有误！花括号需要成对出现");
		}
		return keyList;
	}

	private static String getKey(char[] keyCharList) {
		//去除多余括号
		char[] newCharArray = new char[keyCharList.length - 2];
		System.arraycopy(keyCharList, 1, newCharArray, 0, keyCharList.length - 1 - 1);
		return new String(newCharArray);
	}

	private static char[] reverse(char[] chars) {
		int n = chars.length;
		char[] b = new char[n];
		int j = n;
		for (char k : chars) {
			b[j - 1] = k;
			j = j - 1;
		}
		return b;
	}
}
