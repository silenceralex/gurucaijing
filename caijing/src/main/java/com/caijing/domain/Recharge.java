package com.caijing.domain;

import java.util.Date;

public class Recharge {
	long rcid = 0;
	String userid = null;
	float cash = 0;
	//����ʱ��
	Date ctime = null;
	//����ʱ��
	Date lmodify = null;
	//֧�����ͣ�0֧������1�Ƹ�ͨ��2��Ǯ
	int type = 0;
	//֧��״̬��0��֧����1֧���ɹ���-1֧��ʧ��
	int status = 0;

	public long getRcid() {
		return rcid;
	}

	public void setRcid(long rcid) {
		this.rcid = rcid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public Date getLmodify() {
		return lmodify;
	}

	public void setLmodify(Date lmodify) {
		this.lmodify = lmodify;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
