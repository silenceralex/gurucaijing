package com.caijing.domain;

import java.util.Date;

public class StockEarn {
	private String stockcode = null;
	private Date date = null;
	private float ratio = 0;
	private float price = 0;
	private float currratio = 0;

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getCurrratio() {
		return currratio;
	}

	public void setCurrratio(float currratio) {
		this.currratio = currratio;
	}

}
