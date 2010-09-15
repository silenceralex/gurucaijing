package com.caijing.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.EconomistDao;
import com.caijing.dao.UserDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.Economist;
import com.caijing.domain.User;
import com.caijing.util.TopicNameConfig;

@Controller
@SessionAttributes("currUser")
public class LoginController {
	@Autowired
	@Qualifier("userDao")
	private UserDao ibatisUserDao = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@Autowired
	@Qualifier("economistDao")
	private EconomistDao economistDao = null;

	@RequestMapping("/admin/login.do")
	public void showColomn(HttpServletResponse response,
			@RequestParam(value = "username", required = false)
			String username,
			@RequestParam(value = "password", required = false)
			String password, HttpServletRequest request, ModelMap model) {
		User user = new User();
		if (ibatisUserDao.identify(username, password)) {
			user.setUsername(username);
		}
		model.put("currUser", user);
		try {
			response.sendRedirect("/admin/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/admin/top.htm")
	public String topPage(@ModelAttribute("currUser")
	User user, ModelMap model) {
		model.put("currUser", user);
		return ("/admin/top.htm");
	}

	@RequestMapping("/admin/menu.htm")
	public String showMenu(HttpServletResponse response, ModelMap model,
			HttpServletRequest request) throws IOException, Exception {
		User user = (User) request.getSession().getAttribute("currUser");
		Set<String> topicList = topicNameMap.getTopicNameMap().keySet();
		model.put("topicList", topicList);
		model.put("topicNameMap", topicNameMap);
		return "/admin/menu.htm";
	}

	@RequestMapping("/admin/menu2.htm")
	public String showMenu2(HttpServletResponse response, ModelMap model,
			HttpServletRequest request) throws IOException, Exception {
		User user = (User) request.getSession().getAttribute("currUser");
		Set<String> topicList = topicNameMap.getTopicNameMap().keySet();
		model.put("topicList", topicList);
		model.put("topicNameMap", topicNameMap);
		List<Economist> economistList = economistDao.getAllEconomist();
		model.put("economistList", economistList);
		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		model.put("analyzerlist", analyzerlist);
		return "/admin/menu2.htm";
	}

	@RequestMapping("/admin/logout.do")
	public void logout(HttpServletResponse response, ModelMap model,
			SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		HttpSession session = request.getSession();
		session.removeAttribute("operatebean");
		session.removeAttribute("JSONRPCBridge");
		response.sendRedirect("/admin/login.html");
		return;
	}
}
