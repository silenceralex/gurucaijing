package com.caijing.util;

public class ParseErrorMessageThreadLocal {
	public static ThreadLocal<String> parseErrorThreadLocal = new ThreadLocal<String>() ;
	
    public static void setParseErrorMessage(String errMsg) {
    	parseErrorThreadLocal.set(errMsg);
    }
    
    public static String getParseErrorMessage() {
    	return parseErrorThreadLocal.get();
    }
}
