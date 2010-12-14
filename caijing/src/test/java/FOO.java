import java.util.List;

import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.StockAgencyEntity;
import com.caijing.util.ContextFactory;

public class FOO {
	public static void main(String[] args) {
		//		System.out.println(DigestUtils.md5Hex("raymond"));
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		StockEarnDao stockDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		//		List<RecommendStock> stocks = recommendStockDao.getGradeRecommendStocks();
		//		System.out.println("stocks size : " + stocks.size());
		//		for (RecommendStock stock : stocks) {
		//			String judgeStr = stock.getGrade().replaceAll("\\s", "");
		//			if (judgeStr.contains("买入") || judgeStr.contains("增持")) {
		//				recommendStockDao.updateStatus(stock.getRecommendid(), 2);
		//				System.out.println("Recommendid() : " + stock.getRecommendid() + " grade:" + stock.getGrade()
		//						+ " update status to 1");
		//			} else if (judgeStr.contains("推荐")) {
		//				if (!judgeStr.contains("谨慎") && !judgeStr.contains("审慎")) {
		//					recommendStockDao.updateStatus(stock.getRecommendid(), 2);
		//					System.out.println("Recommendid() : " + stock.getRecommendid() + " grade:" + stock.getGrade()
		//							+ " update status to 1");
		//				}
		//			} else if (judgeStr.contains("中性")) {
		//				recommendStockDao.updateStatus(stock.getRecommendid(), 1);
		//				System.out.println("Recommendid() : " + stock.getRecommendid() + " grade:" + stock.getGrade()
		//						+ " update status to -1");
		//			}
		//		}
		List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgency(0, 10);
		for (StockAgencyEntity entity : entitys) {
			List<String> sanames = recommendStockDao.getSanamesByStockcode(entity.getStockcode());
			String tmp = "";
			for (String saname : sanames) {
				tmp += saname + " ";
			}
			entity.setSanames(tmp.trim());
			System.out.println("Stockname:" + entity.getStockname() + "  sanames:" + tmp.trim());
			System.out.println("Stockname:" + entity.getStockname() + "  sacounts:" + entity.getSacounts());
			float price = stockDao.getCurrentPriceByCode(entity.getStockcode());
			System.out.println("Stockname:" + entity.getStockname() + "  price:" + price);
		}
	}
}
