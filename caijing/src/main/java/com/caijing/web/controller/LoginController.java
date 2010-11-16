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
	public String showColomn(HttpServletResponse response, @RequestParam(value = "username", required = false)
	String username, @RequestParam(value = "password", required = false)
	String password, @RequestParam(value = "random", required = false)
	String random, HttpServletRequest request, ModelMap model) {
		User user = new User();
		String srandom = (String) request.getSession().getAttribute("random");
		System.out.println("srandom : " + srandom);

		System.out.println("random : " + random);
		try {
			if (ibatisUserDao.identify(username, password)) {
				System.out.println("用户名验证成功！");
				if (random != null && random.equals(srandom)) {
					System.out.println("随即图验证成功！");
					user.setUsername(username);
					model.put("currUser", user);
					response.sendRedirect("/admin/index.html");
					return null;
				}
			}
			response.sendRedirect("/admin/err.html?login=true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/admin/top.htm")
	public String topPage(@ModelAttribute("currUser")
	User user, ModelMap model) {
		model.put("currUser", user);
		return ("/admin/top.htm");
	}

	@RequestMapping("/admin/menu.htm")
	public String showMenu(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		User user = (User) request.getSession().getAttribute("currUser");
		Set<String> topicList = topicNameMap.getTopicNameMap().keySet();
		model.put("topicList", topicList);
		model.put("topicNameMap", topicNameMap);
		return "/admin/menu.htm";
	}

	@RequestMapping("/admin/menu2.htm")
	public String showMenu2(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		User user = (User) request.getSession().getAttribute("currUser");
		Set<String> topicList = topicNameMap.getTopicNameMap().keySet();
		model.put("topicList", topicList);
		model.put("topicNameMap", topicNameMap);
		List<Economist> economistList = economistDao.getAllEconomist();
		model.put("economistList", economistList);
		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		model.put("analyzerList", analyzerlist);
		return "/admin/menu2.htm";
	}

	@RequestMapping("/admin/logout.do")
	public void logout(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		HttpSession session = request.getSession();
		session.removeAttribute("operatebean");
		session.removeAttribute("JSONRPCBridge");
		response.sendRedirect("/admin/login.html");
		return;
	}
}
