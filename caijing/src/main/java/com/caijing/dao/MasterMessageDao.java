package com.caijing.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.caijing.util.CrudDao;

public interface MasterMessageDao extends CrudDao {
	/**
	 * ��ĳ��ĵ�num�����ֱ����Ϣ
	 * @param date ����dateTools�������yyyy-MM-dd��ʽ
	 * @param num
	 * @return
	 */
	List<Map> getMessagesFrom(int masterid, String date, int num);

	/**
	 * 
	 * @param masterid
	 * @param date  ����dateTools�������yyyy-MM-dd��ʽ
	 * @return
	 */
	List<Map> getMessagesByMasteridDate(int masterid, String date);

	List<Date> getDatesByMasterid(int masterid);

	Integer getCurrentNumByMasterid(int masterid, String date);
}
