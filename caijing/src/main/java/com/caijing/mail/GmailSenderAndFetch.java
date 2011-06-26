package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.caijing.flush.VMFactory;
import com.caijing.util.DateTools;

/**
 * 使用Gmail发送邮件
 */
public class GmailSenderAndFetch {

	private static String messageContentMimeType = "text/html; charset=gb2312";

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	public static Properties getProperties() {
		Properties props = System.getProperties();
		// props.setProperty("mail.imap.host", "imap.gmail.com");
		// Gmail提供的POP3和SMTP是使用安全套接字层SSL的
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.auth", "true");

		// props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		// props.setProperty("mail.imap.socketFactory.fallback", "false");
		// props.setProperty("mail.imap.port", "993");
		// props.setProperty("mail.imap.socketFactory.port", "993");

		// props.put("mail.smtp.auth", "true");
		return props;
	}

	/**
	 * 构建邮件
	 * 
	 * @param username
	 * @param password
	 * @param receiver
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	public static Message buildMimeMessage(final String username,
			final String password, String receiver,String link) throws AddressException,
			MessagingException {
		Session session = Session.getDefaultInstance(getProperties(),
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		Message msg = new MimeMessage(session);

		// 鉴别发送者，您可以使用setFrom()和setReplyTo()方法。
	
		try {
			msg.setFrom(new InternetAddress(username,"51gurus"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		msg.addFrom(InternetAddress.parse("chenjun"));// 地址来源,没作用?
		msg.setReplyTo(InternetAddress.parse("51gurus"));// 回复时用的地址
		// 消息接收者
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(receiver, false));
		msg.setSubject("欢迎注册51gurus价值投资网站，获取投资资讯，交流投资心得");
		msg.setSentDate(new Date());
		
		String date=DateTools.transformYYYYMMDDDate(new Date());
		VMFactory introvmf = new VMFactory();
		introvmf.setTemplate("/template/mail/mailcontent.htm");
		introvmf.put("link", link);
		introvmf.put("date", date);
		introvmf.put("email", receiver);
		
		String content =introvmf.toString();
		System.out.println("send mail content:"+content);
//		String content = "尊敬的用户：" +
//				"感谢您注册51gurus网站，请点击如下链接进行激活：\r\n" +
//				link;
		// 邮件内容数据（Content）
		msg.setContent(buildMimeMultipart(content,null));
//		msg.setContent(buildMimeMultipart(content, new Vector() {
//			{
//				add("F:/test.txt");
//			}
//		}));

		return msg;
	}

	/**
	 * 构建邮件的正文和附件
	 * 
	 * @param msgContent
	 * @param attachedFileList
	 * @return
	 * @throws MessagingException
	 */
	public static Multipart buildMimeMultipart(String msgContent,
			Vector attachedFileList) throws MessagingException {
		Multipart mPart = new MimeMultipart();// 多部分实现

		// 邮件正文
		MimeBodyPart mBodyContent = new MimeBodyPart();// MIME邮件段体
		if (msgContent != null) {
			mBodyContent.setContent(msgContent, messageContentMimeType);
		} else {
			mBodyContent.setContent("", messageContentMimeType);
		}
		mPart.addBodyPart(mBodyContent);

		// 附件
		String file;
		String fileName;
		if (attachedFileList != null) {
			for (Enumeration fileList = attachedFileList.elements(); fileList
					.hasMoreElements();) {
				file = (String) fileList.nextElement();
				fileName = file.substring(file.lastIndexOf("/") + 1);
				MimeBodyPart mBodyPart = new MimeBodyPart();
				// 远程资源
				// URLDataSource uds=new
				// URLDataSource(http://www.javaeye.com/logo.gif);
				FileDataSource fds = new FileDataSource(file);
				mBodyPart.setDataHandler(new DataHandler(fds));
				mBodyPart.setFileName(fileName);
				mPart.addBodyPart(mBodyPart);
			}
		}

		return mPart;
	}

