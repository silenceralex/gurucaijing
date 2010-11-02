package com.caijing.spider;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.caijing.dao.ColumnArticleDao;

public class SpiderSchedule {
	private String paramXml = null;
	private ColumnArticleDao columnArticleDao = null;

	public void run() {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {

			System.out.println("Input xml : " + paramXml);
			xml = sr.read(new File(paramXml));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		RssJob rssjob = ConfigReader.getRssJobFromXML(xml);
		rssjob.setColumnArticleDao(columnArticleDao);
		rssjob.run();
	}

	public String getParamXml() {
		return paramXml;
	}

	public void setParamXml(String paramXml) {
		this.paramXml = paramXml;
	}

	public ColumnArticleDao getColumnArticleDao() {
		return columnArticleDao;
	}

	public void setColumnArticleDao(ColumnArticleDao columnArticleDao) {
		this.columnArticleDao = columnArticleDao;
	}
}
