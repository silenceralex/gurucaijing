package com.caijing.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.domain.Report;
import com.caijing.util.Paginator;

@Controller
public class FinancialReportController {

	@Autowired
	@Qualifier("financialReportDao")
	private FinancialReportDao financialReportDao = null;

	private static int STARTYEAR = 1996;
	private static int ENDYEAR = 2011;

	@RequestMapping("/financialreport/financialreportLab.htm")
	public String showColomn(HttpServletResponse response, @RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "kind", required = false) String kind,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model) {

		System.out.println("year:" + year + " kind:" + kind + " query:" + query + " type:" + type + " page:" + page);

		Paginator<Report> paginator = new Paginator<Report>();
		int size = 20; //分页大小
		if (page == null || page < 1) { //当前分页号
			page = 1;
		}
		paginator.setPageSize(size);
		paginator.setCurrentPageNumber(page);
		int start = (page - 1) * size;

		HashMap<String, Object> params = new HashMap<String, Object>();

		StringBuffer urlPattern = new StringBuffer();
		urlPattern.append("/financialreport/financialreportLab.htm?page=$number$");
		params.put("status", 0); //正常的财报
		if (type != null) { //有类型
			params.put("type", type);
		}
		if (year != null) {
			params.put("year", year);
		}
		if ("1".equals(kind) && query != null && query.trim().length() > 0) {
			params.put("stockcode", query);
		} else if ("2".equals(kind) && query != null && query.trim().length() > 0) {
			params.put("stockname", query);
		}

		for (Map.Entry<String, Object> m : params.entrySet()) {
			urlPattern.append("&" + m.getKey() + "=" + m.getValue());
		}
		int total = financialReportDao.getReportsListCount((Map<String, Object>) params.clone());
		paginator.setTotalRecordNumber(total);
		System.out.println("total ： " + total);
		System.out.println("current ： " + page);
		List<String> years = new ArrayList<String>();
		for (int i = ENDYEAR; i > STARTYEAR; --i) {
			years.add("" + i);
		}
		params.put("years", years);

		params.put("start", start);
		params.put("size", size);
		//最多查10页
		params.put("page", 10);
		params.put("orderby", "year desc,type desc");
		List<FinancialReport> reportList = financialReportDao.getReportsList(params);

		paginator.setUrl(urlPattern.toString());
		model.put("reportlist", reportList);
		model.put("paginatorLink", paginator.getPageNumberList());

		return "/search/financialreportlab.htm";

	}
}
