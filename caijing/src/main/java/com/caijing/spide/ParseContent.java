package com.caijing.spide;

import java.io.IOException;
import java.util.Vector;

public class ParseContent {
	Vector<ParseDiv> divVector;
	Vector<ParseDiv> divStack;
	boolean isstat = false;// 是否统计
	boolean isignore =false;//是否忽略
	int startpos=0;
	int endpos=0;

	ParseContent() {
		divVector = new Vector<ParseDiv>();
		divStack = new Vector<ParseDiv>();
	}

	void getContent(String content) {
		HtmlParser p = new HtmlParser(content);
		int event = p.getEventType();
		while (true) {
			switch (event) {
				case HtmlParser.START_ELEMENT:
					System.out.println("start"+p.getName());
					if (!isstat && p.getName().equals("body")) {
						isstat = true;
					}
					if(p.getName().equals("script")){//处理忽略的内容
						p.mv2RespEnd();
					} else if (isstat) {
						if (p.getName().equals("div") || p.getName().equals("td")) {
							System.out.println(content.substring(p.curpos));
							ParseDiv curDiv = new ParseDiv();
							curDiv.startpos = p.curpos;
							divStack.add(curDiv);
							System.out.println("div");
						} else if (p.getName().equals("p") && divStack.size() > 0) {
							divStack.get(divStack.size() - 1).pnum++;
						}
					}
					break;
				case HtmlParser.CHARACTERS:
					break;
				case HtmlParser.END_ELEMENT:
					System.out.println("end "+p.getName());
					if (p.getName().equals("body")) {
						isstat = false;
						System.out.println("reached body");
						return;
					}
					if (isstat) {
						if (p.getName().equals("div") || p.getName().equals("td")) {
							divStack.lastElement().endpos = p.getEleEndPos()+1;
							divVector.add(divStack.lastElement());
							divStack.remove(divStack.size() - 1);
						}
					}
					break;
				case HtmlParser.END_DOCUMENT:
					System.out.println("END_DOCUMENT");
					break;
				default:
					break;
			}

			if (p.hasnext() == 0){
				System.out.println("has no event" +
						"");
				break;
			}

			event = p.next();
			System.out.println(event);
		}
	}
		
		void printDiv(){
			for(ParseDiv tmp:divVector){
				System.out.println(tmp.startpos+":"+tmp.endpos+":"+tmp.pnum);
			}
		}
		
		int getMaxPDiv(){
			int idx=0;
			int maxpnum=0;
			
			for(int i=0; i<divVector.size();i++){
				if(divVector.get(i).pnum > maxpnum){
					maxpnum = divVector.get(i).pnum;
					idx = i;					
				}
			}
			if(maxpnum >0){
				startpos = divVector.get(idx).startpos;
				endpos = divVector.get(idx).endpos ;
				return idx;
			}
			return -1;
		}
		
		int getStartPos(){
			return startpos;
		}
		int getEndPos(){
			return endpos;
		}
		
		String delLable(String content){
			String rlt="";
			HtmlParser p = new HtmlParser(content);
			int event = p.getEventType();
		    event = p.next();
		    while(true){
				switch (event) {
					 case HtmlParser.START_ELEMENT:
						 if("script".equals(p.getName()) || "style".equals(p.getName())){
							 p.mv2RespEnd();
						 }
					 break;
					 case HtmlParser.END_ELEMENT:
					 break;
					 case HtmlParser.END_DOCUMENT:
					 break;
					 case HtmlParser.CHARACTERS:
						 rlt+=p.getText();
					 break;
					 default : 
					 break;
				}
				 if (p.hasnext()==0)
				     break;
				 event = p.next();
			 }
		    return rlt;
		}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String con=ContentDown.downContent("http://news.xinmin.cn/world/gjkb/2010/08/21/6417308.html","utf-8");
		System.out.println(con);
		ParseContent pc= new ParseContent();
		pc.getContent(con);
		pc.printDiv();
		pc.getMaxPDiv();
		System.out.println(con.substring(pc.getStartPos(),pc.getEndPos()));
		System.out.println(pc.delLable(con.substring(pc.getStartPos(),pc.getEndPos())));
	}

}
