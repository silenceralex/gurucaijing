package com.caijing.business.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.caijing.business.GroupGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.util.DateTools;

public class GroupGainManagerImpl implements GroupGainManager, InitializingBean {

	static String[] buys = { "����", "�Ƽ�", "ǿ���Ƽ�", "�����Ƽ�", "����", "ά���Ƽ�", "�� �� ά��", "�ϵ����Ƽ�", "�� �� ά��", "����ά��", "�����״�" };

	static String[] sells = { "����", "ά�������Ƽ�", "�����Ƽ�", "����", "�� �� ����" };

	private HashSet<String> buyset = new HashSet<String>();

	private HashSet<String> sellset = new HashSet<String>();

	private HashMap<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();

	private AnalyzerDao analyzerDao = null;

	private GroupStockDao groupStockDao = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : analyzers) {
			analyzerMap.put(analyzer.getName(), analyzer);
		}
		for (String buy : buys) {
			buyset.add(buy);
		}
		for (String sell : sells) {
			sellset.add(sell);
		}
	}

	public void extractGroupStock(RecommendStock rs) {
		String[] names = rs.getAname().split(" ");
		for (String name : names) {
			if (analyzerMap.containsKey(name)) {
				GroupStock gs = new GroupStock();
				gs.setGroupid(analyzerMap.get(name).getAid());
				gs.setGroupname(name);
				gs.setStockcode(rs.getStockcode());
				try {
					if (buyset.contains(rs.getGrade())) {
						gs.setIntime(DateTools.parseYYYYMMDDDate(rs.getCreatedate()));
						groupStockDao.insert(gs);
					}
					if (sellset.contains(rs.getGrade())) {
						gs.setOuttime(DateTools.parseYYYYMMDDDate(rs.getCreatedate()));
						groupStockDao.update(gs);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public GroupStockDao getGroupStockDao() {
		return groupStockDao;
	}

	public void setGroupStockDao(GroupStockDao groupStockDao) {
		this.groupStockDao = groupStockDao;
	}

}
