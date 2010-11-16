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

		//�����������Ϊͼ�񣬸�ʽΪjpeg
		res.setContentType("image/jpg");
		try {
			//�������������Ӧ�ͻ��˶����������У����ɵ�ͼƬ�а���6���ַ�

			String v = RandomGraph.createInstance(6).drawAlpha(RandomGraph.GRAPHIC_JPEG, res.getOutputStream());
			//���ַ�����ֵ������session�У����ں��û��ֹ��������֤��Ƚϣ��Ƚϲ��ֲ��Ǳ��������ص㣬����

			req.getSession().setAttribute("random", v);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
