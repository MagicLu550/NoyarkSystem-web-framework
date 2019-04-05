package net.noyark.www.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.noyark.www.annotations.ExceptionHandle;
import net.noyark.www.interf.HandlerBase;

public class Handler implements HandlerBase{
	private Object object;//控制器对象
	private Method method;//对应的方法
	public Handler(Object object,Method method) {
		this.object = object;
		this.method = method;
	}
	public Object excuteThat(HttpServletRequest req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchFieldException, SecurityException {
		try {
			Class<?>[] params = method.getParameterTypes();
			if(params.length==1) {
				if(params[0].equals(HttpServletRequest.class)) {
					return excute(req);
				}else {
					Object o;
					try {
						o = params[0].newInstance();
						Field[] fields = params[0].getDeclaredFields();
						for(Field f:fields) {
							String webParam = req.getParameter(f.getName());
							f.set(o,webParam);
						}
						return excute(o);
					}catch(IllegalAccessException e) {
						Enumeration<?> es = req.getParameterNames();
						while(es.hasMoreElements()) {
							o = req.getParameter(es.nextElement()+"");
							return excute(o);
						}
					}	
				}
			}
			//这里打开eclipse的preferences>java>Complier>最后一个选项
			Parameter[] parameters = method.getParameters();
			Object[] os = new Object[parameters.length];
			int i = 0;
			for(Parameter p:parameters) {
				String methodName = p.getName();//method
				String webParam = req.getParameter(methodName);//form
				os[i] = webParam;
				i++;
			}
			return excute(os);
		}catch(Exception e) {
			return parseException(e);
		}
	}
	//req参数
	private Object excute(HttpServletRequest req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(object,req);
	}
	private Object excute(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(object,o);
	}
	private Object excute(Object... o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(object,o);
	}
	public Object parseException(Exception e) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method[] ms = object.getClass().getMethods();
		for(Method m:ms) {
			ExceptionHandle handle = m.getAnnotation(ExceptionHandle.class);
			if(handle!=null) {
				Class<?> clazz = handle.value();
				if(e.getClass().getSuperclass().equals(clazz)) {
					return m.invoke(object,e);
				}else if(e.getClass().equals(clazz)) {
					return m.invoke(object,e);
				}
			}
		}
		return null;
	}
}
