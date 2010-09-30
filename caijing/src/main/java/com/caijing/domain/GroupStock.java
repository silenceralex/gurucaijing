package com.caijing.domain;

import java.util.List;

public class GroupStock {
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

}
