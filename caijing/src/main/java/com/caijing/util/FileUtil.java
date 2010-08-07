package com.caijing.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class FileUtil {
	static HashMap cache = null;
	static int MAXCACHE = 100;
	public static String encoding = "GBK";
	public static final String THISYEAR="2010";
	
	public static final String path = "/home/app/papers";
	public static final String html = "/home/html/papers";
	
	public static String getDatefromSubject(String subject){
		String[] strs=subject.split("\\.");
		if(strs.length==2){
			String month=strs[0];
			if(strs[0].length()==1){
				month="0"+strs[0];
			}
			String day=strs[1];
			if(strs[1].length()==1){
				month="0"+strs[1];
			}
			return THISYEAR+month+day;
		}
		return subject;
	}
	
//	public static Date getDatefromSubject(String subject){
//		SimpleDateForm
//	}

	/**
	 * д���ļ�
	 * 
	 * @param filename
	 * @param data
	 * @throws IOException
	 */
	public static void write(String filename, String data) {
		FileOutputStream fos = null;
		try {
			if (filename.indexOf("\\") > -1) {
				filename = StringUtils.replace(filename, "\\", "/");
			}
			mkdir(filename.substring(0, filename.lastIndexOf("/")));
			fos = new FileOutputStream(filename);
			fos.write(data.getBytes(encoding));
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �����ݼӽ��ļ���
	 * 
	 * @param filename
	 * @param data
	 */
	public static void appendWrite(String filename, String data) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filename, true));
			out.write(new String(data.getBytes(encoding)));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param filename
	 * @return @throws
	 *         IOException
	 */
	public static String read(String filename) {
		return read(filename, "\n");
	}

	public static String read(String filename, String split) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		try {
			File file = new File(filename);
			if (!file.exists()) {
				System.out.println("filename:" + filename + "  not exists!");
				return "";
			}
			if (!file.canRead()) {
				System.out.println("filename:" + filename + "  cannot read!");
				return "";
			}
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, encoding);
			reader = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			for (String str = null; (str = reader.readLine()) != null;) {
				sb.append(str);
				sb.append(split);
			}
			return sb.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				fis.close();
				isr.close();
				reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �ļ��Ƿ����
	 * 
	 * @param filename
	 * @return @throws
	 *         IOException
	 */
	public static boolean exists(String filename) {
		try {
			File file = new File(filename);
			return file.exists();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * �õ��ļ��Ĵ�С��bytes��
	 * 
	 * @param filename
	 * @return
	 */
	public static long getFileSize(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			return file.length();
		}
		return 0;
	}

	/**
	 * �õ��ļ�������
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileType(String filename) {
		String extname = filename.substring(filename.lastIndexOf("."));
		if (extname.length() <= 5) {
			return extname;
		}
		return "unknown";
	}

	/**
	 * �õ�������·��
	 * 
	 * @param unrealpath
	 * @return
	 */
	public static String realPath(String filename) {
		return new File(filename).getAbsolutePath();
	}

	/**
	 * �õ��ļ�����޸�ʱ��
	 * 
	 * @param filename
	 * @return
	 */
	public static long getFileTime(String filename) {
		try {
			return new File(filename).lastModified();
		} catch (Exception e) {
			return (long) 0;
		}
	}

	/**
	 * �ļ�����
	 * 
	 * @param oldname
	 * @param newname
	 * @return
	 */
	public static boolean rename(String oldname, String newname) {
		try {
			File file = new File(oldname);
			File dest = new File(newname);
			if (file.canWrite()) {
				file.renameTo(dest);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ����Ŀ¼
	 * 
	 * @param dirname
	 * @return
	 */
	public static boolean mkdir(String dir) {
		try {
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ��Ŀ¼
	 * 
	 * @param dirname
	 */
	public static boolean rmdir(String dir) {
		try {
			File file = new File(dir);
			if (file.isDirectory()) {
				file.delete();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean unlink(String filename) {
		try {
			File file = new File(filename);
			if (file.isFile()) {
				file.delete();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param oldname
	 * @param newname
	 * @param overwrite
	 * @return
	 */
	public static boolean copy(String oldname, String newname, boolean overwrite) {
		try {
			if (!overwrite && new File(newname).exists()) {
				return false;
			}
			File file = new File(oldname);
			FileInputStream input = new FileInputStream(file);
			File dest = new File(newname);
			if (!mkdir(dest.getParent())) {
				return false;
			}
			FileOutputStream output = new FileOutputStream(newname);
			byte[] b = new byte[1];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean move(String filename, String newname, boolean overwrite) {
		File newfile = new File(newname);
		File file = new File(filename);
		if (!file.exists()) {
			return false;
		}
		if (!overwrite && newfile.exists()) {
			return false;
		}
		newfile.getParentFile().mkdirs();
		if (newfile.exists()) {
			newfile.delete();
		}
		file.renameTo(newfile);
		return true;
	}

	/**
	 * �����ļ�
	 * 
	 * @param file
	 * @param newname
	 * @param overwrite
	 * @return
	 */
	public static boolean copy(File file, String newname, boolean overwrite) {
		try {
			if (!overwrite && new File(newname).exists()) {
				return false;
			}
			FileInputStream input = new FileInputStream(file);
			File dest = new File(newname);
			if (!mkdir(dest.getParent())) {
				return false;
			}
			FileOutputStream output = new FileOutputStream(newname);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean copyDir(String olddir, String newdir, boolean overwrite) {
		return false;
	}

	/**
	 * �õ�·�����ļ���
	 * 
	 * @param filename
	 * @return
	 */
	public static String basename(String filename) {
		return "";
	}

	/**
	 * �õ�·�����ļ�����
	 * 
	 * @param filename
	 * @return
	 */
	public static String dirname(String filename) {
		return "";
	}

	/**
	 * �ı��ļ�������
	 * 
	 * @param filename
	 * @param mod
	 * @return
	 */
	public static boolean chmod(String filename, String mode) {
		File file = new File(filename);
		return false;
	}

	/**
	 * �õ����·���� �ļ�������Ŀ¼�����ӽڵ�ʱ�����ļ�����
	 * 
	 * @param pathName
	 *            Ŀ¼��
	 * @param fileName
	 *            �ļ���
	 * @return �õ��ļ��������Ŀ¼�������·����Ŀ¼�²����ڸ��ļ�ʱ�����ļ���
	 * @since 0.5
	 */
	public static String getSubpath(String pathName, String fileName) {
		int index = fileName.indexOf(pathName);
		if (index != -1) {
			return fileName.substring(index + pathName.length() + 1);
		} else {
			return fileName;
		}
	}

	/**
	 * ���ļ����е����Ͳ���ȥ����
	 * 
	 * @param filename
	 *            �ļ���
	 * @return ȥ�����Ͳ��ֵĽ��
	 * @since 0.5
	 */
	public static String trimType(String filename) {
		int index = filename.lastIndexOf(".");
		if (index != -1) {
			return filename.substring(0, index);
		} else {
			return filename;
		}
	}

	/**
	 * ��\ת����/
	 * 
	 * @param str
	 * @return
	 */
	public static String unixPath(String str) {
		return str.replace('\\', '/');
	}

	public static Object readObject(String filename) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		} finally {
			try {
				fis.close();
				ois.close();
			} catch (Exception e) {
			}
		}
	}

	public static void writeObject(String filename, Object obj) {
		if (filename.indexOf("\\") > -1) {
			filename = StringUtils.replace(filename, "\\", "/");
		}
		mkdir(filename.substring(0, filename.lastIndexOf("/")));
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filename);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			try {
				fos.close();
				oos.close();
			} catch (Exception ex) {
			}
		}
	}

}
