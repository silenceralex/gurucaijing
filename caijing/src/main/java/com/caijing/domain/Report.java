package com.caijing.domain;

import java.util.Date;

public class Report {
	private String title = null;
	private String rid = null;
	private String said = null;
	private String content = null;
	private String publishdate = null;
	private String saname = null;
	private String aid = null;
	private String filepath = null;
	private String aname = null;
	private String description = null;
	private String stockcode = null;
	private String stockname=null;
	private Date ptime=null;
	//0为晨会，1为公司研究，2为行业研究，3为宏观研究,4为其他
	private int type = 0;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getSaid() {
		return said;
	}

	public void setSaid(String said) {
		this.said = said;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSaname() {
		return saname;
	}

	public void setSaname(String saname) {
		this.saname = saname;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPublishdate() {
		return publishdate;
	}

	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
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

	public Date getPtime() {
		return ptime;
	}

	public void setPtime(Date ptime) {
		this.ptime = ptime;
	}

}
