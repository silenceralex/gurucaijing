package com.caijing.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
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
				System.out.println("�û�����֤�ɹ���");
				if (random != null && random.equals(srandom)) {
					System.out.println("�漴ͼ��֤�ɹ���");
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
		List<Analyzer> recommendanalyzerList = analyzerDao.getAnalyzersByAgency("����֤ȯ");
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
			@RequestParam(value = "passwd", required = true) String password,
			@RequestParam(value = "nickname", required = true) String nickname, HttpServletRequest request,
			ModelMap model) {
		WebUser user = new WebUser();
		user.setEmail(email);
		System.out.println("email : " + email);
		System.out.println("password : " + password);
		user.setPassword(DigestUtils.md5Hex(password));
		user.setPtime(new Date());
		user.setUid(ServerUtil.getid());
		user.setNickname(nickname);

		try {
			webUserDao.insert(user);
			request.getSession().setAttribute("currWebUser", user);
			model.put("user", user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/template/user/myInfo.htm";
	}

	@RequestMapping("/user/login.do")
	public String webLogin(HttpServletResponse response, @RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "random", required = true) String random, HttpServletRequest request, ModelMap model) {
		//		User user = new User();
		String srandom = (String) request.getSession().getAttribute("random");
		System.out.println("srandom : " + srandom);

		System.out.println("random : " + random);
		try {
			if (random != null && random.equals(srandom)) {
				System.out.println("�漴ͼ��֤�ɹ���");
				if (webUserDao.identify(email, password)) {
					System.out.println("�û�����֤�ɹ���");
					WebUser user = webUserDao.getUserByEmail(email);
					System.out.println("nickname:" + user.getNickname());
					request.getSession().setAttribute("currWebUser", user);
					model.put("currWebUser", user);
					response.setHeader("Cookie", "");
					Cookie cookie = new Cookie("useremail", user.getEmail());
					cookie.setDomain("www.51gurus.com");
					cookie.setMaxAge(36000);
					response.addCookie(cookie);
					Cookie cookie2 = new Cookie("userid", user.getUid());
					cookie2.setDomain("51gurus.com");
					cookie2.setMaxAge(36000);
					response.addCookie(cookie2);
					response.sendRedirect("/user/myInfo.htm");
					return null;
				}
			}
			response.sendRedirect("/user/err.html?login=true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/user/logout.do")
	public void weblogout(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		HttpSession session = request.getSession();
		session.removeAttribute("currWebUser");
		response.sendRedirect("/user/login.htm");
		return;
	}

	@RequestMapping("/user/update.do")
	public void userupdate(HttpServletResponse response,
			@RequestParam(value = "nickname", required = true) String nickname,
			@RequestParam(value = "gender", required = true) int gender,
			@RequestParam(value = "province", required = true) String province,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "mobile", required = true) String mobile, HttpServletRequest request, ModelMap model) {
		WebUser user = (WebUser) request.getSession().getAttribute("currWebUser");
		System.out.println("nickname : " + nickname);
		System.out.println("city : " + city);
		System.out.println("province : " + province);
		System.out.println("gender : " + gender);
		System.out.println("email : " + user.getEmail());
		System.out.println("mobile : " + mobile);
		user.setCity(city);
		user.setGender(gender);
		user.setMobile(mobile);
		user.setProvince(province);
		user.setNickname(nickname);
		user.setLmodify(new Date());

		int num = webUserDao.update(user);
		if (num > 0) {
			try {
				request.getSession().setAttribute("currWebUser", user);
				model.put("user", user);
				response.setContentType("text/html;charset=GBK");
				response.getWriter().write("<script>alert('�������ϳɹ�!');window.location='/user/myInfo.htm';</script>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				model.put("user", user);
				response.setContentType("text/html;charset=GBK");
				response.getWriter().write("<script>alert('��������ʧ��!');window.location='/usr/myInfo.htm';</script>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping("/user/changepasswd.do")
	public void changepasswd(HttpServletResponse response,
			@RequestParam(value = "oldPass", required = true) String oldPass,
			@RequestParam(value = "newPass", required = true) String newPass,
			@RequestParam(value = "newPass2", required = true) String newPass2, HttpServletRequest request,
			ModelMap model) {
		WebUser user = (WebUser) request.getSession().getAttribute("currWebUser");
		if (user.getPassword().equals(DigestUtils.md5Hex(oldPass))) {
			if (newPass.endsWith(newPass2)) {
				user.setPassword(DigestUtils.md5Hex(newPass));
				user.setLmodify(new Date());
				webUserDao.update(user);
				try {
					response.setContentType("text/html;charset=GBK");
					response.getWriter()
							.write("<script>alert('��������ɹ�!');window.location='" + request.getHeader("REFERER")
									+ "';</script>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			response.setContentType("text/html;charset=GBK");
			response.getWriter().write(
					"<script>alert('��������ʧ�ܣ�');window.location='" + request.getHeader("REFERER") + "';</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/reg/reg.htm")
	public String reg(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/reg/userreg.htm";
	}

	@RequestMapping("/user/myAccount.htm")
	public String account(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myAccount.htm";
	}

	@RequestMapping("/user/myInfo.htm")
	public String myinfo(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		model.put("user", request.getSession().getAttribute("currWebUser"));
		return "/template/user/myInfo.htm";
	}

	@RequestMapping("/user/myConsumer.htm")
	public String myconsumer(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/user/myConsumer.htm";
	}

	@RequestMapping("/user/login.htm")
	public String login(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/template/reg/login.htm";
	}

}
