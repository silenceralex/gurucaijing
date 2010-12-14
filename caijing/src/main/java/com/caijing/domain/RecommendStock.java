package com.caijing.domain;

import java.util.Date;

public class RecommendStock {
	private String recommendid = null;
	private String reportid = null;
	private String stockcode = null;
	private String stockname = null;
	private String aname = null;
	private String saname = null;
	private int level = 0;
	//买入及等同状态2，中性或卖出等同状态1,0为未标注或者谨慎推荐类
	private int status = 0;
	private String createdate = null;
	private String enddate = null;
	private String info = null;
	private String title = null;
	private String user = null;
	private float buyprice = 0;
	private float objectprice = 0;
	private int extractnum = 0;
	// private Date ptime = null;
	private Date lmodify = null;
	private String eps = null;
	// 推荐级别，下调，推荐，买入，卖出，维持
	private String grade = null;
	private String filepath = null;

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public String getSaname() {
		return saname;
	}

	public void setSaname(String saname) {
		this.saname = saname;
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

	// public Date getPtime() {
	// return ptime;
	// }
	// public void setPtime(Date ptime) {
	// this.ptime = ptime;
	// }
	public Date getLmodify() {
		return lmodify;
	}

	public void setLmodify(Date lmodify) {
		this.lmodify = lmodify;
	}

	public String getEps() {
		return eps;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setEps(String eps) {
		this.eps = eps;
	}

	public String getRecommendid() {
		return recommendid;
	}

	public void setRecommendid(String recommendid) {
		this.recommendid = recommendid;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public String getStockname() {
		return stockname;
	}

	public void setStockname(String stockname) {
		this.stockname = stockname;
	}

	public int getExtractnum() {
		return extractnum;
	}

	public void setExtractnum(int extractnum) {
		this.extractnum = extractnum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
