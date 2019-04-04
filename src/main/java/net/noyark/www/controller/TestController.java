package net.noyark.www.controller;

//TODO <constructor-arg index="0" value="mike"></constructor-arg>
//TODO <util:properties id="db" location="classpath:conn.properties">
//TODO 自动装配
import java.util.Map;

import net.noyark.www.annotations.Controller;
import net.noyark.www.annotations.RequestMapping;
@Controller
public class TestController {
	public String name;
	public Map<String,User> map;
	public String properties;
	
	@RequestMapping("a.no")
	public String test(String name,String age) {
		System.out.println(name+","+age);
		return null;
	}
}
