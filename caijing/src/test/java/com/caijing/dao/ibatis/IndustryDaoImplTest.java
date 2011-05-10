package com.caijing.dao.ibatis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caijing.dao.IndustryDao;
import com.caijing.domain.Industry;
import com.caijing.util.ContextFactory;

public class IndustryDaoImplTest {

	IndustryDao target;

	@Before
	public void setUp() throws Exception {
		target = (IndustryDao) ContextFactory.getBean("industryDao");
	}
	
	@Test
	public void type() throws Exception {
		assertNotNull(IndustryDaoImpl.class);
	}

	@Test
	public void instantiation() throws Exception {
		assertNotNull(target);
	}

	@Test
	public void select1level_A$() throws Exception {
		List<Industry> actual = target.selectlv1();
		assertTrue(actual.size()==13);
	}

}
