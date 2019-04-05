package net.noyark.www.core;

import java.lang.reflect.Method;

import net.noyark.www.interf.Instance;

public class ProtoTypeInstance implements Instance{
	private Class<?> classpath;
	
	
	public Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return classpath.newInstance();
	}
	public Class<?> getClasspath(){
		return classpath;
	}
	@Override
	public Method getDesporyMethod() {
		return null;
	}
	public ProtoTypeInstance(Class<?> classpath) {
		this.classpath = classpath;
	}
}
