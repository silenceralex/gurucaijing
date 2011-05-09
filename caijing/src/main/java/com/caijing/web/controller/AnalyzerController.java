package com.caijing.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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

import com.caijing.business.StockGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.dao.UserrightDAO;
import com.caijing.domain.Analyzer;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.Report;
import com.caijing.domain.StockEarn;
import com.caijing.domain.WebUser;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;
import com.caijing.util.GroupGain;
import com.caijing.util.Paginator;

@Controller
public class AnalyzerController {

	private Log logger = LogFactory.getLog(AnalyzerController.class);

	@Autowired
	@Qualifier("stockGainManager")
	private StockGainManager stockGainManager = null;

	@Autowired
	@Qualifier("groupEarnDao")
	private GroupEarnDao groupEarnDao = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

	@Autowired
	@Qualifier("recommendSuccessDao")
	private RecommendSuccessDao recommendSuccessDao = null;

	@Autowired
	@Qualifier("userrightDAO")
	private UserrightDAO userrightDao = null;

	@Autowired
	@Qualifier("groupGain")
	private GroupGain gg = null;

	@RequestMapping("/admin/groupgainlist.htm")
	public String showGroupGainList(HttpServletResponse response,
			@RequestParam(value = "aid", required = true) String aid,
			@RequestParam(value = "debug", required = false) String debug,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model)
			throws ParseException {
		//		GroupGain gg = new GroupGain();
		//		try {
		//			aname = URLDecoder.decode(aname, "UTF-8");
		//		} catch (UnsupportedEncodingException e) {
		//			logger.error("关键词utf-8解码失败：" + e.getMessage());
		//		} catch (Exception e) {
		//			logger.error(e.getMessage());
		//		}
		//		gg.init();
		//		GroupPeriod gs = gg.processASC(aname);
		//		model.put("aname", aname);
		//		Analyzer analyzer = gg.getAnalyzerDao().getAnalyzerByName(aname);

		//		List<GroupStock> currentstocks = gg.getGroupStockDao().getCurrentStockByGroupid(analyzer.getAid());
		//		model.put("currentstocks", currentstocks);
		//		System.out.println("gs.getFirstdate():" + gs.getFirstdate());
		//		System.out.println("Stockname:" + gs.getFirststock());
		//		HashMap<String, String> codeMap = new HashMap<String, String>();
		//		for (StockGain sg : gs.getStockGains()) {
		//			codeMap.put(sg.getStockcode(), sg.getStockname());
		//		}
		//		//		float weight = gs.getWeights().get(gs.getWeights().size() - 1);
		//		List<Float> groupearn = new ArrayList<Float>();
		//		for (float weight : gs.getWeights()) {
		//			groupearn.add(FloatUtil.getTwoDecimal(weight - 100));
		//		}

		Analyzer analyzer = (Analyzer) analyzerDao.select(aid);
		System.out.println("aname:" + analyzer.getName());
		model.put("analyzer", analyzer);
		model.put("aname", analyzer.getName());
		Date startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate("2010-01-01"));

