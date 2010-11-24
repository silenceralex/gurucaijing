package com.caijing.spider;

import java.io.File;
import java.util.List;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;
import com.caijing.util.MD5Utils;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * ��дBerkeleyDB���࣬����url�����أ��ص㣺��Ч���
 * ��ҪĿ����Ϊ�˷�ֹ�Ѿ���������url��������
 * @author jun-chen
 *
 */
public class BerkeleyDB {

	private Environment myEnv;
	private String envName = null;
	EntryBinding strBinding = TupleBinding.getPrimitiveBinding(String.class);
	private Database myDB = null;

	public BerkeleyDB() {

	}

	public void setup(String envHome, boolean readOnly) throws DatabaseException {
		EnvironmentConfig myEnvConfig = new EnvironmentConfig();
		DatabaseConfig myDbConfig = new DatabaseConfig();
		myEnvConfig.setReadOnly(readOnly);
		myEnvConfig.setTransactional(true);
		myEnvConfig.setAllowCreate(!readOnly);
		File file = new File(envHome);
		if (!file.exists()) {
			file.mkdirs();
		}
		myEnv = new Environment(new File(envHome), myEnvConfig);
		myDbConfig.setAllowCreate(!readOnly);
		myDbConfig.setReadOnly(readOnly);
		myDbConfig.setTransactional(true);
		myDB = myEnv.openDatabase(null, "urlDB", myDbConfig);
	}

	/**
	 * key-value�ԵĴ洢
	 * @param key 
	 * @param data
	 * @throws DatabaseException
	 */
	private void put(String key, String data) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();
		strBinding.objectToEntry(key, theKey);
		strBinding.objectToEntry(data, theData);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.put(txn, theKey, theData);
		txn.commit();
	}

	/**
	 * ��һ��url��ŵ�bdb�У��Ա����ȥ�ص��ж�
	 * @param url ҳ�������
	 * @throws DatabaseException
	 */
	public void putUrl(String url) throws DatabaseException {
		String md5 = makeUrlId(url);
		put(md5, url);
	}

	/**
	 * Ϊurl����md5��ɢ�У�������bdb��key
	 * @param page ҳ�������
	 * @return
	 */
	public String makeUrlId(String page) {
		return MD5Utils.hash(page.trim().toLowerCase());
	}

	/**
	 * �ṩ���ط�����ʱ���ж��ã��Ƿ��Ѿ�������ur������	
	 * @param url  
	 * @return urldb�Ѿ������򷵻�true������false
	 * @throws DatabaseException
	 */
	public boolean contains(String url) throws DatabaseException {
		String md5 = MD5Utils.hash(url.trim().toLowerCase());
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();
		strBinding.objectToEntry(md5, theKey);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.get(txn, theKey, theData, LockMode.DEFAULT);
		if (status != OperationStatus.SUCCESS) {
			// throw new DatabaseException("Data insertion got status " +
			// status);
			return false;
		}
		txn.commit();
		return true;
	}

	public String getUrl(String md5) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();
		strBinding.objectToEntry(md5, theKey);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.get(txn, theKey, theData, LockMode.DEFAULT);
		txn.commit();
		if (status != OperationStatus.SUCCESS) {
			// throw new DatabaseException("Data insertion got status " +
			// status);
			return null;
		} else {
			return (String) strBinding.entryToObject(theData);
		}
	}

	public boolean delete(String url) throws DatabaseException {
		DatabaseEntry key = new DatabaseEntry();
		String md5 = MD5Utils.hash(url.trim().toLowerCase());
		strBinding.objectToEntry(md5, key);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.delete(txn, key);
		txn.commit();
		return status == OperationStatus.SUCCESS;
	}

	/**
	 * �ر�urldb
	 */
	public void close() {
		if (myEnv != null) {
			try {
				myDB.close();
				myEnv.close();
			} catch (DatabaseException dbe) {
				System.err.println("Error closing environment, exception:" + dbe.toString());
			}
		}
	}

	public static void main(String argv[]) {
		BerkeleyDB db = new BerkeleyDB();
		db.setup("/home/app/urldb/wsj", false);
		ColumnArticleDao columnArticleDao = (ColumnArticleDao) ContextFactory.getBean("columnArticleDao");
		List<ColumnArticle> articles = columnArticleDao.getColumnArticleBySource("�������ձ�");
		for (ColumnArticle article : articles) {
			String md5 = MD5Utils.hash(article.getTitle() + article.getAuthor());
			if (!db.contains(md5)) {
				System.out.println("article.getTitle():" + article.getTitle());
				System.out.println("article.getAuthor():" + article.getAuthor());
				db.putUrl(md5);
			}
		}
		db.close();
		System.out.println("wsj articles:" + articles.size());
		db = new BerkeleyDB();
		db.setup("/home/app/urldb/caijing", false);
		articles = columnArticleDao.getColumnArticleBySource("���ƾ�����-ר������");
		for (ColumnArticle article : articles) {
			String md5 = MD5Utils.hash(article.getTitle() + article.getAuthor());
			if (!db.contains(md5)) {
				System.out.println("article.getTitle():" + article.getTitle());
				System.out.println("article.getAuthor():" + article.getAuthor());
				db.putUrl(md5);
			}
		}
		db.close();
		System.out.println("caijing articles:" + articles.size());

		db = new BerkeleyDB();
		db.setup("/home/app/urldb/asstocks", false);
		articles = columnArticleDao.getColumnArticleBySource("http://www.aastocks.com.cn/");
		for (ColumnArticle article : articles) {
			String md5 = MD5Utils.hash(article.getTitle() + article.getAuthor());
			if (!db.contains(md5)) {
				System.out.println("article.getTitle():" + article.getTitle());
				System.out.println("article.getAuthor():" + article.getAuthor());
				db.putUrl(md5);
			}
		}
		db.close();
		System.out.println("asstocks articles:" + articles.size());
	}
}
