package com.caijing.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.business.StockGainManager;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.Report;
import com.caijing.domain.StockGain;
import com.caijing.util.Paginator;

@Controller
public class AnalyzerController {
	@Autowired
	@Qualifier("stockGainManager")
	private StockGainManager stockGainManager = null;

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
		System.out.println("aname:" + aname);
		List<StockGain> stockgainlist = null;
		try {
			total = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
			paginator.setTotalRecordNumber(total);

			stockgainlist = stockGainManager.getStockGainByAname(aname, page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		urlPattern = "/admin/analyzergainlist.htm?aname=" + aname
				+ "&page=$number$";
		model.put("aname", aname);
		paginator.setUrl(urlPattern);
		model.put("stockgainlist", stockgainlist);
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/analyzergainlist.htm";

	}
}
