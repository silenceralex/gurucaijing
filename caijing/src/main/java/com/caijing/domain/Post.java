package com.caijing.domain;

import java.util.Date;

public class Post {
	private String title = null;
	private String nick = null;
	private String pid = null;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	private String groupid = null;
	private String content = null;
	private Date ptime = null;
	private String relatedstocks = null;

	public String getRelatedstocks() {
		return relatedstocks;
	}

	public void setRelatedstocks(String relatedstocks) {
		this.relatedstocks = relatedstocks;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPtime() {
		return ptime;
	}

	public void setPtime(Date ptime) {
		this.ptime = ptime;
	}
}
