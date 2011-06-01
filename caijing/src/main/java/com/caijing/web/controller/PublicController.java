package com.caijing.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caijing.dao.IndustryDao;
import com.caijing.dao.MasterDao;
import com.caijing.dao.ProductDAO;
import com.caijing.domain.Industry;
import com.caijing.domain.Master;
import com.caijing.domain.Product;

@Controller
public class PublicController {

	@Autowired
	@Qualifier("productDAO")
	private ProductDAO productDAO;

	@Autowired
	@Qualifier("industryDao")
	private IndustryDao industryDao;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao;

	private static final Log logger = LogFactory.getLog(PublicController.class);

	
	@RequestMapping(value = "/get/interface.do")
	public void getInterface(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<Industry> lv1Industries = industryDao.selectlv1();
		String industryJsonstr = JSONArray.fromObject(lv1Industries).toString();
		if(industryJsonstr==null) industryJsonstr="[]";
		List<Product> products = productDAO.getAllProduct();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "rightpaths" });
		String productjsonstr = JSONArray.fromObject(products, jsonConfig).toString();
		if(productjsonstr==null) productjsonstr="[]";
		List<Master> Masters = masterDao.getAllMasters(null, null);
		 jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "crawlnum", "status" });
		String masterjsonstr = JSONArray.fromObject(Masters, jsonConfig).toString();
		if(masterjsonstr==null) masterjsonstr="[]";
		String json="{industry:"+industryJsonstr+",product:"+productjsonstr+",master:"+masterjsonstr+"}";
		response.setContentType("application/json; charset=utf-8");
		try {
			response.getWriter().write(json);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getInterface: ", e);
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/get/industry.do")
	public void getAllLv1Industry(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<Industry> lv1Industries = industryDao.selectlv1();
		String jsonstr = JSONArray.fromObject(lv1Industries).toString();
		if (jsonstr == null) {
			jsonstr = "[]";
		}
		response.setContentType("application/json; charset=utf-8");
		try {
			response.getWriter().write(jsonstr);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getAllLv1Industry: ", e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/get/product.do")
	public void getAllProduct(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<Product> products = productDAO.getAllProduct();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "rightpaths" });
		String jsonstr = JSONArray.fromObject(products, jsonConfig).toString();
		if (jsonstr == null) {
			jsonstr = "[]";
		}
		response.setContentType("application/json; charset=utf-8");
		try {
			response.getWriter().write(jsonstr);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getAllProduct: ", e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/get/master.do")
	public void getAllMaster(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<Master> Masters = masterDao.getAllMasters(null, null);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "crawlnum", "status" });
		String jsonstr = JSONArray.fromObject(Masters, jsonConfig).toString();
		logger.debug("jsonstr:" + jsonstr);
		if (jsonstr == null) {
			jsonstr = "[]";
		}
		response.setContentType("application/json; charset=utf-8");
		try {
			response.getWriter().write(jsonstr);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getAllMaster: ", e);
			e.printStackTrace();
		}
	}
}
