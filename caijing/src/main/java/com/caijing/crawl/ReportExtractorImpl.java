package com.caijing.crawl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;

import com.caijing.dao.StockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.domain.Stock;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;

public class ReportExtractorImpl implements ReportExtractor {

	private Pattern stockPattern = Pattern.compile("(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	Pattern numberPattern = Pattern.compile("[0-9\\.]+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	Pattern characterPattern = Pattern.compile("^([\\u4e00-\\u9fa5\\u9d84\\s\\?]+)[0-9AS\\s]+\\n",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	HashMap<String, String> stockmap = new HashMap<String, String>();

	HashMap<String, Pattern> sanameMap = new HashMap<String, Pattern>();
	private Config config = null;

	private StockDao dao = null;
	
	private String invalidoldpapers = "/data/shells/log3/";

	public void init() {
		List<Stock> list = dao.getAllStock();
		for (Stock stock : list) {
			if (!stockmap.containsKey(stock.getStockcode())) {
				stockmap.put(stock.getStockcode(), stock.getStockname());
			}
		}
	}

	public RecommendStock extractFromFile(Report report, String file) {

		Pattern publishDatePattern = Pattern.compile((String) config.getValue(report.getSaname()).get("publishdate"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern objectprice = Pattern.compile((String) config.getValue(report.getSaname()).get("objectprice"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("analyzer"));
		//
		// Pattern grade = Pattern.compile((String) config.getValue(
		// report.getSaname()).get("grade"), Pattern.CASE_INSENSITIVE
		// | Pattern.DOTALL | Pattern.UNIX_LINES);
		int num = 0;
		RecommendStock rs = new RecommendStock();
		rs.setRecommendid(ServerUtil.getid());
		rs.setSaname(report.getSaname());
		rs.setStockcode(report.getStockcode());
		rs.setStockname(report.getStockname());
		String content = FileUtil.read(file, "GBK");
		// System.out.println("content:" + content);
		Matcher m = publishDatePattern.matcher(content);
		if (m != null && m.find()) {
			String month = m.group(2);
			String day = m.group(3);
			month = month.trim().length() > 1 ? month.trim() : "0" + month.trim();
			day = day.trim().length() > 1 ? day.trim() : "0" + day.trim();
			System.out.println("publishdate:" + m.group(1).trim() + month + day);
			rs.setCreatedate(m.group(1).trim() + month + day);
			num++;
		}

		String analyzer = fetchAnaylzer(report.getSaname(), content);
		if (analyzer != null) {
			rs.setAname(analyzer);
			num++;
		} else {
			rs.setAname("N/A");
		}
		m = objectprice.matcher(content);
		if (m != null && m.find()) {
			System.out.println("objectprice:" + m.group(1));
			String oprice = m.group(1);
			String[] strs = oprice.split("-");
			if (strs.length > 1) {
				oprice = strs[1];
			}
			try {
				rs.setObjectprice(Float.parseFloat(oprice));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			num++;
		}
		String grade = fetchGrade(report.getSaname(), content);
		if (grade == null) {
			System.out.println("Connot match the grade!");
		} else {
			System.out.println("grade: " + grade);
			rs.setGrade(grade);
			rs.setStatus(judgeStaus(grade));
			num++;
		}
		String eps = fetchEPS(report.getSaname(), content);
		if (eps == null) {
			System.out.println("Connot match the EPS!");
		} else {
			rs.setEps(eps);
			num++;
		}
		System.out.println("Extractnum: " + num);
		rs.setExtractnum(num);
		return rs;
	}

	/**
	 * ����������Ϣ�ж�ȯ���б���������(statusΪ2) ����������(statusΪ1)
	 * @param grade
	 * @return
	 */
	public int judgeStaus(String grade) {
		String judgeStr = grade.replaceAll("\\s", "");
		if (!judgeStr.contains("����") && !judgeStr.contains("����")) {
			if (judgeStr.contains("����") || judgeStr.contains("����") || judgeStr.contains("�Ƽ�")) {
				return 2;
			} else if (judgeStr.contains("����")) {
				return 1;
			}
		} else {
			return 1;
		}
		return 0;
	}

	private String fetchGrade(String saname, String content) {
		List<String> grades = (List<String>) config.getValue(saname).get("grade");
		Matcher m = null;
		for (String str : grades) {
			Pattern grade = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			m = grade.matcher(content);
			if (m != null && m.find()) {
				String grad = m.group(1).trim();
				System.out.println("grade:" + grad);
				if (grad.length() > 6) {
					if (saname.equals("��֤ͨȯ") || saname.equals("����֤ȯ")) {
						return grad.replaceAll("\\s", "");
					}
					String[] strs = grad.split("\\n|��");
					if (strs.length == 2 && strs[1].trim().length() < 5) {
						return strs[1].trim();
					} else {
						return null;
					}
				} else {
					return grad;
				}
			}
		}
		return null;
	}

	private String fetchAnaylzer(String saname, String content) {
		Pattern analyzerPattern = null;
		Matcher m = null;
		String analyzer = null;
		if (config.getValue(saname).get("analyzerEps") != null) {
			String str = (String) config.getValue(saname).get("analyzerEps");
			// System.out.println("analyzerEps: " + str);
			analyzerPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			m = analyzerPattern.matcher(content);
			if (m != null && m.find()) {
				String analyzers = m.group(1);
				analyzer = "";
				if (saname.equals("��Դ֤ȯ")) {
					System.out.println("analyzer: " + analyzers);
					return analyzers.trim();
				}
				// System.out.println("analyzers: " + analyzers);
				m = characterPattern.matcher(analyzers);
				int threshold = 0;
				while (m != null && m.find() && threshold < 3) {
					// System.out.println("m.group(1).trim(): "
					// +m.group(1).trim());
					analyzer += m.group(1).trim() + " ";
					threshold++;
				}
				analyzer = analyzer.trim();
			}
			System.out.println("analyzer: " + analyzer);
		}
		if (analyzer == null) {
			String str = (String) config.getValue(saname).get("analyzer");
			// System.out.println("anaylzer:" + str);
			analyzerPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			m = analyzerPattern.matcher(content);
			while (m != null && m.find()) {
				System.out.println("anaylzer:" + m.group(1));
				String aname = "";
				// �����������ǰ׺
				if (m.group(1).trim().length() > 10) {
					String[] tests = m.group(1).trim().split("\n");
					for (String tmp : tests) {
						// �н�˾��ȥ���е������м�Ŀո�
						if (saname.equals("�н�˾")) {
							tmp = tmp.replaceAll("\\s", "").trim();
						} else {
							tmp = tmp.trim();
						}
						if (tmp.trim().length() != 0 && tmp.trim().length() < 4 && tmp.trim().length() > 1) {
							aname += tmp.trim() + " ";
						}
						if (saname.equals("��Դ֤ȯ")) {
							// ��Դ֤ȯ��ȥ���е������м��/
							aname = m.group(1).trim().replaceAll("/", " ").trim() + " ";
						} else {
							aname = m.group(1).trim() + " ";
						}
					}
					// System.out.println("after anaylzer:" + aname.trim());
				} else {
					// �н�˾��ȥ���е������м�Ŀո�
					if (saname.equals("�н�˾")) {
						aname = m.group(1).trim().replaceAll("\\s", "").trim() + " ";
					} else {
						aname = m.group(1).trim() + " ";
					}
				}
				if (analyzer == null) {
					analyzer = aname.trim() + " ";
				} else {
					analyzer += aname.trim() + " ";
				}

			}
			System.out.println("after anaylzer:" + analyzer);
			if (analyzer == null) {
				System.out.println("after anaylzer no match:");
				return null;
			}
		}
		return analyzer.trim();
	}

	private String fetchEPS(String saname, String content) {
		Pattern epsPattern = null;
		Matcher m = null;
		String eps = null;
		if (config.getValue(saname).get("specialEps") != null) {
			String str = (String) config.getValue(saname).get("specialEps");
			epsPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			m = epsPattern.matcher(content);
			while (m != null && m.find()) {
				String epss = m.group(1);
				System.out.println("epss: " + epss);
				Matcher m2 = numberPattern.matcher(epss);
				ArrayList<String> epslist = new ArrayList<String>();
				while (m2 != null && m2.find()) {
					epslist.add(m2.group().trim());
				}
				System.out.println("epslist size:" + epslist.size());
				if (epslist.size() == 2) {
					eps = "{'2010':'" + epslist.get(0) + "','2011':'" + epslist.get(1) + "','2012':'n/a'}";
					break;
				} else if (epslist.size() >= 3) {
					if ("�н�˾".equals(saname) && !content.contains("2012E")) {
						eps = "{'2010':'" + epslist.get(epslist.size() - 2) + "','2011':'"
								+ epslist.get(epslist.size() - 1) + "','2012':'n/a'}";
					} else {
						eps = "{'2010':'" + epslist.get(epslist.size() - 3) + "','2011':'"
								+ epslist.get(epslist.size() - 2) + "','2012':'" + epslist.get(epslist.size() - 1)
								+ "'}";
					}
					break;
				}
			}
			System.out.println("eps:" + eps);
		}
		if (eps == null) {
			List<String> strs = (List<String>) config.getValue(saname).get("eps");
			// System.out.println("str size:" + strs.size());
			for (String str : strs) {
				epsPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
				m = epsPattern.matcher(content);
				while (m != null && m.find() && eps == null) {
					// System.out.println("Group count:" + m.groupCount());
					if (m.groupCount() == 1) {
						System.out.println("2010:" + m.group(1).trim());
						eps = "{'2010':'" + m.group(1) + "','2011':'n/a','2012':'n/a'}";
						return eps;
					} else if (m.groupCount() == 2) {
						System.out.println("2010:" + m.group(1).trim());
						System.out.println("2011:" + m.group(2).trim());
						eps = "{'2010':'" + m.group(1) + "','2011':'" + m.group(2) + "','2012':'n/a'}";
						return eps;
					} else {
						if ("�н�˾".equals(saname) && !content.contains("2012E")) {
							System.out.println("2010:" + m.group(2).trim());
							System.out.println("2011:" + m.group(3).trim());
							eps = "{'2010':'" + m.group(2) + "','2011':'" + m.group(3) + "','2012':'n/a'}";
							return eps;
						} else {
							// System.out.println("count:" + m.groupCount());
							System.out.println("2010:" + m.group(1).trim());
							System.out.println("2011:" + m.group(2).trim());
							System.out.println("2012:" + m.group(3).trim());
							eps = "{'2010':'" + m.group(1) + "','2011':'" + m.group(2) + "','2012':'" + m.group(3)
									+ "'}";
							return eps;
						}
					}
				}
				if (eps != null)
					break;
			}
		}
		return eps;
	}

	private String fetchTitle(String saname, String content) {
		List<String> titles = (List<String>) config.getValue(saname).get("titlePattern");
		Matcher m = null;
		for (String str : titles) {
			Pattern title = Pattern.compile(str, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			m = title.matcher(content);
			if (m != null && m.find()) {
				String grad = m.group(1).trim();
				System.out.println("grade:" + grad);
				if (grad.length() > 6) {
					if (saname.equals("��֤ͨȯ") || saname.equals("����֤ȯ")) {
						return grad.replaceAll("\\s", "");
					}
					String[] strs = grad.split("\\n|��");
					if (strs.length == 2 && strs[1].trim().length() < 5) {
						return strs[1].trim();
					} else {
						return null;
					}
				} else {
					return grad;
				}
			}
		}
		return null;
	}
	
	public Report extractFromTitleAndSaname(String path, String rid, String saname) {
		//List<String> titlePatterns = (List<String>) config.getValue(saname).get("titlePattern");
		
		
		Pattern titlePattern = Pattern.compile((String) config.getValue(saname).get("titlePattern"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

		System.out.println("titlePattern:" + config.getValue(saname).get("titlePattern"));
		System.out.println("path:" + path);
		Matcher m = titlePattern.matcher(path);
		Report report = new Report();
		report.setRid(rid);
		if (saname.equalsIgnoreCase("����֤ȯ")) {
			if (m != null && m.find()) {
				String sanam = m.group(1);
				String stockname = m.group(2);
				String title = m.group(3);
				String stockcode = m.group(4);
				String aname = m.group(5);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1);
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"axzq"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("�������")) {
			if (m != null && m.find()) {
				String sanam = m.group(1); //saname
				String stockname = m.group(4);
				String title = m.group(6);
				String stockcode = m.group(3);
				String aname = m.group(5);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				//TODO ��title��ȡ�������ֶ�д��һ����־�ļ���ÿ��ȯ��һ����־
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"sywg"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("����֤ȯ")) {
			if (m != null && m.find()) {
				String sanam = m.group(1);//saname
				String stockname = m.group(3);
				String title = m.group(5);
				String stockcode = m.group(4);
				String aname = m.group(6);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"zszq"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("��̩����")) {
			if (m != null && m.find()) {
				String sanam = m.group(1); //saname
				String stockname = m.group(3);
				String title = m.group(5);
				String stockcode = m.group(4);
				String aname = m.group(6);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"gtja"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("�㷢֤ȯ")) {
			//TODO ��Ҫ�������ֹ���
			///data/oldpapers2/201005-06/�㷢��ɼɼ�ɷ�(600884)﮵�ز�ҵ���걸,����ͻ��.pdf
			///data/oldpapers2/201005-06/�����㷢֤ȯ-��˾����-ú̿��ѡҵ-����úҵ(600188.sh)-����-�ֺ���.pdf
			if (m != null && m.find()) {
				String sanam = m.group(1); //saname
				String stockname = m.group(3);
				String title = m.group(2);
				String stockcode = m.group(4);
				String aname = m.group(6);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"gfzq"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("����֤ȯ")) {
			if (m != null && m.find()) {
				String sanam = m.group(1); //saname
				String stockname = m.group(3);
				String title = m.group(5);
				String stockcode = m.group(4);
				String aname = m.group(6);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
				FileUtil.appendWrite(invalidoldpapers+"gjzq"+".log", path+"\n", "UTF-8");
			}
		} else if (saname.equalsIgnoreCase("����֤ȯ")) {
			if (m != null && m.find()) {
				String sanam = m.group(1); //saname
				String stockname = m.group(3);
				String title = m.group(5);
				String stockcode = m.group(4);
				String aname = m.group(6);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1); //m.group(2)
				report.setTitle(title);
				report.setAname(aname);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname(code):" + stockname + "(" + stockcode + ")");
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				System.out.println("aname:" + aname);
			} else {
				System.out.println("No match:");
			}
		}
		return report;
	}

	public Report extractFromTitle(String file, String rid) {
		String name = new File(file).getName();
		name = name.substring(0, name.lastIndexOf('.'));
		System.out.println("name:" + name);
		try {
			// �ϰ汾�ı����ʽ
			Matcher m = stockPattern.matcher(name);
			Report report = new Report();
			report.setRid(rid);
			if (m != null && m.find()) {
				String sanam = m.group(1);
				String stockname = m.group(2);
				String stockcode = m.group(3);
				report.setSaname(sanam);
				report.setStockcode(stockcode);
				report.setStockname(stockname);
				report.setType(1);
				String title = name.split("--")[1];
				report.setTitle(title);
				System.out.println("sanam:" + sanam);
				System.out.println("stockname:" + stockname);
				System.out.println("type:" + 1);
				System.out.println("title:" + title);
				return report;
			}
			// �°汾�ı����ʽ
			m = stockcodePattern.matcher(name);
			if (m != null && m.find()) {
				String stockcode = m.group(1);
				String[] strs = name.split("-");
				if (strs.length > 1) {
					String saname = strs[0];
					String title = name.substring(name.indexOf('-') + 1);
					System.out.println("title:" + title);
					if (strs[1].length() == 6 && strs[1].startsWith("1")) {
						title = title.substring(title.indexOf('-') + 1);
						System.out.println("title:" + title);
					}
					report.setSaname(saname);
					report.setStockcode(stockcode);
					report.setTitle(title);
					report.setType(1);
					report.setStockname(stockmap.get(stockcode));
					System.out.println("sanam:" + saname);
					System.out.println("stockname:" + stockmap.get(stockcode));
					System.out.println("stockcode:" + stockcode);
					System.out.println("type:" + 1);
					System.out.println("title:" + title);
				} else {
					name.substring(0, 4);
					report.setSaname(name.substring(0, 4));
					report.setStockcode(stockcode);
					report.setTitle(name.substring(4));
					report.setType(1);
					report.setStockname(stockmap.get(stockcode));
					System.out.println("sanam:" + name.substring(0, 4));
					System.out.println("stockname:" + stockmap.get(stockcode));
					System.out.println("stockcode:" + stockcode);
					System.out.println("type:" + 1);
					System.out.println("title:" + name.substring(4));
				}
				return report;
			}
			// �ϰ汾�ı����ʽ
			String[] strs = name.split("--");
			if (strs.length > 1) {
				String sanam = strs[0];
				report.setSaname(sanam);
				report.setTitle(strs[1]);
				// ����
				if (strs[1].length() <= 4) {
					report.setType(0);
					System.out.println("type:" + 0);
				} else if (strs[1].length() > 4 && strs[1].contains("���")) {
					report.setType(3);
					System.out.println("type:" + 3);
				} else if (strs[1].length() > 4 && strs[1].contains("ҵ")) {
					report.setType(2);
					System.out.println("type:" + 2);
				} else {
					report.setType(4);
					System.out.println("type:" + 4);
				}
				System.out.println("sanam:" + sanam);
				System.out.println("title:" + strs[1]);
				return report;
			}
			strs = name.split("-");
			System.out.println("strs:" + strs.length);
			if (strs.length > 1) {
				String sanam = strs[0];
				System.out.println("sanam:" + sanam);
				// ���titleΪ�н�˾��ͷ��
				if (sanam.length() > 6) {
					sanam.startsWith("�н�˾");
					report.setSaname("�н�˾");
					String title = name.substring(name.indexOf('-') + 5);
					System.out.println("title:" + title);
					report.setTitle(title);
				} else {
					report.setSaname(sanam);
					String title = name.substring(name.indexOf('-') + 1);
					System.out.println("title:" + title);
					if (strs[1].length() == 6 && strs[1].startsWith("1")) {
						title = title.substring(title.indexOf('-') + 1);
						System.out.println("title:" + title);
					}
					report.setTitle(title);
				}
				// ����
				String tmp = name.substring(name.indexOf('-') + 1);
				if (tmp.length() <= 4 || tmp.contains("����") || tmp.contains("���") || tmp.contains("����")
						|| tmp.contains("ÿ��") || tmp.contains("����")) {
					report.setType(0);
					System.out.println("type:" + 0);
				} else if (tmp.length() > 4 && strs[1].contains("���")) {
					report.setType(3);
					System.out.println("type:" + 3);
				} else if (tmp.length() > 4 && strs[1].contains("ҵ")) {
					report.setType(2);
					System.out.println("type:" + 2);
				} else {
					report.setType(4);
					System.out.println("type:" + 4);
				}

				System.out.println("sanam:" + sanam);
				return report;
			}
			System.out.println("sanam:" + name.substring(0, 4));
			System.out.println("title:" + name.substring(4));
			System.out.println("type:" + 4);
			report.setSaname(name.substring(0, 4));
			report.setTitle(name.substring(4));
			report.setType(4);
			return report;
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {
		ReportExtractorImpl extractor = new ReportExtractorImpl();
		ApplicationContext context = ContextFactory.getApplicationContext();
		Config config = (Config) context.getBean("config");
		StockDao dao = (StockDao) context.getBean("stockDao");
		extractor.setConfig(config);
		extractor.setDao(dao);
		extractor.init();
		//String test = "/data/oldpapers2/200904/����֤ȯ_��˾�챨_���ֹɷݣ������е�ҵ������»�_601003_��־��,������.pdf";

		//extractor.extractFromTitleAndSaname(test, "test", "����֤ȯ");

		/*==== testing extractor ====*/
		//testHuaTai(extractor);
		//testGuoXin(extractor);
		//testChangJiang(extractor);
		//testGuangDa(extractor);
		//testYinHe(extractor);
		//testHongYuan(extractor);
		testDongFang(extractor);
		//testZhongXin(extractor);
		//testZhongTou(extractor);
		//testHuaTai2(extractor);

		// extractor.extractFromTitle(
		// "����֤ȯ-100825-002311������10�б�����-���ϡ��������ȡ�����ҵ������.pdf", "");

		// extractor.extractFromFile("����֤ȯ",
		// "F:\\email\\�о�����7.07\\����֤ȯ--���ɷ�(600256)���������Ͻ��������������.txt",
		// ServerUtil.getid());
		// RecommendStockDao recommendStockDao = (RecommendStockDaoImpl)
		// ContextFactory
		// .getBean("recommendStockDao");
		// RecommendStock rs = extractor.extractFromFile("��̩����", "�й�ƽ��",
		// "http://guru.caijing.com/papers/20100818/6DFFKFR8.txt",
		// ServerUtil.getid());
		//		Report report = new Report();
		//		report.setSaname("�㷢֤ȯ");
		//		RecommendStock rs = extractor.extractFromFile(report, "http://guru.caijing.com/papers/20100721/6CLPTKQK.txt");
		//		"http://guru.caijing.com/papers/20100721/6CLPTKQK.txt");

		//		report.setSaname("����֤ȯ");
		//		RecommendStock rs = extractor.extractFromFile(report,
		////		"http://guru.caijing.com/papers/20100709/6CLQ1CE8.txt");
		////		"http://guru.caijing.com/papers/20100728/6CLQEN3T.txt");
		////		"http://guru.caijing.com/papers/20100729/6CLQ8ICT.txt");
		//		"http://guru.caijing.com/papers/20100901/6ENHL78L.txt");

		//		report.setSaname("����֤ȯ");
		//		RecommendStock rs = extractor.extractFromFile(report,
		////		"http://guru.caijing.com/papers/20100826/6EBPLO3R.txt");
		//		
		//		"http://guru.caijing.com/papers/20100824/6DV81AFH.txt");

		//		report.setSaname("��֤ͨȯ");
		//		RecommendStock rs = extractor.extractFromFile(report,
		// "http://guru.caijing.com/papers/20100803/6CLPPQ0P.txt");
		//				"http://guru.caijing.com/papers/20100806/6CR50GB0.txt");
		//				"http://guru.caijing.com/papers/20100728/6CLQESHQ.txt");
		//				"http://guru.caijing.com/papers/20100830/6EHD98EV.txt");

		//		report.setSaname("����֤ȯ");
		////		extractor.extractFromTitle("/home/app/papers/20100830/�н�˾ũҵ100830_�ֻ��г�������ʤ���������Ǽ۽�������5800-6000Ԫ��.pdf", "");
		//		RecommendStock rs = extractor.extractFromFile(report,
		//		// "http://guru.caijing.com/papers/20100823/6DSQ8GD4.txt");
		//				// "http://guru.caijing.com/papers/20100823/6DSQ8I7I.txt");
		//				// "http://guru.caijing.com/papers/20100824/6DV81EQ7.txt");
		//				"http://guru.caijing.com/papers/20100824/6DV7VFM6.txt");

		//		report.setSaname("�н�˾");
		//		RecommendStock rs = extractor.extractFromFile(report,
		// "http://guru.caijing.com/papers/20100823/6DSQ8GD4.txt");
		// "http://guru.caijing.com/papers/20100823/6DSQ8I7I.txt");
		// "http://guru.caijing.com/papers/20100824/6DV81EQ7.txt");
		//				"http://guru.caijing.com/papers/20100823/6DSQ8PNA.txt");

		// "http://guru.caijing.com/papers/20100729/6CLQ6V6M.txt");
		// "http://guru.caijing.com/papers/20100728/6CLQDDU5.txt");
		// "http://guru.caijing.com/papers/20100727/6CLPMSD3.txt");
		// "http://guru.caijing.com/papers/20100722/6CLQ0D0R.txt");
		// "http://guru.caijing.com/papers/20100722/6CLPVL4E.txt");
		// "http://guru.caijing.com/papers/20100721/6CLPTHLC.txt");
		// "http://guru.caijing.com/papers/20100824/6DV7VTTM.txt");
		// "http://guru.caijing.com/papers/20100817/6DFEUUCV.txt");
		// "http://guru.caijing.com/papers/20100817/6DFEUH0H.txt");

		// RecommendStock rs = extractor.extractFromFile(report,
		// "http://guru.caijing.com/papers/20100818/6DFFM141.txt",
		// "http://guru.caijing.com/papers/20100805/6CLPSVML.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNA4E8.txt");
		// "http://guru.caijing.com/papers/20100813/6DAKKVV0.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNAMVB.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNA20A.txt");

		// extractor.extractFromFile("�н�˾", "��ɽ����",
		// "http://guru.caijing.com/papers/20100818/6DFFK8O1.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFK6F7.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKM04.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKP85.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKD65.txt",
		// ServerUtil.getid());

		// RecommendStock rs
		// =extractor.extractFromFile("��֤ͨȯ","000987","F:\\email\\�о�����7.07\\��֤ͨȯ--��������(000987)��Ӫ�������ã�����Ͷ�ʰ�ȫ�߼�.txt",ServerUtil.getid());

		// extractor.extractFromFile("�������","F:\\email\\�о�����7.07\\6CR4CDUO.txt",ServerUtil.getid());
		// extractor.extractFromFile("�н�˾","","F:\\email\\�о�����7.07\\6CR4CVCJ.txt",ServerUtil.getid());
		// recommendStockDao.insert(rs);
		// File file = new
		// File("F:\\email\\�о�����7.19\\��̩����--����ٻ�(600729)������ҵ������30�����ͷų�ַ���Ԥ��.txt");
		// extractor
		// .extractFromFile(
		// "F:\\email\\�о�����7.19\\����֤ȯ--���ϵ���(000939)2010���б����ش�����������б����ã���ɶ����ٴ������������й�˾Ͷ�������ʷ���.pdf",
		// "F:\\email\\papers\\�о�����7.19\\����֤ȯ--ʥũ��չ(002299)10�����ڲƱ����������ֹ�����������.txt");

		// char hi='��';
		// char hi = '��';

		// String str="����ʦ \n���� A0230208100457 \nluojing@swsresearch.com \n\nӦ����
		// \nyingzz@swsresearch.com \n \n��ϵ�� ";
		// ����ÿ������ 1.70 1.55 2.01 1.40 1.80
		// 2.20 ����ÿ������ 1.70 1.55 2.01 1.40 1.80 2.20
		// ȫ��̯��ÿ������ 0.71 0.74 1.00 1.40 1.80 2.20

		// String str = "�о��� \n�ϵ��������Ƽ�\n��ɽ�ɷ�(000877.CH)";
		// String str = FileUtil.read(
		// "http://guru.caijing.com/papers/20100729/6CLQ6V6M.txt", "GBK");
		// String str ="\n �� �� ά�� 2010�� 8�� 2��";
		//
		// System.out.println("stri:!" + str);
		// Pattern publishDatePattern = Pattern.compile(
		// "\\n\\s*([�����м���]\\s*[����Գ�]\\s*[����ά]\\s*[�ߵͳִ�])\\s+201",
		// Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		// Matcher m = publishDatePattern.matcher("" + str);
		// while (m != null && m.find()) {
		// System.out.println("groupCount: !" + m.groupCount());
		// String grade = m.group(1);
		// // System.out.println("ananlyzer: !" + ananlyzer +
		// // m.group(2)+m.group(3));
		// System.out.println("grade: !" + grade);
		// System.out.println("Matcher!");
		// }
		// Pattern publishDatePattern = Pattern
		// .compile("�о���\\s+([\u4e00-\u9fa5]+)\\s+[\u4e00-\u9fa5]{4}\\(",
		// Pattern.CASE_INSENSITIVE | Pattern.DOTALL
		// | Pattern.UNIX_LINES);
		// Matcher m = publishDatePattern.matcher("" + str);
		// // System.out.println("hi:!" + Integer.toHexString((int) hi));
		// while (m != null && m.find()) {
		// System.out.println("groupCount: !" + m.groupCount());
		// String ananlyzer = m.group(1);
		// // System.out.println("ananlyzer: !" + ananlyzer +
		// // m.group(2)+m.group(3));
		// System.out.println("ananlyzer: !" + ananlyzer);
		// System.out.println("Matcher!");
		// }
		// if (m == null || !m.find()) {
		// System.out.println("Not Matcher!");
		// }

		// Pattern stockcodePattern = Pattern.compile(
		// "(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
		// Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		//
		// Matcher m = stockcodePattern
		// .matcher("����֤ȯ-100825-002311������10�б�����-���ϡ��������ȡ�����ҵ������");
		// if (m != null && m.find()) {
		// System.out.println("code:" + m.group(1));
		// } 
	}

	public static void testHuaTai(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("��̩����");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101102/6JJABJV9.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101207/6ME38N57.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101207/6ME38PU2.txt");
		extractor.extractFromFile(report, "http://51gurus.com/papers/20100916/6FQ6LHOR.txt");
		//extractor.extractFromFile(report, "http://51gurus.com/papers/20101026/6JG010EI.txt");

	}

	public static void testAnxin(ReportExtractor extractor) {

	}

	public static void testGuoXin(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("����֤ȯ");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101126/6LOU51MC.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101214/6N0442VH.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101105/6K1PKN33.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101028/6JG6L3FA.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100823/6DSQ8JDK.txt");
		//extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100720/6CLQ686V.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100709/6CLQ134A.txt");

	}

	public static void testChangJiang(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("����֤ȯ");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100721/6CLPVB6U.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100709/6CLQ1238.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100727/6CLPL92G.txt");

	}

	public static void testGuangDa(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("���֤ȯ");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101220/6NKNIIPD.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101220/6NKNKBQB.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101220/6NKNIPGQ.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101214/6N041NI5.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101216/6NEGLFLL.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100720/6CLQ674G.txt");
		extractor.extractFromFile(report, "http://51gurus.com/papers/20100804/6CLQAHU0.txt");
	}

	public static void testYinHe(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("����֤ȯ");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101019/6IF8N9AA.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100902/6ER8O3PB.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100914/6FNVFP9U.txt");

	}

	public static void testHongYuan(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("��Դ֤ȯ");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101021/6IV1EJJL.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101021/6IV1EVCQ.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100902/6ER8OLK5.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100720/6CLQ5MCD.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100720/6CLQ5VHP.txt");
		//		extractor.extractFromFile(report, "http://51gurus.com/papers/20100817/6DFEUH9Q.txt");
		extractor.extractFromFile(report, "http://51gurus.com/papers/20100729/6CLQ7EL6.txt");
	}

	public static void testDongFang(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("����֤ȯ");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101222/6NM7BKKI.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101224/6O0RDJOD.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101220/6NKNJIMT.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101026/6JG01F0V.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101111/6KAC7HKS.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101222/6NM7BKKI.txt");
	}

	public static void testZhongXin(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("���Ž�Ͷ");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101223/6NN9PK9F.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101224/6O0RCG3J.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101223/6NN9PNQ1.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101223/6NN9Q6T2.txt");
		//		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100817/6DFEUH58.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100816/6DFGMTPQ.txt");

	}

	public static void testZhongTou(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("��Ͷ֤ȯ");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101215/6NE31N5T.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101214/6N0433B0.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101207/6ME39EST.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101109/6K57IKS5.txt");
	}

	public static void testHuaTai2(ReportExtractor extractor) {
		//��˾�б�
		Report report = new Report();
		report.setSaname("��̩֤ȯ");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100830/6EHD8M75.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100830/6EHD83T0.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100830/6EHD8FOG.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20101015/6ICPKERT.txt");
		extractor.extractFromFile(report, "http://www.51gurus.com/papers/20100902/6ER8NGHE.txt");
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public StockDao getDao() {
		return dao;
	}

	public void setDao(StockDao dao) {
		this.dao = dao;
	}
}
