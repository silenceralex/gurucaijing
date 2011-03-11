package com.caijing.web.controller;

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

	@RequestMapping("/financialreport/financialreportLab.htm")
	public String showColomn(HttpServletResponse response, 
			@RequestParam(value = "year", required = false) String year, 
			@RequestParam(value = "stockname", required = false) String stockname, 
			@RequestParam(value = "stockcode", required = false) String stockcode, 
			@RequestParam(value = "type", required = false) Integer type, 
			@RequestParam(value = "page", required = false) Integer page, 
			HttpServletRequest request, ModelMap model) {

		System.out.println("year:" + year + " " + "stockname:" + stockname
				+ " " + "stockcode:" + stockcode + " " + "type:" + type + " " + "page:" + page);
		
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
		if(type!=null){ //有类型
			params.put("type", type);
		} 
		if(year!=null){
			params.put("year", year);
		} 
		if(stockcode!=null){
			params.put("stockcode", stockcode);
		} 
		if(stockname!=null){
			params.put("stockname", stockname);
		}
		for (Map.Entry<String, Object> m : params.entrySet()) {
			urlPattern.append("&" + m.getKey() + "=" + m.getValue());
		}
		int total = financialReportDao.getReportsListCount((Map<String, Object>)params.clone());
		paginator.setTotalRecordNumber(total);

		params.put("start", start);
		params.put("size", size);
		params.put("orderby", "year desc");
		List<FinancialReport> reportList = financialReportDao.getReportsList(params);

		paginator.setUrl(urlPattern.toString());
		model.put("reportlist", reportList);
		model.put("paginatorLink", paginator.getPageNumberList());

		return "/template/financialreportlab.htm";

	}
}
