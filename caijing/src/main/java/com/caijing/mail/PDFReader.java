package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.crawl.ReportExtractor;
import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.ReportDao;
import com.caijing.dao.ibatis.ReportDaoImpl;
import com.caijing.domain.Report;
import com.caijing.util.ContextFactory;

public class PDFReader {
	ReportExtractor extractor=new ReportExtractorImpl();
	ReportDao reportDao=(ReportDaoImpl)ContextFactory.getBean("reportDao");
	
	public void read(String path) throws Exception {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile() && f.getAbsolutePath().contains(".pdf")) {

					String pdfPath = f.getAbsolutePath();
					System.out.println("path:" + pdfPath);
					String textFile = null;
					if (pdfPath.length() > 4) {
						textFile = pdfPath.substring(0, pdfPath.length() - 4) + ".txt";
					}
					readFdf(pdfPath, textFile);
					Report report=extractor.extractFromFile(pdfPath, textFile);
					if(report!=null){
						report.setFilepath(pdfPath);
						reportDao.insert(report);
					}
				} else if (f.isDirectory()) {
					read(f.getAbsolutePath());
				}
			}
		}
	}

	public void readFdf(String file, String outPath) {
		// 是否排序
		boolean sort = false;
		// pdf文件名
		String pdfFile = file;
		// 输入文本文件名称
		//		String textFile = null;
		// 编码方式
		String encoding = "gbk";
		// 开始提取页数
		int startPage = 1;
		// 结束提取页数
		int endPage = Integer.MAX_VALUE;
		// 文件输入流，生成文本文件
		Writer output = null;
		// 内存中存储的PDF Document
		PDDocument document = null;
		try {
			// 首先当作一个URL来装载文件，如果得到异常再从本地文件系统//去装载文件
			//			URL url = new URL(pdfFile); // 注意参数已不是以前版本中的URL.而是File。
			document = PDDocument.load(pdfFile);
			// 获取PDF的文件名
			//			String fileName = url.getFile();
			String fileName = pdfFile;
			// 以原来PDF的名称来命名新产生的txt文件
			//			if (fileName.length() > 4) {
			//				textFile = fileName.substring(0, fileName.length() - 4) + ".txt";
			//				File outputFile = new File(textFile);
			//				//				textFile = outputFile.getName();
			//			}
			System.out.println("textFile：" + outPath);
			// 文件输入流，写入文件倒textFile
			output = new OutputStreamWriter(new FileOutputStream(outPath), encoding);
			// PDFTextStripper来提取文本
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			// 设置是否排序
			stripper.setSortByPosition(sort);
			// 设置起始页
			stripper.setStartPage(startPage);
			// 设置结束页
			stripper.setEndPage(endPage);
			// 调用PDFTextStripper的writeText提取并输出文本
			stripper.writeText(document, output);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				output.close();
				document.close();
			} catch (Exception e) {
				System.out.print(e.getMessage());
				//				e.printStackTrace();
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
			// 取得E盘下的SpringGuide.pdf的内容
			//			pdfReader.read("C:\\Users\\chenjun\\Desktop\\touzi\\");
			pdfReader.read("F:\\email\\papers\\研究报告7.19");
			//			pdfReader.read("/home/app/email/papers");
			//			 pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
