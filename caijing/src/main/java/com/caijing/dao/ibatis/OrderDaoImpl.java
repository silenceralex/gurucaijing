package com.caijing.dao.ibatis;

import com.caijing.dao.OrderDao;
import com.caijing.domain.OrderMeta;
import com.caijing.domain.OrderPr;
import com.caijing.util.CrudDaoDefault;

public class OrderDaoImpl extends CrudDaoDefault implements OrderDao {
	
	public OrderMeta selectWithOrderPr(Long orderid){
		return (OrderMeta) getSqlMapClientTemplate().queryForObject(this.getNameSpace()+".selectWithOrderPr", orderid);
	}

	@Override
	public void insertOrderPr(OrderPr orderPr) {
		getSqlMapClientTemplate().insert(this.getNameSpace()+".insertOrderPr", orderPr);
	}
}
