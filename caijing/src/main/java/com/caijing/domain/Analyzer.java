package com.caijing.domain;

import java.util.Date;

public class Analyzer implements Comparable {
	private String name = null;
	private String aid = null;
	private int level = 0;
	private int status = 0;
	private String industry = null;
	private String agency = null;
	private String info = null;
	private Date ptime = null;
	private Date lmodify = null;
	private String image_url = null;
	private String position = null;
	private float weight = 0;
	private float successratio = 0;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	private int total = 0;
	private int success = 0;

	public float getSuccessratio() {
		return successratio;
	}

	public void setSuccessratio(float successratio) {
		this.successratio = successratio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Object o) {
		if (this.weight > ((Analyzer) o).getWeight()) {
			return 1;
		}
		return 0;
	}

}
