package com.caijing.model;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RandomGraphServlet extends HttpServlet {

	public RandomGraphServlet() {
		super();

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		//设置输出内容为图像，格式为jpeg
		res.setContentType("image/jpg");
		try {
			//将内容输出到响应客户端对象的输出流中，生成的图片中包含6个字符

			String v = RandomGraph.createInstance(6).drawAlpha(RandomGraph.GRAPHIC_JPEG, res.getOutputStream());
			//将字符串的值保留在session中，便于和用户手工输入的验证码比较，比较部分不是本文讨论重点，故略

			req.getSession().setAttribute("random", v);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
