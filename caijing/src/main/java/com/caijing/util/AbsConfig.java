package com.caijing.util;

import java.util.Map;

public class AbsConfig {
	private Map configMap = null;

	public void setConfigMap(Map map) {
		this.configMap = map;
	}

	public Map getValue(String key) {
		return (Map) this.configMap.get(key);
	}

	public Object getObject(String key) {
		return this.configMap.get(key);
	}

	public Map getConfigMap() {
		return configMap;
	}
}
