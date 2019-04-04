package net.noyark.www.interf;

public interface Instance {
	Object instance() throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	Class<?> getClasspath();
}
