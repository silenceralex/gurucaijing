package com.caijing.spide;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DownTest {
	private static Log logger = LogFactory.getLog(DownTest.class);
	public void DownRss() throws IOException{
		int rlt=0;
		ReadConfig r = new ReadConfig();
//		r.GetAllSite("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\rsssite.xml","utf-8");
		r.GetAllSite("/home/app/caijing/src/main/java/com/caijing/spide/rsssite.xml","utf-8");
//		r.GetAllPeople("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\focuspeople.xml","utf-8");
		r.GetAllPeople("/home/app/caijing/src/main/java/com/caijing/spide/focuspeople.xml","utf-8");
		RssDown down = new RssDown();
		rlt = down.initRssDown();
		if(rlt <0){
			logger.debug("init rssdown failed");
			return ;
		}
		for(Economistor people:r.allpeople){
			RssItem site=r.getSiteByName(people.sitename);
			logger.debug("start ...... "+people.name+".......");
			down.getRssArList(people.siteurl,people.name,site);
			logger.debug("end ...... "+people.name+".......");
			logger.debug("");
		}
		
	}

}