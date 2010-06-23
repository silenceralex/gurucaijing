package com.caijing.exception;

/**
 * 无法找到Site信息时抛出的异常
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
