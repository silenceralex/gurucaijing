package com.caijing.domain;

public class StockAgencyEntity {
	private String stockcode = null;
	private String stockname = null;
	private String sanames = null;
	private int sacounts = 0;
	private float currentprice = 0;

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

	public String getSanames() {
		return sanames;
	}

	public void setSanames(String sanames) {
		this.sanames = sanames;
	}

	public int getSacounts() {
		return sacounts;
	}

	public void setSacounts(int sacounts) {
		this.sacounts = sacounts;
	}

	public float getCurrentprice() {
		return currentprice;
	}

	public void setCurrentprice(float currentprice) {
		this.currentprice = currentprice;
	}

}
