package com.caijing.exception;

/**
 * �޷��ҵ�Site��Ϣʱ�׳����쳣
 * @author Raymond
 *
 */
public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
