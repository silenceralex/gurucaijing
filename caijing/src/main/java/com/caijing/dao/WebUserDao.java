package com.caijing.dao;

import com.caijing.domain.WebUser;
import com.caijing.util.CrudDao;

public interface WebUserDao extends CrudDao {
	public WebUser getUserByEmail(String email);

	//-1Ϊʧ�ܣ�0Ϊ��δ���1Ϊ�ɹ�
	public int identify(String email, String password);
	
	public boolean activate(String uid);

	/**
	 * ����money�����ӻ���٣������ţ�������remain���
	 * @param userid
	 * @param money
	 * @return
	 */
	boolean updateRemainMoney(String userid, float money);
}
