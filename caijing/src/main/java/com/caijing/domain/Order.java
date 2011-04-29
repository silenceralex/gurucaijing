package com.caijing.domain;

import java.util.Date;
import java.util.List;

public class Order {

	private Long orderid;
	private Long userid;
	private Float cost;
	private Byte status;
	private Date ctime;
	private Date lmodify;
	private List<OrderPr> orderPrs;

	public List<OrderPr> getOrderPrs() {
		return orderPrs;
	}

	public void setOrderPrs(List<OrderPr> orderPrs) {
		this.orderPrs = orderPrs;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
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