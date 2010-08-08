package com.caijing.util;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class EncodingFilter extends HttpServlet implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5847409869083971219L;

	private FilterConfig filterConfig;

	private String targetEncoding = "GBK";
  
    private String[] ignoreList = null;

	private boolean isUsed = true;
  
  public void init(ServletConfig config) throws ServletException{
    super.init(config);
  }

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.targetEncoding = filterConfig.getInitParameter("encoding");
		if (this.targetEncoding == null
				|| this.targetEncoding.equalsIgnoreCase("")) {
			this.targetEncoding = "GBK";
		}
		String useFilter = filterConfig.getInitParameter("isUsed");
		if (useFilter != null && useFilter.equalsIgnoreCase("false")) {
			this.isUsed = false;
		}
    
    String ignores = filterConfig.getInitParameter("ignores");
    
    if(ignores != null && !ignores.equals("")){
      ignoreList = ignores.split(",");
    }
    
	}
  
  private boolean isIgnoreURL(String requestURL){
    if (ignoreList!=null && ignoreList.length!=0){
      for (int i = 0;i< ignoreList.length;i++){
        if (requestURL.indexOf(ignoreList[i])!=-1){
          filterConfig.getServletContext().log("ºöÂÔ¶Ô ".concat(requestURL).concat(" µÄ×ªÂë..."));
          return true;
        }
      }
      return false;
    }
    return false;
  }

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) {
		try {
			if (isUsed) {
				HttpServletRequest sRequest = (HttpServletRequest) request;
        StringBuffer url = new StringBuffer(sRequest.getRequestURI());
        if (sRequest.getQueryString()!=null){
          url.append("?").append(sRequest.getQueryString());
        }
        if (!isIgnoreURL(url.toString())){
				sRequest.setCharacterEncoding(targetEncoding);
        }
			}
			filterChain.doFilter(request, response);
		} catch (ServletException sx) {
			filterConfig.getServletContext().log(sx.getMessage());
		} catch (IOException iox) {
			filterConfig.getServletContext().log(iox.getMessage());
		}
	}

	public void setFilterConfig(final FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}
}
