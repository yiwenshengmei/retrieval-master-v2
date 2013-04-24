package com.zj.retrieval.master.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zj.retrieval.master.entity.Node;

import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;

public class NodeService {
	
	private SqlSession session;
	
	public void addNode() {
	}
	
	public List<Node> getNodeByCName(String cname) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("cname", cname);
		List<Node> nodes = session.selectList("selectNodeByCName", param);
		return nodes;
	}
	
	public List<Node> getNodeByEName(String ename) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("ename", ename);
		List<Node> nodes = session.selectList("selectNodeByEName", param);
		return nodes;
	}
	
	@Transactional
	public void updateENameByEName(String ename, String newName) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("ename", ename);
		param.put("newEName", newName);
		session.update("updateENameByEName", param);
		session.update("clearCName");
	}

	public SqlSession getSession() {
		return session;
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	
}
