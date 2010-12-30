package com.caijing.dao.ibatis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.StockEarnDao;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;

public class StockDaoImplTest extends TestCase {
	StockEarnDao dao;

	private Log log = LogFactory.getLog(getClass());

	protected void setUp() throws Exception {
		super.setUp();
		dao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");

	}

	public void testInsert() {
		//		String content = FileUtil.read("D:\\stockhq.txt", "GBK");
		String filename = "D:\\hq.txt";
		String filename2 = "D:\\stockhq2.txt";
		FileOutputStream fos = null;
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("filename:" + filename + "  not exists!");
			return;
		}
		if (!file.canRead()) {
			System.out.println("filename:" + filename + "  cannot read!");
			return;
		}
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		int i = 0;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "GBK");
			reader = new BufferedReader(isr);
			fos = new FileOutputStream(filename2);
			reader.readLine();
			for (String str = null; (str = reader.readLine()) != null;) {
				i++;
				String[] strs = str.trim().split("\\s");

				if (strs.length == 4) {
					String stockcode = strs[0].trim();
					System.out.println("round : " + i + "  " + stockcode);
					Date date = DateTools.parseYYYYMMDDDate(strs[1].replaceAll("\"", "").trim());
					if (strs[2].equalsIgnoreCase("0"))
						continue;

					//					StockEarn se = new StockEarn();
					//					se.setStockcode(stockcode);
					//					se.setDate(date);
					//					se.setPrice(Float.parseFloat(strs[2].trim()));
					//					se.setRatio(Float.parseFloat(strs[3].trim()));
					//					if (dao.select(se) == null) {
					//						dao.insert(se);
					//					}
					String insert = "insert into stockearn (stockcode,date,price,ratio) values(" + stockcode + ","
							+ strs[1].trim() + "," + strs[2].trim() + "," + strs[3].trim() + ");\n";
					fos.write(insert.getBytes());
					if (i % 100 == 0) {
						System.out.println("round : " + i + "  " + strs.length);
						System.out.println("round : " + i + "  " + insert);
						fos.flush();
					}
				}
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
				fis.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("lines: " + i);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
