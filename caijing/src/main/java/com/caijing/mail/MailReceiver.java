package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;

import com.caijing.util.Command;
import com.sun.mail.pop3.POP3Message;

public class MailReceiver {
	private static Log logger = LogFactory.getLog(MailReceiver.class);
	private static final String path = "/home/email/papers";

	public static void main(String[] args) {

		MailReceiver receiver = new MailReceiver();
		// receiver.setHost("pop3.126.com");
		// receiver.setUsername("bg20052008");//您的邮箱账号
		// receiver.setPassword("336699");//您的邮箱密码
		receiver.setHost("pop3.126.com");
		receiver.setUsername("bg20052008");// 您的邮箱账号
		receiver.setPassword("336699");// 您的邮箱密码
		// receiver.setAttachPath("f:\\email");//您要存放附件在什么位置？绝对路径
		receiver.setAttachPath("/home/email");// 您要存放附件在什么位置？绝对路径
		try {
			receiver.reveiveMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reveiveMail() throws Exception {

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("pop3");
		store.connect(getHost(), getUsername(), getPassword());

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		// Message message[] = folder.getMessages(arg0, arg1);
		Message message[] = folder.getMessages();
		// System.out.println("Messages''s length: " + message.length);
		int count = folder.getMessageCount();
		// int count = folder.getNewMessageCount();
		System.out.println("Messages''s count: " + count);

		// FetchProfile profile = new FetchProfile();
		// profile.add(FetchProfile.Item.ENVELOPE);
		// folder.fetch(message, profile);
		// Message mess=folder.getMessage(4);
		// handleMultipart(mess);
		for (int i = 0; i < message.length; i++) {
			// POP3Message message2=(POP3Message)message[0];
			// message[i].setFlag(Flags.Flag.DELETED,
			// true);//必须先设置：folder.open(Folder.READ_WRITE);
			System.out.println("%%%%%%%%%%%%%%%%%正在处理第:" + i
					+ " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
			handleMultipart(message[i]);
			((POP3Message) message[i]).invalidate(true);
			System.out.println("%%%%%%%%%%%%%%%%%处理完毕第:" + i
					+ " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
		}
		if (folder != null) {
			folder.close(true);
		}
		if (store != null) {
			store.close();
		}
	}

	private void handleMultipart(Message msg) throws Exception {

		String disposition = null;
		handle(msg);
		Multipart mp = (Multipart) msg.getContent();
		int mpCount = mp.getCount();
		System.out.println("mpCount:" + mpCount);
		for (int m = 0; m < mpCount; m++) {
			BodyPart part = mp.getBodyPart(m);
			disposition = part.getDisposition();
			if (disposition != null && disposition.equals(Part.ATTACHMENT)) {
				saveAttach(part, getAttachPath(), msg.getSubject(), msg
						.getSentDate());
			} else {
				System.out.println("!!!!!!! NO ATTACHMENT Fund!!!!! 　body  NO."
						+ m + "  part＄＄＄＄＄＄＄＄＄＄＄＄＄");
				System.out.println(part.getContent());
			}
		}
	}

	private static void handle(Message msg) throws Exception {

		System.out.println("邮件主题:" + msg.getSubject());// 测试
		System.out.println("邮件作者:" + msg.getFrom()[0].toString());
		System.out.println("发送日期:" + msg.getSentDate());
	}

	private static void saveAttach(BodyPart part, String filePath,
			String title, Date date) throws Exception {

		String temp = part.getFileName();
		// String fileName = part.getFileName();
		String s = temp.substring(8, temp.indexOf("?="));
		String fileName = base64Decoder(s);
		System.out.println("有附件:" + fileName);

		InputStream in = part.getInputStream();
		// Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dstr = sdf.format(date);
		filePath += "/" + title;
		File dir = new File(filePath + "/" + title);
		System.out.println("路径:" + dir.getAbsolutePath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File ddir = new File(path + "/" + dstr);
		if (!ddir.exists()) {
			ddir.mkdirs();
		}
		fileName = filePath + "/" + fileName;
		FileOutputStream writer = new FileOutputStream(new File(fileName));
		byte[] content = new byte[255];
		while ((in.read(content)) != -1) {
			writer.write(content);
		}
		writer.close();
		in.close();
		String commendStr = "unrar e " + fileName + " " + path + "/" + dstr;
		StringWriter sw = new StringWriter();
		Command.run(commendStr, sw);
		logger.debug(sw.toString());
	}

	private static String base64Decoder(String s) throws Exception {

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(s);

		return (new String(b));
	}

	private String host = null;
	private String username = null;
	private String password = null;
	private String attachPath = null;

	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
