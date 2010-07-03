package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

import sun.misc.BASE64Decoder;

import com.caijing.util.Command;
import com.caijing.util.UrlDownload;

public class MailReceiver {
	private static Log logger = LogFactory.getLog(MailReceiver.class);
	private static Pattern titlePattern = Pattern
			.compile(
					"<A.*?expiretime=\"(.*?)\" filesize=\"(.*?)\".*?download=\"(.*?)\">(.*?)</A>",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL
							| Pattern.UNIX_LINES);
	private static Pattern linkPattern = Pattern.compile(
			"downloadlink = '(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
					| Pattern.UNIX_LINES);

	private static final String path = "/home/email/papers";
	
	UrlDownload down=new UrlDownload();
	
	private static String cookie="vjuids=3abc49a70.128ca9a09ee.0.596522c96f3c68; vjlast=1274709412.1277905501.11; _ntes_nnid=6ad9fc27892d32e9e55c8abadefe2d49,0; _ntes_nuid=6ad9fc27892d32e9e55c8abadefe2d49; P_INFO=bg20052008@126.com|1278173308|0|mail126|11&25|tij&1278162615&mail126#bej&null#0|&0; USERTRACK=221.223.98.3.1274709433709479; ALLYESID4=00100524215726846416; MAIL163_SSN=1983foolish; Province=010; City=010; NTES_REPLY_NICKNAME=johnnychenjun%7Cjohnnychenju%7C1%7C0%7C3%7CKekGfemVIMy0iFkg2fNSypO8IbP912.7ZoOpIjPPY7gJm0bCt9ai_5a3ShO1OtzHAr0uGAoZcnmQ7Bhp1NFZ7CcaDQHZiF_CpJeXQUcveoAaj%7C; NTES_SESS=Ov7WQvMxP1XcYUUvXRSL9UDND2VmbEWxUOoQjeFu5P8GrrO1KJuysxP6qfnE4kdN.NXVeSYzt6YdTcXd2ykO5WtKwJAQg5MalI3lPAT.Ey9vIXz1lJaEbHQsn; S_INFO=1278173308|0|#1&25#; EUSERTRACK=221.223.96.161.1278174467978901; NTES_FS=8d8f2d05e369eec5f5d89efee93d0c6f4435ad641025a88febc47b3e1fac6c7de450ad1789771a23; FSTRACK=1278177188928.546ff5a3-b60f-4f9a-84c2-cc63e7ac56b8";

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
		handleMultipart(message[0]);
		// for (int i = 0; i < message.length; i++) {
		// // POP3Message message2=(POP3Message)message[0];
		// // message[i].setFlag(Flags.Flag.DELETED,
		// // true);//必须先设置：folder.open(Folder.READ_WRITE);
		// System.out.println("%%%%%%%%%%%%%%%%%正在处理第:" + i
		// + " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
		// handleMultipart(message[i]);
		// ((POP3Message) message[i]).invalidate(true);
		// System.out.println("%%%%%%%%%%%%%%%%%处理完毕第:" + i
		// + " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
		// }
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
				Matcher m1 = titlePattern.matcher((String)part.getContent());
				if (m1 != null && m1.find()) {
					String expire = m1.group(1);
					String filesize = m1.group(2);
					String link = m1.group(3);
					String title = m1.group(4);
					System.out.println("expire: "+expire);
					System.out.println("filesize: "+filesize);
					System.out.println("link: "+link);
					System.out.println("title: "+title);
					HttpGet get =new HttpGet(link);
					get.setHeader("Cookie", cookie);
					String content=down.load(get);
					System.out.println("content: "+content);
//					http://download.fs.163.com/dl/?file=rIMMxh7KmcLUDbyuFCHa_lJGm7INBaOElDAPDwuKbo7fAhMXVvKBb8X2hA0felFjH_k1spAQLITnujZJNZQiuA&callback=coremail
					String url=link.replace("http://fs.163.com/fs/display/", "http://download.fs.163.com/dl/")+"&callback=coremail";
					System.out.println("link:"+link);
					System.out.println("link:"+url);
					get =new HttpGet(url);
					get.setHeader("Cookie", cookie);
					get.setHeader("Referer", link);
					down.downAttach(get,"F:\\"+title);
					
				} 
// System.out.println(part.getContent());
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
