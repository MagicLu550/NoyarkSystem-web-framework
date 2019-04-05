package net.noyark.www.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.noyark.www.interf.Instance;

public class LazyInstance implements Instance{
	private Object obj;
	private Class<?> classpath;
	private Method init;
	private Method despory;
	private Method factory_method;
	private Object factory_bean;
	public LazyInstance(Class<?> classpath,Method init,Method despory,Method factory_method,Object factory_Bean) {
		this.classpath = classpath;
		this.init = init;
		this.factory_bean = factory_Bean;
		this.factory_method = factory_method;
	}
	public Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
		if(obj==null) {
			if(despory!=null) {
				if(factory_bean!=null) {
					obj = factory_method.invoke(factory_bean);
				}else {
					obj = factory_method.invoke(null);
				}
			}else {
				obj = classpath.newInstance();
			}
			if(init!=null) {
				init.invoke(obj);
			}
		}	
		return obj;
	}
	public Method getDesporyMethod() {
		return despory;
	}
	public Class<?> getClasspath() {
		return classpath;
	}
}