	/**
	 * 字串解码
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected static String decodeText(String text)
			throws UnsupportedEncodingException {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
			text = MimeUtility.decodeText(text);
		} else {
			text = new String(text.getBytes("ISO8859_1"));
		}
		return text;
	}

	/**
	 * 分析邮件
	 * 
	 * @param mPart
	 */
	public static void parseMailContent(Object content) {
		try {
			if (content instanceof Multipart) {
				Multipart mPart = (MimeMultipart) content;
				for (int i = 0; i < mPart.getCount(); i++) {
					extractPart((MimeBodyPart) mPart.getBodyPart(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 抽取内容
	 * 
	 * @param part
	 */
	public static void extractPart(MimeBodyPart part) {
		try {
			String disposition = part.getDisposition();

			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition
							.equalsIgnoreCase(Part.INLINE))) {// 附件
				String fileName = decodeText(part.getFileName());
				System.out.println(fileName);
				saveAttachFile(part);// 保存附件
			} else {// 正文
				if (part.getContent() instanceof String) {// 接收到的纯文本
					System.out.println(part.getContent());
				}
				if (part.getContent() instanceof MimeMultipart) {// 接收的邮件有附件时
					BodyPart bodyPart = ((MimeMultipart) part.getContent())
							.getBodyPart(0);
					System.out.println(bodyPart.getContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存附件
	 * 
	 * @param part
	 */
	public static void saveAttachFile(Part part) {
		try {
			if (part.getDisposition() == null)
				return;

			String dir = "F:/uploadDir/";
			String filename = decodeText(part.getFileName());

			InputStream in = part.getInputStream();
			OutputStream out = new FileOutputStream(new File(dir + filename));

			byte[] buffer = new byte[8192];
			while (in.read(buffer) != -1) {
				out.write(buffer);
			}
			in.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static void sendRegistMail(String receive,String link) throws AddressException,
			MessagingException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		// Transport 是用来发送信息的
		Transport.send(buildMimeMessage("shadowcj@gmail.com", "cjlovelily",
				receive,link));

		System.out.println("Message send...");

	}

	/**
	 * 取邮件信息
	 * 
	 * @throws Exception
	 */
	public static void fetchMail() throws Exception {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Session session = Session.getDefaultInstance(getProperties(), null);
		// 用popgmail.com", 995, null,"[邮箱帐号]", "[邮箱密码]");
		// 用IMAP协议
		URLName urln = new URLName("imap", "imap.gmail.com", 993, null,
				"shadowcj", "cjlovelily");
		Store store = null;
		Folder inbox = null;
		try {
			// Store用来收信,Store类实现特定邮件协议上的读、写、监视、查找等操作。
			store = session.getStore(urln);
			store.connect();
			inbox = store.getFolder("INBOX");// 收件箱
			inbox.open(Folder.READ_ONLY);
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			Message[] messages = inbox.getMessages();
			inbox.fetch(messages, profile);
			System.out.println("收件箱的邮件数：" + messages.length);
			System.out.println("未读邮件数：" + inbox.getUnreadMessageCount());
			System.out.println("新邮件数：" + inbox.getNewMessageCount());

			for (int i = 0; i < messages.length; i++) {
				// 邮件发送者
				String from = decodeText(messages[i].getFrom()[0].toString());
				InternetAddress ia = new InternetAddress(from);
				System.out.println("FROM:" + ia.getPersonal() + '('
						+ ia.getAddress() + ')');
				// 邮件标题
				System.out.println("TITLE:" + messages[i].getSubject());
				// 邮件内容
				parseMailContent(messages[i].getContent());

				// 邮件大小
				System.out.println("SIZE:" + messages[i].getSize());
				// 邮件发送时间
				System.out.println("DATE:" + messages[i].getSentDate());
			}
		} finally {
			try {
				inbox.close(false);
			} catch (Exception e) {
			}
			try {
				store.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		GmailSenderAndFetch mail = new GmailSenderAndFetch();
		try {
			// mail.fetchMail();
			String link="http://51gurus.com/validate/uid/v123-w234921234";
			mail.sendRegistMail("johnnychenjun@163.com",link);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
