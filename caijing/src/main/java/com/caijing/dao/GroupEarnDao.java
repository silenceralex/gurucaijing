package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupEarn;
import com.caijing.util.CrudDao;

public interface GroupEarnDao extends CrudDao {
	public GroupEarn getGroupEarnByIDAndDate(String groupid, String date);

	public Date getLatestDate();

	public List<GroupEarn> getGroupEarnRankByDate(Date date);

	public List<GroupEarn> getWeightList(String aid, Date date);

	public List<GroupEarn> getWeightListBetween(String aid, Date startDate, Date endDate);

	/**
	 * 获取某个分析师自date之前最近的一个交易日的组合收益
	 * @param aid 分析师id
	 * @param date 日期
	 * @return
	 */
	public GroupEarn getFormerNearPriceByCodeDate(String aid, Date date);
}
