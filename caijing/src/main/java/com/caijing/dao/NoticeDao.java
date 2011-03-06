package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.Notice;
import com.caijing.util.CrudDao;

public interface NoticeDao extends CrudDao {
	public List<Notice> getNotices(int start, int length);

	public int getNoticesCount();

	/**
	 * ������ʼ���ڻ�ȡ�����е�ĳ�����Ĺ�Ʊ����
	 * @param type ĳ�����͵Ĺ������ݣ�type 0 ���� ��2 ���� ��1����
	 * @param startDate
	 * @return
	 */
	public List<Notice> getNoticeStocksByType(int type, Date startDate, Date endDate);

}
