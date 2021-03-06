package com.caijing.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;
import com.caijing.util.Config;
import com.caijing.util.DateTools;
import com.caijing.util.Paginator;
import com.caijing.util.ResponseUtil;
import com.caijing.util.ServerUtil;
import com.caijing.util.TopicNameConfig;
import com.caijing.util.Vutil;

@Controller
public class RecommendController {
	private Log logger = LogFactory.getLog(RecommendController.class);
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@Autowired
	@Qualifier("responseUtil")
	private ResponseUtil resUtil = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;

	@RequestMapping("/admin/showAllRecommend.htm")
	public String showAllRecommend(HttpServletResponse response,
			@RequestParam(value = "saname", required = false) String saname,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "type", required = false) Integer type, HttpServletRequest request, ModelMap model) {
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
		if (saname != null) {
			try {
				saname = URLDecoder.decode(saname, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("关键词utf-8解码失败：" + e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			logger.debug("saname:" + saname);
			if (type == 1) {
				total = recommendStockDao.getGoodCounts(saname);
				paginator.setTotalRecordNumber(total);
				recommendlist = recommendStockDao.getGoodRecommendStocksBySaname(saname, (page - 1) * 20, 20);
				urlPattern = "/admin/showAllRecommend.htm?type=1&saname=" + saname + "&page=$number$";
			} else {
				total = recommendStockDao.getUncompletedCounts(saname);
				paginator.setTotalRecordNumber(total);
				recommendlist = recommendStockDao.getUncompletedRecommendStocksBySaname(saname, (page - 1) * 20, 20);
				urlPattern = "/admin/showAllRecommend.htm?type=0&saname=" + saname + "&page=$number$";
			}
			//			total=recommendStockDao.getAllRecommendCountBySaname(saname);
			//			paginator.setTotalRecordNumber(total);
			//			recommendlist=recommendStockDao.getRecommendStocksBySaname(saname,(page-1)*20,20);
			//			urlPattern = "/admin/showAllRecommend.htm?saname=" + saname + "&page=$number$";
			model.put("saname", saname);
		} else {
			total = recommendStockDao.getAllRecommendStocksCount();
			paginator.setTotalRecordNumber(total);
			recommendlist = recommendStockDao.getRecommendStocks((page - 1) * 20, 20);
			urlPattern = "/admin/showAllRecommend.htm?page=$number$";
		}

		paginator.setUrl(urlPattern);
		model.put("topicNameMap", topicNameMap);
		model.put("vutil", vutil);
		model.put("recommendlist", recommendlist);
		model.put("paginatorLink", paginator.getPageNumberList());

		return "/admin/recommendlist.htm";
	}

	@RequestMapping("/admin/showRecommend.htm")
	public String showRecommend(HttpServletResponse response,
			@RequestParam(value = "saname", required = false) String saname,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model) {
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);

		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		try {
			saname = URLDecoder.decode(saname, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("关键词utf-8解码失败：" + e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.debug("saname:" + saname);
		List<Report> reportlist = new ArrayList();
		if (saname != null) {
			total = reportDao.getAllReportsCountBySaname(saname);
			paginator.setTotalRecordNumber(total);
			reportlist = reportDao.getReportsBySaname(saname, (page - 1) * 20, 20);
			urlPattern = "/admin/showRecommend.htm?saname=" + saname + "&page=$number$";
			model.put("saname", saname);
		} else {
			total = reportDao.getAllReportsCount();
			paginator.setTotalRecordNumber(total);
			reportlist = reportDao.getAllReports((page - 1) * 20, 20);
			urlPattern = "/admin/showColumn.htm?page=$number$";
		}

		paginator.setUrl(urlPattern);
		model.put("topicNameMap", topicNameMap);
		model.put("vutil", vutil);
		model.put("reportlist", reportlist);
		model.put("paginatorLink", paginator.getPageNumberList());

		return "/admin/reportlist.htm";
	}

	@RequestMapping("/admin/recommendedit.htm")
	public String showRecommend(HttpServletResponse response,
			@RequestParam(value = "rid", required = true) String reportid, HttpServletRequest request, ModelMap model) {
		Report report = null;
		report = (Report) reportDao.select(reportid);
		RecommendStock rstock = recommendStockDao.getRecommendStockbyReportid(reportid);
		model.put("vutil", vutil);
		model.put("rstock", rstock);
		model.put("report", report);
		return "/admin/recommendinfo.htm";
	}

	@RequestMapping("/admin/stockgain.htm")
	public String showStockGain(HttpServletResponse response,
			@RequestParam(value = "rid", required = true) String reportid, HttpServletRequest request, ModelMap model) {
		Report report = null;
		report = (Report) reportDao.select(reportid);
		RecommendStock rstock = recommendStockDao.getRecommendStockbyReportid(reportid);
		StockGain sg = sp.getStockGainByPeriod(rstock.getStockcode(),
				DateTools.transformYYYYMMDDDateFromStr(rstock.getCreatedate()),
				DateTools.transformYYYYMMDDDate(new Date()));
		sg.setSaname(rstock.getSaname());
		sg.setReportid(reportid);
		sg.setStockname(rstock.getStockname());
		sg.setObjectprice(rstock.getObjectprice());
		List<String> dates = sg.getPerioddate();
		List<Float> peroidprice = sg.getPeriodprice();
		List<Float> periodratio = sg.getPeriodratio();
		System.out.println("periodratio size:" + periodratio.size());
		System.out.println("periodearn size:" + sg.getPeriodearn().size());
		Collections.reverse(dates);
		Collections.reverse(peroidprice);
		Collections.reverse(periodratio);
		model.put("vutil", vutil);
		model.put("rstock", rstock);
		model.put("report", report);
		model.put("dates", dates);
		model.put("peroidprice", peroidprice);
		model.put("periodratio", sg.getPeriodearn());
		if (sg.getObjectprice() != 0) {
			model.put("change", sg.getEndprice() - sg.getObjectprice());
		}
		model.put("stockgain", sg);
		model.put("startdate", rstock.getCreatedate());

		StockGain zssg = sp.getZSGainByPeriod(DateTools.transformYYYYMMDDDateFromStr(rstock.getCreatedate()),
				DateTools.transformYYYYMMDDDate(new Date()));
		zssg.setStockname("沪深300指数");
		List<Float> zsperoidprice = zssg.getPeriodprice();
		List<Float> zsperiodratio = zssg.getPeriodratio();
		System.out.println("zsperiodratio size:" + zsperiodratio.size());
		System.out.println("getPeriodearn size:" + zssg.getPeriodearn().size());
		Collections.reverse(zsperoidprice);
		Collections.reverse(zsperiodratio);
		model.put("zsperoidprice", zsperoidprice);
		model.put("zsperiodratio", zssg.getPeriodearn());

		return "/admin/stockgain.htm";
	}

	@RequestMapping("/admin/recommendedit.do")
	public void editRecommend(HttpServletResponse response, RecommendStock recommendStock, HttpServletRequest request,
			ModelMap model) throws IOException {

		int ret = recommendStockDao.update(recommendStock);
		if (ret > 0) {
			resUtil.alertAndBack("修改研报抽取结果成功", 2, response);
		} else if (ret == 0) {
			if (recommendStock.getRecommendid() == null && recommendStock.getRecommendid().isEmpty()) {
				recommendStock.setRecommendid(ServerUtil.getid());
			}
			recommendStockDao.insert(recommendStock);
			resUtil.alertAndBack("修改研报抽取结果成功", 2, response);
		} else {
			resUtil.alertAndBack("修改研报抽取结果失败", 2, response);
		}
	}

}
