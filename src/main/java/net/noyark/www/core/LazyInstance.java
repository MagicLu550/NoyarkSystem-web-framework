package net.noyark.www.core;

import net.noyark.www.interf.Instance;

public class LazyInstance implements Instance{
	private Object obj;
	private Class<?> classpath;
	public LazyInstance(Class<?> classpath) {
		this.classpath = classpath;
	}
	public Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(obj==null)
			obj = classpath.newInstance();
		return obj;
	}
	public Class<?> getClasspath() {
		return classpath;
	}
}
