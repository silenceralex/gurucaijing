package com.caijing.domain;

import java.util.Date;

public class RecommendStock {
	private String sid = null;
	private String aid = null;
	private String said = null;
	private int level = 0;
	private int status = 0;
	private String createdate = null;
	private String enddate = null;
	private String info = null;
	private float buyprice=0;
	private float objectprice=0;
	private Date ptime = null;
	private Date lmodify = null;
	private float eps=0;
	//推荐级别，下调，推荐，买入，卖出，维持
	private String grade=null;
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getSaid() {
		return said;
	}
	public void setSaid(String said) {
		this.said = said;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public float getBuyprice() {
		return buyprice;
	}
	public void setBuyprice(float buyprice) {
		this.buyprice = buyprice;
	}
	public float getObjectprice() {
		return objectprice;
	}
	public void setObjectprice(float objectprice) {
		this.objectprice = objectprice;
	}
	public Date getPtime() {
		return ptime;
	}
	public void setPtime(Date ptime) {
		this.ptime = ptime;
	}
	public Date getLmodify() {
		return lmodify;
	}
	public void setLmodify(Date lmodify) {
		this.lmodify = lmodify;
	}
	public float getEps() {
		return eps;
	}
	public void setEps(float eps) {
		this.eps = eps;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
}
