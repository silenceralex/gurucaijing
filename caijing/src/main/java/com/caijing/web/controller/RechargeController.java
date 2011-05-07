package com.caijing.web.controller;

import java.util.List;

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

import com.caijing.business.RechargeManager;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Recharge;
import com.caijing.domain.WebUser;

@Controller
@SessionAttributes("currWebUser")
public class RechargeController {

	@Autowired
	@Qualifier("rechargeManager")
	private RechargeManager rechargeManager = null;

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	private static final Log logger = LogFactory.getLog(RechargeController.class);

	@RequestMapping(value = "/user/recharge.do", method = RequestMethod.POST)
	public String recharge(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "cash", required = true) Float cash,
			@RequestParam(value = "orderid", required = false) Long orderid,
			@RequestParam(value = "type", required = true) Integer type, HttpServletRequest request, ModelMap model) {
		try {
			Recharge recharge = rechargeManager.recharge(user.getUid(), type, cash, orderid);
			model.put("recharge", recharge);
			model.put("user", user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/template/user/rechargeCallback.htm";
	}

	@RequestMapping(value = "/user/rconfirm.do", method = RequestMethod.POST)
	public String rechargeConfirm(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			@RequestParam(value = "rcid", required = true) Long rcid,
			@RequestParam(value = "cash", required = true) Float cash,
			@RequestParam(value = "status", required = true) Integer status, HttpServletRequest request, ModelMap model) {
		try {
			if (status == 1) {
				logger.debug("user:" + user.getEmail() + "  recharge success!" + status);
			} else {
				logger.debug("user:" + user.getEmail() + "  recharge failed!" + status);
			}
			user = rechargeManager.confirm(rcid, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Recharge> recharges = rechargeManager.getRechargeByUserid(user.getUid());
		Float total = rechargeManager.getTotalByUserid(user.getUid());
		model.put("user", user);
		model.put("recharges", recharges);
		model.put("total", total);
		return "/template/user/myRecharge.htm";
	}

	@RequestMapping("/user/recharge.htm")
	public String reg(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		logger.debug("user:" + user.getEmail());
		model.put("user", user);
		return "/template/user/recharge.htm";
	}

	@RequestMapping("/user/myRecharge.htm")
	public String recharge(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		try {
			logger.debug("user:" + user.getEmail());
			List<Recharge> recharges = rechargeManager.getRechargeByUserid(user.getUid());
			Float total = rechargeManager.getTotalByUserid(user.getUid());
			logger.debug("total:" + total);
			//取得最新的remain值
			user = (WebUser) webUserDao.select(user.getUid());
			model.put("user", user);
			model.put("recharges", recharges);
			model.put("total", total);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/template/user/myRecharge.htm";
	}

	@RequestMapping("/cart/myCart.htm")
	public String cart(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		logger.debug("user:" + user.getEmail());
		model.put("user", user);
		return "/template/cart/myCart.htm";
	}

	@RequestMapping("/cart/pay.htm")
	public String pay(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		logger.debug("user:" + user.getEmail());
		model.put("user", user);
		return "/template/cart/pay.htm";
	}
}