		System.out.println("startDate:" + startDate);
		List<GroupStock> groupstocks = groupStockDao.getCurrentStockByGroupid(analyzer.getAid());
		System.out.println("analyzer: " + analyzer.getName() + "  startDate:" + startDate.toString());
		//		startDateMap.put(analyzer.getAid(), DateTools.transformYYYYMMDDDate(startDate));
		List<GroupEarn> weightList = groupEarnDao.getWeightList(analyzer.getAid(), startDate);
		LinkedList<GroupEarn> reverseWeightList = new LinkedList<GroupEarn>();
		for (GroupEarn ge : weightList) {
			ge.setRatio(FloatUtil.getTwoDecimal((ge.getWeight() - 100)));
			reverseWeightList.addFirst(ge);
		}
		model.put("weightList", weightList);
		model.put("reverseWeightList", reverseWeightList);
		float startprice = stockEarnDao.getStockEarnByCodeDate("000300", DateTools.transformYYYYMMDDDate(startDate))
				.getPrice();
		List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
				DateTools.transformYYYYMMDDDate(startDate));
		for (StockEarn se : priceList) {
			se.setCurrratio(FloatUtil.getTwoDecimal((se.getPrice() - startprice) * 100 / startprice));
		}
		model.put("priceList", priceList);
		model.put("groupstocks", groupstocks);
		model.put("dateTools", new DateTools());
		List<GroupStock> currentstocks = gg.getGroupStockDao().getCurrentStockByGroupid(analyzer.getAid());
		String totalratio = weightList.get(weightList.size() - 1).getRatio() + "%";
		model.put("totalratio", totalratio);
		if ("true".equals(debug)) {
			model.put("debug", 1);
		} else {
			model.put("debug", 0);
		}
		//		List reversedates = new ArrayList<String>();
		//		reversedates.addAll(gs.getDates());
		//		Collections.reverse(reversedates);
		//		model.put("reversedates", reversedates);
		//		model.put("joinmap", gs.getJoinMap());
		//		model.put("codeMap", codeMap);
		//		model.put("joinmap", gs.getJoinMap());
		//		model.put("firststock", gs.getFirststock());
		//		model.put("firstdate", gs.getFirstdate());
		//		model.put("dates", gs.getDates());
		//		model.put("stockdatemap", gs.getStockdateMap());
		//		model.put("stockcodes", gs.getStockdateMap().keySet());
		//		model.put("ratios", gs.getRatios());
		//		model.put("weights", gs.getWeights());
		//		model.put("totalratio", totalratio);
		//		model.put("groupearn", groupearn);

		//		StockGain zssg = stockGainManager.getZSGainByPeriod(gs.getFirstdate(),
		//				DateTools.transformYYYYMMDDDate(new Date()));
		//		zssg.setStockname("上证指数");
		//		List<String> zsdate = zssg.getPerioddate();
		//		//加入第一只股票的时间
		//		zsdate.add(gs.getFirstdate());
		//		//		Collections.reverse(zsdate);
		//		List<Float> zsperoidprice = zssg.getPeriodprice();
		//		List<Float> zsperiodratio = zssg.getPeriodratio();
		//		System.out.println("dates size:" + gs.getDates().size());
		//		System.out.println("weights size:" + gs.getWeights().size());
		//		System.out.println("getPeriodearn size:" + zssg.getPeriodearn().size());
		//		Collections.reverse(zsperoidprice);
		//		model.put("zsdate", zsdate);
		//		model.put("zsperoidprice", zsperoidprice);
		//		model.put("zsperiodratio", zssg.getPeriodearn());
		//		model.put("groupearnlist", groupearnlist);
		return "/admin/groupgainlist.htm";
	}

	@RequestMapping("/admin/analyzergainlist.htm")
	public String showAnalyzerGainList(HttpServletResponse response,
			@RequestParam(value = "aid", required = true) String aid,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model)
			throws ParseException {
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);
		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);

		System.out.println("aid:" + aid);
		//		List<StockGain> stockgainlist = null;
		Date startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate("2010-01-01"));
		List<GroupStock> stocks = null;
		try {
			Analyzer analyzer = (Analyzer) gg.getAnalyzerDao().select(aid);
			model.put("analyzer", analyzer);

			//			total = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
			paginator.setTotalRecordNumber(total);
			stocks = groupStockDao.getCurrentStockByGroupid(aid);
			total = stocks.size();
			//			stocks = groupStockDao.getGroupStockListDesc((page - 1) * 20, 20);
			//			stockgainlist = stockGainManager.getStockGainByAname(aname, page);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String urlPattern = "/admin/analyzergainlist.htm?aid=" + aid + "&page=$number$";
		model.put("aid", aid);
		if (stocks != null && stocks.size() > 0) {
			model.put("currdate", DateTools.transformYYYYMMDDDate(stocks.get(0).getLtime()));
		} else {
			model.put("currdate", DateTools.transformYYYYMMDDDate(new Date()));
		}
		paginator.setUrl(urlPattern);
		model.put("stocks", stocks);
		model.put("dateTools", new DateTools());
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/analyzergainlist.htm";
	}

	@RequestMapping("/admin/analyzerrank.htm")
	public String showAnalyzerRank(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		Date date = gg.getGroupEarnDao().getLatestDate();
		List<GroupEarn> groupearnlist = gg.getGroupEarnDao().getGroupEarnRankByDate(date);
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		List<Float> gains = new ArrayList<Float>();
		for (GroupEarn groupearn : groupearnlist) {
			String aid = groupearn.getGroupid();
			//						String aid = groupearn.getGroupid().substring(1);
			Analyzer analyzer = (Analyzer) gg.getAnalyzerDao().select(aid);
			analyzers.add(analyzer);
			gains.add(FloatUtil.getTwoDecimal(groupearn.getWeight() - 100));
		}
		model.put("currentdate", DateTools.transformYYYYMMDDDate(date));
		model.put("groupearnlist", groupearnlist);
		model.put("analyzers", analyzers);
		model.put("gains", gains);
		return "/admin/analyzerrank.htm";
	}

	@RequestMapping("/admin/successrank.htm")
	public String showSuccessRank(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<Analyzer> analyzers = analyzerDao.getSuccessRankedAnalyzersByAgency("安信证券");
		for (Analyzer analyzer : analyzers) {
			int success = recommendSuccessDao.getRecommendSuccessCountByAid(analyzer.getAid());
			int total = recommendSuccessDao.getTotalRecommendCountByAid(analyzer.getAid());
			analyzer.setSuccess(success);
			analyzer.setTotal(total);
		}
		model.put("analyzers", analyzers);
		model.put("floatUtil", new FloatUtil());
		return "/admin/successrank.htm";
	}

	@RequestMapping("/admin/analyzersuccess.htm")
	public String showAnalyzerSuccessRatio(HttpServletResponse response,
			@RequestParam(value = "aid", required = true) String aid, HttpServletRequest request, ModelMap model) {

		List<RecommendSuccess> recommends = recommendSuccessDao.getRecommendsByAid(aid);
		model.put("recommends", recommends);
		int success = recommendSuccessDao.getRecommendSuccessCountByAid(aid);
		int total = recommendSuccessDao.getTotalRecommendCountByAid(aid);
		if (total == 0) {
			model.put("ratio", "0.0%");
		} else {
			float ratio = ((float) success / total) * 100;
			model.put("ratio", "" + FloatUtil.getTwoDecimal(ratio) + "%");
		}
		if (recommends != null && recommends.size() > 0) {
			model.put("aname", recommends.get(0).getAname());
		} else {
			Analyzer analyzer = (Analyzer) analyzerDao.select(aid);
			model.put("aname", analyzer.getName());
		}
		model.put("dateTools", new DateTools());
		model.put("floatUtil", new FloatUtil());
		return "/admin/anayzersuccess.htm";
	}

	@RequestMapping("/analyzer/industry.htm")
	public String getAnalyzerIndustry(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		WebUser user = (WebUser) request.getSession().getAttribute("currWebUser");
		List<String> industryList = userrightDao.getIndustriesByUserid(user.getUid(), "analyzer");
		logger.debug("own industry size:" + industryList.size());
		model.put("industryList", industryList);
		return "/template/industryList.htm";
	}

}
