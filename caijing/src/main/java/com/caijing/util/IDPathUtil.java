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
	 * 按照id获取路径规则 (例：id=1234567KV 路径：/K/V)
	 * 
	 * @param id
	 * @return
	 */
	private String getIDPath(String id) {
		String path = "/" + id.charAt(id.length() - 2) + "/" + id.charAt(id.length() - 1);
		return path;
	}

	/**
	 * 按照id和日期获取 年/月/日 路径规则(例：id=1234567KV date=2008-12-20 路径: /2008/12/20/K/V)
	 * 
	 * @param id
	 * @param date
	 * @return 路径
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
	 * 仅仅只考虑时间的urlpath
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
	 * 按照id和日期获取 年/月 路径规则(例：id=1234567KV date=2008-12-20 路径: /2008/12/K/V)
	 * 
	 * @param id
	 * @param date
	 * @return 路径
	 */
	private String getYearMonthPath(String id, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String path = "/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ id.charAt(id.length() - 2) + "/" + id.charAt(id.length() - 1);

		return path;
	}

	/**
	 * 获取id（1位服务器识别号+8位id）
	 * 
	 * @return
	 */
	public String getId() {
		return config.getValue("hostID") + ServerUtil.getid();
	}

	/**
	 * 获取播放路径xml文件路径
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
	 * 获取源视频存放路径
	 * 
	 * @param filename
	 *            用户上传文件名
	 * @param id
	 *            视频id
	 * @param date
	 *            创建日期
	 * @return 存放路径
	 */
	public String getRawFilePath(String filename, String id, Date date) {
		String suffix = filename.substring(filename.lastIndexOf("."));
		return config.getValue("rawpath") + getYearMonthDayPath(id, date) + "/" + id + suffix;
	}

	/**
	 * 获取qik上传的源视频存放路径
	 * @param filename
	 * @param date
	 * @return
	 */
	public String getQikRawFilePath(String filename, Date date) {
		return config.getValue("rawpath") + getYearMonthDayPath(date) + "/" + filename;
	}

	/**
	 * 获取编码后文件存放路径
	 * 
	 * @param id
	 *            视频id
	 * @param date
	 *            创建日期
	 * @param suffix
	 *            压缩后缀
	 * @return 存放路径
	 */
	public String getEncVideoPath(String id, Date date, String suffix) {
		//String newid = idConvert(id);
		return config.getValue("repopath") + getYearMonthPath(id, date) + "/" + id + "." + suffix;
	}

	/**
	 * 获取编码后文件访问url
	 * 
	 * @param id
	 *            视频id
	 * @param date
	 *            创建日期
	 * @param suffix
	 *            压缩后缀
	 * @return 访问url
	 */
	public String getEncVideoURL(String id, Date date, String suffix) {
		//String newid = idConvert(id);
		return config.getValue("cdnHost") + getYearMonthPath(id, date) + "/" + id + "." + suffix;
	}

	/**
	 * 获取编码进程标准输出日志文件路径
	 * 
	 * @param id
	 *            视频id
	 * @param date
	 *            创建日期
	 * @return 路径字符串
	 */
	public String getStdLogPath(String id, Date date) {
		return config.getValue("logpath") + getYearMonthPath(id, date) + "/" + id + "_std.log";
	}

	/**
	 * 获取编码进程错误输出日志文件路径
	 * 
	 * @param id
	 *            视频id
	 * @param date
	 *            创建日期
	 * @return 路径字符串
	 */
	public String getErrLogPath(String id, Date date) {
		return config.getValue("logpath") + getYearMonthPath(id, date) + "/" + id + "_err.log";
	}

	/**
	 * 获取图片路径
	 * 
	 * @param id
	 * @param date
	 * @return
	 */
	public String getSnapShotPath(String id, Date date) {
		return config.getValue("snapshotpath") + getYearMonthPath(id, date) + "/" + id;
	}

	/**
	 * 获取图片
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
