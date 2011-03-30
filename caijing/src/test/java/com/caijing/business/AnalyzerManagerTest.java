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
		String[] agencys = { "�������", "��̩����", "����֤ȯ", "����֤ȯ", "�㷢֤ȯ", "����֤ȯ", "����֤ȯ", "����֤ȯ", "��̩֤ȯ", "��̩����", "���֤ȯ",
				"��Ͷ֤ȯ", "���Ž�Ͷ" };
		for (String agency : agencys) {
			analyzerManager.fetchAnalyzersFromSA(agency);
		}
		fail("Not yet implemented");
	}

}
