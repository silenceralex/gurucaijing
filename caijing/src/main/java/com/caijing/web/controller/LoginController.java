package com.caijing.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
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
import org.springframework.web.bind.support.SessionStatus;

import com.caijing.business.RechargeManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.IndustryDao;
import com.caijing.dao.MasterDao;
import com.caijing.dao.OrderDao;
import com.caijing.dao.ProductDAO;
import com.caijing.dao.UserDao;
import com.caijing.dao.UserrightDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.Industry;
import com.caijing.domain.Master;
import com.caijing.domain.OrderMeta;
import com.caijing.domain.Product;
import com.caijing.domain.User;
import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;
import com.caijing.mail.GmailSenderAndFetch;
import com.caijing.spider.BerkeleyDB;
import com.caijing.util.DateTools;
import com.caijing.util.ServerUtil;
import com.caijing.util.TopicNameConfig;

@Controller
@SessionAttributes({ "currWebUser", "currRights" })
public class LoginController {

	private static final Log logger = LogFactory.getLog(LoginController.class);
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

	@Autowired
	@Qualifier("productDAO")
	private ProductDAO productDAO;

	@Autowired
	@Qualifier("userrightDAO")
	private UserrightDAO userrightDao = null;

	@Autowired
	@Qualifier("industryDao")
	private IndustryDao industryDao;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao;

	@Autowired
	@Qualifier("rechargeManager")
	private RechargeManager rechargeManager = null;

	@Autowired
	@Qualifier("orderDao")
	private OrderDao orderDao;

	@Autowired
	@Qualifier("cartDB")
	private BerkeleyDB cartDB;

