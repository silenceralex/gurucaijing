package com.caijing.spide;

import java.util.Vector;

//һƪ���µ�������
//start  ��ʾ��ʼ����Ԫ��
//del ��ʾҪֱ��ɾ������Ԫ��
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