package com.caijing.domain;

import java.util.Date;

public class RecommendSuccess {

	private String aid = null;
	private String aname = null;
	private Date recommenddate = null;
	private Date validate = null;
	private String stockcode = null;
	private String stockname = null;
	private String reportid = null;
	private float objectprice = 0;
	//0未达到，1为达到,2为尚未到验证时间,3为无目标价
	private int isAchieved = 2;

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public Date getRecommenddate() {
		return recommenddate;
	}

	public void setRecommenddate(Date recommenddate) {
		this.recommenddate = recommenddate;
	}

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getStockname() {
		return stockname;
	}

	public void setStockname(String stockname) {
		this.stockname = stockname;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public float getObjectprice() {
		return objectprice;
	}

	public void setObjectprice(float objectprice) {
		this.objectprice = objectprice;
	}

	public int getIsAchieved() {
		return isAchieved;
	}

	public void setIsAchieved(int isAchieved) {
		this.isAchieved = isAchieved;
	}

	public void setValidate(Date validate) {
		this.validate = validate;
	}

	public Date getValidate() {
		return validate;
	}

}
