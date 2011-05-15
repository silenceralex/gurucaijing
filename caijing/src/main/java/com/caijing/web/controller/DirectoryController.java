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
		String file = FileUtils.readFileToString(new File(filepath.toLowerCase()), "gbk");
		//		System.out.println("file:" + file);
		response.setContentType("text/html;charset=gbk");
		response.getWriter().write(file);
		return;
	}

	@RequestMapping("/report/*.htm")
	public String report(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/master/[0-9]+.htm")
	public String master(HttpServletResponse response, ModelMap model, SessionStatus status, HttpServletRequest request)
			throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/financialreport/*.htm")
	public String financialreport(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/stockagency/*.htm")
	public String stockagency(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/earnrank/*.htm")
	public String earnrank(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/discount/*.htm")
	public String discount(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/analyzerrank/*.htm")
	public String analyzerrank(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/successrank/*.htm")
	public String successrank(HttpServletResponse response, ModelMap model, SessionStatus status,
			HttpServletRequest request) throws IOException, Exception {
		status.setComplete();
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}
}
