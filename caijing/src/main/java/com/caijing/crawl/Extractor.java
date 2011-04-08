package com.caijing.crawl;

import com.caijing.domain.Post;

public interface Extractor {
	int getTotalPages(String content);

	public Post extractFromHtml(String content, String groupID);
}
