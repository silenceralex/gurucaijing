package com.caijing.business.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.caijing.business.OrderManager;
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
		String userid = null;
		long rechargeid = 0L;
		long orderid = 0L;
		target.orderByRecharge(userid, rechargeid, orderid);
	}

	@Test
	public void orderByRemain_A$String$long() throws Exception {
		String userid = "71IO1BPO";
		long orderid = 1111L;
		target.orderByRemain(userid, orderid);
	}
	
	@Test
	public void saveUserright_A$String$long$List() throws Exception {
		String userid = "71IO1BPO";
		long orderid = 1111L;
		List<OrderPr> orderPrs = new ArrayList<OrderPr>();
		OrderPr orderpr = new OrderPr();
		orderpr.setPid(11);
		orderPrs.add(orderpr);
		target.saveUserright(userid, orderid, orderPrs);
	}

	@Test
	public void getUserrightsByUserid_A$String() throws Exception {
		String userid = "71IO1BPO";
		List<Userright> actual = target.getUserrightsByUserid(userid);
		System.out.println("right size: "+actual.size());
		assertTrue(actual.size()>0);
	}

}
