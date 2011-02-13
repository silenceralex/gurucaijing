package com.caijing.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

import sun.misc.BASE64Decoder;

import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.ReportDao;
import com.caijing.dao.ibatis.ReportDaoImpl;
import com.caijing.util.Command;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.UrlDownload;

public class MailReceiver {
	private static Log logger = LogFactory.getLog(MailReceiver.class);
	private static Pattern titlePattern = Pattern.compile(
			"<A.*?expiretime=\"(.*?)\" filesize=\"(.*?)\".*?download=\"(.*?)\">(.*?)</A>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);
	private static Pattern linkPattern = Pattern.compile("downloadlink = '(.*?)'", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static Pattern expiredPattern = Pattern.compile("lExpiredTime = '(.*?)';", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static final String path = "/home/app/papers";
	private static final String timeStamp = "/home/app/timeStamp";
	private static final String flag = "/home/app/flag";
	// private static final String path = "f:/email/papers";
	PDFReader reader = null;
	UrlDownload down = new UrlDownload();

	private static String cookie = "vjuids=-bc3d49d8.12dfec5ffe3.0.5c0807a494ae88; vjlast=1297059545.1297474583.11; _ntes_nnid=b9bdb732e41d6be11bc3e55bbbe07708,0; _ntes_nuid=b9bdb732e41d6be11bc3e55bbbe07708; ntes_ucc=c001_0|c033_0|c017_0; NTES_UFC=0110001001110000000000000000000000000000000000000000000000000000; P_INFO=bg20052008@126.com|1297565469|0|mail126|11&25|bej&1297499474&mail126#bej&null#10|&0; USERTRACK=61.135.255.86.1297229299267745; ALLYESID4=00110210153825505809785; NTES_SESS=pvD8hiY2A2lhWGmazwsy4.ZV3hXmEbau3o_cHlftNgBLHHgNrdI_mFLWc48p5sokukC2P63.iW3o7TCot_sgKBirMdJwQKAjX2I5p.qbv4m5AgPD9QSPEStHLl.Sgw784HxpUpiOIgRz0; S_INFO=1297565469|0|#1&25#; EUSERTRACK=221.223.119.57.1297566942921051; NTES_FS=48dce3327d38e9f336230be9545c6ddf4435ad641025a88febc47b3e1fac6c7de450ad1789771a23";

	public static void main(String[] args) {

		MailReceiver receiver = new MailReceiver();
		ReportExtractorImpl extractor = (ReportExtractorImpl) ContextFactory.getBean("reportExtractor");
		ReportDao reportDao = (ReportDaoImpl) ContextFactory.getBean("reportDao");
		PDFReader reader = new PDFReader();
		reader.setExtractor(extractor);
		reader.setReportDao(reportDao);
		reader.init();
		receiver.setReader(reader);
		// receiver.setHost("pop3.126.com");
		// receiver.setUsername("bg20052008");//您的邮箱账号
		// receiver.setPassword("336699");//您的邮箱密码
		receiver.setHost("pop.126.com");
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

	/**
	 * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】
	 */
	public boolean isSeen(Message mimeMessage) throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				System.out.println("seen Message.......");
				break;
			}
		}
		return isnew;
	}

	public void reveiveMail() throws Exception {

		Properties props = new Properties();
		// props.setProperty("mail.imap.host", "imap.126.com");
		// props.setProperty("mail.imap.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		// props.setProperty("mail.imap.socketFactory.fallback", "false");
		// props.setProperty("mail.imap.port", "143");
		// props.setProperty("mail.imap.socketFactory.port", "143");
		Session session = Session.getDefaultInstance(props, null);
		URLName url = new URLName("pop3", "pop.126.com", 110, null, "bg20052008", "336699");

		Store store = session.getStore(url);
		store.connect();
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message message[] = folder.getMessages();
		int count = folder.getMessageCount();
		// int count = folder.getNewMessageCount();
		System.out.println("Messages''s count: " + count);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// FetchProfile profile = new FetchProfile();
		// profile.add(FetchProfile.Item.ENVELOPE);
		// folder.fetch(message, profile);
		// Message mess=folder.getMessage(4);
		// handleMultipart(mess);
		// handleMultipart(message[0]);
		String lasttime = "";
		String judgetime = FileUtil.read(timeStamp, "GBK");
		System.out.println("judgetime:" + judgetime);
		Date date = null;
		Date lastdate = null;
		if (judgetime == null || judgetime.trim().length() == 0) {
			date = sdf.parse("2010-08-22 00:00:00");
			lastdate = sdf.parse("2010-08-22 00:00:00");
		} else {
			date = sdf.parse(judgetime);
			lastdate = sdf.parse(judgetime);
		}
		for (int i = 0; i < message.length && i < count; i++) {
			// POP3Message message2=(POP3Message)message[0];
			// message[i].setFlag(Flags.Flag.DELETED,
			// true);//必须先设置：folder.open(Folder.READ_WRITE);
			// FetchProfile profile = new FetchProfile();
			// profile.add(FetchProfile.Item.ENVELOPE);
			// folder.fetch(message, profile);
			// Message mess=folder.getMessage(i);
			System.out.println("%%%%%%%%%%%%%%%%%正在处理第:" + i + " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
			Message mess = message[i];
			Date messdate = mess.getSentDate();
			System.out.println("date:" + messdate);
			if (messdate.after(date)) {
				String subject = mess.getSubject();

				System.out.println("subject:" + subject);
				// if(!isSeen(message[i])){
				if (subject.startsWith("Fw:研究报告")) {
					// 从js的lExpiredTime 部分来获取过期时间，比较看是否需要下载。

					// SimpleDateFormat sdf = new SimpleDateFormat(
					// "yyyy-MM-dd HH:mm:ss");
					// Date date = sdf.parse(judgetime);
					//				
					// if (mess.getSentDate().after(date)) {
					handleMultipart(mess);
				}
			}
			if (messdate.after(lastdate)) {
				lastdate = messdate;
				lasttime = sdf.format(messdate);
			}
			// ((IMAPMessage) message[i]).;
			System.out.println("%%%%%%%%%%%%%%%%%处理完毕第:" + i + " 封邮件！ %%%%%%%%%%%%%%%%%%%%%");
		}
		if (lasttime.length() > 0) {
			System.out.println("write new judgetime:" + lasttime);
			FileUtil.write(timeStamp, lasttime);
		}
		if (folder != null) {
			folder.close(true);
			System.out.println("close folder!");
		}
		if (store != null) {
			store.close();
			System.out.println("close store!");
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
			System.out.println("ContentType:" + part.getContentType());
			System.out.println("disposition:" + disposition);
			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
				saveAttach(part, getAttachPath(), msg.getSubject(), msg.getSentDate());
			} else if (disposition == null) {// 接收的邮件有附件时

			} else if (part.getContent() instanceof MimeMultipart) {// 接收的邮件有附件时
				BodyPart bodyPart = ((MimeMultipart) part.getContent()).getBodyPart(0);
				System.out.println(((MimeMultipart) part.getContent()).getContentType());
				saveAttach(bodyPart, getAttachPath(), msg.getSubject(), msg.getSentDate());
			}
			if (part.getContentType().contains("text/html")) {
				System.out.println("!!!!!!! NO ATTACHMENT Fund!!!!! 　body  NO." + m + "  part＄＄＄＄＄＄＄＄＄＄＄＄＄");
				String body = "";
				if (part.getContent() instanceof String) {// 接收到的纯文本
					body = (String) part.getContent();
					// System.out.println(part.getContent());
				}
				Matcher m1 = titlePattern.matcher((String) part.getContent());
				if (m1 != null && m1.find()) {
					String expire = m1.group(1);
					String filesize = m1.group(2);
					String link = m1.group(3);
					String title = m1.group(4);
					System.out.println("expire: " + expire);
					System.out.println("now: " + System.currentTimeMillis());
					if (System.currentTimeMillis() > Long.parseLong(expire)) {
						System.out.println("File out of date : " + expire);
						continue;
					}
					System.out.println("filesize: " + filesize);
					System.out.println("link: " + link.replace(";extra={preview:false}", "").replace("&amp;", "&"));
					System.out.println("title: " + title);
					HttpGet get = new HttpGet(link.replace(";extra={preview:false}", "").replace("&amp;", "&"));
					get.setHeader("Cookie", cookie);
					String content = down.load(get);
					// System.out.println("content: " + content);
					// http://download.fs.163.com/dl/?file=
					// rIMMxh7KmcLUDbyuFCHa_lJGm7INBaOElDAPDwuKbo7fAhMXVvKBb8X2hA0felFjH_k1spAQLITnujZJNZQiuA
					// &callback=coremail
					// ).replaceAll("&amp;", "&")
					String url = link.replace("http://fs.163.com/fs/display/", "http://download.fs.163.com/dl/")
							.replace("p=X-NETEASE-HUGE-ATTACHMENT&amp;", "").replace("&amp;extra={preview:false}", "");
					// + "&callback=coremail";
					System.out.println("link:" + link);
					System.out.println("url:" + url);
					get = new HttpGet(url);
					get.setHeader("Cookie", cookie);
					get.setHeader("Referer", link.replaceAll("&amp", "&"));
					String subject = msg.getSubject();
					if (subject.startsWith("Fw:")) {
						// subject = subject.replaceAll("Fw:研究报告", "").trim();
						subject = subject.replaceAll("Fw:", "").trim();
					}
					// String date=FileUtil.getDatefromSubject(subject);
					// String filename = getAttachPath() + "/" + date + "/" +
					// date + ".rar";
					// File dir = new File(getAttachPath() + "/" + date);
					String tmpdate = subject.replace("研究报告", "");
					String filename = getAttachPath() + "/" + tmpdate + "/" + subject + ".rar";
					File dir = new File(getAttachPath() + "/" + tmpdate);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File file = new File(filename.replaceAll("\\s", ""));
					// 如果每次rar已经存在则跳过
					if (file.exists()) {
						continue;
					}
					try {
						int length = down.downAttach(get, filename.replaceAll("\\s", ""));
						System.out.println("length:" + length);
						int threshold = 0;
						while (length < 1000000 && threshold < 10) {
							System.out.println("download again at NO.:" + threshold);
							length = down.downAttach(get, filename.replaceAll("\\s", ""));
							threshold++;
						}
						if (length < 1000000) {
							FileUtil.write(flag, "" + (new Date()) + "\n" + url);
						}
					} catch (Exception e) {
						System.out.println("Catch exceptioin:" + e.getMessage());
					}

					// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					// String dstr = sdf.format(msg.getSentDate());
					String toPath = path + "/" + FileUtil.getDatefromSubject(tmpdate);
					String commendStr = "unrar e " + filename + " " + toPath;

					File ddir = new File(toPath);
					System.out.println("Store path:" + toPath);
					if (!ddir.exists()) {
						ddir.mkdirs();
					}
					StringWriter sw = new StringWriter();
					Command.run(commendStr, sw);
					logger.debug(sw.toString());
					reader.read(toPath);
				}
			}
		}
	}

	private static void handle(Message msg) throws Exception {

		System.out.println("邮件主题:" + msg.getSubject());// 测试
		System.out.println("邮件作者:" + msg.getFrom()[0].toString());
		System.out.println("发送日期:" + msg.getSentDate());
	}

	protected static String decodeText(String text) throws UnsupportedEncodingException {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
			text = MimeUtility.decodeText(text);
		} else {
			text = new String(text.getBytes("ISO8859_1"));
		}
		return text;
	}

	private static void saveAttach(BodyPart part, String filePath, String title, Date date) throws Exception {

		// String temp = part.getFileName();
		// System.out.println("fileName:" + temp);
		// // String fileName = part.getFileName();
		// String s = temp.substring(8, temp.indexOf("?="));
		// String fileName = base64Decoder(s);
		String fileName = decodeText(part.getFileName());
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

	public PDFReader getReader() {
		return reader;
	}

	public void setReader(PDFReader reader) {
		this.reader = reader;
	}
}
