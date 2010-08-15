package com.caijing.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;


@Component("responseUtil")
public class ResponseUtil {
	
	public void alertAndBack(String msg, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=GBK");
		PrintWriter out = response.getWriter();
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		out.print("<script>alert('" + msg + "');self.history.back();</script>");
		out.flush();
	}
	
	public void alertAndBack(String msg, int step, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=GBK");
		PrintWriter out = response.getWriter();
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		if (msg!=null && msg.trim().length()>0) {
			out.print("<script>alert('" + msg + "');self.history.go(-" + step + ");</script>");
		} else {
			out.print("<script>self.history.go(-" + step + ");</script>");
		}
		out.flush();
	}
	
	public void alertAndFlush(String msg,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=GBK");
		PrintWriter out = response.getWriter();
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		if (msg!=null && msg.trim().length()>0) {
			out.print("<script>alert('" + msg + "');parent.left.location.reload();</script>");
		} else {
			out.print("<script>self.parent.reload();</script>");
		}
		out.flush();
	}
	
	public void alertAndBack(String msg, String redirect, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=GBK");
		PrintWriter out = response.getWriter();
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		if (msg!=null && msg.trim().length()>0) {
			out.print("<script>alert('" + msg + "');</script>");
		} 
		out.flush();
		response.sendRedirect(redirect);
	}
}
