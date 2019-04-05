package net.noyark.www.interf;


import java.util.List;


public interface Mapper {
	void handleMap(List<Object> controllers) throws IllegalArgumentException, IllegalAccessException, InstantiationException;
	
	HandlerBase getHandler(String url);
}
