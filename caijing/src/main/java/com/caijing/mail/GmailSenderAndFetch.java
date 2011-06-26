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
 * ʹ��Gmail�����ʼ�
 */
public class GmailSenderAndFetch {

	private static String messageContentMimeType = "text/html; charset=gb2312";

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	public static Properties getProperties() {
		Properties props = System.getProperties();
		// props.setProperty("mail.imap.host", "imap.gmail.com");
		// Gmail�ṩ��POP3��SMTP��ʹ�ð�ȫ�׽��ֲ�SSL��
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
	 * �����ʼ�
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

		// �������ߣ�������ʹ��setFrom()��setReplyTo()������
	
		try {
			msg.setFrom(new InternetAddress(username,"51gurus"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		msg.addFrom(InternetAddress.parse("chenjun"));// ��ַ��Դ,û����?
		msg.setReplyTo(InternetAddress.parse("51gurus"));// �ظ�ʱ�õĵ�ַ
		// ��Ϣ������
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(receiver, false));
		msg.setSubject("��ӭע��51gurus��ֵͶ����վ����ȡͶ����Ѷ������Ͷ���ĵ�");
		msg.setSentDate(new Date());
		
		String date=DateTools.transformYYYYMMDDDate(new Date());
		VMFactory introvmf = new VMFactory();
		introvmf.setTemplate("/template/mail/mailcontent.htm");
		introvmf.put("link", link);
		introvmf.put("date", date);
		introvmf.put("email", receiver);
		
		String content =introvmf.toString();
		System.out.println("send mail content:"+content);
//		String content = "�𾴵��û���" +
//				"��л��ע��51gurus��վ�������������ӽ��м��\r\n" +
//				link;
		// �ʼ��������ݣ�Content��
		msg.setContent(buildMimeMultipart(content,null));
//		msg.setContent(buildMimeMultipart(content, new Vector() {
//			{
//				add("F:/test.txt");
//			}
//		}));

		return msg;
	}

	/**
	 * �����ʼ������ĺ͸���
	 * 
	 * @param msgContent
	 * @param attachedFileList
	 * @return
	 * @throws MessagingException
	 */
	public static Multipart buildMimeMultipart(String msgContent,
			Vector attachedFileList) throws MessagingException {
		Multipart mPart = new MimeMultipart();// �ಿ��ʵ��

		// �ʼ�����
		MimeBodyPart mBodyContent = new MimeBodyPart();// MIME�ʼ�����
		if (msgContent != null) {
			mBodyContent.setContent(msgContent, messageContentMimeType);
		} else {
			mBodyContent.setContent("", messageContentMimeType);
		}
		mPart.addBodyPart(mBodyContent);

		// ����
		String file;
		String fileName;
		if (attachedFileList != null) {
			for (Enumeration fileList = attachedFileList.elements(); fileList
					.hasMoreElements();) {
				file = (String) fileList.nextElement();
				fileName = file.substring(file.lastIndexOf("/") + 1);
				MimeBodyPart mBodyPart = new MimeBodyPart();
				// Զ����Դ
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
	 * �ִ�����
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
	 * �����ʼ�
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
	 * ��ȡ����
	 * 
	 * @param part
	 */
	public static void extractPart(MimeBodyPart part) {
		try {
			String disposition = part.getDisposition();

			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition
							.equalsIgnoreCase(Part.INLINE))) {// ����
				String fileName = decodeText(part.getFileName());
				System.out.println(fileName);
				saveAttachFile(part);// ���渽��
			} else {// ����
				if (part.getContent() instanceof String) {// ���յ��Ĵ��ı�
					System.out.println(part.getContent());
				}
				if (part.getContent() instanceof MimeMultipart) {// ���յ��ʼ��и���ʱ
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
	 * ���渽��
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
	 * �����ʼ�
	 * 
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static void sendRegistMail(String receive,String link) throws AddressException,
			MessagingException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		// Transport ������������Ϣ��
		Transport.send(buildMimeMessage("shadowcj@gmail.com", "cjlovelily",
				receive,link));

		System.out.println("Message send...");

	}

	/**
	 * ȡ�ʼ���Ϣ
	 * 
	 * @throws Exception
	 */
	public static void fetchMail() throws Exception {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Session session = Session.getDefaultInstance(getProperties(), null);
		// ��popgmail.com", 995, null,"[�����ʺ�]", "[��������]");
		// ��IMAPЭ��
		URLName urln = new URLName("imap", "imap.gmail.com", 993, null,
				"shadowcj", "cjlovelily");
		Store store = null;
		Folder inbox = null;
		try {
			// Store��������,Store��ʵ���ض��ʼ�Э���ϵĶ���д�����ӡ����ҵȲ�����
			store = session.getStore(urln);
			store.connect();
			inbox = store.getFolder("INBOX");// �ռ���
			inbox.open(Folder.READ_ONLY);
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			Message[] messages = inbox.getMessages();
			inbox.fetch(messages, profile);
			System.out.println("�ռ�����ʼ�����" + messages.length);
			System.out.println("δ���ʼ�����" + inbox.getUnreadMessageCount());
			System.out.println("���ʼ�����" + inbox.getNewMessageCount());

			for (int i = 0; i < messages.length; i++) {
				// �ʼ�������
				String from = decodeText(messages[i].getFrom()[0].toString());
				InternetAddress ia = new InternetAddress(from);
				System.out.println("FROM:" + ia.getPersonal() + '('
						+ ia.getAddress() + ')');
				// �ʼ�����
				System.out.println("TITLE:" + messages[i].getSubject());
				// �ʼ�����
				parseMailContent(messages[i].getContent());

				// �ʼ���С
				System.out.println("SIZE:" + messages[i].getSize());
				// �ʼ�����ʱ��
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
