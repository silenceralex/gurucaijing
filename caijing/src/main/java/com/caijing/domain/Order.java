package com.caijing.domain;

import java.util.Date;

public class Order {

	private Long orderid;
	private Integer pid;
	private Long userid;
	private Integer num;
	private Float cost;
	private Byte status;
	private Date ctime;
	private Date lmodify;

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
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

}
