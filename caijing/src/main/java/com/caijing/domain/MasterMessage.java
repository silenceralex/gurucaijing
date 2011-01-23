package com.caijing.domain;

import java.util.Date;

public class MasterMessage {
	private int masterid = 0;
	private String content = null;
	private Date currdate = null;
	private String ptime = null;
	private String messageid = null;

	public int getMasterid() {
		return masterid;
	}

	public void setMasterid(int masterid) {
		this.masterid = masterid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCurrdate() {
		return currdate;
	}

	public void setCurrdate(Date currdate) {
		this.currdate = currdate;
	}

	public String getPtime() {
		return ptime;
	}

	public void setPtime(String ptime) {
		this.ptime = ptime;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

}
