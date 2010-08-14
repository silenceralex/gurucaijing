package com.caijing.util;

import java.util.HashMap;

import com.caijing.dao.IndustryDao;
import com.caijing.dao.StockDao;
import com.caijing.dao.ibatis.IndustryDaoImpl;
import com.caijing.dao.ibatis.StockDaoImpl;
import com.caijing.domain.Industry;
import com.caijing.domain.Stock;

public class DataReader {

	IndustryDao industryDao = (IndustryDaoImpl) ContextFactory
			.getBean("industryDao");
	StockDao stockDao = (StockDaoImpl) ContextFactory.getBean("stockDao");

	public void readIndustry(String filename) {
		String file = FileUtil.read(filename, "utf-8");
		String[] strs = file.split("\n");
		int i = 0;
		for (String str : strs) {
			String[] ind = str.split(",");
			if (ind.length == 2) {
				Industry industry = new Industry();
				industry.setIndustryid(ind[0]);
				industry.setIndustryname(ind[1]);
				System.out.print("Insert:" + ind[1]);
				industryDao.insert(industry);
				i++;
			}
		}
		System.out.print("Insert Total number:" + i);
	}

	public void readStock(String filename) {
		String file = FileUtil.read(filename, "utf-8");
		String[] strs = file.split("\n");
		int i = 0;
		HashMap<String, Stock> map = new HashMap<String, Stock>();
		for (String str : strs) {
			String[] ind = str.split(",");
			if (ind.length == 4) {
				Stock stock = new Stock();
				stock.setStockcode(ind[0]);
				stock.setStockname(ind[1]);
				stock.setIndustryid(ind[2]);
				if (!map.containsKey(ind[0])
						|| (map.get(ind[0]).getIndustryid().length() < stock
								.getIndustryid().length())) {
					map.put(ind[0], stock);
				}
			}
		}
		for (String key : map.keySet()) {
			stockDao.insert(map.get(key));
			i++;
		}
		System.out.print("Insert Total number:" + i);
	}

	public static void main(String[] args) {
		System.out.println(args.length);
		DataReader reader = new DataReader();
		if (args.length == 1) {
			// pdfReader.read(args[0]);
			System.out.println(args[0]);
			// reader.readIndustry(args[0]);
			reader.readStock(args[0]);
			// pdfReader.read(args[0]);
		}
	}
}
