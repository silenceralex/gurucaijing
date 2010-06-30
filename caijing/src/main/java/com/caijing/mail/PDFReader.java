package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFReader {
	public void readFdf(String file) throws Exception {
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
			try {
				// ���ȵ���һ��URL��װ���ļ�������õ��쳣�ٴӱ����ļ�ϵͳ//ȥװ���ļ�
				URL url = new URL(pdfFile); //ע������Ѳ�����ǰ�汾�е�URL.����File��
				document = PDDocument.load(pdfFile);
				// ��ȡPDF���ļ���
				String fileName = url.getFile();
				// ��ԭ��PDF�������������²�����txt�ļ�
				if (fileName.length() > 4) {
					File outputFile = new File(fileName.substring(0, fileName.length() - 4) + ".txt");
					textFile = outputFile.getName();
				}
			} catch (MalformedURLException e) {
				// �����ΪURLװ�صõ��쳣����ļ�ϵͳװ��    //ע������Ѳ�����ǰ�汾�е�URL.����File��
				document = PDDocument.load(pdfFile);
				if (pdfFile.length() > 4) {
					textFile = pdfFile.substring(0, pdfFile.length() - 4) + ".txt";
				}
			}
			// �ļ���������д���ļ���textFile
			output = new OutputStreamWriter(new FileOutputStream(textFile), encoding);
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
		} finally {
			if (output != null) {
				// �ر������
				output.close();
			}
			if (document != null) {
				// �ر�PDF Document
				document.close();
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
			pdfReader.readFdf("D:\\financial_web\\�о�����6.01\\����֤ȯ--�����Ҫ.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
