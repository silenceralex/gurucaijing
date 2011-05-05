package com.caijing.web.controller;

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
import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;

@Controller
@SessionAttributes({"currWebUser","currRights"})
public class OrderController {

	@Autowired
	@Qualifier("orderManager")
	private OrderManager orderManager = null;

	private static final Log logger = LogFactory.getLog(OrderController.class);

	@RequestMapping(value = "/user/orderByRecharge.do", method = RequestMethod.POST)
	public boolean orderByRecharge(
			@ModelAttribute("currWebUser") WebUser user, 
			@ModelAttribute("currRights") List<Userright> currRights, 
			HttpServletResponse response,
			@RequestParam(value = "rechargeid", required = true) Long rechargeid,
			@RequestParam(value = "status", required = true) Integer status,
			HttpServletRequest request, ModelMap model) {
		String userid = user.getUid();
		try {
			if (status == 1) {
				logger.debug("user:" + user.getEmail() + "  recharge success!" + status);
				orderManager.orderByRecharge(userid, rechargeid);
				List<Userright> rights = orderManager.getUserrightsByUserid(userid);
				model.addAttribute("currRights", rights);
				return true;
			} else {
				logger.debug("user:" + user.getEmail() + "  recharge failed!" + status);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@RequestMapping(value = "/user/orderByRemain.do", method = RequestMethod.POST)
	public boolean orderByRemain(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "orderid", required = true) Long orderid, HttpServletRequest request, ModelMap model) {
		try {
			String userid = user.getUid();
			orderManager.orderByRemain(userid, orderid);
			List<Userright> rights = orderManager.getUserrightsByUserid(userid);
			model.addAttribute("currRights", rights);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@RequestMapping(value = "/user/productcart.do", method = RequestMethod.POST)
	public long saveOrder(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "jsondata", required = true) String jsondata, HttpServletRequest request, ModelMap model) {
		try {
			long orderid = -1;
			String userid = user.getUid();
			JSONArray products = JSONArray.fromObject(jsondata);
			if (products != null && products.size() != 0) {
				orderid = orderManager.saveOrder(userid, products);
			}
			return orderid;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
