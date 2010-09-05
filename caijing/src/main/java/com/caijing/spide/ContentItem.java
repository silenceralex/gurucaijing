package com.caijing.spide;

import java.util.Vector;

//一篇文章的内容项
//start  表示开始的三元组
//del 表示要直接删除的三元组
public class ContentItem {
	public class Triple {
		String startEle;
		String name;
		String value;
		
		Triple(String[] rlt){
			startEle=rlt[0];
			name = rlt[1];
			value = rlt[2];
		}
	}
	
	Triple start;
	Vector<Triple> del;
	ContentItem(){
		del = new Vector<Triple>();
	}
	public void setStart(String[] rlt){
		start = new Triple(rlt);
	}
	public void addDelItem(String[] rlt){
		del.add(new Triple(rlt));
		
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