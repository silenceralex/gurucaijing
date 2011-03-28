package com.caijing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Title:[POI�����ϵ�Excel���ݶ�ȡ����] Description:
 * [֧��Excell2003,Excell2007,�Զ���ʽ����ֵ������,�Զ���ʽ������������]
 */
public class POIExcelUtil {

	/** ������ */
	private int totalRows = 0;

	/** ������ */
	private int totalCells = 0;
	
	private static final SimpleDateFormat timeFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/** ���췽�� */
	public POIExcelUtil() {
	}

	/**
	 * �����ļ�����ȡexcel�ļ�
	 */
	public List<ArrayList<String>> read(String fileName) {
		List<ArrayList<String>> dataLst = new ArrayList<ArrayList<String>>();

		/** ����ļ����Ƿ�Ϊ�ջ����Ƿ���Excel��ʽ���ļ� */
		if (fileName == null || !fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
			return dataLst;
		}

		boolean isExcel2003 = true;
		/** ���ļ��ĺϷ��Խ�����֤ */
		if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
			isExcel2003 = false;
		}
		
		/** ����ļ��Ƿ���� */
		File file = new File(fileName);
		if (file == null || !file.exists()) {
			return dataLst;
		}

		try {
			/** ���ñ����ṩ�ĸ�������ȡ�ķ��� */
			dataLst = read(new FileInputStream(file), isExcel2003);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/** ��������ȡ�Ľ�� */
		return dataLst;
	}

	/**
	 * <li>Description:[��������ȡExcel�ļ�]</li>
	 */
	public List<ArrayList<String>> read(InputStream inputStream,
			boolean isExcel2003) {
		List<ArrayList<String>> dataLst = null;
		try {
			/** ���ݰ汾ѡ�񴴽�Workbook�ķ�ʽ */
			Workbook wb = isExcel2003 ? new HSSFWorkbook(inputStream)
					: new XSSFWorkbook(inputStream);
			dataLst = read(wb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataLst;
	}

	/**
	 * <li>Description:[�õ�������]</li>
	 * 
	 * @return
	 */
	public int getTotalRows() {
		return totalRows;
	}

	/**
	 * <li>Description:[�õ�������]</li>
	 */
	public int getTotalCells() {
		return totalCells;
	}

	/**
	 * <li>Description:[��ȡ����]</li>
	 */
	private List<ArrayList<String>> read(Workbook wb) {
		List<ArrayList<String>> dataLst = new ArrayList<ArrayList<String>>();

		/** �õ���һ��shell */
		Sheet sheet = wb.getSheetAt(0);
		this.totalRows = sheet.getPhysicalNumberOfRows();
		if (this.totalRows >= 1 && sheet.getRow(0) != null) {
			this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}

		/** ѭ��Excel���� */
		for (int r = 0; r < this.totalRows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}

			ArrayList<String> rowLst = new ArrayList<String>();

			/** ѭ��Excel���� */
			for (short c = 0; c < this.getTotalCells(); c++) {
				Cell cell = row.getCell(c);
				String cellValue = "";
				if (cell == null) {
					rowLst.add(cellValue);
					continue;
				}

				/** ���������͵�,�Զ�ȥ�� */
				if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

					/** ��excel��,����Ҳ������,�ڴ�Ҫ�����ж� */
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						cellValue = timeFORMAT.format(cell.getDateCellValue());
					} else {
						cellValue = getRightStr(cell.getNumericCellValue() + "");
					}
				}
				/** �����ַ����� */
				else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
					cellValue = cell.getStringCellValue();
				}
				/** �������� */
				else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
					cellValue = cell.getBooleanCellValue() + "";
				}

				/** ������,�����ϼ����������� */
				else {
					cellValue = cell.toString() + "";
				}

				rowLst.add(cellValue);
			}
			dataLst.add(rowLst);
		}
		return dataLst;
	}

	/**
	 * <li>Description:[��ȷ�ش����������Զ���������]</li>
	 */
	private String getRightStr(String sNum) {
		DecimalFormat decimalFormat = new DecimalFormat("#.000000");
		String resultStr = decimalFormat.format(new Double(sNum));
		if (resultStr.matches("^[-+]?\\d+\\.[0]+$")) {
			resultStr = resultStr.substring(0, resultStr.indexOf("."));
		}
		return resultStr;
	}

	public static void main(String[] args) throws Exception {
		List<ArrayList<String>> dataLst = new POIExcelUtil()
				.read("e:/Book1.xls");
		for (ArrayList<String> innerLst : dataLst) {
			StringBuffer rowData = new StringBuffer();
			for (String dataStr : innerLst) {
				rowData.append(",").append(dataStr);
			}
			if (rowData.length() > 0) {
				System.out.println(rowData.deleteCharAt(0).toString());
			}
		}
	}
}