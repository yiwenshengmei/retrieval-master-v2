package com.zj.retrieval.master.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zj.retrieval.master.Utils;
import com.zj.retrieval.master.entity.NodeAttribute;

public class NodeAttributeService {
	
	private SqlSession session;
	public SqlSession getSession() {
		return session;
	}


	public void setSession(SqlSession session) {
		this.session = session;
	}

	private final static Logger logger = LoggerFactory.getLogger(NodeAttributeService.class);
	
	public static JSONArray parse(Map<String, String> fields) {
		JSONArray result = new JSONArray();
		for (String key : fields.keySet()) {
			JSONObject jField = new JSONObject();
			try {
				jField.put("key", key);
				jField.put("value", fields.get(key));
			} catch (JSONException e) { logger.error("在将自定义字段转换成json格式时发生错误。", e); }
			result.put(jField);
		}
		return result;
	}
	
	
	public static Map<String, String> parse(JSONArray jUserfields) {
		try {
			Map<String, String> result = new HashMap<String, String>();
			for (int i = 0; i < jUserfields.length(); i++) {
				JSONObject jField = jUserfields.getJSONObject(i);
				result.put(jField.getString("key"), jField.getString("value"));
			}
			return result;
		} catch (JSONException e) { 
			logger.error("在将json字符串解析成自定义字段时发生错误。", e);
			return null;
		}
	}
	
	@Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
	public void addNodeAttribute(NodeAttribute attr) {
		attr.setId(StringUtils.isBlank(attr.getId()) ? Utils.getId() : attr.getId());
		session.insert("InsertNodeAttribute", attr);
		throw new RuntimeException("fdfff");
	}
}
