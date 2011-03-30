/**
 * 
 */
package com.caijing.business;

import junit.framework.TestCase;

import com.caijing.business.impl.AnalyzerManagerImpl;
import com.caijing.util.ContextFactory;

/**
 * @author Administrator
 *
 */
public class AnalyzerManagerTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link com.caijing.business.impl.AnalyzerManagerImpl#fetchAnalyzersFromSA(java.lang.String)}.
	 */
	public void testFetchAnalyzersFromSA() {
		AnalyzerManagerImpl analyzerManager = (AnalyzerManagerImpl) ContextFactory.getBean("analyzerManager");
		String[] agencys = { "申银万国", "国泰君安", "招商证券", "安信证券", "广发证券", "国金证券", "国信证券", "长江证券", "华泰证券", "华泰联合", "光大证券",
				"中投证券", "中信建投" };
		for (String agency : agencys) {
			analyzerManager.fetchAnalyzersFromSA(agency);
		}
		fail("Not yet implemented");
	}

}
