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

import com.caijing.crawl.ReportExtractor;
import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.ReportDao;
import com.caijing.dao.ibatis.ReportDaoImpl;
import com.caijing.domain.Report;
import com.caijing.util.Command;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;
import com.caijing.util.Vutil;

public class PDFReader {
	private static Log logger = LogFactory.getLog(PDFReader.class);
	ReportExtractor extractor = new ReportExtractorImpl();
	ReportDao reportDao = (ReportDaoImpl) ContextFactory.getBean("reportDao");

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;


	public void read(String path) throws Exception {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile() && f.getAbsolutePath().contains(".pdf")) {
					String pdfPath = f.getAbsolutePath();
					System.out.println("path:" + pdfPath);
					String textFile = null;
					String rid = ServerUtil.getid();
					textFile = pdfPath.substring(0,
							pdfPath.lastIndexOf('/') + 1)
							+ rid + ".txt";
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
						System.out.println("url:"
								+ mvfile.replace("/home/html", ""));
						report.setFilepath(mvfile.replace("/home/html", ""));
						Date ptime = vutil.stringtodate(f.getName());
						System.out.println("ptime :" + f.getName());
						report.setPtime(ptime);
						reportDao.insert(report);
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
			output = new OutputStreamWriter(new FileOutputStream(outPath),
					encoding);
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
					String toPath = FileUtil.path + "/"
							+ FileUtil.getDatefromSubject(f.getName());
					System.out.println("Store path:" + toPath);
					for (File f2 : files2) {
						if (f2.isFile()
								&& f2.getAbsolutePath().endsWith(".rar")) {
							String commendStr = "unrar e "
									+ f2.getAbsolutePath() + " " + toPath;
							File ddir = new File(toPath);
							System.out.println("rar file:" + f2.getAbsolutePath());
							if (!ddir.exists()) {
								ddir.mkdirs();
							}
							StringWriter sw = new StringWriter();
							Command.run(commendStr, sw);
							logger.debug(sw.toString());
							try {
								read(toPath);
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PDFReader pdfReader = new PDFReader();

		try {
			// ȡ��E���µ�SpringGuide.pdf������
			// pdfReader.read("C:\\Users\\chenjun\\Desktop\\touzi\\");
			// pdfReader.read("F:\\email\\papers\\�о�����7.19");
			if (args.length > 1) {
				// pdfReader.read(args[0]);
				System.out.println(args[1]);
			}
			// pdfReader.read("/home/app/email/papers/20100723");
			pdfReader.processPath("/home/app/email");
			// pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
