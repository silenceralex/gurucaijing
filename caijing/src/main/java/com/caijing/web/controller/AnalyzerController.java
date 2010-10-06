package com.caijing.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.caijing.domain.GroupPeriod;
import com.caijing.domain.Report;
import com.caijing.domain.StockGain;
import com.caijing.util.GroupGain;
import com.caijing.util.Paginator;

@Controller
public class AnalyzerController {
	@Autowired
	@Qualifier("stockGainManager")
	private StockGainManager stockGainManager = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;
	
	@Autowired
	@Qualifier("groupGain")
	private GroupGain gg = null;
	

	@RequestMapping("/admin/groupgainlist.htm")
	public String showGroupGainList(HttpServletResponse response, @RequestParam(value = "aname", required = true)
	String aname, @RequestParam(value = "page", required = false)
	Integer page, HttpServletRequest request, ModelMap model) {
//		GroupGain gg = new GroupGain();
		gg.init();
//		gg.setRecommendStockDao(recommendStockDao);
		GroupPeriod gs = gg.processASC(aname);

//		List<GroupPeriod> gps=gg.processGroupPeriod(aname);
//		for(GroupPeriod gp:gps){
//			for(String code:gp.getStockInGroup().keySet()){
//				StockGain sg=gp.getStockInGroup().get(code);
//				System.out.println("Code:"+code);
//				System.out.println("getStockname:"+sg.getStockname());				
//			}
//		}
//		List<GroupEarn> groupearnlist = new ArrayList<GroupEarn>(gs.getDates().size());
//		for (int i = 0; i < gs.getDates().size(); i++) {
//			GroupEarn ge = new GroupEarn();
//			ge.setDate(gs.getDates().get(i));
//			ge.setRatio(gs.getRatios().get(i));
//			ge.setWeight(gs.getWeights().get(i));
//			groupearnlist.add(ge);
//		}
		
		model.put("aname", aname);
//		gps.get(1).getStockInGroup().
//		model.put("gp", gs);
		
		System.out.println("gs.getFirstdate():"+gs.getFirstdate());
		System.out.println("Stockname:"+gs.getFirststock());			
		HashMap<String,String> codeMap=new HashMap<String,String>();
		for(StockGain sg:gs.getStockGains()){
			codeMap.put(sg.getStockcode(), sg.getStockname());
		}
		model.put("joinmap", gs.getJoinMap());
		model.put("codeMap", codeMap);
		model.put("joinmap", gs.getJoinMap());
		model.put("firststock", gs.getFirststock());
		model.put("firstdate", gs.getFirstdate());
		model.put("dates", gs.getDates());
		model.put("stockdatemap", gs.getStockdateMap());
		model.put("stockcodes", gs.getStockdateMap().keySet());
		model.put("ratios",	gs.getRatios());
		model.put("weights",	gs.getWeights());
//		model.put("groupearnlist", groupearnlist);
		return "/admin/groupgainlist.htm";
	}

	@RequestMapping("/admin/analyzergainlist.htm")
	public String showAnalyzerGainList(HttpServletResponse response, @RequestParam(value = "aname", required = true)
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
		urlPattern = "/admin/analyzergainlist.htm?aname=" + aname + "&page=$number$";
		model.put("aname", aname);
		paginator.setUrl(urlPattern);
		model.put("stockgainlist", stockgainlist);
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/analyzergainlist.htm";

	}
}
