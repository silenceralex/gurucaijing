package com.caijing.domain;

public class StockHQ {
	private String stockcode = null;
	private String date = null;
	private float openprice = 0;
	private float endprice = 0;
	private float highest = 0;
	private float lowest = 0;
	private float volum = 0;
	private float changerate = 0;
	private float gainrate = 0;

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public float getOpenprice() {
		return openprice;
	}

	public void setOpenprice(float openprice) {
		this.openprice = openprice;
	}

	public float getEndprice() {
		return endprice;
	}

	public void setEndprice(float endprice) {
		this.endprice = endprice;
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

	public float getVolum() {
		return volum;
	}

	public void setVolum(float volum) {
		this.volum = volum;
	}

	public float getChangerate() {
		return changerate;
	}

	public void setChangerate(float changerate) {
		this.changerate = changerate;
	}

	public float getGainrate() {
		return gainrate;
	}

	public void setGainrate(float gainrate) {
		this.gainrate = gainrate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
