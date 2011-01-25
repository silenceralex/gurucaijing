package com.caijing.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.caijing.util.CrudDao;

public interface MasterMessageDao extends CrudDao {
	/**
	 * 从某天的第num条后的直播信息
	 * @param date 需用dateTools处理过的yyyy-MM-dd格式
	 * @param num
	 * @return
	 */
	List<Map> getMessagesFrom(int masterid, String date, int num);

	/**
	 * 
	 * @param masterid
	 * @param date  需用dateTools处理过的yyyy-MM-dd格式
	 * @return
	 */
	List<Map> getMessagesByMasteridDate(int masterid, String date);

	List<Date> getDatesByMasterid(int masterid);

	Integer getCurrentNumByMasterid(int masterid, String date);
}
