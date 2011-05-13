package com.caijing.web.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.caijing.dao.MasterDao;
import com.caijing.dao.MasterMessageDao;
import com.caijing.dao.UserrightDAO;
import com.caijing.domain.Master;
import com.caijing.domain.WebUser;
import com.caijing.util.Config;
import com.caijing.util.DateTools;

@Controller
@SessionAttributes({ "currWebUser" })
public class MasterMessageController {

	private static final Log logger = LogFactory.getLog(MasterMessageController.class);

	@Autowired
	@Qualifier("masterMessageDao")
	private MasterMessageDao masterMessageDao = null;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("userrightDAO")
	private UserrightDAO userrightDao = null;

	@RequestMapping("/master/online.do")
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

	@RequestMapping("/master/liveDetail.htm")
	public String showMessages(HttpServletResponse response,
			@RequestParam(value = "masterid", required = true) Integer masterid, HttpServletRequest request,
			ModelMap model) {
		Map map = (Map) config.getValue("groupid");
		Map propertys = (Map) map.get("" + masterid);
		logger.info("masterid: " + masterid + "  name:" + propertys.get("name"));
		model.put("mastername", propertys.get("name"));
		model.put("masterid", masterid);

		Master master = (Master) masterDao.select(masterid);
		List<Master> masters = masterDao.getAllMasters(0, 100);
		model.put("masterList", masters);
		model.put("master", master);
		return "/template/master/liveDetail.htm";
	}

	@RequestMapping("/master/index.htm")
	public String getMasters(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		logger.debug("userid:" + user.getUid());
		List<String> masterids = userrightDao.getMasteridsByUserid(user.getUid().trim());
		logger.debug("masterids:" + masterids.size());
		List<Master> masters = new ArrayList<Master>();
		for (String masterid : masterids) {
			Master master = (Master) masterDao.select(masterid);
			logger.debug("masterid:" + masterid);
			masters.add(master);
		}
		model.put("masters", masters);
		return "/template/master/masterList.htm";
	}
}
