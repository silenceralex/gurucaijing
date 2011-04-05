package com.caijing.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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

	String prefix = "/home/html/";

	@RequestMapping("/notice/*.htm")
	public void notice(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "/");
		System.out.println("getRequestURL:" + url);
		String filepath = prefix + url;
		System.out.println("filepath:" + filepath);
		String file = FileUtils.readFileToString(new File(filepath.toLowerCase()), "utf-8");
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(file);
		return;
	}
}
