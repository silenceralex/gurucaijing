package com.caijing.domain;

import java.util.Date;

public class Recharge {
	long rcid = 0;
	String userid = null;
	float cash = 0;
	//创建时间
	Date ctime = null;
	//更新时间
	Date lmodify = null;
	//支付类型，0支付宝，1财付通，2块钱
	int type = 0;
	//支付状态，0待支付，1支付成功，-1支付失败
	int status = 0;

	public long getRcid() {
		return rcid;
	}

	public void setRcid(long rcid) {
		this.rcid = rcid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public Date getLmodify() {
		return lmodify;
	}

	public void setLmodify(Date lmodify) {
		this.lmodify = lmodify;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
