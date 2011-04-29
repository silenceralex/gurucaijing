package com.caijing.domain;

import java.util.Date;

public class OrderPr {

	private Integer industryid;
	private Long orderid;
	private Integer pid;
	private Integer num;
	private Float cost;
	private Date ctime;

	public Integer getIndustryid() {
		return industryid;
	}

	public void setIndustryid(Integer industryid) {
		this.industryid = industryid;
	}

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

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

}