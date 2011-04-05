package com.caijing.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.UserDao;
import com.caijing.util.TopicNameConfig;

@Controller
@SessionAttributes("currUser")
public class DirectoryController {
	@Autowired
	@Qualifier("userDao")
	private UserDao ibatisUserDao = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@RequestMapping("/notice/*.htm")
	public void notice(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		String path = request.getRemoteAddr();
		System.out.println("path:" + path);
		System.out.println("getContextPath:" + request.getContextPath());
		System.out.println("getRequestURL:" + request.getRequestURL());
		response.sendRedirect("/notice/*.html");
		return;
	}
}
