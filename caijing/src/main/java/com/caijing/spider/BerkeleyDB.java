package com.caijing.spider;

import java.io.File;

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
 * 读写BerkeleyDB的类，用于url的排重，特点：高效轻便
 * 主要目的是为了防止已经遍历过的url重新下载
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
	 * key-value对的存储
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
	 * 将一个url存放到bdb中，以便进行去重的判断
	 * @param url 页面的链接
	 * @throws DatabaseException
	 */
	public void putUrl(String url) throws DatabaseException {
		String md5 = makeUrlId(url);
		put(md5, url);
	}

	/**
	 * 为url进行md5的散列，用于做bdb的key
	 * @param page 页面的链接
	 * @return
	 */
	public String makeUrlId(String page) {
		return MD5Utils.hash(page.trim().toLowerCase());
	}

	/**
	 * 提供下载分析的时候判断用，是否已经存在有ur的下载	
	 * @param url  
	 * @return urldb已经包含则返回true，否则false
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
	 * 关闭urldb
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
		// BerkeleyDB db = new BerkeleyDB();
		// String url = "www.hia.com";
		// System.out.println("url's md5: " + db.makeUrlId(url));
		//
		// try {
		// db.setup("d:\\urldb\\anhuiTV", false);
// // db.putUrl(url);
		// // if (db.contains(url)) {
		// // System.out.println("Contains the url!");
		// // } else {
		// // System.out.println("Not Contains the url!");
		// // }
		// // String url2=db.getUrl(db.makeUrlId(url));
		// // System.out.println("URL:"+url2);
		// db.insertFromMysql("安徽卫视");
		// } catch (DatabaseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
}
