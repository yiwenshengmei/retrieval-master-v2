package com.zj.retrieval.master.util;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zj.retrieval.master.entity.Node;

public class JSONUtils {
	
	public static JSONObject getJSON(Node nd) throws JSONException {
		JSONObject j = new JSONObject();
		j.put("pid", nd.getParentId());
		j.put("ename", nd.getEname());
		return j;
	}
	
	public static JSONObject getJSON4ZTree(Map<String, Object> nd) throws JSONException {
		JSONObject j = new JSONObject();
		j.put("name", nd.get("CNAME"));
		j.put("cname", nd.get("ENAME"));
		j.put("id", nd.get("ID"));
		j.put("isParent", ((Long)nd.get("CNT")) > 0 ? "true" : "false");
		return j;
	}
	
	public static JSONArray getJSONArray(List<Node> nds) throws JSONException {
		JSONArray array = new JSONArray();
		for (Node nd : nds) {
			array.put(getJSON(nd));
		}
		return array;
	}
	
	public static JSONArray getJSONArray4ZTree(List<Map<String, Object>> nds) throws JSONException {
		JSONArray array = new JSONArray();
		for (Map<String, Object> nd : nds) {
			array.put(getJSON4ZTree(nd));
		}
		return array;
	}
}
