package com.caijing.mail;

public class MailSchedule {

	public void processMail() {
		MailReceiver receiver = new MailReceiver();
		receiver.setUsername("bg20052008");// ���������˺�
		receiver.setPassword("336699");// ������������
		receiver.setAttachPath("/home/app/email");// ��Ҫ��Ÿ�����ʲôλ�ã�����·��
		try {
			receiver.reveiveMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("process over!");
	}
}
