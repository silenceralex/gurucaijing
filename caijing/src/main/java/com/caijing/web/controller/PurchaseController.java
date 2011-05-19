package com.caijing.web.controller;

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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.caijing.business.OrderManager;
import com.caijing.dao.ProductDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.WebUser;

@Controller
@SessionAttributes({ "currWebUser", "currRights" })
public class PurchaseController {

	@Autowired
	@Qualifier("orderManager")
	private OrderManager orderManager = null;

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("productDAO")
	private ProductDAO productDAO;

	private static final Log logger = LogFactory.getLog(PurchaseController.class);

	@RequestMapping(value = "/cart/products.htm")
	public String orderdetail(@ModelAttribute("currWebUser") WebUser user, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		//		logger.debug("orderid:" + orderid);
		//
		//		List<OrderPr> orderprs = orderManager.selectWithOrderPr(orderid).getOrderPrs();
		//		List<Product> products = new ArrayList<Product>();
		//		for (OrderPr orderpr : orderprs) {
		//
		//			Product p = (Product) productDAO.select(orderpr.getPid());
		//			p.setContinuedmonth(p.getContinuedmonth() * orderpr.getNum());
		//			products.add(p);
		//		}
		//		model.put("orderprs", orderprs);
		//		model.put("products", products);
		return "/template/cart/products.htm";
	}

}
