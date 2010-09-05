package com.caijing.spide;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParseContent {
	private static Log logger = LogFactory.getLog(ParseContent.class);
	public class ParseDiv {
		int startpos;
		int endpos;
		int pnum;//<p> 
	}
	
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

	void getDivPNum(String content) {
		HtmlParser p = new HtmlParser(content);
		int event = p.getEventType();
		while (true) {
			switch (event) {
				case HtmlParser.START_ELEMENT:
					logger.debug("start"+p.getName());
					if (!isstat && p.getName().equals("body")) {
						isstat = true;
					}
					if(p.getName().equals("script")){//处理忽略的内容
						p.mv2RespEnd();
					} else if (isstat) {
						if (p.getName().equals("div") || p.getName().equals("td")) {
//							logger.debug(content.substring(p.curpos));
							ParseDiv curDiv = new ParseDiv();
							curDiv.startpos = p.curpos;
							divStack.add(curDiv);
//							logger.debug("div");
						} else if (p.getName().equals("p") && divStack.size() > 0) {
							divStack.get(divStack.size() - 1).pnum++;
						}
					}
					break;
				case HtmlParser.CHARACTERS:
					break;
				case HtmlParser.END_ELEMENT:
					logger.debug("end "+p.getName());
					if (p.getName().equals("body")) {
						isstat = false;
						logger.debug("reached body");
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
					logger.debug("END_DOCUMENT");
					break;
				default:
					break;
			}

			if (p.hasnext() == 0){
				logger.debug("has no event" +
						"");
				break;
			}

			event = p.next();
			logger.debug(event);
		}
	}
		
		void printDiv(){
			for(ParseDiv tmp:divVector){
				logger.debug(tmp.startpos+":"+tmp.endpos+":"+tmp.pnum);
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
		
		
	
	public static void main(String[] args) throws IOException {
	}

}
