package com.caijing.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.caijing.dao.ProductDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.WebUser;
import com.caijing.spider.BerkeleyDB;

@Controller
@SessionAttributes( { "currWebUser", "currRights" })
public class PurchaseController {

	@Autowired
	@Qualifier("orderManager")
	private OrderManager orderManager = null;

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("cartDB")
	private BerkeleyDB cartdb = null;;

	private static final Log logger = LogFactory
			.getLog(PurchaseController.class);

	@RequestMapping(value = "/cart/products.htm")
	public String orderdetail(@ModelAttribute("currWebUser")
	WebUser user, HttpServletResponse response, HttpServletRequest request,
			ModelMap model) {
		// logger.debug("orderid:" + orderid);
		//
		// List<OrderPr> orderprs =
		// orderManager.selectWithOrderPr(orderid).getOrderPrs();
		// List<Product> products = new ArrayList<Product>();
		// for (OrderPr orderpr : orderprs) {
		//
		// Product p = (Product) productDAO.select(orderpr.getPid());
		// p.setContinuedmonth(p.getContinuedmonth() * orderpr.getNum());
		// products.add(p);
		// }
		// model.put("orderprs", orderprs);
		// model.put("products", products);
		return "/template/cart/products.htm";
	}

	@RequestMapping(value = "/cart/save.do", method = RequestMethod.POST)
	public void saveCart(@ModelAttribute("currWebUser")
	WebUser user, HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "cartStr", required = true)
			String cartStr, @RequestParam(value = "userid", required = true)
			String userid) {
		String ret = "success";
		if (userid.equalsIgnoreCase(user.getEmail())) {
			logger.debug("put userid:" + userid + " cartstr:" + cartStr);
			cartdb.put(userid, cartStr);
		} else {
			ret = "failed by not login!";
		}
		response.setContentType("application/txt; charset=utf-8");
		try {
			response.getWriter().write(ret);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getInterface: ", e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/cart/get.do")
	public void getCart(@ModelAttribute("currWebUser")
	WebUser user, HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "userid", required = true)
			String userid) {
		String ret = null;
		if (userid.equalsIgnoreCase(user.getEmail())) {
			ret = cartdb.get(userid);
		}
		response.setContentType("application/txt; charset=utf-8");
		try {
			if (ret == null) {
				ret = "";
			}
			response.getWriter().write(ret);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getInterface: ", e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/cart/clean.do")
	public void cleanCart(@ModelAttribute("currWebUser")
	WebUser user, HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "userid", required = true)
			String userid) {
		String ret = "success";
		if (userid.equalsIgnoreCase(user.getEmail())) {
			if (!cartdb.deleteKey(userid)) {
				ret = "failed delete key";
			}
		}else{
			ret = "failed not login";
		}
		response.setContentType("application/txt; charset=utf-8");
		try {
			response.getWriter().write(ret);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getInterface: ", e);
			e.printStackTrace();
		}
	}

}
