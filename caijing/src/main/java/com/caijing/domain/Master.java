package com.caijing.domain;

public class Master {
	public String getMastername() {
		return mastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	public int getMasterid() {
		return masterid;
	}

	public void setMasterid(int masterid) {
		this.masterid = masterid;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getCrawlnum() {
		return crawlnum;
	}

	public void setCrawlnum(int crawlnum) {
		this.crawlnum = crawlnum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private String mastername = null;
	private int masterid = 0;
	private String intro = null;
	private int crawlnum = 0;
	private int status = 0;
}
