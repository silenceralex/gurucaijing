package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Notice;
import com.caijing.util.CrudDao;

public interface NoticeDao extends CrudDao {
	public List<Notice> getNotices(int start, int length);

	public int getNoticesCount();

}
