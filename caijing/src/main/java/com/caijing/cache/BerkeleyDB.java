package com.caijing.cache;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.caijing.crawl.ThreadCrawler;
import com.caijing.util.ContextFactory;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
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
 * 读写BerkeleyDB的类，用于url的排重，特点：高效轻便 主要目的是为了防止已经遍历过的url重新下载
 * 
 * @author jun-chen
 * 
 */
public class BerkeleyDB implements InitializingBean {

	private static final Log log = LogFactory.getLog(BerkeleyDB.class);

	private Environment myEnv;
	private String envName = null;
	EntryBinding strBinding = TupleBinding.getPrimitiveBinding(String.class);
	EntryBinding resultBinding = null;
	private Database myDB = null;
	private StoredClassCatalog classCatalog = null;
	private String cacheHome = null;
	
	

	public void afterPropertiesSet() throws Exception {
		EnvironmentConfig myEnvConfig = new EnvironmentConfig();
		DatabaseConfig myDbConfig = new DatabaseConfig();
		myEnvConfig.setReadOnly(false);
		myEnvConfig.setTransactional(true);
		myEnvConfig.setAllowCreate(true);
		File file = new File(cacheHome);
		if (!file.exists()) {
			file.mkdirs();
		}
		myEnv = new Environment(new File(cacheHome), myEnvConfig);
		myDbConfig.setAllowCreate(true);
		myDbConfig.setReadOnly(false);
		myDbConfig.setTransactional(true);
		myDB = myEnv.openDatabase(null, "urlDB", myDbConfig);
		classCatalog = new StoredClassCatalog(myDB);
//		resultBinding = new SerialBinding(classCatalog, Result.class);

	}

	/**
	 * key-value对的存储
	 * 
	 * @param key
	 * @param data
	 * @throws DatabaseException
	 */
	public void put(String key, String data) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();
		strBinding.objectToEntry(key, theKey);
		strBinding.objectToEntry(data, theData);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.put(txn, theKey, theData);
		txn.commit();
	}

	public String get(String key) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();
		strBinding.objectToEntry(key, theKey);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.get(txn, theKey, theData,
				LockMode.DEFAULT);
		txn.commit();
		if (status != OperationStatus.SUCCESS) {
			return null;
		} else {
			return (String)strBinding.entryToObject(theData);
		}
	}


	public boolean delete(String key) throws DatabaseException {
		DatabaseEntry thekey = new DatabaseEntry();
//		String md5 = MD5Utils.hash(url.trim().toLowerCase());
		strBinding.objectToEntry(key, thekey);
		Transaction txn = myEnv.beginTransaction(null, null);
		OperationStatus status = myDB.delete(txn, thekey);
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
				System.err.println("Error closing environment, exception:"
						+ dbe.toString());
			}
		}
	}

	public long clearAllData() {
		try {
			long numDiscarded = myEnv.truncateDatabase(null, myDB
					.getDatabaseName(), true);
			System.out.println("一共删除了 " + numDiscarded + " 条记录 从数据库 "
					+ myDB.getDatabaseName());
			myEnv.sync();
			myEnv.cleanLog();
			return numDiscarded;
		} catch (Exception e) {
			log.error("Exception: e.message: "+e.getMessage());
			return -1;
		}

	}

	public static void main(String argv[]) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		BerkeleyDB db = (BerkeleyDB) context.getBean("berkeleyDB");
		String str=db.get("103_2010-06-28_stock");
		System.out.println("str: " + str);
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

	public String getCacheHome() {
		return cacheHome;
	}

	public void setCacheHome(String cacheHome) {
		this.cacheHome = cacheHome;
	}

}