	@RequestMapping("/admin/login.do")
	public String showColomn(
			HttpServletResponse response,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "random", required = false) String random,
			HttpServletRequest request, ModelMap model) {
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
		// List<Economist> economistList = economistDao.getAllEconomist();
		// model.put("economistList", economistList);
		List<Analyzer> analyzerlist = analyzerDao.getStarAnalyzers();
		List<Analyzer> recommendanalyzerList = analyzerDao
				.getAnalyzersByAgency("安信证券");
		model.put("analyzerList", analyzerlist);
		System.out.println("recommendanalyzerList size:"
				+ recommendanalyzerList.size());

		model.put("recommendanalyzerList", recommendanalyzerList);

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

	@RequestMapping("/reg/validate.do")
	public void activate(
			HttpServletResponse response,
			@RequestParam(value = "uid", required = true) String uid,
			@RequestParam(value = "validate", required = true) String validateCode,
			HttpServletRequest request) throws IOException {
		logger.debug("uid:" + uid + "   validateCode=" + validateCode);
		String code = cartDB.get(ServerUtil.getUserKey(uid));
		if (validateCode.equals(code)) {
			webUserDao.activate(uid);
			logger.debug("update webuser status");
			response.setContentType("text/html;charset=GBK");
			response.getWriter()
					.print("<script>alert('您已经成功激活了在51gurus网站的账号，请您在跳转的页面登录访问！');self.location='/';</script>");
			response.getWriter().flush();
		} else {
			response.setContentType("text/html;charset=GBK");
			response.getWriter()
					.print("<script>alert('您的登录验证未成功激活，请通过注册邮箱收到的邮件链接激活账户！如仍有问题，与我们管理员取得联系！');self.location='/';</script>");
			response.getWriter().flush();
		}
	}

	@RequestMapping("/reg/regist.do")
	public void regist(HttpServletResponse response,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "passwd", required = true) String password,
			@RequestParam(value = "nickname", required = true) String nickname,
			HttpServletRequest request, ModelMap model) {
		WebUser user = new WebUser();
		user.setEmail(email);
		System.out.println("email : " + email);
		// System.out.println("password : " + password);
		user.setPassword(DigestUtils.md5Hex(password));
		user.setPtime(new Date());
		String uid = ServerUtil.getid();
		user.setUid(uid);
		user.setNickname(nickname);

		// 将验证码存储于bdb里
		String validate = ServerUtil.getid("v");
		cartDB.put(ServerUtil.getUserKey(uid), validate);
		String link = "http://51gurus.com/reg/validate.do?uid=" + uid
				+ "&validate=" + validate;

		GmailSenderAndFetch mail = new GmailSenderAndFetch();
		try {
			mail.sendRegistMail(email, link);
		} catch (AddressException e1) {
			e1.printStackTrace();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		logger.debug("send email to :" + email);
		try {
			webUserDao.insert(user);
			// request.getSession().setAttribute("currWebUser", user);
			// setCookie(user, response);
			// model.put("user", user);
			response.setContentType("text/html;charset=GBK");
			response.getWriter()
					.print("<script>alert('注册成功！已经给您发送了激活邮件，请登录邮箱点击相应链接激活注册账号！');self.location='/';</script>");
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return "/template/user/myInfo.htm";
	}

	@RequestMapping("/user/login.do")
	public String webLogin(HttpServletResponse response,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "random", required = true) String random,
			HttpServletRequest request, ModelMap model) {
		// User user = new User();
		String srandom = (String) request.getSession().getAttribute("random");
		System.out.println("srandom : " + srandom);

		System.out.println("random : " + random);
		int retint = webUserDao.identify(email, password);
		try {
			if (random != null && random.equals(srandom)) {
				System.out.println("随即图验证成功！");
				if (retint == 1) {
					System.out.println("用户名验证成功！");
					WebUser user = webUserDao.getUserByEmail(email);
					System.out.println("nickname:" + user.getNickname());
					request.getSession().setAttribute("currWebUser", user);
					List<Userright> currRights = userrightDao
							.getUserrightByUserid(user.getUid());
					request.getSession().setAttribute("currRights", currRights);
					model.put("currWebUser", user);
					setCookie(user, response);
					response.sendRedirect("/user/myAccount.htm");
					return null;
				} else if (retint == 0) {
					response.setContentType("text/html;charset=GBK");
					response.getWriter()
							.print("<script>alert('您的账号尚未激活，请您到注册邮箱激活后再登陆！');top.location='/';</script>");
					response.getWriter().flush();
					return null;
				}
			}
			response.sendRedirect("/template/user/err.html?login=true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/user/weblogin.do")
	public void webLogin2(HttpServletResponse response,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			HttpServletRequest request, ModelMap model) {
		int retint = webUserDao.identify(email, password);
		try {
			if (retint == 1) {
				System.out.println("用户名验证成功！");
				WebUser user = webUserDao.getUserByEmail(email);
				System.out.println("nickname:" + user.getNickname());
				request.getSession().setAttribute("currWebUser", user);
				model.put("currWebUser", user);
				List<Userright> currRights = userrightDao
						.getUserrightByUserid(user.getUid());
				request.getSession().setAttribute("currRights", currRights);
				setCookie(user, response);
				String referer = request.getHeader("Referer");
				logger.debug("referer：" + referer);
				if (referer
						.equalsIgnoreCase("http://www.51gurus.com/user/login.htm")) {
					response.setContentType("text/html;charset=GBK");
					response.getWriter()
							.print("<script>alert('您已经成功登录了51gurus网站，即将跳转至登录前的页面！');self.history.go(-2);</script>");
					response.getWriter().flush();
				} else {
					response.setContentType("text/html;charset=GBK");
					response.getWriter().print(
							"<script>alert('您已经成功登录了51gurus网站，即将跳转至登录前的页面！');self.location='"
									+ referer + "';</script>");
					response.getWriter().flush();
				}
			} else if (retint == 0) {
				response.setContentType("text/html;charset=GBK");
				response.getWriter()
						.print("<script>alert('您的账号尚未激活，请您到注册邮箱激活后再登陆！');top.location='/';</script>");
				response.getWriter().flush();
			} else {
				response.sendRedirect("/template/user/err.html?login=true");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/user/logout.do")
	public void weblogout(HttpServletResponse response, ModelMap model,
			SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		HttpSession session = request.getSession();
		session.removeAttribute("currWebUser");
		setCookie(null, response);
		response.setContentType("text/html;charset=GBK");
		response.getWriter()
				.print("<script>alert('您已经安全退出了51gurus网站，即将跳转至首页！');top.location='/';</script>");
		response.getWriter().flush();

		return;
	}

	@RequestMapping("/user/update.do")
	public void userupdate(HttpServletResponse response,
			@RequestParam(value = "nickname", required = true) String nickname,
			@RequestParam(value = "gender", required = true) int gender,
			@RequestParam(value = "province", required = true) String province,
			@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "mobile", required = true) String mobile,
			HttpServletRequest request, ModelMap model) {
		WebUser user = (WebUser) request.getSession().getAttribute(
				"currWebUser");
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
				response.getWriter()
						.write("<script>alert('更改资料成功!');window.location='/user/myInfo.htm';</script>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				model.put("user", user);
				response.setContentType("text/html;charset=GBK");
				response.getWriter()
						.write("<script>alert('更改资料失败!');window.location='/usr/myInfo.htm';</script>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping("/user/changepasswd.do")
	public void changepasswd(HttpServletResponse response,
			@RequestParam(value = "oldPass", required = true) String oldPass,
			@RequestParam(value = "newPass", required = true) String newPass,
			@RequestParam(value = "newPass2", required = true) String newPass2,
			HttpServletRequest request, ModelMap model) {
		WebUser user = (WebUser) request.getSession().getAttribute(
				"currWebUser");
		if (user.getPassword().equals(DigestUtils.md5Hex(oldPass))) {
			if (newPass.endsWith(newPass2)) {
				user.setPassword(DigestUtils.md5Hex(newPass));
				user.setLmodify(new Date());
				webUserDao.update(user);
				try {
					response.setContentType("text/html;charset=GBK");
					response.getWriter().write(
							"<script>alert('更改密码成功!');window.location='"
									+ request.getHeader("REFERER")
									+ "';</script>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			response.setContentType("text/html;charset=GBK");
			response.getWriter().write(
					"<script>alert('更改密码失败！');window.location='"
							+ request.getHeader("REFERER") + "';</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @RequestMapping("/user/myAccount.htm") public String
	 *                                        account(@ModelAttribute(
	 *                                        "currWebUser") WebUser user,
	 *                                        HttpServletResponse response,
	 *                                        HttpServletRequest request,
	 *                                        ModelMap model) { Float total =
	 *                                        rechargeManager
	 *                                        .getTotalByUserid(user.getUid());
	 *                                        List<Product> products =
	 *                                        productDAO.getAllProduct(); int
	 *                                        times =
	 *                                        rechargeManager.getCountByUserid
	 *                                        (user.getUid()); //取得最新的remain值
	 *                                        user = (WebUser)
	 *                                        webUserDao.select(user.getUid());
	 *                                        String ptime =
	 *                                        DateTools.transformYYYYMMDDDate
	 *                                        (user.getPtime());
	 *                                        model.put("ptime", ptime);
	 *                                        model.put("productList",
	 *                                        products); model.put("total",
	 *                                        total); model.put("times", times);
	 *                                        model.put("user", user); return
	 *                                        "/template/user/myAccount.htm"; }
	 */

	@RequestMapping("/user/myAccount.htm")
	public String account(@ModelAttribute("currWebUser") WebUser user,
			HttpServletResponse response, HttpServletRequest request,
			ModelMap model) {
		Float total = rechargeManager.getTotalByUserid(user.getUid());
		// List<Product> products = productDAO.getAllProduct();
		List<HashMap<String, Object>> myProductList = new LinkedList<HashMap<String, Object>>();
		List<Userright> currRights = userrightDao.getUserrightByUserid(user
				.getUid());
		if (currRights != null) {
			for (Userright userright : currRights) {
				HashMap<String, Object> myProduct = new HashMap<String, Object>();
				Product product = (Product) productDAO.select(userright
						.getPid());
				myProduct.put("productName", product.getName());
				String todate = DateTools.transformYYYYMMDDDate(userright
						.getTodate());
				myProduct.put("todate", todate);
				myProduct.put("url", product.getUrl());
				String unknownid = userright.getIndustryid(); // industryid or
																// masterid
				if (unknownid != null) {
					if (product.getPid() == 10) {
						Master master = (Master) masterDao.select(unknownid);
						String masterName = master.getMastername();
						myProduct.put("masterName", masterName);
						myProduct.put("url",
								product.getUrl()
										.replace("$masterid", unknownid));
					} else if (product.getIsIndustry() == 1) {
						Industry industry = (Industry) industryDao
								.select(unknownid);
						String industryName = industry.getIndustryname();
						myProduct.put("industryName", industryName);
						myProduct.put(
								"url",
								product.getUrl().replace("$industryid",
										unknownid));
					}
				}
				myProductList.add(myProduct);
			}
		}

		int times = rechargeManager.getCountByUserid(user.getUid());
		// 取得最新的remain值
		user = (WebUser) webUserDao.select(user.getUid());
		String ptime = DateTools.transformYYYYMMDDDate(user.getPtime());
		model.put("ptime", ptime);
		// String myProductsJson = JSONArray.fromObject(myProducts).toString();
		model.put("myProductList", myProductList);
		// model.put("productList", products);
		model.put("total", total);
		model.put("times", times);
		model.put("user", user);
		return "/template/user/myAccount.htm";
	}

	@RequestMapping("/user/myInfo.htm")
	public String myinfo(HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		model.put("user", request.getSession().getAttribute("currWebUser"));
		return "/template/user/myInfo.htm";
	}

	@RequestMapping("/reg/reg.htm")
	public String reg(HttpServletResponse response, HttpServletRequest request,
			ModelMap model) {
		return "/template/reg/userreg.htm";
	}

	@RequestMapping("/user/myConsumer.htm")
	public String myconsumer(@ModelAttribute("currWebUser") WebUser user,
			@RequestParam(value = "isEmpty", required = false) Integer isEmpty,
			HttpServletResponse response, HttpServletRequest request,
			ModelMap model) {
		// 取得最新的remain值
		user = (WebUser) webUserDao.select(user.getUid());
		String ptime = DateTools.transformYYYYMMDDDate(user.getPtime());
		Float total = rechargeManager.getTotalByUserid(user.getUid());
		List<OrderMeta> orderList = orderDao.getOrdersByUserid(user.getUid());
		logger.debug("orderList:" + orderList.size());
		model.put("ptime", ptime);
		model.put("orderList", orderList);
		model.put("total", total);
		model.put("user", user);
		if (isEmpty != null && isEmpty == 1) {
			logger.debug("isEmpty:" + isEmpty);
			model.put("isEmpty", isEmpty);
		}
		return "/template/user/myConsumer.htm";
	}

	@RequestMapping("/user/login.htm")
	public String login(HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		return "/template/reg/login.htm";
	}

	private void setCookie(WebUser user, HttpServletResponse response) {
		if (user != null) {
			Cookie cookie = new Cookie("useremail", user.getEmail());
			// cookie.setMaxAge(36000);
			cookie.setPath("/");
			response.addCookie(cookie);
			Cookie cookie2 = new Cookie("userid", user.getUid());
			cookie.setPath("/");
			// cookie2.setMaxAge(36000);
			response.addCookie(cookie2);
		} else {
			Cookie cookie = new Cookie("useremail", null);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			Cookie cookie2 = new Cookie("userid", null);
			cookie.setPath("/");
			cookie2.setMaxAge(0);
			response.addCookie(cookie2);
		}
	}

}
