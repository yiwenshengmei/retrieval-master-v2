package com.zj.retrieval.master.listener;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitDataBaseListener {
	
	private Logger logger = LoggerFactory.getLogger(InitDataBaseListener.class);
	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public void initalDataBase() {
		logger.debug("===================¿ªÊ¼=======================");
//		session.update("deleteDataBaseRetrieval");
//		session.update("createDataBaseRetrieval");
		session.update("deleteTableNode");
		session.update("createTableNode");
		session.update("insertVirtualNode");
		logger.debug("===================½áÊø=======================");
	}
}
