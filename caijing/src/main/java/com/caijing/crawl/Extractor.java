package com.caijing.crawl;

import java.util.List;

import com.caijing.domain.Post;


public interface Extractor {
	int getTotalPages(String content);
	public List<Post> extractFromHtml(String content,String groupID);
}
