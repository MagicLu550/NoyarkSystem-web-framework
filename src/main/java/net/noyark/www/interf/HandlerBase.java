package net.noyark.www.interf;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

public interface HandlerBase {
	Object excuteThat(HttpServletRequest req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchFieldException, SecurityException;
}
