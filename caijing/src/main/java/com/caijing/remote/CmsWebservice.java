package com.caijing.remote;

import java.net.MalformedURLException;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

import com.zving.cms.webservice.CmsService;

public class CmsWebservice {
	private static CmsService cmsService = null;
	static {
		Service serviceModel = new ObjectServiceFactory().create(CmsService.class);
		XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
		String url = "http://cms.caijing.com:8080/Services/wsdl/CmsService";
		try {
			cmsService = (CmsService) factory.create(serviceModel, url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long addArticle(long catalogID, String title, String author, String content, String publishDate) {
		return cmsService.addArticle(catalogID, title, author, content, publishDate);
	}

	public static void main(String[] args) {
		//		try {
		//			ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
		//			Service serviceModel = serviceFactory.create(CmsService.class);
		//			XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
		//			String url = "http://cms.caijing.com:8080/Services/wsdl/CmsService";
		//			CmsService cmsService = (CmsService) factory.create(serviceModel, url);
		//			cmsService.addArticle(9129, "测试标题1", "admin", "测试内容1", "2010-05-04");

		CmsWebservice cmsService = new CmsWebservice();
		long id = cmsService.addArticle(9129, "测试标题5", "admin", "测试内容5", "2010-05-04 01:23:12");
		System.out.println(id);
		//		} catch (MalformedURLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}
}
