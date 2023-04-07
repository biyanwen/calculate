package com.github.byw.param;

import com.ql.util.express.IExpressContext;

/**
 * 参数管理器
 *
 * @author byw
 * @date 2023/04/07
 */
public interface ParamManager extends IExpressContext<String, Object> {

	/**
	 * 是否包含对应的 key
	 *
	 * @param key key
	 * @return boolean
	 */
	boolean containsKey(String key);
}
