package com.caijing.util;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("IdUtil")
public class IDPathUtil {

	@Autowired
	private Config config = null;

	/**
	 * ����id��ȡ·������ (����id=1234567KV ·����/K/V)
	 * 
	 * @param id
	 * @return
	 */
	private String getIDPath(String id) {
		String path = "/" + id.charAt(id.length() - 2) + "/" + id.charAt(id.length() - 1);
		return path;
	}

	/**
	 * ����id�����ڻ�ȡ ��/��/�� ·������(����id=1234567KV date=2008-12-20 ·��: /2008/12/20/K/V)
	 * 
	 * @param id
	 * @param date
	 * @return ·��
	 */
	private String getYearMonthDayPath(String id, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String path = "/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "/" + id.charAt(id.length() - 2) + "/"
				+ id.charAt(id.length() - 1);

		return path;
	}

	/**
	 * ����ֻ����ʱ���urlpath
	 * @param date
	 * @return
	 */
	private String getYearMonthDayPath(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String path = "/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.DAY_OF_MONTH);
		return path;
	}

	/**
	 * ����id�����ڻ�ȡ ��/�� ·������(����id=1234567KV date=2008-12-20 ·��: /2008/12/K/V)
	 * 
	 * @param id
	 * @param date
	 * @return ·��
	 */
	private String getYearMonthPath(String id, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String path = "/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ id.charAt(id.length() - 2) + "/" + id.charAt(id.length() - 1);

		return path;
	}

	/**
	 * ��ȡid��1λ������ʶ���+8λid��
	 * 
	 * @return
	 */
	public String getId() {
		return config.getValue("hostID") + ServerUtil.getid();
	}

	/**
	 * ��ȡ����·��xml�ļ�·��
	 * @param vid
	 * @param topicid
	 * @return
	 */
	public String getMasterLiveFilePath(String masterid, Date date) {
		return config.getProperty("masterPath") + masterid + "/live/" + DateTools.getYear(date) + "/"
				+ DateTools.transformYYYYMMDDDate(date) + ".html";
	}

	public String getMasterFilePath(String vid, String product) {
		return config.getValue("masterPath") + getIDPath(vid) + "/" + product + "_" + vid + ".xml";
	}

	/**
	 * ��ȡԴ��Ƶ���·��
	 * 
	 * @param filename
	 *            �û��ϴ��ļ���
	 * @param id
	 *            ��Ƶid
	 * @param date
	 *            ��������
	 * @return ���·��
	 */
	public String getRawFilePath(String filename, String id, Date date) {
		String suffix = filename.substring(filename.lastIndexOf("."));
		return config.getValue("rawpath") + getYearMonthDayPath(id, date) + "/" + id + suffix;
	}

	/**
	 * ��ȡqik�ϴ���Դ��Ƶ���·��
	 * @param filename
	 * @param date
	 * @return
	 */
	public String getQikRawFilePath(String filename, Date date) {
		return config.getValue("rawpath") + getYearMonthDayPath(date) + "/" + filename;
	}

	/**
	 * ��ȡ������ļ����·��
	 * 
	 * @param id
	 *            ��Ƶid
	 * @param date
	 *            ��������
	 * @param suffix
	 *            ѹ����׺
	 * @return ���·��
	 */
	public String getEncVideoPath(String id, Date date, String suffix) {
		//String newid = idConvert(id);
		return config.getValue("repopath") + getYearMonthPath(id, date) + "/" + id + "." + suffix;
	}

	/**
	 * ��ȡ������ļ�����url
	 * 
	 * @param id
	 *            ��Ƶid
	 * @param date
	 *            ��������
	 * @param suffix
	 *            ѹ����׺
	 * @return ����url
	 */
	public String getEncVideoURL(String id, Date date, String suffix) {
		//String newid = idConvert(id);
		return config.getValue("cdnHost") + getYearMonthPath(id, date) + "/" + id + "." + suffix;
	}

	/**
	 * ��ȡ������̱�׼�����־�ļ�·��
	 * 
	 * @param id
	 *            ��Ƶid
	 * @param date
	 *            ��������
	 * @return ·���ַ���
	 */
	public String getStdLogPath(String id, Date date) {
		return config.getValue("logpath") + getYearMonthPath(id, date) + "/" + id + "_std.log";
	}

	/**
	 * ��ȡ������̴��������־�ļ�·��
	 * 
	 * @param id
	 *            ��Ƶid
	 * @param date
	 *            ��������
	 * @return ·���ַ���
	 */
	public String getErrLogPath(String id, Date date) {
		return config.getValue("logpath") + getYearMonthPath(id, date) + "/" + id + "_err.log";
	}

	/**
	 * ��ȡͼƬ·��
	 * 
	 * @param id
	 * @param date
	 * @return
	 */
	public String getSnapShotPath(String id, Date date) {
		return config.getValue("snapshotpath") + getYearMonthPath(id, date) + "/" + id;
	}

	/**
	 * ��ȡͼƬ
	 * 
	 * @param id
	 * @param date
	 * @return
	 */
	public String[] getSnapShotURLs(String id, Date date, int count) {
		String[] urls = new String[count];
		for (int i = 0; i < count; i++) {
			urls[i] = config.getValue("snapshoturl") + getYearMonthPath(id, date) + "/" + id + "_" + (i + 1) + ".jpg";
		}
		return urls;
	}

}
