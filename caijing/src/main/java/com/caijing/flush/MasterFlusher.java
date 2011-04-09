package com.caijing.flush;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.caijing.dao.MasterDao;
import com.caijing.dao.MasterMessageDao;
import com.caijing.dao.PostDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.domain.Master;
import com.caijing.domain.Post;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.IDPathUtil;
import com.caijing.util.Paginator;

@Component("masterFlusher")
public class MasterFlusher {

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao = null;

	@Autowired
	@Qualifier("postDao")
	private PostDao postDao = null;

	@Autowired
	@Qualifier("IdUtil")
	private IDPathUtil idPathUtil = null;

	public MasterDao getMasterDao() {
		return masterDao;
	}

	public void setMasterDao(MasterDao masterDao) {
		this.masterDao = masterDao;
	}

	@Autowired
	@Qualifier("masterMessageDao")
	private MasterMessageDao masterMessageDao = null;

	public MasterMessageDao getMasterMessageDao() {
		return masterMessageDao;
	}

	public void setMasterMessageDao(MasterMessageDao masterMessageDao) {
		this.masterMessageDao = masterMessageDao;
	}

	@Autowired
	@Qualifier("config")
	private Config config = null;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	private String getLink(ColumnArticle article) {
		String linkprefix = "http://51gurus.com/articles/" + article.getType() + "/";
		String link = linkprefix + DateTools.getYear(article.getPtime()) + "/" + DateTools.getMonth(article.getPtime())
				+ "/" + article.getAid() + ".html";
		return link;
	}

	public void flushArchive() {
		List<Master> masters = masterDao.getAllMasters(0, 10);
		for (Master master : masters) {
			List<Date> dates = masterMessageDao.getDatesByMasterid(master.getMasterid());
			List<String> urls = new ArrayList<String>();
			List<String> curdates = new ArrayList<String>();
			for (Date date : dates) {
				curdates.add(DateTools.transformYYYYMMDDDate(date));
				urls.add(idPathUtil.getMasterLiveFilePath("" + master.getMasterid(), date).replace("/home/html", ""));
				flushOneStatic(master, masters, date);
			}
			List<Integer> pages = new ArrayList<Integer>();
			int page = 0;
			if (dates.size() % 10 == 0) {
				page = dates.size() / 10;
			} else {
				page = dates.size() / 10 + 1;
			}
			for (int i = 0; i < page; i++) {
				pages.add(i);
			}

			try {
				String encodename = master.getMastername();
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/master/masterArchive.htm");
				vmf.put("master", master);
				vmf.put("dateTools", new DateTools());
				vmf.put("urls", urls);
				vmf.put("masterList", masters);
				vmf.put("encodename", encodename);
				vmf.put("pages", pages);
				vmf.put("curdates", curdates);
				vmf.save(config.getProperty("masterPath") + master.getMasterid() + "/archive_1.html");
				System.out.println("write page : " + config.getProperty("masterPath") + master.getMasterid()
						+ "/archive_1.html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushLiveStatic() {
		Map map = config.getValue("groupid");
		List<Master> masters = masterDao.getAllMasters(0, 100);
		for (Master master : masters) {
			System.out.println("masterid: " + master.getMasterid() + "  name:" + master.getMastername());
			flushOneStatic(master, masters, new Date());
		}
	}

	private void flushOneStatic(Master master, List<Master> masters, Date date) {
		List<Map> maps = masterMessageDao.getMessagesFrom(master.getMasterid(), DateTools.transformYYYYMMDDDate(date),
				0);
		try {
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/master/liveStatic.htm");
			vmf.put("maps", maps);
			vmf.put("mastername", master.getMastername());
			vmf.put("masterid", master.getMasterid());
			vmf.put("masterList", masters);
			vmf.put("encodename", master.getMastername());
			vmf.put("master", master);
			vmf.put("date", DateTools.transformYYYYMMDDDate(date));
			String filePath = idPathUtil.getMasterLiveFilePath("" + master.getMasterid(), date);
			vmf.save(filePath);
			System.out.println("write page : " + filePath);
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void flushThreadList() {
		List<Master> masters = masterDao.getAllMasters(0, 100);
		DateTools dateTools = new DateTools();
		for (Master master : masters) {
			int total = postDao.getPostCountByGroupid("" + master.getMasterid());
			int totalpage = (total % 20 == 0) ? total : (total / 20 + 1);
			Paginator<Post> paginator = new Paginator<Post>();
			paginator.setPageSize(20);
			paginator.setTotalRecordNumber(total);
			paginator.setUrl("/master/" + master.getMasterid() + "/thread_$number$.html");
			for (int i = 1; i <= totalpage; i++) {
				paginator.setCurrentPageNumber(i);
				List<Post> postList = postDao.getPostByGroupid("" + master.getMasterid(), (i - 1) * 20, 20 * i);
				for (Post post : postList) {
					post.setUrl(idPathUtil.getMasterPostFilePath(post).replace("/home/html", ""));
					flushOnePost(post);
				}

				try {
					VMFactory vmf = new VMFactory();
					vmf.setTemplate("/template/master/masterThreadList.htm");
					vmf.put("masters", masters);
					vmf.put("articlelist", postList);
					vmf.put("dateTools", dateTools);
					vmf.put("paginatorLink", paginator.getPageNumberList());
					String url = config.getProperty("masterPath") + master.getMasterid() + "/thread_" + i + ".html";
					vmf.save(url);
					System.out.println("write page : " + url);
				} catch (Exception e) {
					System.out.println("===> exception !!");
					System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
					e.printStackTrace();
				}
			}

		}
	}

	public void flushMasterInfo() {
		List<Master> masters = masterDao.getAllMasters(0, 100);
		for (Master master : masters) {
			List<Post> postList = postDao.getPostByGroupid("" + master.getMasterid(), 0, 10);
			for (Post post : postList) {
				post.setUrl(idPathUtil.getMasterPostFilePath(post).replace("/home/html", ""));
				flushOnePost(post);
			}
			try {
				String encodename = master.getMastername();
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/master/masterIntro.htm");
				vmf.put("master", master);
				vmf.put("dateTools", new DateTools());
				vmf.put("masterList", masters);
				vmf.put("encodename", encodename);
				vmf.put("postList", postList);
				vmf.save(config.getProperty("masterPath") + master.getMasterid() + ".html");
				System.out.println("write page : " + config.getProperty("masterPath") + master.getMasterid() + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/master/masterList.htm");
			vmf.put("masters", masters);
			vmf.save(config.getProperty("masterPath") + "index.html");
			System.out.println("write page : " + config.getProperty("masterPath") + "index.html");
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void flushOnePost(Post post) {
		try {
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/master/masterThread.htm");
			vmf.put("post", post);
			vmf.put("dateTools", new DateTools());
			String filePath = idPathUtil.getMasterPostFilePath(post);
			vmf.save(filePath);
			System.out.println("write page : " + filePath);
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MasterFlusher flusher = (MasterFlusher) ContextFactory.getBean("masterFlusher");
		//		flusher.flushLiveStatic();
		flusher.flushMasterInfo();
		flusher.flushArchive();
		flusher.flushThreadList();
		System.exit(0);
	}

}
