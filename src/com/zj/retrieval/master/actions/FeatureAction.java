package com.zj.retrieval.master.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.RequestAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.zj.retrieval.master.DALService;
import com.zj.retrieval.master.IDALAction;
import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.entity.NodeFeature;

public class FeatureAction {
	private String parentId = null;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public String getParentFeatureAjax() throws Exception {
		if (parentId == null)
			throw new IllegalArgumentException("没有输入父节点ID");
		List<Map> features = (List<Map>) DALService.doAction(new IDALAction() {
			@Override
			public Object doAction(Session sess, Transaction tx) throws Exception {
				StringBuilder sql = new StringBuilder()
				.append("select f.name, f.id")
				.append(" from t_node nd")
				.append(" left join t_retrieval_data_source rds on rds.node_id = nd.id")
				.append(" left join t_node_feature f on f.rds_id = rds.id")
				.append(" where nd.id=:id");
				return sess.createSQLQuery(sql.toString())
						.addScalar("name", StandardBasicTypes.STRING)
						.addScalar("id", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.setString("id", parentId)
						.list();
			}
		});
		
		this.dataMap.put("features", features);
		return NodeAction.ACTION_RESULT_JSON;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}
}
