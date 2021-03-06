package com.caijing.web.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.caijing.business.OrderManager;
import com.caijing.business.RechargeManager;
import com.caijing.dao.IndustryDao;
import com.caijing.dao.MasterDao;
import com.caijing.dao.OrderDao;
import com.caijing.dao.ProductDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Industry;
import com.caijing.domain.Master;
import com.caijing.domain.OrderMeta;
import com.caijing.domain.OrderPr;
import com.caijing.domain.Product;
import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;

@Controller
@SessionAttributes({ "currWebUser", "currRights" })
public class OrderController {

	@Autowired
	@Qualifier("orderManager")
	private OrderManager orderManager = null;

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("productDAO")
	private ProductDAO productDAO;

	@Autowired
	@Qualifier("industryDao")
	private IndustryDao industryDao;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao;


	private static final Log logger = LogFactory.getLog(OrderController.class);

	@RequestMapping(value = "/user/orderByRecharge.do", method = RequestMethod.POST)
	public void orderByRecharge(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "rechargeid", required = true) Long rechargeid,
			@RequestParam(value = "status", required = true) Integer status, HttpServletRequest request, ModelMap model) {
		String userid = user.getUid();
		try {
			if (status == 1) {
				logger.debug("user:" + user.getEmail() + "  recharge success!" + status);
				orderManager.orderByRecharge(userid, rechargeid);
				List<Userright> rights = orderManager.getUserrightsByUserid(userid);
				model.addAttribute("currRights", rights);
				response.sendRedirect("/user/myConsumer.htm?isEmpty=1");
			} else {
				logger.debug("user:" + user.getEmail() + "  recharge failed!" + status);
				response.sendRedirect("/user/myConsumer.htm");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/user/orderByRemain.do", method = RequestMethod.POST)
	public void orderByRemain(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "orderid", required = true) Long orderid, HttpServletRequest request, ModelMap model) {
		try {
			String userid = user.getUid();
			orderManager.orderByRemain(userid, orderid);
			List<Userright> rights = orderManager.getUserrightsByUserid(userid);
			model.addAttribute("currRights", rights);
			response.sendRedirect("/user/myConsumer.htm?isEmpty=1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/user/productcart.do", method = RequestMethod.POST)
	public String saveOrder(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "param", required = true) String jsondata, HttpServletRequest request, ModelMap model) {
		logger.debug("user id:" + user.getUid() + "  jsondata:" + jsondata);
		try {
			OrderMeta order = null;
			String userid = user.getUid();
			JSONArray products = JSONArray.fromObject(jsondata);
			if (products != null && products.size() != 0) {
				order = orderManager.saveOrder(userid, products);
			}
			user = (WebUser) webUserDao.select(user.getUid());
			if (user.getRemain() >= order.getCost()) {
				model.put("useRemain", 1);
			} else {
				model.put("useRemain", 0);
			}
			model.put("user", user);
			model.put("orderid", order.getOrderid());
			return "/template/cart/pay.htm";
		} catch (Exception e) {
			e.printStackTrace();
			return "/template/cart/pay.htm";
		}
	}

	@RequestMapping(value = "/user/orderDetail.htm")
	public String orderdetail(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "orderid", required = true) Long orderid, HttpServletRequest request, ModelMap model) {
		logger.debug("orderid:" + orderid);

		OrderMeta order = orderManager.selectWithOrderPr(orderid);
		List<Product> products = new ArrayList<Product>();
		for (OrderPr orderpr : order.getOrderPrs()) {

			Product p = (Product) productDAO.select(orderpr.getPid());

			p.setContinuedmonth(p.getContinuedmonth() * orderpr.getNum());
			logger.debug("Product url:" + p.getUrl());
			if (p.getIsIndustry() == 1) {
				String url = p.getUrl().replace("$industryid", orderpr.getIndustryid());
				p.setUrl(url);
				Industry object = (Industry) industryDao.select(orderpr.getIndustryid());
				p.setIndustry(object.getIndustryname());
			} else if (p.getPid() == 10) {
				//�ݸ�ֱ����
				String url = p.getUrl().replace("$masterid", orderpr.getIndustryid());
				p.setUrl(url);
				Master master = (Master) masterDao.select(orderpr.getIndustryid());
				p.setIndustry(master.getMastername());
			}
			products.add(p);
		}
		model.put("order", order);
		model.put("products", products);
		return "/template/user/orderDetail.htm";
	}
}
