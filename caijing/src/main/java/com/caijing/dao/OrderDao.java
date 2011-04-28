package com.caijing.dao;

import com.caijing.domain.OrderPr;
import com.caijing.util.CrudDao;

public interface OrderDao extends CrudDao {
	
	public void insertOrderPr(OrderPr orderPr);
}
