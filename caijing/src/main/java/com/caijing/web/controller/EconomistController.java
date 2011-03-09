package com.caijing.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.dao.EconomistDao;
import com.caijing.dao.MasterDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.domain.Economist;
import com.caijing.domain.Master;
import com.caijing.util.DateTools;
import com.caijing.util.Paginator;

@Controller
public class EconomistController {
	private Log logger = LogFactory.getLog(EconomistController.class);

	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	@Autowired
	@Qualifier("economistDao")
	private EconomistDao economistDao = null;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao = null;

	@RequestMapping("/search/columnarticlelist.htm")
	public String showEcolumnarlst(HttpServletResponse response,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<ColumnArticle> paginator = new Paginator<ColumnArticle>();
		paginator.setPageSize(20);

		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		try {
			author = URLDecoder.decode(author, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("关键词utf-8解码失败：" + e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		List<ColumnArticle> articlelist = new ArrayList();
		DateTools datetool = new DateTools();
		if (author != null) {
			logger.debug("author:" + author);
			total = columnArticleDao.getColumnArticleCountByAuthor(author);
			paginator.setTotalRecordNumber(total);
			articlelist = columnArticleDao.getColumnArticleByAuthor(author, (page - 1) * 20, 20);
			articlelist = alertUrl(articlelist);
			logger.debug("articlelist size :" + articlelist.size());
			urlPattern = "/search/columnarticlelist.htm?author=" + author + "&page=$number$";
			model.put("author", author);
		} else {

			total = columnArticleDao.getAllColumnArticleCount();
			paginator.setTotalRecordNumber(total);
			articlelist = columnArticleDao.getAllColumnArticle((page - 1) * 20, 20);
			articlelist = alertUrl(articlelist);
			logger.debug("articlelist size :" + articlelist.size());
			urlPattern = "/search/columnarticlelist.htm?page=$number$";
		}

		paginator.setUrl(urlPattern);
		model.put("articlelist", articlelist);
		model.put("paginatorLink", paginator.getPageNumberList());
		model.put("dateTools", datetool);

		return "/template/list.htm";

	}

	@RequestMapping("/master/bloglist.htm")
	public String showMasterBlogList(HttpServletResponse response,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<ColumnArticle> paginator = new Paginator<ColumnArticle>();
		paginator.setPageSize(20);

		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		try {
			author = URLDecoder.decode(author, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("关键词utf-8解码失败：" + e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		List<ColumnArticle> articlelist = new ArrayList();
		DateTools datetool = new DateTools();
		if (author != null) {
			logger.debug("author:" + author);
			total = columnArticleDao.getColumnArticleCountByAuthor(author);
			paginator.setTotalRecordNumber(total);
			articlelist = columnArticleDao.getColumnArticleByAuthor(author, (page - 1) * 20, 20);
			articlelist = alertUrl(articlelist);
			logger.debug("articlelist size :" + articlelist.size());
			urlPattern = "/master/bloglist.htm?author=" + author + "&page=$number$";
			model.put("author", author);
		} else {
			total = columnArticleDao.getAllColumnArticleCount();
			paginator.setTotalRecordNumber(total);
			articlelist = columnArticleDao.getAllColumnArticle((page - 1) * 20, 20);
			articlelist = alertUrl(articlelist);
			logger.debug("articlelist size :" + articlelist.size());
			urlPattern = "/master/bloglist.htm?page=$number$";
		}

		paginator.setUrl(urlPattern);
		Master master = masterDao.getMasterByName(author);
		List<Master> masters = masterDao.getAllMasters(0, 100);
		model.put("articlelist", articlelist);
		model.put("masterList", masters);
		model.put("master", master);

		model.put("paginatorLink", paginator.getPageNumberList());
		model.put("dateTools", datetool);

		return "/template/masterbloglist.htm";

	}

	@RequestMapping("/admin/ecname.htm")
	public String shwoEcname(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		List<Economist> economistList = economistDao.getAllEconomist();
		model.put("economistList", economistList);
		return "/admin/ecname.htm";
	}

	@RequestMapping("/admin/article.htm")
	public String shwoArticle(HttpServletResponse response, @RequestParam(value = "aid", required = true) int aid,
			ModelMap model, HttpServletRequest request) throws IOException, Exception {
		ColumnArticle article = columnArticleDao.getColumnArticleByaid(aid).get(0);
		model.put("article", article);
		DateTools datetool = new DateTools();
		model.put("datetool", datetool);
		return "/admin/article.htm";
	}

	private List<ColumnArticle> alertUrl(List<ColumnArticle> articles) {
		List<ColumnArticle> retlist = new ArrayList<ColumnArticle>(articles.size());
		for (int i = 0; i < articles.size(); i++) {
			String url = "http://51gurus.com/cms/";
			String date = DateTools.transformDateDetail(articles.get(i).getPtime());
			System.out.println("date:" + date);
			String[] strs = date.split("-");
			System.out.println("strs length:" + strs);
			if (strs.length == 3) {
				url += strs[0] + "/" + strs[1] + "/" + articles.get(i).getCmsid() + ".shtml";
				articles.get(i).setLink(url);
				System.out.println("url:" + articles.get(i).getLink());
				retlist.add(articles.get(i));
			} else {
				System.out.println("Date Format Parse ERROR!");
			}
		}
		return retlist;
	}

}
