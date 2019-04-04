package net.noyark.www.core;

import java.util.Map;

import net.noyark.www.exception.BeanRepeatException;

public class Assert {
	public static void notNull(Object o,Exception e) throws Exception {
		if(o==null) {
			throw e;
		}
	}
	public static void notRepeat(Map<String,Object> objPool,String id) {
		if(objPool.containsKey(id)) {
			throw new BeanRepeatException("the bean "+id+" have existed");
		}
	}
}
