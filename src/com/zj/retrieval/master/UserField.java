package com.zj.retrieval.master;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserField {
	private static Logger logger = LoggerFactory.getLogger(UserField.class);
	
	public static JSONArray parse(Map<String, String> fields) {
		JSONArray result = new JSONArray();
		for (String key : fields.keySet()) {
			JSONObject jField = new JSONObject();
			try {
				jField.put("key", key);
				jField.put("value", fields.get(key));
			} catch (JSONException e) { logger.error("�ڽ��Զ����ֶ�ת����json��ʽʱ��������", e); }
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
			logger.error("�ڽ�json�ַ����������Զ����ֶ�ʱ��������", e);
			return null;
		}
	}
}
