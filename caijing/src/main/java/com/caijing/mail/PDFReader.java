package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.GroupGainManager;
import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.ibatis.ReportDaoImpl;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.util.Command;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;
import com.caijing.util.Vutil;

public class PDFReader {
	private static Log logger = LogFactory.getLog(PDFReader.class);
	@Autowired
	@Qualifier("reportExtractor")
	private ReportExtractorImpl extractor = null;

	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	// @Autowired
	// @Qualifier("vutil")
	private Vutil vutil = new Vutil();

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("groupGainManager")
	private GroupGainManager groupGainManager = null;

	public void init() {
		extractor.init();
	}

	public void read(String path) throws Exception {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile() && f.getAbsolutePath().contains(".pdf")) {
					try {
						String pdfPath = f.getAbsolutePath();
						System.out.println("path:" + pdfPath);
						String textFile = null;
						String rid = ServerUtil.getid();
						textFile = pdfPath.substring(0, pdfPath.lastIndexOf('/') + 1) + rid + ".txt";
						String mvpath = pdfPath.replace(FileUtil.path, FileUtil.html);
						mvpath = mvpath.substring(0, mvpath.lastIndexOf('/') + 1);
						String mvfile = mvpath + rid + ".pdf";
						String commendStr = "cp " + pdfPath + " " + mvfile;
						File ddir = new File(mvpath);
						System.out.println("Copy path:" + mvpath);
						if (!ddir.exists()) {
							ddir.mkdirs();
						}
						StringWriter sw = new StringWriter();
						Command.run(commendStr, sw);
						logger.debug(sw.toString());

						textFile = mvfile.replace(".pdf", ".txt");
						System.out.println("Copy path:" + pdfPath);
						readFdf(pdfPath, textFile);
						Report report = extractor.extractFromTitle(pdfPath, rid);
						if (report != null) {
							System.out.println("url:" + mvfile.replace("/home/html", ""));
							report.setFilepath(mvfile.replace("/home/html", ""));
							Date ptime = vutil.stringtodate(file.getName());
							System.out.println("ptime :" + file.getName());
							report.setPtime(ptime);
							reportDao.insert(report);
							if (report.getType() == 1 && config.getConfigMap().containsKey(report.getSaname())) {
								System.out.println("Be in top10 stockagency start to extrator!");

								RecommendStock rs = extractor.extractFromFile(report, textFile);
								if (rs != null) {
									rs.setReportid(report.getRid());
									if (rs.getExtractnum() > 2) {
										recommendStockDao.insert(rs);
										System.out.println("Reports getAname: " + rs.getAname());
										System.out.println("Reports getObjectprice: " + rs.getObjectprice());
										System.out.println("Reports getCreatedate: " + rs.getCreatedate());
										System.out.println("Reports getGrade: " + rs.getGrade());
										System.out.println("Reports getEps: " + rs.getEps());
										groupGainManager.extractGroupStock(rs);
									}
								}

							}
						}
					} catch (Exception e) {
						System.out.print(e.getMessage());
						e.printStackTrace();
					}
				} else if (f.isDirectory()) {
					read(f.getAbsolutePath());
				}
			}
		} else {
			System.out.println("path:" + path);
		}
	}

	public void readFdf(String file, String outPath) {
		boolean sort = false;
		String pdfFile = file;
		String encoding = "gbk";
		int startPage = 1;
		int endPage = Integer.MAX_VALUE;
		Writer output = null;
		PDDocument document = null;
		try {
			document = PDDocument.load(pdfFile);
			// String fileName = url.getFile();
			System.out.println("textFile:" + outPath);
			output = new OutputStreamWriter(new FileOutputStream(outPath), encoding);
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			stripper.setSortByPosition(sort);
			stripper.setStartPage(startPage);
			stripper.setEndPage(endPage);
			stripper.writeText(document, output);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				output.close();
				document.close();
			} catch (Exception e) {
				System.out.print(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void processPath(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					File[] files2 = f.listFiles();
					String toPath = FileUtil.path + "/" + FileUtil.getDatefromSubject(f.getName());
					System.out.println("Store path:" + toPath);
					for (File f2 : files2) {
						if (f2.isFile() && f2.getAbsolutePath().endsWith(".rar")) {
							String commendStr = "unrar e " + f2.getAbsolutePath() + " " + toPath;
							File ddir = new File(toPath);
							System.out.println("rar file:" + f2.getAbsolutePath());
							if (!ddir.exists()) {
								ddir.mkdirs();
								System.out.println("Mkdir:" + toPath);
								StringWriter sw = new StringWriter();
								System.out.println("Strart run :" + commendStr);
								Command.run(commendStr, sw);
								System.out.println(sw.toString());
								logger.debug(sw.toString());
							}
							try {
								System.out.println("Start read pdf path:" + toPath);
								read(toPath);
								System.out.println("finish read pdf path:" + toPath);
							} catch (Exception e) {
								logger.debug(e.getMessage());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public void processHistoryPath(String logfile) {
		String text = FileUtil.read(logfile, "utf-8");
//		int start = 1; //开始的行号
//		int end = 3693; //最后的行号
//		int i = 0; //当前的行号
		int limit = 10000;
		int count = 0;
		for (String line : text.split("\n")) {
//			++i;
//			if (i < start) {
//				continue;
//			}
//			if (i > end) {
//				break;
//			}
			
			if(count==limit){
				break;
			}
			
//			if (line.contains("申银万国")) {//1-3693
//				processOneFile(line, "申银万国");
//				count++;
//			}
			if (line.contains("招商证券")) {//3694-5077
				processOneFile(line, "招商证券");
				count++;
			}
//			if (line.contains("国泰君安")) {//5078-8316
//				processOneFile(line, "国泰君安");
//				count++;
//			}
//			if (line.contains("安信证券")) {//5078-8316
//				processOneFile(line, "安信证券");
//				count++;
//			}
//			if (line.contains("广发：")||line.contains("广发证券")) {//TODO 需要两套规则 8317-8483
//				processOneFile(line, "广发证券");
//				count++;
//			}
//			if (line.contains("国金证券")) {//8484-8879
//				processOneFile(line, "国金证券");
//				count++;
//			}
//			if (line.contains("国信证券")) {//8880-9383
//				processOneFile(line, "国信证券");
//				count++;
//			}
//			if (line.contains("长江：")) {//数量很少，基本只有title
//				processOneFile(line, "长江证券");
//				count++;
//			}
//			if (line.contains("光大：")) {//没有研报，暂不处理
//				processOneFile(line, "光大证券");
//				count++;
//			}
//			if (line.contains("东海：")) {//目前没有这个券商，暂不处理
//				processOneFile(line, "东海证券");
//				count++;
//			}
//			if (line.contains("华泰：")) {//编码问题，导致有的研报不能读取
//				processOneFile(line, "华泰证券");
//				count++;
//			}
//			if (line.contains("联合：")) {//编码问题，导致有的研报不能读取
//				processOneFile(line, "华泰联合");
//				count++;
//			}			
//			if (line.contains("中金：")) {
//				processOneFile(line, "中金公司");
//				count++;
//			}
//			if (line.contains("中投：")) {
//				processOneFile(line, "中投证券");
//				count++;
//			}
//			if (line.contains("中信建投")) {
//				processOneFile(line, "中信建投");
//				count++;
//			}
			
			/*
			if (logfile.contains("axzq")) {
				processOneFile(line, "安信证券");
			} else if (logfile.contains("sywg")) {
				processOneFile(line, "申银万国");
			} else if (logfile.contains("zszq")) {
				processOneFile(line, "招商证券");
			} else if (logfile.contains("gtja")) {
				processOneFile(line, "国泰君安");
			} else if (logfile.contains("gfzq")) {
				processOneFile(line, "广发证券");
			} else if (logfile.contains("gjzq")) {
				processOneFile(line, "国金证券");
			} else if (logfile.contains("gxzq")) {
				processOneFile(line, "国信证券");
			} */
		}
		System.out.println("num: "+count);
	}

	public void processOneFile(String pdfPath, String saname) {
		System.out.println("path:" + pdfPath);
		String textFile = null;
		String rid = ServerUtil.getid();
		try {
			//			textFile = pdfPath.substring(0, pdfPath.lastIndexOf('/') + 1) + rid + ".txt";
			String mvpath = pdfPath.replace("/data/oldpapers", FileUtil.oldhtml); //新建一个文件夹存放oldpapers
			mvpath = mvpath.substring(0, mvpath.lastIndexOf('/') + 1);
			String mvfile = mvpath + rid + ".pdf";
			String commendStr = "cp " + pdfPath + " " + mvfile;
			File ddir = new File(mvpath);
			System.out.println("Copy path:" + mvfile);
			if (!ddir.exists()) {
				ddir.mkdirs();
			}
			StringWriter sw = new StringWriter();
			Command.run(commendStr, sw); //FIXME to remove comment
			logger.debug(sw.toString());

			textFile = mvfile.replace(".pdf", ".txt");
			System.out.println("Copy path:" + textFile);
			readFdf(pdfPath, textFile); //FIXME to remove comment
			Report report = extractor.extractFromTitleAndSaname(pdfPath, rid, saname);
			//FIXME to remove comment

			if (report != null) {
				System.out.println("url:" + mvfile.replace("/home/html", ""));
				report.setFilepath(mvfile.replace("/home/html", ""));
				//			Date ptime = vutil.stringtodate(file.getName());
				//			System.out.println("ptime :" + file.getName());
				Date now = new Date();
				report.setPtime(now);
				reportDao.insert(report);
				if (report.getType() == 1 && config.getConfigMap().containsKey(report.getSaname())) {
					System.out.println("Be in top10 stockagency start to extrator!");

					RecommendStock rs = extractor.extractFromFile(report, textFile);
					//TODO 将从File抽取出来的字段写入一个日志文件，每个券商一个日志 与标题同一日志，以便对比
					if (rs.getAname() == null
							|| (report.getAname() != null && !rs.getAname().equals(report.getAname()))) {
						rs.setAname(report.getAname());
					}
					if (rs != null) {
						rs.setReportid(report.getRid());
						if (rs.getExtractnum() > 2) {
							recommendStockDao.insert(rs);
							System.out.println("Reports getAname: " + rs.getAname());
							System.out.println("Reports getObjectprice: " + rs.getObjectprice());
							System.out.println("Reports getCreatedate: " + rs.getCreatedate());
							System.out.println("Reports getGrade: " + rs.getGrade());
							System.out.println("Reports getEps: " + rs.getEps());
							//							groupGainManager.extractGroupStock(rs);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReportExtractorImpl extractor = (ReportExtractorImpl) ContextFactory.getBean("reportExtractor");
		ReportDao reportDao = (ReportDaoImpl) ContextFactory.getBean("reportDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		Config config = (Config) ContextFactory.getBean("config");
		GroupGainManager groupGainManager = (GroupGainManager) ContextFactory.getBean("groupGainManager");
		PDFReader pdfReader = new PDFReader();
		pdfReader.setExtractor(extractor);
		pdfReader.setReportDao(reportDao);
		pdfReader.setRecommendStockDao(recommendStockDao);
		pdfReader.setConfig(config);
		pdfReader.setGroupGainManager(groupGainManager);
		pdfReader.init();

		try {
			pdfReader.processHistoryPath("/data/shells/log/result1.log");
			System.exit(0);
			/*
			//安信证券
			//pdfReader.processHistoryPath("/data/oldpapers2/log/axzq.log");
			//申银万国
			pdfReader.processHistoryPath("/data/oldpapers2/log/sywg.log");
			//招商证券
			//pdfReader.processHistoryPath("/data/oldpapers2/log/zszq.log");
			//国泰君安
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gtja.log");
			//广发证券
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gfzq.log");
			//国金证券
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gjzq.log");
			//国信证券
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gxzq.log");
			*/
			//			System.out.println(args.length);
			//			System.out.println(args[0]);
			//			if (args.length == 1) {
			//				// pdfReader.read(args[0]);
			//				System.out.println(args[0]);
			//				pdfReader.read(args[0]);
			//			} else {
			//				// pdfReader.processPath("/home/app/email");
			//			}
			// pdfReader.read("/home/app/email/papers/20100723");
			// pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReportExtractorImpl getExtractor() {
		return extractor;
	}

	public void setExtractor(ReportExtractorImpl extractor) {
		this.extractor = extractor;
	}

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public GroupGainManager getGroupGainManager() {
		return groupGainManager;
	}

	public void setGroupGainManager(GroupGainManager groupGainManager) {
		this.groupGainManager = groupGainManager;
	}
}
