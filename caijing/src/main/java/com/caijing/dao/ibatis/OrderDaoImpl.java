package com.caijing.dao.ibatis;

import com.caijing.dao.OrderDao;
import com.caijing.domain.Order;
import com.caijing.domain.OrderPr;
import com.caijing.util.CrudDaoDefault;

public class OrderDaoImpl extends CrudDaoDefault implements OrderDao {
	
	public Order selectWithOrderPr(Long orderid){
		return (Order) getSqlMapClientTemplate().queryForObject(this.getNameSpace()+".selectWithOrderPr", orderid);
	}

	@Override
	public void insertOrderPr(OrderPr orderPr) {
		getSqlMapClientTemplate().insert(this.getNameSpace()+".insertOrderPr", orderPr);
	}
}
