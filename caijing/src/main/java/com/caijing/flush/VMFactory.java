package com.caijing.flush;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.springframework.dao.DataAccessException;

import com.caijing.util.FileUtil;
import com.caijing.util.ParseErrorMessageThreadLocal;

/**
 * add exception handler
 * 
 * @author jeff
 * 
 */
public class VMFactory implements MethodExceptionEventHandler {

	private static final Log LOG = LogFactory.getLog(VMFactory.class);

	private String template = "";

	private VelocityContext context = null;

	VelocityEngine engine = new VelocityEngine();

	private boolean fatalException = false;

	//	private static final String[] flitterTexts = new String[] { "220.181.", "202.108." };

	/**
	 * 构造
	 */
	public VMFactory() {
		context = new VelocityContext();
		try {
			//			engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new MyVelocityLog());
			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", "/home/html");

			engine.init(p);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传递模板的构造
	 * 
	 * @param template
	 */
	public VMFactory(String template) {
		context = new VelocityContext();
		setTemplate(template);
		/* 首先创建一个模板引擎的实例，并予以初始化 */
		try {
			engine.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VMFactory(VelocityEngine engine, String template) {
		context = new VelocityContext();
		setTemplate(template);
		this.engine = engine;
	}

	/**
	 * 设定模板
	 * 
	 * @param template
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * 填充数据
	 * 
	 * @param name
	 * @param obj
	 */
	public void put(String name, Object obj) {
		context.put(name, obj);
	}

	/**
	 * 保存到文件
	 * 
	 * @param filename
	 * @return
	 */
	public boolean save(String filename) {
		return this.save(filename, "GBK");
	}

	/**
	 * 保存到文件
	 * 
	 * @param filename
	 * @return
	 */
	public boolean save(String filename, String encode) {
		String filebody = this.toString();
		if (fatalException) {
			if (LOG.isErrorEnabled()) {
				LOG.error("fatal exception happen html file will not be over write");
			}
			return false;
		}
		FileUtil.write(filename, filebody, encode);
		return true;
	}

	/**
	 * 输出字符串
	 */
	public String toString() {
		StringWriter out = new StringWriter();
		try {
			output(out);
			return out.toString();
		} catch (Exception e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
			return "";
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 输出到流
	 * 
	 * @param out
	 * @return
	 */
	public void output(Writer out) throws Exception {
		try {
			Template tpl = engine.getTemplate(template, "GBK");
			EventCartridge ec = new EventCartridge();
			ec.addEventHandler(this);
			ec.attachToContext(context);
			tpl.merge(context, out);
		} catch (DataAccessException e) {
			ParseErrorMessageThreadLocal.setParseErrorMessage(e.getMessage());
			this.fatalException = true;
		} catch (Exception e) {
			ParseErrorMessageThreadLocal.setParseErrorMessage(e.getMessage());
			this.fatalException = true;
			e.printStackTrace();
		}
	}

	public Object methodException(Class target, String methodName, Exception exception) throws Exception {
		if (LOG.isErrorEnabled()) {
			LOG.error("exception happend when dealing with template [" + template + "] target class is ["
					+ target.getName() + "] method name is [" + methodName + "]", exception);
		}
		if (exception instanceof DataAccessException) {
			this.fatalException = true; // 
			ParseErrorMessageThreadLocal.setParseErrorMessage(exception.getMessage());
			if (exception.getCause() instanceof SQLException) {
				LOG.error("在刷新模板  " + template + " 时发生SQL异常，本模板刷新取消！\n 异常内容： " + exception.getMessage());
			} else {
				LOG.error("在刷新模板  " + template + " 时发生严重异常，本模板刷新取消！\n 异常内容： " + exception.getMessage());
			}
			throw exception;
		}
		return null;
	}
}
