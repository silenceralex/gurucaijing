package com.caijing.dao;

import java.util.List;

import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDao;

public interface RecommendStockDao extends CrudDao {
	List<RecommendStock> getRecommendStocksByStockcode(String stockcode);

	List<RecommendStock> getRecommendStocksBySaid(String said);
}
