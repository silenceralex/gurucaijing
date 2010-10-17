package com.caijing.domain;

public class DiscountStock implements Comparable {
	private String reportid = null;
	private String stockname = null;
	private String stockcode = null;
	private String createdate = null;
	private String saname = null;
	private String aname = null;
	private String grade = null;
	private float recommendprice = 0;
	private float currentprice = 0;
	private float discountratio = 0;

	public String getStockname() {
		return stockname;
	}

	public void setStockname(String stockname) {
		this.stockname = stockname;
	}

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getSaname() {
		return saname;
	}

	public void setSaname(String saname) {
		this.saname = saname;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public float getRecommendprice() {
		return recommendprice;
	}

	public void setRecommendprice(float recommendprice) {
		this.recommendprice = recommendprice;
	}

	public float getDiscountratio() {
		return discountratio;
	}

	public void setDiscountratio(float discountratio) {
		this.discountratio = discountratio;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	@Override
	public int compareTo(Object o) {
		if (this.getDiscountratio() < ((DiscountStock) o).getDiscountratio()) {
			return 1;
		} else {
			return 0;
		}
	}

	public float getCurrentprice() {
		return currentprice;
	}

	public void setCurrentprice(float currentprice) {
		this.currentprice = currentprice;
	}
}
