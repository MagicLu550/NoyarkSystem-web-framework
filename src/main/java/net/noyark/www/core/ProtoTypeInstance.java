package net.noyark.www.core;

import net.noyark.www.interf.Instance;

public class ProtoTypeInstance implements Instance{
	private Class<?> classpath;
	public ProtoTypeInstance(Class<?> classpath) {
		this.classpath = classpath;
	}
	public Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return classpath.newInstance();
	}
	public Class<?> getClasspath(){
		return classpath;
	}
}
