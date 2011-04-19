package com.caijing.domain;

/**
 * product收费物品表
 */
public class Product {
	private Integer pid;
	private String name;
	private Integer continuedmonth;
	private Float price;
	private String rightpaths;
	private String desc;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getContinuedmonth() {
		return continuedmonth;
	}

	public void setContinuedmonth(Integer continuedmonth) {
		this.continuedmonth = continuedmonth;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getRightpaths() {
		return rightpaths;
	}

	public void setRightpaths(String rightpaths) {
		this.rightpaths = rightpaths;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}