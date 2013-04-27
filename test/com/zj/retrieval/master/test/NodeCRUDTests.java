package com.zj.retrieval.master.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.zj.retrieval.master.entity.Node;
import com.zj.retrieval.master.entity.NodeAttribute;

import com.zj.retrieval.master.service.NodeService;

public class NodeCRUDTests {
	
	private Logger logger = LoggerFactory.getLogger(NodeCRUDTests.class);
	
	@Test
	public void addRootNodeTest() {
		Node nd = getTestNode();
		nd.setAttributes(getTestNodeAttributes());
		TestUtils.getNodeService().addNode(nd);
	}
	
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
		NodeService s = TestUtils.getNodeService();
		s.updateENameByEName("Men", "Women");
	}
	
	private Node getTestNode() {
		Node nd = new Node();
		nd.setCname("Ð¡Äñ");
		nd.setEname("Small Bird");
		nd.setCreatorId(-1);
		nd.setDesc("");
		nd.setModifierId(-1);
		nd.setRemark("");
		nd.setUri("");
		nd.setParentId(Node.VIRTUAL_NODE_ID);
		return nd;
	}
	
	private List<NodeAttribute> getTestNodeAttributes() {
		List<NodeAttribute> attrs = new ArrayList<NodeAttribute>();
		NodeAttribute attr1 = new NodeAttribute("³á°ò", "¶Ì");
		attr1.setCreatorId(-1);
		attr1.setModifierId(-1);
		attr1.setRemark("");
		NodeAttribute attr2 = new NodeAttribute("Ã«É«", "×ØÉ«");
		attr2.setCreatorId(-1);
		attr2.setModifierId(-1);
		attr2.setRemark("");
		attrs.add(attr1);
		attrs.add(attr2);
		return attrs;
	}
}
