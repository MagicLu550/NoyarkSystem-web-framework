package net.noyark.www.controller;

import java.util.Calendar;

import net.noyark.www.annotations.Param;

public class User {
	public String string;
	public String string2;
	public User() {
		
	}
	public User(@Param("string")User string) {
		
	}
	public Calendar get() {
		return Calendar.getInstance();
	}
	@Override
	public String toString() {
		return "User [string=" + string + ", string2=" + string2 + "]";
	}
}
