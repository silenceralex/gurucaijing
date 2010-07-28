package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFReader {

	public void read(String path) throws Exception {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile() && f.getAbsolutePath().contains(".pdf")) {
					System.out.println("path:"+f.getAbsolutePath());
					readFdf(f.getAbsolutePath());
				} else if (f.isDirectory()) {
					read(f.getAbsolutePath());
				}
			}
		}
	}

	public void readFdf(String file) {
		// �Ƿ�����
		boolean sort = false;
		// pdf�ļ���
		String pdfFile = file;
		// �����ı��ļ�����
		String textFile = null;
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
			if (fileName.length() > 4) {
				textFile=fileName.substring(0, fileName.length() - 4)+ ".txt";
				File outputFile = new File(textFile);
//				textFile = outputFile.getName();
			}
			System.out.println("textFile��"+textFile);
			// �ļ���������д���ļ���textFile
			output = new OutputStreamWriter(new FileOutputStream(textFile),
					encoding);
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
//			pdfReader.read("F:\\email\\�о�����7.07");
			pdfReader.read("/home/app/email/papers");
//			 pdfReader.readFdf("/home/email/papers/20100608/zx.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
