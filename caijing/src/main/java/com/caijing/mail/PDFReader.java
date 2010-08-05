package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.crawl.ReportExtractor;
import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.ReportDao;
import com.caijing.dao.ibatis.ReportDaoImpl;
import com.caijing.domain.Report;
import com.caijing.util.Command;
import com.caijing.util.ContextFactory;
import com.caijing.util.ServerUtil;

public class PDFReader {
	private static Log logger = LogFactory.getLog(PDFReader.class);
	ReportExtractor extractor = new ReportExtractorImpl();
	ReportDao reportDao = (ReportDaoImpl) ContextFactory.getBean("reportDao");
	
	private static final String html="/home/html/papers";

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
					textFile = pdfPath.substring(0, pdfPath.lastIndexOf('/')+1)
							+ rid + ".txt";
					String mvpath=pdfPath.replace("/home/app/papers", html);
					mvpath=mvpath.substring(0, mvpath.lastIndexOf('/')+1);
					String mvfile=mvpath+rid+".pdf";
					String commendStr = "cp " + pdfPath + " " + mvfile;		
					File ddir = new File(mvpath);
					System.out.println("Copy path:" + mvpath);
					if (!ddir.exists()) {
						ddir.mkdirs();
					}
					StringWriter sw = new StringWriter();
					Command.run(commendStr, sw);
					logger.debug(sw.toString());
					
					textFile=mvfile.replace(".pdf", ".txt");
					readFdf(pdfPath, textFile);
					Report report = extractor.extractFromFile(pdfPath, rid,
							textFile);
					if (report != null) {
						report.setFilepath(pdfPath);			
						reportDao.insert(report);
					}
				} else if (f.isDirectory()) {
					read(f.getAbsolutePath());
				}
			}
		}else{
			System.out.println("path:" + path);
		}
	}

	public void readFdf(String file, String outPath) {
		// �Ƿ�����
		boolean sort = false;
		// pdf�ļ���
		String pdfFile = file;
		// �����ı��ļ����
		// String textFile = null;
		// ���뷽ʽ
		String encoding = "gbk";
		// ��ʼ��ȡҳ��
		int startPage = 1;
		// ������ȡҳ��
		int endPage = Integer.MAX_VALUE;
		// �ļ�����������ı��ļ�
		Writer output = null;
		// �ڴ��д洢��PDF Document
		PDDocument document = null;
		try {
			// ���ȵ���һ��URL4װ���ļ������õ��쳣�ٴӱ����ļ�ϵͳ//ȥװ���ļ�
			// URL url = new URL(pdfFile); // ע������Ѳ�����ǰ�汾�е�URL.����File��
			document = PDDocument.load(pdfFile);
			// ��ȡPDF���ļ���
			// String fileName = url.getFile();
			String fileName = pdfFile;
			// ��ԭ4PDF�����4�����²����txt�ļ�
			// if (fileName.length() > 4) {
			// textFile = fileName.substring(0, fileName.length() - 4) + ".txt";
			// File outputFile = new File(textFile);
			// // textFile = outputFile.getName();
			// }
			System.out.println("textFile��" + outPath);
			// �ļ�������д���ļ���textFile
			output = new OutputStreamWriter(new FileOutputStream(outPath),
					encoding);
			// PDFTextStripper4��ȡ�ı�
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			// �����Ƿ�����
			stripper.setSortByPosition(sort);
			// ������ʼҳ
			stripper.setStartPage(startPage);
			// ���ý���ҳ
			stripper.setEndPage(endPage);
			// ����PDFTextStripper��writeText��ȡ������ı�
			stripper.writeText(document, output);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				output.close();
				document.close();
			} catch (Exception e) {
				System.out.print(e.getMessage());
				// e.printStackTrace();
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
			if(args.length>1){
//				pdfReader.read(args[0]);
				System.out.println(args[1]);
			}
			pdfReader.read("/home/app/email/papers/20100723");
			// pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
