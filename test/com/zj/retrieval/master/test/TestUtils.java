package com.zj.retrieval.master.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.zj.retrieval.master.service.NodeService;

public class TestUtils {
	public static ApplicationContext getApplicationContext() {
		return new FileSystemXmlApplicationContext("/WebContent/WEB-INF/beans.xml");
	}
	
	public static NodeService getNodeService() {
		return (NodeService) getApplicationContext().getBean("NodeService");
	}
}
