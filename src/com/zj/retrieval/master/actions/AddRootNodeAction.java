package com.zj.retrieval.master.actions;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.zj.retrieval.master.AbstractNodeCRUDAction;
import com.zj.retrieval.master.Node;

public class AddRootNodeAction extends AbstractNodeCRUDAction {

	private static Logger logger = LoggerFactory.getLogger(AddRootNodeAction.class);
	
	@Override
	protected void beforeSaveNode(Node node, Session sess) {
		Node virtualNode = (Node) sess.get(Node.class, Node.VIRTUAL_NODE_ID);
		virtualNode.getChildNodes().add(node);
		node.setParentNode(virtualNode);
	}

	@Override
	protected String getSuccessfulMesssage() {
		return "O(กษ_กษ)O~";
	}

	@Override
	protected String getActionResult() {
		return ActionSupport.SUCCESS;
	}

	@Override
	protected String getActionName() {
		return AddRootNodeAction.class.getName();
	}
}
