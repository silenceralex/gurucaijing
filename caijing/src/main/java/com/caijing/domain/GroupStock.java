package com.caijing.domain;

import java.util.Date;

public class GroupStock {
	String groupid = null;
	String stockcode = null;
	String groupname = null;
	Date intime = null;
	Date outtime = null;
	Date ltime = null;
	String inreportid = null;
	String outreportid = null;
	float inprice = 0;
	float objectprice = 0;
	float currentprice = 0;
	float gain = 0;
	int status = 1;
	int type = 0;

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public Date getIntime() {
		return intime;
	}

	public void setIntime(Date intime) {
		this.intime = intime;
	}

	public Date getOuttime() {
		return outtime;
	}

	public void setOuttime(Date outtime) {
		this.outtime = outtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getInreportid() {
		return inreportid;
	}

	public void setInreportid(String inreportid) {
		this.inreportid = inreportid;
	}

	public String getOutreportid() {
		return outreportid;
	}

	public void setOutreportid(String outreportid) {
		this.outreportid = outreportid;
	}

	public float getInprice() {
		return inprice;
	}

	public void setInprice(float inprice) {
		this.inprice = inprice;
	}

	public float getObjectprice() {
		return objectprice;
	}

	public void setObjectprice(float objectprice) {
		this.objectprice = objectprice;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public Date getLtime() {
		return ltime;
	}

	public void setLtime(Date ltime) {
		this.ltime = ltime;
	}

	public float getCurrentprice() {
		return currentprice;
	}

	public void setCurrentprice(float currentprice) {
		this.currentprice = currentprice;
	}
}
