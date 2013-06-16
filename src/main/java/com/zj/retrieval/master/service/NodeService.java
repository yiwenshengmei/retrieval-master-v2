package com.zj.retrieval.master.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.zj.retrieval.master.entity.Node;
import com.zj.retrieval.master.util.MyUtils;

@Repository(value="NodeService")
public class NodeService {

	private final static Logger logger = LoggerFactory.getLogger(NodeService.class);
	
	@Resource(name="SqlSession")
	private SqlSession session;
	
	public List<Node> getNodeList(int begin, int end) {
		Map<String, Integer> param = new HashMap<String, Integer>();
		param.put("begin", begin);
		param.put("end", end);
		List<Node> nds = session.selectList("SelectAll", param);
		if (nds.size() > 0) logger.debug(nds.get(1).getCname());
		return nds;
	}
	
	public List<Node> getAllNodeList() {
		return null;
	}

	public List<Map<String, Object>> getNodeListByPid(String pid) {
		return session.selectList("SelectNodeByPid", MyUtils.asMap("pid", pid));
	}
}
