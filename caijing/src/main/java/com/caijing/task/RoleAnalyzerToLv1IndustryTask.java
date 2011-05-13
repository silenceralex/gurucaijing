package com.caijing.task;

import java.util.List;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.IndustryDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.Industry;
import com.caijing.util.ContextFactory;

/**
 * ����ʦ����ҵӳ����£���ԭ���ķ���ʦ��ҵ����ͳһ��һ������ҵ���ࡣupdate analyzer������
 */
public class RoleAnalyzerToLv1IndustryTask {

	private AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
	private IndustryDao industryDao = (IndustryDao) ContextFactory.getBean("industryDao");;
	
	public void run(){
		List<Analyzer> Analyzers =  analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : Analyzers) {
			String industryName = analyzer.getIndustry();
			Industry industry = industryDao.selectByIndustryname(industryName);
			String lv1IndustryId = industry.getIndustryid().substring(0, 2);
			Industry lv1Industry = (Industry) industryDao.select(lv1IndustryId);
			analyzer.setIndustry(lv1Industry.getIndustryname());
			analyzerDao.update(analyzer);
		}
	}
	
	public static void main(String[] args) {
		RoleAnalyzerToLv1IndustryTask task = new RoleAnalyzerToLv1IndustryTask();
		task.run();
	}
}
