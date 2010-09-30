package com.caijing.domain;

import java.util.List;

public class StockGain {

	private String stockcode = null;
	private String stockname = null;
	private String reportid = null;
	private String grade = null;
	private String saname = null;
	private String analyzer = null;
	private String startdate = null;
	private String enddate = null;
	private float startprice = 0;
	private float endprice = 0;
	private float objectprice = 0;
	private float highest = 0;
	private float lowest = 0;
	private float volume = 0;
	private int dealdays = 0;
	private List<Float> periodprice = null;
	private List<Float> periodratio = null;
	private List<String> perioddate = null;
	private float gain = 0;
	private float changerate = 0;

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

	public String getSaname() {
		return saname;
	}

	public void setSaname(String saname) {
		this.saname = saname;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public float getStartprice() {
		return startprice;
	}

	public void setStartprice(float startprice) {
		this.startprice = startprice;
	}

	public float getEndprice() {
		return endprice;
	}

	public void setEndprice(float endprice) {
		this.endprice = endprice;
	}

	public String getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(String analyzer) {
		this.analyzer = analyzer;
	}

	public float getHighest() {
		return highest;
	}

	public void setHighest(float highest) {
		this.highest = highest;
	}

	public float getLowest() {
		return lowest;
	}

	public void setLowest(float lowest) {
		this.lowest = lowest;
	}

	public int getDealdays() {
		return dealdays;
	}

	public void setDealdays(int dealdays) {
		this.dealdays = dealdays;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public float getChangerate() {
		return changerate;
	}

	public void setChangerate(float changerate) {
		this.changerate = changerate;
	}

	public float getObjectprice() {
		return objectprice;
	}

	public void setObjectprice(float objectprice) {
		this.objectprice = objectprice;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public List<Float> getPeriodprice() {
		return periodprice;
	}

	public void setPeriodprice(List<Float> periodprice) {
		this.periodprice = periodprice;
	}

	public List<String> getPerioddate() {
		return perioddate;
	}

	public void setPerioddate(List<String> perioddate) {
		this.perioddate = perioddate;
	}

	public List<Float> getPeriodratio() {
		return periodratio;
	}

	public void setPeriodratio(List<Float> periodratio) {
		this.periodratio = periodratio;
	}

}
