package net.noyark.www.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.noyark.www.exception.CloseException;
import net.noyark.www.exception.NoSuchBeanException;
import net.noyark.www.interf.NoyarkAbstractApplicationContext;
import net.noyark.www.scan.PackageScanner;
import net.noyark.www.interf.Instance;
import net.noyark.www.xml.XMLHandler;

public class NoyarkApplicationContext implements NoyarkAbstractApplicationContext{
	private  XMLHandler handler;
	private static Map<String, XMLHandler> handlers = new HashMap<String, XMLHandler>();
	public NoyarkApplicationContext(String file,boolean... isStream)  {
		try {
				XMLHandler xh = handlers.get(file);
				if(xh!=null) {
					handler = xh;
				}else {
					handler = new XMLHandler(file,isStream);
					handlers.put(file,handler);
				}
				scanBean();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(String id,Class<T> clzz) {
		return (T)getBean(id);
	}
	public Object getBean(String id)  {
		if(handler != null) {
			Object o = handler.getBeans().get(id);
			if(o==null) {
				throw new NoSuchBeanException("the bean named "+id+" is not found");
			}
			try {
				Instance instance;
				if(o instanceof Instance) {
					instance = ((Instance)o);
					return instance.instance();
				}
			}catch(Exception e) {
			}
			return o;
		}
		throw new CloseException("the application have been closed");
	}
	public void close() {
		try {
			Map<Object, Method> obj_method = handler.getDesporyMethod();
			Set<Map.Entry<Object,Method>> set = obj_method.entrySet();
			for(Map.Entry<Object, Method> e:set) {
				e.getValue().invoke(e.getKey());
			}
			handler = null;
		}catch(Exception e) {
		}
	}
	public static Map<String, XMLHandler>getHandlers() {
		return handlers;
	}
	public void scanBean() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		PackageScanner scanner = new PackageScanner(handler);
		scanner.scanPackage();
	}
	public Collection<Object> getBeans(){
		return handler.getBeans().values();
	}
}
