package net.noyark.www.interf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Instance {
	Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException;
	Class<?> getClasspath();
	Method getDesporyMethod();
}
