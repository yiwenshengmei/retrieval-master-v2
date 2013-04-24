package com.zj.retrieval.master.springmvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.zj.retrieval.master.Util;
import com.zj.retrieval.master.dao.NodeDao;
import com.zj.retrieval.master.entity.Node;

public class QueryNodeOWLAction {

	private static Logger logger = LoggerFactory.getLogger(QueryNodeOWLAction.class);
	
	private String format;
	private String node_id;
	private String owl;
	
	protected String execute() {
		try {
			
			logger.info("查询owl的节点id为：" + node_id);
			
			NodeDao ndService = Util.getNodeDao();
			
			Node node = ndService.getNodeById(node_id);
			
			this.owl = Boolean.valueOf(format) ? XMLUtil.format(node.getOwl(), 4) : node.getOwl();
			return ActionSupport.SUCCESS;
			
		} catch (Exception ex) {
			logger.error("获得OWL时发生错误", ex);
			return ActionSupport.ERROR;
		}
		
	}

	public String getOwl() {
		return owl;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
}
