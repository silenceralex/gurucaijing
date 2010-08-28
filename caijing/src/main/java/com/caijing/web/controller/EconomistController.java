package com.caijing.web.controller;

import java.io.IOException;
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
import com.caijing.dao.EconomistDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.domain.Economist;
import com.caijing.util.Paginator;


@Controller
public class EconomistController {
	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;
	
	@Autowired
	@Qualifier("economistDao")
	private EconomistDao economistDao = null;
	
	@RequestMapping("/admin/economist.html")
	public void showEconomist(HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {		
		try {
			response.sendRedirect("/admin/economist.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/admin/columnarticlelist.html")
	public String showEcolumnarlst(HttpServletResponse response,
			@RequestParam(value = "saname", required = false)
			String saname, @RequestParam(value = "page", required = false)
			Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<ColumnArticle> paginator = new Paginator<ColumnArticle>();
		paginator.setPageSize(20);
	
		int total=0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		System.out.println("saname:"+saname);
		List<ColumnArticle> articlelist= new ArrayList();
		if(saname!=null){
			total=columnArticleDao.getColumnArticleByname(saname).size();
			paginator.setTotalRecordNumber(total);
			articlelist=columnArticleDao.getColumnArticleByname(saname,(page-1)*20,20);
			urlPattern = "/admin/columnarticlelist.html?saname="+saname+"&page=$number$";
			model.put("saname", saname);
		}else{
			total=columnArticleDao.getAllColumnArticleCount();
			paginator.setTotalRecordNumber(total);
			articlelist=columnArticleDao.getAllColumnArticle((page-1)*20,20);
			urlPattern = "/admin/columnarticlelist.html?page=$number$";
		}
		
		paginator.setUrl(urlPattern);
		model.put("articlelist", articlelist);
		model.put("paginatorLink", paginator.getPageNumberList());
		
		return "/admin/columnarticlelist.html";

	}
	
	@RequestMapping("/admin/ecname.html")
	public String shwoEcname(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		List<Economist> economistList=economistDao.getAllEconomist();
		model.put("economistList", economistList);
		return "/admin/ecname.html";	
	}
	

}
