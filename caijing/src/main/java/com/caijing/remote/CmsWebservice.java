package com.caijing.remote;

import java.net.MalformedURLException;
import java.util.List;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.springframework.context.ApplicationContext;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.zving.cms.webservice.CmsService;

public class CmsWebservice {
	private static CmsService cmsService = null;
	public static long catelogID = 9133;

	public static CmsService getInstance() {
		if (cmsService == null) {
			Service serviceModel = new ObjectServiceFactory().create(CmsService.class);
			XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
			String url = "http://cms.caijing.com:8080/Services/wsdl/CmsService";
			try {
				cmsService = (CmsService) factory.create(serviceModel, url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return cmsService;
	}

	public long addArticle(long catalogID, String title, String author, String content, String publishDate) {
		return cmsService.addArticle(catalogID, title, author, content, publishDate);
	}

	public long addArticle(long catalogID, String title, String author, String source, String summary, String content,
			String publishDate) {
		return cmsService.addArticle(catalogID, title, author, source, summary, content, publishDate);
	}

	public boolean publishArticle(long articleID) {
		return cmsService.publishArticle(articleID);
	}

	public static void main(String[] args) {
		//		try {
		//			ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
		//			Service serviceModel = serviceFactory.create(CmsService.class);
		//			XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
		//			String url = "http://cms.caijing.com:8080/Services/wsdl/CmsService";
		//			CmsService cmsService = (CmsService) factory.create(serviceModel, url);
		//			cmsService.addArticle(9129, "���Ա���1", "admin", "��������1", "2010-05-04");

		//		CmsWebservice cmsService = new CmsWebservice();
		//		long id = cmsService.addArticle(9129, "���Ա���5", "admin", "��������5", "2010-05-04 01:23:12");
		//		long id = cmsService.addArticle(9129, "ժҪ����", "admin", "summary", "��������5", "2010-05-04 01:23:12");

		ApplicationContext context = ContextFactory.getApplicationContext();
		ColumnArticleDao columnArticleDao = (ColumnArticleDao) context.getBean("columnArticleDao");
		for (int i = 0; i < 10; i++) {
			List<ColumnArticle> articles = columnArticleDao.getAllColumnArticle(i * 40, 40);
			for (ColumnArticle article : articles) {
				long aid = CmsWebservice.getInstance().addArticle(catelogID, article.getTitle(), article.getAuthor(),
						article.getSrc(), article.getAbs(), article.getContent(),
						DateTools.transformDateDetail(article.getPtime()));
				CmsWebservice.getInstance().publishArticle(aid);
				System.out.println(aid);
			}
		}

	}
}
