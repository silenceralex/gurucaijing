package com.caijing.spide;

import java.io.IOException;
import java.util.Vector;

public class ParseContent {
	Vector<ParseDiv> divVector;
	Vector<ParseDiv> divStack;
	boolean isstat = false;// 是否统计
	boolean isignore =false;//是否忽略

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
							ParseDiv curDiv = new ParseDiv();
							curDiv.startpos = p.curpos;
							divStack.add(curDiv);
							System.out.println("div");
						} else if (p.getName().equals("p") && divStack.size() > 0) {
							divStack.get(divStack.size() - 1).pnum++;
							System.out.println("pnum="+p);
						}
					}
					break;
				case HtmlParser.CHARACTERS:
					break;
				case HtmlParser.END_ELEMENT:
					if (p.getName().equals("body")) {
						isstat = false;
						System.out.println("reached body");
						return;
					}
					if (isstat) {
						if (p.getName().equals("div") || p.getName().equals("td")) {
							divStack.lastElement().endpos = p.getEleEndPos();
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
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String con=ContentDown.downContent("http://finance.sina.com.cn/review/20100817/08408494217.shtml","utf-8");
		System.out.println(con);
		ParseContent pc= new ParseContent();
		pc.getContent(con);
		pc.printDiv();
	}

}
