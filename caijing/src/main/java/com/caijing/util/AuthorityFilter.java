package com.caijing.util;

import java.io.IOException;

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

import com.caijing.domain.User;

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
				logger.debug("获取路径请求 " + URL);
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

			if (isUsed) {
				User user = (User) session.getAttribute("currUser");
				if (user != null) {
					logger.debug("user: " + user.getUsername());
				} else {
					logger.debug("user is null!");
				}

				if (containsOneOfList(URL, ignoreList)) {
					logger.debug("对请求的 " + URL + " 忽略检查。");
					isValid = true;
				} else if ((user == null) || (StringUtils.isEmpty(user.getUsername()))) {
					logger.debug("对请求的 " + URL + " 进行安全检查。");
					isValid = false;
				}
			}

			if (isValid) {
				logger.debug("对请求的文件 " + URL + " 检查通过。");
				filterChain.doFilter(sRequest, response);
			} else {
				if (sRequest.getRequestURI().toLowerCase().contains("notice")) {
					logger.warn("用户跳转如介绍页。");
					sResponse.sendRedirect(topicNameMap.getTopicName("notice"));
				} else {
					logger.warn("用户还没有登录,强制用户登录。");
					sResponse.sendRedirect("/admin/err.html?login=true");
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