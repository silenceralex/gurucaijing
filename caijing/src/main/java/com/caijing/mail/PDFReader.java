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
		// �Ƿ�����
		boolean sort = false;
		// pdf�ļ���
		String pdfFile = file;
		// �����ı��ļ�����
		//		String textFile = null;
		// ���뷽ʽ
		String encoding = "gbk";
		// ��ʼ��ȡҳ��
		int startPage = 1;
		// ������ȡҳ��
		int endPage = Integer.MAX_VALUE;
		// �ļ��������������ı��ļ�
		Writer output = null;
		// �ڴ��д洢��PDF Document
		PDDocument document = null;
		try {
			// ���ȵ���һ��URL��װ���ļ�������õ��쳣�ٴӱ����ļ�ϵͳ//ȥװ���ļ�
			//			URL url = new URL(pdfFile); // ע������Ѳ�����ǰ�汾�е�URL.����File��
			document = PDDocument.load(pdfFile);
			// ��ȡPDF���ļ���
			//			String fileName = url.getFile();
			String fileName = pdfFile;
			// ��ԭ��PDF�������������²�����txt�ļ�
			//			if (fileName.length() > 4) {
			//				textFile = fileName.substring(0, fileName.length() - 4) + ".txt";
			//				File outputFile = new File(textFile);
			//				//				textFile = outputFile.getName();
			//			}
			System.out.println("textFile��" + outPath);
			// �ļ���������д���ļ���textFile
			output = new OutputStreamWriter(new FileOutputStream(outPath), encoding);
			// PDFTextStripper����ȡ�ı�
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
			// ȡ��E���µ�SpringGuide.pdf������
			//			pdfReader.read("C:\\Users\\chenjun\\Desktop\\touzi\\");
			pdfReader.read("F:\\email\\papers\\�о�����7.19");
			//			pdfReader.read("/home/app/email/papers");
			//			 pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
