package net.noyark.www.interf;


import java.util.List;


public interface Mapper {
	void handleMap(List<Object> controllers) throws IllegalArgumentException, IllegalAccessException;
	
	HandlerBase getHandler(String url);
}
