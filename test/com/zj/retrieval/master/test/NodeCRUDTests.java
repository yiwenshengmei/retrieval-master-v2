package com.zj.retrieval.master.test;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.zj.retrieval.master.entity.Node;

import com.zj.retrieval.master.service.NodeService;

public class NodeCRUDTests {
	
	private Logger logger = LoggerFactory.getLogger(NodeCRUDTests.class);
	
	@Test
	public void testGetNodeByName() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("/WebContent/WEB-INF/beans.xml");
		NodeService s = (NodeService) ctx.getBean("NodeService");
		List<Node> ns = s.getNodeByEName("Men");
		logger.debug("ns.size = " + ns.size());
		if (ns.size() >= 1) {
			logger.debug(ns.get(0).getEname());
		}
	}
	
	@Test
	public void testTransaction() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("/WebContent/WEB-INF/beans.xml");
		NodeService s = (NodeService) ctx.getBean("NodeService");
		s.updateENameByEName("Men", "Women");
	}
}
