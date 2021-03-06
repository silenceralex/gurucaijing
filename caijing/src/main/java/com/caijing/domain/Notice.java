package com.caijing.domain;

import java.util.Date;

public class Notice implements Comparable {
	String id = null;
	String title = null;
	String stockcode = null;
	String stockname = null;
	String content = null;
	Date date = null;
	//0为激励，1为资产重组类（收购，注入，并购），2增持，3减持,4 业绩预增,5业绩预减
	int type = 0;
	String url = null;
	float gain = 0;

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int compareTo(Object o) {
		if (this.gain > ((Notice) o).getGain()) {
			return 0;
		}
		return 1;
	}

}
