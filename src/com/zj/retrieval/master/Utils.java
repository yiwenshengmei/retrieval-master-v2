package com.zj.retrieval.master;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

import com.zj.retrieval.master.dao.NodeDao;
import com.zj.retrieval.master.dao.UserDao;

public class Utils {
	public static ApplicationContext applicationContext = null;
	public static String IMAGE_PATH_PREFIX = "images/";
	
	public static ApplicationContext getApplicationContext() {
//		if (applicationContext == null) {
//			ServletContext servletCtx = ServletActionContext.getServletContext();
//			WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(servletCtx);
//			return springCtx;
//		} else {
//			return applicationContext;
//		}
		
		return null;
	}
	
	public static String getId() {
		return UUID.randomUUID().toString();
	}
	
	public static String getImageNameExcludePath(String url) {
		return url.substring(IMAGE_PATH_PREFIX.length(), url.length());
	}
	
	public static UserDao getUserDao() {
		ApplicationContext ctx = getApplicationContext();
		return (UserDao) ctx.getBean("userDao");
	}
	
	public static NodeDao getNodeDao() {
		ApplicationContext ctx = getApplicationContext();
		return (NodeDao) ctx.getBean("nodeDao");
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
	public static void cleanList(List... arrays) {
		for (List array : arrays) {
			if (array == null)
				continue;
			Iterator iter = array.iterator();
			while (iter.hasNext()) {
				if (iter.next() == null) 
					iter.remove();
			}
		}
	}
	
	public static String null2Empty(String value) {
		return (value == null ? StringUtils.EMPTY : value);
	}
}
