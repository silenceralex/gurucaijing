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
//		int start = 1; //��ʼ���к�
//		int end = 3693; //�����к�
//		int i = 0; //��ǰ���к�
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
			
//			if (line.contains("�������")) {//1-3693
//				processOneFile(line, "�������");
//				count++;
//			}
			if (line.contains("����֤ȯ")) {//3694-5077
				processOneFile(line, "����֤ȯ");
				count++;
			}
//			if (line.contains("��̩����")) {//5078-8316
//				processOneFile(line, "��̩����");
//				count++;
//			}
//			if (line.contains("����֤ȯ")) {//5078-8316
//				processOneFile(line, "����֤ȯ");
//				count++;
//			}
//			if (line.contains("�㷢��")||line.contains("�㷢֤ȯ")) {//TODO ��Ҫ���׹��� 8317-8483
//				processOneFile(line, "�㷢֤ȯ");
//				count++;
//			}
//			if (line.contains("����֤ȯ")) {//8484-8879
//				processOneFile(line, "����֤ȯ");
//				count++;
//			}
//			if (line.contains("����֤ȯ")) {//8880-9383
//				processOneFile(line, "����֤ȯ");
//				count++;
//			}
//			if (line.contains("������")) {//�������٣�����ֻ��title
//				processOneFile(line, "����֤ȯ");
//				count++;
//			}
//			if (line.contains("���")) {//û���б����ݲ�����
//				processOneFile(line, "���֤ȯ");
//				count++;
//			}
//			if (line.contains("������")) {//Ŀǰû�����ȯ�̣��ݲ�����
//				processOneFile(line, "����֤ȯ");
//				count++;
//			}
//			if (line.contains("��̩��")) {//�������⣬�����е��б����ܶ�ȡ
//				processOneFile(line, "��̩֤ȯ");
//				count++;
//			}
//			if (line.contains("���ϣ�")) {//�������⣬�����е��б����ܶ�ȡ
//				processOneFile(line, "��̩����");
//				count++;
//			}			
//			if (line.contains("�н�")) {
//				processOneFile(line, "�н�˾");
//				count++;
//			}
//			if (line.contains("��Ͷ��")) {
//				processOneFile(line, "��Ͷ֤ȯ");
//				count++;
//			}
//			if (line.contains("���Ž�Ͷ")) {
//				processOneFile(line, "���Ž�Ͷ");
//				count++;
//			}
			
			/*
			if (logfile.contains("axzq")) {
				processOneFile(line, "����֤ȯ");
			} else if (logfile.contains("sywg")) {
				processOneFile(line, "�������");
			} else if (logfile.contains("zszq")) {
				processOneFile(line, "����֤ȯ");
			} else if (logfile.contains("gtja")) {
				processOneFile(line, "��̩����");
			} else if (logfile.contains("gfzq")) {
				processOneFile(line, "�㷢֤ȯ");
			} else if (logfile.contains("gjzq")) {
				processOneFile(line, "����֤ȯ");
			} else if (logfile.contains("gxzq")) {
				processOneFile(line, "����֤ȯ");
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
			String mvpath = pdfPath.replace("/data/oldpapers", FileUtil.oldhtml); //�½�һ���ļ��д��oldpapers
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
					//TODO ����File��ȡ�������ֶ�д��һ����־�ļ���ÿ��ȯ��һ����־ �����ͬһ��־���Ա�Ա�
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
			//����֤ȯ
			//pdfReader.processHistoryPath("/data/oldpapers2/log/axzq.log");
			//�������
			pdfReader.processHistoryPath("/data/oldpapers2/log/sywg.log");
			//����֤ȯ
			//pdfReader.processHistoryPath("/data/oldpapers2/log/zszq.log");
			//��̩����
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gtja.log");
			//�㷢֤ȯ
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gfzq.log");
			//����֤ȯ
			//pdfReader.processHistoryPath("/data/oldpapers2/log/gjzq.log");
			//����֤ȯ
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
