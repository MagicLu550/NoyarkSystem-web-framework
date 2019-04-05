package net.noyark.www.controller;

//TODO <constructor-arg index="0" value="mike"></constructor-arg>
//TODO <util:properties id="db" location="classpath:conn.properties">
//TODO 自动装配
import java.util.Map;

import net.noyark.www.annotations.AutoWired;
import net.noyark.www.annotations.Controller;
import net.noyark.www.annotations.ExceptionHandle;
import net.noyark.www.annotations.RequestMapping;
@Controller
@RequestMapping("user")
public class TestController {
	public String name;
	public Map<String,User> map;
	public String properties;
	@AutoWired
	public User user;
	@RequestMapping("a.no")
	public String test(String name,String age) {
		System.out.println(user);
		return "b.no";
	}
	@RequestMapping("b.no")
	public String test1() {
		System.out.println(111);
		throw new TestException();
	}
	@ExceptionHandle(RuntimeException.class)
	public String HandleEx(Exception e) {
		System.out.println("excpetion");
		return null;
	}
}
