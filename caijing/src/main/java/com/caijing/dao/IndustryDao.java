package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Industry;
import com.caijing.util.CrudDao;

public interface IndustryDao extends CrudDao {

	List<Industry> selectlv1();

}
