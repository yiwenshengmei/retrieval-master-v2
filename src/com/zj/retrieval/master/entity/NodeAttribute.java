package com.zj.retrieval.master.entity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeAttribute {
	private static Logger logger = LoggerFactory.getLogger(NodeAttribute.class);
	private String key;
	private String value;
	private String id;
	private Node node;
	
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
	
	public NodeAttribute() { }
	
	public NodeAttribute(String key, String value, Node node) {
		this.key = key;
		this.value = value;
		this.node = node;
	}
	
	public NodeAttribute(String key, String value) {
		this(key, value, null);
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
