package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.NoticeDao;
import com.caijing.domain.Notice;
import com.caijing.util.CrudDaoDefault;

public class NoticeDaoImpl extends CrudDaoDefault implements NoticeDao {

	@Override
	public List<Notice> getNotices(int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("length", length);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getNotices");
	}

	@Override
	public int getNoticesCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getNoticesCount");
	}

}
