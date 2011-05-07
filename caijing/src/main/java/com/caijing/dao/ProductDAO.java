package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Product;
import com.caijing.util.CrudDao;

public interface ProductDAO extends CrudDao {
	List<Product> getAllProduct();
}