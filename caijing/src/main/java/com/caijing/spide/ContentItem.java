package com.caijing.spide;

import java.util.Vector;

public class ContentItem {
	Triple start;
	Vector<Triple> del;
	ContentItem(){
		del = new Vector<Triple>();
	}
	public String toString(){
		String contenthtml="";
		contenthtml = start.startEle+start.name+start.value;
		for(Triple t:del){
			contenthtml+=t.startEle+t.name+t.value;
		}
		return contenthtml;
	}
}
