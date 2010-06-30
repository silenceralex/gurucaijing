package com.caijing.util;

import java.util.Map;

public class Config {
	private Map configMap = null;
	private String prefix = "";
	private String searchPage = "";
	private String params="";
	private String autoTagUrl="";
	private String[] imgHostList=null;
	
	public void setConfigMap(Map map) {
		this.configMap = map;
	}
	
	public Map getValue(String key) {
		return (Map)this.configMap.get(key);
	}
	
	public Object getObject(String key) {
		return this.configMap.get(key);
	}
	

	public String[] getImgHostList() {
		return imgHostList;
	}

	public void setImgHostList(String[] imgHostList) {
		this.imgHostList = imgHostList;
	}

  public String getAutoTagUrl() {
    return autoTagUrl;
  }

  public void setAutoTagUrl(String autoTagUrl) {
    this.autoTagUrl = autoTagUrl;
  }

public String getPrefix() {
	return prefix;
}

public void setPrefix(String prefix) {
	this.prefix = prefix;
}

public String getSearchPage() {
	return searchPage;
}

public void setSearchPage(String searchPage) {
	this.searchPage = searchPage;
}

public String getParams() {
	return params;
}

public void setParams(String params) {
	this.params = params;
}

public Map getConfigMap() {
	return configMap;
}
}
