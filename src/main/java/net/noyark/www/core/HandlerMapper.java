package net.noyark.www.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.noyark.www.annotations.AutoWired;
import net.noyark.www.annotations.RequestMapping;
import net.noyark.www.interf.HandlerBase;
import net.noyark.www.interf.Mapper;
import net.noyark.www.interf.NoyarkAbstractApplicationContext;

/**
 * url-contoller-method
 * @author magiclu550
 *
 */
public class HandlerMapper implements Mapper{
	Map<String, Handler> mapper = new HashMap<String, Handler>();
	public void handleMap(List<Object> controllers) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		for(Object o:controllers) {
			RequestMapping classRm = o.getClass().getDeclaredAnnotation(RequestMapping.class);
			String parentPath = "";
			if(classRm!=null) {
				parentPath = classRm.value()+"/";
			}
			Method[] methods = o.getClass().getDeclaredMethods();
			for(Method m:methods) {
				RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
				if(requestMapping!=null) {
					String url = requestMapping.value();
					Handler handler = new Handler(o,m);
					//最前面没有'/'
					mapper.put(parentPath+url, handler);
				}
			}
			Field[] fields = o.getClass().getDeclaredFields();
			for(Field field:fields) {
				AutoWired autoWired = field.getAnnotation(AutoWired.class);
				if(autoWired!=null) {
					for(Object con:controllers) {
						if(con.getClass().equals(field.getType())) {
							field.set(o,con);
							return;
						}
					}
					field.set(o,field.getType().newInstance());
				}
			}
		}
	}
	public HandlerBase getHandler(String url) {
		return mapper.get(url);
	}
}
