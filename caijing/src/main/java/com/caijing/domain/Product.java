package com.caijing.domain;

public class Product {

	private Integer pid;
	private String name;
	private Byte isIndustry;
	private Integer continuedmonth;
	private Float price;
	private String rightpaths;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public Byte getIsIndustry() {
		return isIndustry;
	}

	public void setIsIndustry(Byte isIndustry) {
		this.isIndustry = isIndustry;
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

}