package test;


import java.io.IOException;

import org.dom4j.DocumentException;

import cn.gulesberry.www.exception.IllegalMappingException;
import cn.gulesberry.www.exception.IndexLengthException;
import net.noyark.www.annotations.Controller;
import net.noyark.www.core.NoyarkApplicationContext;
import net.noyark.www.exception.AliasException;
import net.noyark.www.interf.NoyarkAbstractApplicationContext;
public class Test {
	public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalMappingException, IndexLengthException, DocumentException {
		NoyarkAbstractApplicationContext nac = new NoyarkApplicationContext("noyark.xml",true);
	}
}
