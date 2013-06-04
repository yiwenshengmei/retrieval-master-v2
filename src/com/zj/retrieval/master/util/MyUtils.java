package com.zj.retrieval.master.util;

import java.util.HashMap;
import java.util.Map;

public class MyUtils {
	public static <T> Map<Object, Object> asMap(T ...args) {
		
		if (args.length % 2 != 0)
			throw new IllegalArgumentException("参数个数必须为键值对的倍数");
		
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		Object key = null;
		Object value = null;
		for (int i = 0; i < args.length; i++) {
			if ( i % 2 == 0 ) {
				key = args[i];
			}
			else {
				value = args[i];
				map.put(key, value);
			}
		}
		
		return map;
	}
}
