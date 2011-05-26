package com.caijing.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;
import com.caijing.util.TopicNameConfig;

public class AuthorityFilter extends HttpServlet implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FilterConfig filterConfig;

	private boolean isUsed = true;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	private static Log logger = LogFactory.getLog(AuthorityFilter.class);

	private boolean containsOneOfList(String str, String[] list) {
		for (int i = 0; list != null && i < list.length; i++) {
			if (str.indexOf(list[i]) != -1)
				return true;
		}
		return false;
	}

	private boolean containsOneOfUserrights(String str, List<Userright> currRights) {
		for (Userright right : currRights) {
			if (str.indexOf(right.getPath().toUpperCase()) != -1)
				return true;
		}
		return false;
	}

	public AuthorityFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
		HttpServletRequest sRequest = (HttpServletRequest) request;
		HttpServletResponse sResponse = (HttpServletResponse) response;

		boolean isValid = true;
		try {
			HttpSession session = sRequest.getSession();
			session.setMaxInactiveInterval(10800);

			String URL = sRequest.getRequestURI().toUpperCase();
			if (logger.isDebugEnabled()) {
				logger.debug("��ȡ·������ " + URL);
			}

			String useFilter = filterConfig.getInitParameter("isUsed");
			if (useFilter != null && useFilter.equalsIgnoreCase("false")) {
				this.isUsed = false;
			}

			String ignoreFile = filterConfig.getInitParameter("ignores").toUpperCase();
			String[] ignoreList = null;
			if (ignoreFile != null && StringUtils.isNotEmpty(ignoreFile)) {
				if (ignoreFile.indexOf(",") == -1) {
					ignoreFile = ignoreFile + ",";
				}
				ignoreList = ignoreFile.split(",");
			}
			List<Userright> currRights = (List<Userright>) session.getAttribute("currRights");
			WebUser user = (WebUser) session.getAttribute("currWebUser");
			if (isUsed) {
				if (user != null) {
					logger.debug("user: " + user.getEmail());
				} else {
					logger.debug("user is null!");
				}

				if (containsOneOfList(URL, ignoreList)) {
					logger.debug("������� " + URL + " ���Լ�顣");
					isValid = true;
				} else if (user != null && currRights != null) {
					if (containsOneOfUserrights(URL, currRights) || URL.contains("USER") || URL.contains("CART")) {
						logger.debug("��������ļ� " + URL + " ���ͨ����");
						isValid = true;
					} else {
						if (URL.contains("USER") || URL.contains("CART")) {
							logger.debug("��������ļ� " + URL + " ���ͨ����");
							isValid = true;
						} else {
							logger.debug("user: " + user.getEmail() + "  �޶� URL��" + URL + "   �ķ���Ȩ��");
							isValid = false;
							sResponse.setContentType("text/html; charset=GBK");
							sResponse.getWriter().write(
									"<script>alert('��û�ж� URL:" + URL
											+ " �ķ���Ȩ��,��������תҳ�湺��лл��'); top.location='/user/myAccount.htm';</script>");
							sResponse.flushBuffer();
						}
					}
				} else if ((user == null) || (StringUtils.isEmpty(user.getEmail()))) {
					logger.debug("������� " + URL + " ���а�ȫ��顣");
					isValid = false;
				}
			}

			if (isValid) {
				filterChain.doFilter(sRequest, response);
			} else {
				if (sRequest.getRequestURI().toLowerCase().contains("notice")) {
					logger.warn("�û���ת�����ҳ��" + topicNameMap.getTopicName("notice"));
					sResponse.sendRedirect(topicNameMap.getTopicName("notice"));
				} else {
					logger.warn("�û���û�е�¼,ǿ���û���¼��");
					sResponse.sendRedirect("/template/user/err.html?login=true");
				}
			}
		} catch (ServletException sx) {
			filterConfig.getServletContext().log(sx.getMessage());
		} catch (IOException iox) {
			filterConfig.getServletContext().log(iox.getMessage());
		} catch (Exception ex) {
			filterConfig.getServletContext().log(ex.getMessage());
		}
	}

	public void destroy() {
		filterConfig = null;
	}
}