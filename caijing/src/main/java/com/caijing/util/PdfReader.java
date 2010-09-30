package com.caijing.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfReader {
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

	public void readFirstFdf(String file, String outPath) {
		boolean sort = false;
		String pdfFile = file;
		String encoding = "gbk";
		int startPage = 1;
		int endPage = 1;
		Writer output = null;
		PDDocument document = null;
		System.out.println("textFile:" + outPath);
		try {
			//			Document d =
			document = PDDocument.load(pdfFile);
			//			LucenePDFDocument.getDocument(new File(file));
			PDDocumentInformation info = document.getDocumentInformation();
			System.out.println("Page Count=" + document.getNumberOfPages());
			System.out.println("Title=" + info.getTitle());
			System.out.println("Author=" + info.getAuthor());
			System.out.println("Subject=" + info.getSubject());
			System.out.println("Keywords=" + info.getKeywords());
			System.out.println("Creator=" + info.getCreator());
			System.out.println("Producer=" + info.getProducer());
			System.out.println("Creation Date=" + info.getCreationDate());
			System.out.println("Modification Date=" + info.getModificationDate());
			System.out.println("Trapped=" + info.getTrapped());
			
			StringBuffer sb=new StringBuffer();
			
			output = new OutputStreamWriter(new FileOutputStream(outPath), encoding);
			BufferedWriter bw=new BufferedWriter(output);
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			stripper.setSortByPosition(false);
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

	public static void main(String[] args) {
		PdfReader reader = new PdfReader();
		reader.readFdf("E:\\test\\6GMNS40C.pdf", "E:\\test\\6GMNS40C_1.txt");
		reader.readFirstFdf("E:\\test\\6GMNS40C.pdf", "E:\\test\\6GMNS40C_2.txt");
	}

}
