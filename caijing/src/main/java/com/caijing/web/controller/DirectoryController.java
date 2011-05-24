package com.caijing.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("currUser")
public class DirectoryController {
	private Log logger = LogFactory.getLog(DirectoryController.class);

	String prefix = "/home/html/";

	@RequestMapping("/notice/*.htm")
	public void notice(HttpServletResponse response, ModelMap model, HttpServletRequest request) throws IOException,
			Exception {
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
	public String report(HttpServletResponse response, ModelMap model, HttpServletRequest request) throws IOException,
			Exception {
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/master/[0-9]+.htm")
	public String master(HttpServletResponse response, ModelMap model, HttpServletRequest request) throws IOException,
			Exception {
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/financialreport/*.htm")
	public String financialreport(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/stockagency/*.htm")
	public String stockagency(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/earnrank.do")
	public String earnrank(@RequestParam(value = "kind", required = true) Integer kind,
			@RequestParam(value = "page", required = true) Integer page, HttpServletResponse response, ModelMap model,
			HttpServletRequest request) throws IOException, Exception {
		logger.debug("earnrank.do kind:" + kind + "  page:" + page);
		String url = "/earnrank/" + kind + "/" + page + ".htm";
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/discount.do")
	public String discount(@RequestParam(value = "kind", required = true) Integer kind,
			@RequestParam(value = "page", required = true) Integer page, HttpServletResponse response, ModelMap model,
			HttpServletRequest request) throws IOException, Exception {
		logger.debug("discount.do kind:" + kind + "  page:" + page);
		String url = "/discount/" + kind + "/" + page + ".htm";
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/analyzerrank/*.htm")
	public String analyzerrank(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {

		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/analyzerrank.do")
	public String analyzerrankdo(@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "page", required = true) String page, HttpServletResponse response, ModelMap model,
			SessionStatus status, HttpServletRequest request) throws IOException, Exception {
		logger.debug("analyzerrank.do year:" + year + "  page:" + page);
		String url = "/analyzerrank/" + year + "/" + page + ".htm";
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/successrank/*.htm")
	public String successrank(HttpServletResponse response, ModelMap model, HttpServletRequest request)
			throws IOException, Exception {
		String url = request.getRequestURL().toString().replaceAll("http://.*?\\.com/", "");
		System.out.println("getRequestURL:" + url);
		return url;
	}

	@RequestMapping("/successrank.do")
	public String successrankdo(@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "page", required = true) String page, HttpServletResponse response, ModelMap model,
			SessionStatus status, HttpServletRequest request) throws IOException, Exception {
		logger.debug("successrank year:" + year + "  page:" + page);
		String url = "/successrank/" + year + "/" + page + ".htm";
		System.out.println("getRequestURL:" + url);
		return url;
	}
}
