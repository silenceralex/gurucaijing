package com.caijing.spide;



public class HtmlParser {

	public 
	static final int START_DOCUMENT=0;
	static final int START_ELEMENT =1;
	static final int START_CDATA =2;
	static final int CHARACTERS =3;
	static final int END_ELEMENT =4;
	static final int END_DOCUMENT =5;
	static final int ERROR =-1;
	
	private
	String buff=null;
	int curpos=0;
	int curev=0;
	int length=0;
	int flag1 = 0;//没有解析过
	
	public
	HtmlParser(String source){
		buff=source;
		length=source.length();		
	}
	 
	int getEventType(){
		if(buff == null || length==0) {
			return ERROR;
		}
		if(curpos==0 && flag1==0) {
			curev=START_DOCUMENT;
			return curev;
		}
		if(curpos == length) {
			curev = END_DOCUMENT;
			return curev;
		}
		if(buff.charAt(curpos)=='<' && buff.charAt(curpos+1)=='/'){
			curev = END_ELEMENT;
			return curev;
		}
		if(buff.charAt(curpos)=='<' ){
			if(buff.charAt(curpos+1)=='!' && buff.charAt(curpos+2)=='['){
				curev = START_CDATA;
				return curev;
			}else{
				curev = START_ELEMENT;
				return curev;
			}
		}else{
			curev = CHARACTERS;
			return curev;
		}
	}
	
	String getText(){
		int endpos=curpos;
		if(curev!=CHARACTERS) return null;
		while(buff.charAt(endpos)!='<'){
			endpos++;
			if(endpos >=length ) return buff.substring(curpos);
		}		
		if(endpos>curpos)
			return buff.substring(curpos,endpos);
	    return "";
	}
	
	String getCDATA(){
		int startpos=0;
		int endpos=curpos;
		if(curev!=START_CDATA) return null;
		if(buff.charAt(endpos+1)=='!' && buff.charAt(endpos+2)=='['){			
			while(buff.charAt(endpos)!=']'){
				if(startpos==0 && buff.charAt(endpos-1)!='!' && buff.charAt(endpos)=='['){
					startpos=endpos+1;
				}
				endpos++;
			}
			if(buff.charAt(endpos+1)==']' && buff.charAt(endpos+2)=='>'){
				return buff.substring(startpos, endpos);	
			}
		}
		return null;
	}
	
	String getName(){
		int endpos=curpos;
		if(curev == START_DOCUMENT) return null;
		while(buff.charAt(endpos)!=' ' && buff.charAt(endpos)!='>'){
			endpos++;	
		}
		if(curev ==END_ELEMENT && endpos>curpos+1)	
			return buff.substring(curpos+2,endpos);
		if(curev ==START_ELEMENT  && endpos>curpos)
			return buff.substring(curpos+1,endpos);
	    return "";
	}
	
	int next(){
		if(curpos >= length) return -1;
		
		if(curev == START_DOCUMENT){
			flag1=1;
			return getEventType();
		}
		if(curev == START_ELEMENT || curev == END_ELEMENT ){			
			while(buff.charAt(curpos)!='>'){
				curpos++;
			}			
			curpos++;
			if(curpos ==length) return END_DOCUMENT;
			return getEventType();
		}
		if(curev == START_CDATA){
			while(buff.charAt(curpos)!=']' && buff.charAt(curpos+1)==']' && buff.charAt(curpos+2)=='>'){
				curpos++;
			}
			curpos+=3;
			return getEventType();
		}
		if(curev == CHARACTERS){
			while(buff.charAt(curpos)!='<'){
				curpos++;
				if(curpos ==length) return END_DOCUMENT;
			}
			return getEventType();			
		}
		
		return -1;
	}
	
	int hasnext(){
		if(length <1) return 0;
		if(curpos < length-1) return 1;
		return 0;
	}
	
	int isWhiteSpace(){
		int pos = curpos;
		while(pos< length && buff.charAt(pos) != '<') {
			if(buff.charAt(pos)==' ' || buff.charAt(pos)=='\r' 
				|| buff.charAt(pos)=='\n' || buff.charAt(pos)=='\t' ) pos++;
			else return 0;
		}
		return 1;
	}

	int getAttributeCount(){
		int pos = curpos;
	    int count=0;
	    while(buff.charAt(pos)!='>'){
			if(buff.charAt(pos) == '=') count++;
			pos++;
	    }
	    return count;
	}

	String getAttributeName(int i){
	    int count=-1;
	    int pos = curpos;
	    int startpos;
	    while(pos<length && buff.charAt(pos) != '>' ){
			if(buff.charAt(pos) == '=')count++;
			if(count == i){
			    startpos = pos -1;
			    while(buff.charAt(startpos) != ' '){
				--startpos;
			    }
			    return buff.substring(startpos+1, pos);
			}
		pos++;
	    }
	    return "";
	}

	String getAttributeValue(int i){
	    int count=-1;
	    int pos = curpos;
	    int endpos;
	    char endchar='\"';
	    while(pos<length && buff.charAt(pos) != '>'){
			if(buff.charAt(pos) == '=')count++;
			if(count == i){
			    endpos = pos +1;
			    if(buff.charAt(endpos) != endchar){
			    	if(buff.charAt(endpos)== '\'') endchar='\'';
			    	else endchar=' ';
			    }
			    endpos++;
			    while(buff.charAt(endpos) != endchar){
			    	++endpos;				
			    }
			    if(endchar ==' ')
			    	return buff.substring(pos+1, endpos);
			    else
			    	return buff.substring(pos+2, endpos);
			}
			pos++;
	    }
	    return "";
	    
	}

	String getRespValue(){
	    int startpos=curpos;
	    int level=1;
	    if(curev != START_ELEMENT) return null;
	    String tmp=getName();
	    int event = next();
	    while(true){
		switch (event) {
			 case HtmlParser.START_ELEMENT:
//			 System.out.println("Start Element: " + getName());
			 if(tmp.equals(getName())){
			     level++;
			 }
			 break;
			 case HtmlParser.END_ELEMENT:
//			 System.out.println("End Element:" + getName());
			 if(tmp.equals(getName())){
			     level--;
			     if(level == 0){
				 next();
				 return buff.substring(startpos, curpos);
			     }
			 }
			 break;
			 case HtmlParser.END_DOCUMENT:
//			 System.out.println("End Document.");
			 break;
		}
		 if (hasnext()==0)
		     break;
		
		 event = next();
		 }
	    return null;
	}

}
