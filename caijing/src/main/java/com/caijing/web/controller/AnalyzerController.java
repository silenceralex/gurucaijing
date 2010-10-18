package com.caijing.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;
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
	String aname, @RequestParam(value = "debug", required = false)
	String debug, @RequestParam(value = "page", required = false)
	Integer page, HttpServletRequest request, ModelMap model) {
		//		GroupGain gg = new GroupGain();
		gg.init();
		GroupPeriod gs = gg.processASC(aname);
		model.put("aname", aname);

		System.out.println("gs.getFirstdate():" + gs.getFirstdate());
		System.out.println("Stockname:" + gs.getFirststock());
		HashMap<String, String> codeMap = new HashMap<String, String>();
		for (StockGain sg : gs.getStockGains()) {
			codeMap.put(sg.getStockcode(), sg.getStockname());
		}
		//		float weight = gs.getWeights().get(gs.getWeights().size() - 1);
		List<Float> groupearn = new ArrayList<Float>();
		for (float weight : gs.getWeights()) {
			groupearn.add(FloatUtil.getTwoDecimal(weight - 100));
		}
		String totalratio = groupearn.get(groupearn.size() - 1) + "%";
		if ("true".equals(debug)) {
			model.put("debug", 1);
		} else {
			model.put("debug", 0);
		}
		List reversedates = new ArrayList<String>();
		reversedates.addAll(gs.getDates());
		Collections.reverse(reversedates);
		model.put("reversedates", reversedates);
		model.put("joinmap", gs.getJoinMap());
		model.put("codeMap", codeMap);
		model.put("joinmap", gs.getJoinMap());
		model.put("firststock", gs.getFirststock());
		model.put("firstdate", gs.getFirstdate());
		model.put("dates", gs.getDates());
		model.put("stockdatemap", gs.getStockdateMap());
		model.put("stockcodes", gs.getStockdateMap().keySet());
		model.put("ratios", gs.getRatios());
		model.put("weights", gs.getWeights());
		model.put("totalratio", totalratio);
		model.put("groupearn", groupearn);

		StockGain zssg = stockGainManager.getZSGainByPeriod(gs.getFirstdate(), DateTools
				.transformYYYYMMDDDate(new Date()));
		zssg.setStockname("上证指数");
		List<Float> zsperoidprice = zssg.getPeriodprice();
		List<Float> zsperiodratio = zssg.getPeriodratio();
		System.out.println("dates size:" + gs.getDates().size());
		System.out.println("weights size:" + gs.getWeights().size());
		System.out.println("getPeriodearn size:" + zssg.getPeriodearn().size());
		Collections.reverse(zsperoidprice);
		model.put("zsperoidprice", zsperoidprice);
		model.put("zsperiodratio", zssg.getPeriodearn());
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
