package com.zj.retrieval.master;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zj.retrieval.master.dao.MatrixDao;
import com.zj.retrieval.master.dao.NodeAttributeDao;
import com.zj.retrieval.master.dao.NodeDao;
import com.zj.retrieval.master.dao.NodeImageDao;
import com.zj.retrieval.master.dao.UserDao;

public class Configuration {
	public static ApplicationContext springCtx = null;
	public static String IMAGE_PATH_PREFIX = "images/";
	
	public static ApplicationContext getApplicationContext() {
		if (springCtx == null) {
//			ServletContext servletCtx = ServletActionContext.getServletContext();
//			WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(servletCtx);
			ClassPathXmlApplicationContext springCtx = new ClassPathXmlApplicationContext("beans.xml");
			return springCtx;
		} else {
			return springCtx;
		}
	}
	
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
	
	public static String getImageNameExcludePath(String url) {
		return url.substring(IMAGE_PATH_PREFIX.length(), url.length());
	}
	
	public static NodeImageDao getNodeImageDao() {
		return (NodeImageDao) getApplicationContext().getBean("nodeImageDao");
	}
	
	public static NodeDao getNodeDao() {
		return (NodeDao) getApplicationContext().getBean("nodeDao");
	}
	
	public static MatrixDao getMatrixDao() {
		return (MatrixDao) getApplicationContext().getBean("matrixDao");
	}
	
	public static String html(String content) {
		if(content==null) return "";        
	    String html = content;
	    html = html.replaceAll("'", "&apos;");
	    html = html.replaceAll("\"", "&quot;");
	    html = html.replaceAll("\t", "&nbsp;&nbsp;");
	    html = html.replaceAll(" ", "&nbsp;");
	    html = html.replaceAll("<", "&lt;");
	    html = html.replaceAll(">", "&gt;");
	    html = html.replaceAll("\n", "<br/>");
//	    html = html.replaceAll("&", "&amp;");
	    return html;
	}
	
	public static String urlConnect(String url, String connect) {
		return endsWithSlash(url) ? url + connect : url + "/" + connect;
	}
	
	private static boolean endsWithSlash(String str) {
		return str.endsWith("/") || str.endsWith("\\");
	}
	
	public static String getImagePath() {
		return "";
	}
}
