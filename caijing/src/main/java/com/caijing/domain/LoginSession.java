package com.caijing.domain;

public interface LoginSession {
	public User getUser() ;
	public int getPermission();
	public int getAccountStatus();
}
