package com.caijing.util;

import java.util.List;
import java.util.Map;

public class TopicNameConfig {

	private Map topicNameMap = null;

	public Map getTopicNameMap() {
		return topicNameMap;
	}

	public void setTopicNameMap(Map topicNameMap) {
		this.topicNameMap = topicNameMap;
	}

	public String getTopicName(String topicid) {
		return (String) topicNameMap.get(topicid);
	}
}