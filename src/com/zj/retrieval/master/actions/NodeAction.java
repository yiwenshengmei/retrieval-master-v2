package com.zj.retrieval.master.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.zj.retrieval.master.DALService;
import com.zj.retrieval.master.IDALAction;
import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.Utils;
import com.zj.retrieval.master.entity.NodeFeature;
import com.zj.retrieval.master.entity.RetrievalDataSource;
import com.zj.retrieval.master.service.BizNode;

public class NodeAction implements ModelDriven<Node>, RequestAware, Preparable {
	
	private Node node;
	private String id = null;
	private Map<String, Object> requestMap;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	public final static String ACTION_RESULT_SHOW_NODE = "showNode";
	public final static String ACTION_RESULT_JSON      = "jsonResult";
	public final static String ACTION_RESULT_ADD_SUCCESS = "addSuccess";
	public final static String ACTION_RESULT_VIEW_NODE_DETAIL = "viewDetail";
	private final static Logger logger = LoggerFactory.getLogger(NodeAction.class);
	
	/**
	 * ACTION��������Ӹ��ڵ�
	 * @return
	 * @throws Exception
	 */
	public String addRootNode() throws Exception {
		RetrievalDataSource rds = node.getRetrievalDataSource();
		
		// �������е�index
		Utils.cleanList(node.getAttributes(), node.getNewFeatures(), node.getImages());
		for (NodeFeature feature : rds.getFeatures())
			Utils.cleanList(feature.getImages());
		
		// �����ϴ����ļ�
		BizNode.preProcessImages(node, ServletActionContext.getServletContext().getRealPath("/images"));
		
		node.setParentNode(null);
		
		// ����
		DALService.doAction(new IDALAction() {
			@Override
			public Object doAction(Session sess, Transaction tx) throws Exception {
				// �ؽ���ϵ
				BizNode.buildRelation(node);
				sess.save(node);
				return null;
			}
		});
		
		return ACTION_RESULT_VIEW_NODE_DETAIL;
	}
	
	public String addNode() throws Exception {
		RetrievalDataSource rds = node.getRetrievalDataSource();
		
		// �������е�index
		Utils.cleanList(node.getAttributes(), rds.getFeatures(), node.getImages(), 
				node.getNewFeatures(), node.getFeaturesOfParent());
		for (NodeFeature feature : rds.getFeatures()) 
			Utils.cleanList(feature.getImages());
		
		// �����ϴ����ļ�
		BizNode.preProcessImages(node, ServletActionContext.getServletContext().getRealPath("/images"));
		
		DALService.doAction(new IDALAction() {
			@Override
			public Object doAction(Session sess, Transaction tx) throws Exception {
				// ��ȡ���ڵ�
				String parentId = node.getParentNode().getId();
				Node parent = (Node) sess.get(Node.class, parentId);
				if (parent == null)
					throw new IllegalArgumentException("���ڵ�[id=" + parentId + "]������");
				node.setParentNode(parent);
				
				// ���¸��ڵ�
				BizNode.addChildToParent(node, parent, node.getNewFeatures());
				// �ؽ���ϵ
				BizNode.buildRelation(node);
				
				sess.save(node);
				sess.update(parent);
				return null;
			}
		});
		
		return ACTION_RESULT_VIEW_NODE_DETAIL;
	}
	
	/**
	 * ��������Node��name��desc��id
	 * @return json��ͼ
	 * @throws Exception
	 */
	public String getParentNodesSelectAjax() throws Exception {
		List<Map> nodes = BizNode.getParentNodes();
		dataMap.put("nodes", nodes);
		return ACTION_RESULT_JSON;
	}
	
	public String getNode() throws Exception {
		Node nd = BizNode.getNode(node.getId());
		BizNode.changePath2Url(nd);
		this.requestMap.put("node_id", nd.getId());
		this.node = nd;
		return ACTION_RESULT_SHOW_NODE;
	}
	
	public String getNodeJSON() throws Exception {
		Node nd = BizNode.getNode(node.getId());
		BizNode.changePath2Url(nd);
		dataMap.put("node", nd);
		return ACTION_RESULT_JSON;
	}
	
	public String test() throws Exception {
		this.node.setId("402809813d399b23013d399b32ee0000");
		return ACTION_RESULT_VIEW_NODE_DETAIL;
	}
	
	@Override
	public void prepare() throws Exception {
		logger.debug("===== ִ����prepare���� =====");
		if (id == null) {
			logger.debug("id == null");
			this.node = new Node();
		}
		else {
			logger.debug("id == " + id);
			this.node = BizNode.getNode(id);
		}
	}

	@Override
	public Node getModel() {
		return this.node;
	}

	@Override
	public void setRequest(Map<String, Object> requestMap) {
		this.requestMap = requestMap;
	}
	
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
