package com.caijing.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.caijing.dao.UserDao;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.User;
import com.caijing.domain.WebUser;
import com.caijing.util.ServerUtil;
import com.caijing.util.TopicNameConfig;

@Controller
@SessionAttributes("currUser")
public class LoginController {
	@Autowired
	@Qualifier("userDao")
	private UserDao ibatisUserDao = null;

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@RequestMapping("/admin/login.do")
	public String showColomn(HttpServletResponse response,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "random", required = false) String random, HttpServletRequest request, ModelMap model) {
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
	public String topPage(@ModelAttribute("currUser") User user, ModelMap model) {
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
		//		List<Economist> economistList = economistDao.getAllEconomist();
		//		model.put("economistList", economistList);
		List<Analyzer> analyzerlist = analyzerDao.getStarAnalyzers();
		List<Analyzer> recommendanalyzerList = analyzerDao.getAnalyzersByAgency("安信证券");
		model.put("analyzerList", analyzerlist);
		System.out.println("recommendanalyzerList size:" + recommendanalyzerList.size());

		model.put("recommendanalyzerList", recommendanalyzerList);

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

	@RequestMapping("/reg/regist.do")
	public String regist(HttpServletResponse response, @RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password, HttpServletRequest request,
			ModelMap model) {
		WebUser user = new WebUser();
		user.setEmail(email);
		System.out.println("email : " + email);
		System.out.println("password : " + password);
		user.setPasswd(DigestUtils.md5Hex(password));
		user.setPtime(new Date());
		user.setUid(ServerUtil.getid());

		try {
			webUserDao.insert(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/index.html";
	}

	@RequestMapping("/user/login.do")
	public String webLogin(HttpServletResponse response,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "random", required = true) String random, HttpServletRequest request, ModelMap model) {
		User user = new User();
		String srandom = (String) request.getSession().getAttribute("random");
		System.out.println("srandom : " + srandom);

		System.out.println("random : " + random);
		try {
			if (random != null && random.equals(srandom)) {
				System.out.println("随即图验证成功！");
				if (webUserDao.identify(username, password)) {
					System.out.println("用户名验证成功！");
					user.setUsername(username);
					model.put("currUser", user);
					response.sendRedirect("/index.html");
					return null;
				}
			}
			response.sendRedirect("/user/err.html?login=true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/reg/reg.htm")
	public String reg(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/reg/reg.htm";
	}

	@RequestMapping("/user/myAccount.htm")
	public String account(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myAccount.htm";
	}

	@RequestMapping("/user/myRecharge.htm")
	public String recharge(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myRecharge.htm";
	}

	@RequestMapping("/user/myInfo.htm")
	public String myinfo(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myInfo.htm";
	}

	@RequestMapping("/user/myConsumer.htm")
	public String myconsumer(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myConsumer.htm";
	}

}
