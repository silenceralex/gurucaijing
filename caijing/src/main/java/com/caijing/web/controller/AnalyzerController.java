package com.caijing.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.util.Paginator;

@Controller
public class AnalyzerController {
	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@RequestMapping("/admin/analyzergainlist.htm")
	public String showAnalyzerGainList(HttpServletResponse response,
			@RequestParam(value = "aname", required = true)
			String aname, @RequestParam(value = "page", required = false)
			Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);
		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		List<RecommendStock> recommendlist = new ArrayList<RecommendStock>();
		System.out.println("aname:" + aname);
		total = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
		paginator.setTotalRecordNumber(total);
		recommendlist = recommendStockDao.getRecommendStocksByAnalyzer(aname,
				(page - 1) * 20, 20);
		urlPattern = "/admin/analyzergainlist.htm?aname=" + aname
				+ "&page=$number$";
		model.put("aname", aname);
		paginator.setUrl(urlPattern);

		model.put("recommendlist", recommendlist);
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/analyzergainlist.htm";

	}
}
