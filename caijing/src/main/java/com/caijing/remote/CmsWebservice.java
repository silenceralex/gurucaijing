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
			String url = "http://cms.51gurus.com:8080/Services/wsdl/CmsService";
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
		//		CmsService cmsService = (CmsService) factory.create(serviceModel, url);
		//		cmsService.addArticle(9129, "≤‚ ‘±ÍÃ‚1", "admin", "≤‚ ‘ƒ⁄»›1", "2010-05-04");

		//		CmsWebservice cmsService = new CmsWebservice();
		//		long id = cmsService.addArticle(9129, "≤‚ ‘±ÍÃ‚5", "admin", "≤‚ ‘ƒ⁄»›5", "2010-05-04 01:23:12");
		//		long id = cmsService.addArticle(9129, "’™“™≤‚ ‘", "admin", "summary", "≤‚ ‘ƒ⁄»›5", "2010-05-04 01:23:12");
		//		long aid = CmsWebservice.getInstance().addArticle(9129, "’™“™≤‚ ‘1234", "admin", "summary", "»‹Ω‚", "≤‚ ‘ƒ⁄»›5",
		//				"2010-11-04 11:23:12");
		//		CmsWebservice.getInstance().publishArticle(aid);
		//		System.out.println(aid);

		ApplicationContext context = ContextFactory.getApplicationContext();
		ColumnArticleDao columnArticleDao = (ColumnArticleDao) context.getBean("columnArticleDao");
		//		for (int i = 0; i < 10; i++) {
		//		List<ColumnArticle> articles = columnArticleDao.getAllColumnArticle(0, 2);
		List<ColumnArticle> articles = columnArticleDao.getUnpublishArticles();
		for (ColumnArticle article : articles) {
			if (article.getType() == 0) {
				long aid = CmsWebservice.getInstance().addArticle(catelogID, article.getTitle(), article.getAuthor(),
						article.getSrc(), article.getAbs(), article.getContent(),
						DateTools.transformDateDetail(article.getPtime()));
				System.out.println(aid);
				article.setCmsid(aid);
				columnArticleDao.update(article);
				CmsWebservice.getInstance().publishArticle(aid);
			} else if (article.getType() == 3) {
				long aid = CmsWebservice.getInstance().addArticle(9134, article.getTitle(), article.getAuthor(),
						article.getSrc(), article.getAbs(), article.getContent(),
						DateTools.transformDateDetail(article.getPtime()));
				columnArticleDao.update(article);
				CmsWebservice.getInstance().publishArticle(aid);
				System.out.println(aid);
			} else if (article.getType() == 1) {
				long aid = CmsWebservice.getInstance().addArticle(9135, article.getTitle(), article.getAuthor(),
						article.getSrc(), article.getAbs(), article.getContent(),
						DateTools.transformDateDetail(article.getPtime()));
				columnArticleDao.update(article);
				CmsWebservice.getInstance().publishArticle(aid);
				System.out.println(article.getTitle());
				System.out.println(aid);
			} else if (article.getType() == 2) {
				long aid = CmsWebservice.getInstance().addArticle(9136, article.getTitle(), article.getAuthor(),
						article.getSrc(), article.getAbs(), article.getContent(),
						DateTools.transformDateDetail(article.getPtime()));
				columnArticleDao.update(article);
				CmsWebservice.getInstance().publishArticle(aid);
				System.out.println(aid);
			}
		}
		System.out.println(articles.size());

		//		ColumnArticle article = new ColumnArticle();
		//		article.setAid("fa8a60ddd279512ae1394f3ee20f923c");
		//		article.setCmsid(253269);
		//		int i = columnArticleDao.update(article);
		//		System.out.println("update column num" + i);
	}

}
