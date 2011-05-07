package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.ProductDAO;
import com.caijing.domain.Product;
import com.caijing.util.CrudDaoDefault;

public class ProductDAOImpl extends CrudDaoDefault implements ProductDAO {

	@Override
	public List<Product> getAllProduct() {
		return (List<Product>) getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getAllProduct");
	}

}