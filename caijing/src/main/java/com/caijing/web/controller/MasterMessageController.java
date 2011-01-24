package com.caijing.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.MasterMessageDao;
import com.caijing.util.Config;
import com.caijing.util.DateTools;

@Controller
public class MasterMessageController {

	private static final Log logger = LogFactory.getLog(MasterMessageController.class);

	@Autowired
	@Qualifier("masterMessageDao")
	private MasterMessageDao masterMessageDao = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@RequestMapping("/online.do")
	public void showMessages(HttpServletResponse response,
			@RequestParam(value = "masterid", required = true) Integer masterid,
			@RequestParam(value = "start", required = false) Integer start, HttpServletRequest request, ModelMap model) {
		if (start == null) {
			start = 0;
		}
		logger.info("masterid: " + masterid + "  start:" + start);
		String date = DateTools.transformYYYYMMDDDate(new Date());
		List<Map> maps = masterMessageDao.getMessagesFrom(masterid, date, start);
		String messageJson = JSONArray.fromObject(maps).toString();
		try {
			logger.info("messageJson: " + messageJson);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(messageJson);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("GetshowMessages Error!!! " + e.getMessage());
		}
	}

	@RequestMapping("/liveDetail.htm")
	public String showMessages(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		model.put("mastername", "»Û¸ûË¼");
		return "/template/liveDetail.htm";
		//		model.put("vutil", vutil);
	}
}
