package net.noyark.www.interf;

import java.util.Collection;

public interface NoyarkAbstractApplicationContext {
	<T> T getBean(String id,Class<T> clzz);
	Object getBean(String id);
	void close();
	Collection<Object> getBeans();
}
