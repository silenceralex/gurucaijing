package com.caijing.dao;

import java.util.List;

import com.caijing.domain.OrderMeta;
import com.caijing.domain.OrderPr;
import com.caijing.util.CrudDao;

public interface OrderDao extends CrudDao {
	public OrderMeta selectWithOrderPr(Long orderid);

	public void insertOrderPr(OrderPr orderPr);

	public List<OrderMeta> getOrdersByUserid(String userid);
}
