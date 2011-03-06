package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.Notice;
import com.caijing.util.CrudDao;

public interface NoticeDao extends CrudDao {
	public List<Notice> getNotices(int start, int length);

	public int getNoticesCount();

	/**
	 * 按照起始日期获取公告中的某个类别的股票数据
	 * @param type 某个类型的公告数据，type 0 激励 ，2 增持 ，1重组
	 * @param startDate
	 * @return
	 */
	public List<Notice> getNoticeStocksByType(int type, Date startDate, Date endDate);

}
