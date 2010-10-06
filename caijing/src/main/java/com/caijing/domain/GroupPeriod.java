package com.caijing.domain;

import java.util.HashMap;
import java.util.List;

public class GroupPeriod {
	private HashMap<String, StockGain> stockInGroup = null;
	private List<StockGain> stockGains = null;
	private HashMap<String, String> joinMap = null;
	private HashMap<String,HashMap<String,Float> > stockdateMap=new HashMap<String,HashMap<String,Float> >();
	private String firstdate=null;
	private String firststock=null;
	private List<String> dates = null;
	private List<Float> weights = null;
	private List<Float> ratios = null;

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public List<Float> getWeights() {
		return weights;
	}

	public void setWeights(List<Float> weights) {
		this.weights = weights;
	}

	public List<Float> getRatios() {
		return ratios;
	}

	public void setRatios(List<Float> ratios) {
		this.ratios = ratios;
	}

	public HashMap<String, StockGain> getStockInGroup() {
		return stockInGroup;
	}

	public void setStockInGroup(HashMap<String, StockGain> stockInGroup) {
		this.stockInGroup = stockInGroup;
	}

	public HashMap<String, HashMap<String, Float>> getStockdateMap() {
		return stockdateMap;
	}

	public void setStockdateMap(HashMap<String, HashMap<String, Float>> stockdateMap) {
		this.stockdateMap = stockdateMap;
	}

	public HashMap<String, String> getJoinMap() {
		return joinMap;
	}

	public void setJoinMap(HashMap<String, String> joinMap) {
		this.joinMap = joinMap;
	}

	public String getFirstdate() {
		return firstdate;
	}

	public void setFirstdate(String firstdate) {
		this.firstdate = firstdate;
	}

	public String getFirststock() {
		return firststock;
	}

	public void setFirststock(String firststock) {
		this.firststock = firststock;
	}

	public List<StockGain> getStockGains() {
		return stockGains;
	}

	public void setStockGains(List<StockGain> stockGains) {
		this.stockGains = stockGains;
	}

}
