package com.caijing.domain;

public class AnalyzerSuccess {
	private String aid = null;
	private String aname = null;
	private String saname = null;
	private String year = null;
	private int total = 0;
	private int success = 0;
	private float successratio = 0;

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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public float getSuccessratio() {
		return successratio;
	}

	public void setSuccessratio(float successratio) {
		this.successratio = successratio;
	}

	public void setSaname(String saname) {
		this.saname = saname;
	}

	public String getSaname() {
		return saname;
	}

}
