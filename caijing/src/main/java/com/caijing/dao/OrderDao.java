package com.caijing.dao;

import com.caijing.domain.Order;
import com.caijing.domain.OrderPr;
import com.caijing.util.CrudDao;

public interface OrderDao extends CrudDao {
	public Order selectWithOrderPr(Long orderid);
	public void insertOrderPr(OrderPr orderPr);
}
