package com.zj.retrieval.master.listener;

import org.apache.ibatis.session.SqlSession;

public class InitalDataBaseListener {
	
	private SqlSession sess;
	
	public void initalDataBase() {
		sess.update("deleteDataBaseRetrieval");
		sess.update("createDataBaseRetrieval");
		sess.update("deleteTableNode");
		sess.update("createTableNode");
		sess.update("insertVirtualNode");
	}
}
