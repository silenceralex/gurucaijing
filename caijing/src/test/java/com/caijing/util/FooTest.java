package com.caijing.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FooTest {
	
	static private Log log=LogFactory.getLog(FooTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("Hello from Foo Test");
		System.out.println("Test done!");
	}

}
