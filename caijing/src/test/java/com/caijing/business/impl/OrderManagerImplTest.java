package com.caijing.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import com.caijing.business.OrderManager;
import com.caijing.domain.OrderMeta;
import com.caijing.domain.OrderPr;
import com.caijing.domain.Userright;
import com.caijing.util.ContextFactory;

public class OrderManagerImplTest {

	private OrderManager target;

	@Before
	public void setUp() throws Exception {
		target = (OrderManager) ContextFactory.getBean("orderManager");
	}

	@Test
	public void type() throws Exception {
		assertNotNull(OrderManagerImpl.class);
	}

	@Test
	public void instantiation() throws Exception {
		assertNotNull(target);
	}

	@Test
	public void orderByRecharge_A$String$long$long() throws Exception {
		String userid = "71IO1BPO";
		long rechargeid = 13870556080242688l;
		target.orderByRecharge(userid, rechargeid);
	}

	@Test
	public void orderByRemain_A$String$long() throws Exception {
		String userid = "71IO1BPO";
		long orderid = 0;
		target.orderByRemain(userid, orderid);
	}

	@Test
	public void saveUserright_A$String$long$List() throws Exception {
		String userid = "71IO1BPO";
		long orderid = 0;
		List<OrderPr> orderPrs = new ArrayList<OrderPr>();
		OrderPr orderpr1 = new OrderPr();
		orderpr1.setPid(11);
		orderpr1.setIndustryid("1");
		orderPrs.add(orderpr1);

		OrderPr orderpr2 = new OrderPr();
		orderpr2.setPid(11);
		orderpr2.setIndustryid("1");
		orderPrs.add(orderpr2);

		target.saveUserright(userid, orderid, orderPrs);
	}

	@Test
	public void getUserrightsByUserid_A$String() throws Exception {
		String userid = "71IO1BPO";
		List<Userright> actual = target.getUserrightsByUserid(userid);
		System.out.println("right size: " + actual.size());
		assertTrue(actual.size() > 0);
	}

	@Test
	public void saveOrder_A$String$JSONArray() throws Exception {
		String userid = "71IO1BPO";
		JSONObject product1 = new JSONObject().element("productid", "11").element("num", "2")
				.element("industryid", "1");
		JSONObject product2 = new JSONObject().element("productid", "12").element("num", "2")
				.element("industryid", "2");

		JSONArray products = new JSONArray().element(product1).element(product2);
		System.out.println(products);

		OrderMeta order = target.saveOrder(userid, products);
		Long expected = 0L;
		assertEquals(expected, order.getOrderid());
	}
}
