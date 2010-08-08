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

import com.caijing.dao.ReportDao;
import com.caijing.domain.Report;
import com.caijing.util.Config;
import com.caijing.util.Paginator;
import com.caijing.util.TopicNameConfig;
import com.caijing.util.Vutil;

@Controller
public class ReportController {
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;
	

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;
	
	@RequestMapping("/showColumn.htm")
	public String showColomn(HttpServletResponse response,
			@RequestParam(value = "saname", required = false)
			String saname, @RequestParam(value = "page", required = false)
			Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);
	
		int total=0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		System.out.println("saname:"+saname);
		List<Report> reportlist= new ArrayList();
		if(saname!=null){
			total=reportDao.getAllReportsCountBySaname(saname);
			paginator.setTotalRecordNumber(total);
			reportlist=reportDao.getReportsBySaname(saname,(page-1)*20,20);
			urlPattern = "/showColumn.htm?saname="+saname+"&page=$number$";
		}else{
			total=reportDao.getAllReportsCount();
			paginator.setTotalRecordNumber(total);
			reportlist=reportDao.getAllReports((page-1)*20,20);
			urlPattern = "/showColumn.htm?page=$number$";
		}
		
		paginator.setUrl(urlPattern);
		model.put("topicNameMap", topicNameMap);
		model.put("vutil", vutil);
		model.put("reportlist", reportlist);
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/reportlist.htm";

	}

}
