package com.caijing.mail;

public class MailSchedule {

	public void processMail() {
		MailReceiver receiver = new MailReceiver();
//		receiver.setHost("pop.126.com");
		receiver.setUsername("bg20052008");// 您的邮箱账号
		receiver.setPassword("336699");// 您的邮箱密码
		// receiver.setAttachPath("f:\\email");//您要存放附件在什么位置？绝对路径
		receiver.setAttachPath("/home/app/email");// 您要存放附件在什么位置？绝对路径
		try {
			receiver.reveiveMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
