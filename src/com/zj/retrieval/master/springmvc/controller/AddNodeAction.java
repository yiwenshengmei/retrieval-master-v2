package com.zj.retrieval.master.springmvc.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.zj.retrieval.master.AttributeSelectedWrongException;
import com.zj.retrieval.master.AttributeSelector;
import com.zj.retrieval.master.DetailType;
import com.zj.retrieval.master.NodeType;
import com.zj.retrieval.master.UserField;
import com.zj.retrieval.master.Util;
import com.zj.retrieval.master.dao.NodeDao;
import com.zj.retrieval.master.dao.UserDao;
import com.zj.retrieval.master.entity.Attribute;
import com.zj.retrieval.master.entity.Node;

public class AddNodeAction {
	private static Logger logger = LoggerFactory.getLogger(AddNodeAction.class);
	private String desc;
	private String node_name_en;
	private String node_name;
	private String parent_id;
	private String uri;
	private String images;
	private String user_field;
	private String parent_attr;
	private String new_attr;
	
	private String message;
	private boolean isError;
	
	public boolean getIsError() {
		return this.isError;
	}
	
	private String post_user_name;
	private String post_user_password;
	
	public String execute() {
		try {
			UserDao userDao = Util.getUserDao();
			if (!userDao.verifyUser(post_user_name, post_user_password)) {
				this.isError = true;
				this.message = "�û������������.";
				return ActionSupport.ERROR;
			}
			
			Node newNode = new Node();
			newNode.setDesc(desc);
			newNode.setEnglishName(node_name_en);
			newNode.setName(node_name);
			newNode.setNodeType(NodeType.NODETYPE_CLASS); // ����ʱд��
			newNode.setParentId(parent_id);
			newNode.setUri(uri);
			newNode.setUriName(newNode.getUri() + "#" + newNode.getEnglishName());
			newNode.setDetailType(DetailType.FULL);
			
			// ����images
			List<String> fullPaths = new ArrayList<String>();
			for (String image_id : images.split(";")) {
				fullPaths.add("images/" + image_id);
			}
			newNode.setImages(fullPaths);
			
			// �����Զ����ֶ�
			if (user_field != null && !user_field.isEmpty()) {
				JSONArray userFieldJSONArray = new JSONArray(user_field);
				newNode.setUserfields(UserField.parse(userFieldJSONArray));
			}
			
			NodeDao nodeDao =  Util.getNodeDao();
			
			Node parentNode = nodeDao.getNodeById(newNode.getParentId());
			logger.info("�ҵ����ڵ㣺" + parentNode);
			AttributeSelector attrSelector = nodeDao.getAttributeSelector(parentNode);
			String[] selectedAttributes = parent_attr.isEmpty() ?
				new String[0] : parent_attr.split(" ");
			for (int i = 0; i < selectedAttributes.length; i++) {
				int selectedAttribute = Integer.valueOf(selectedAttributes[i]);
				attrSelector.select(selectedAttribute, true);
				logger.info(String.format("ѡ�񸸽ڵ�����[id=%1$s, name=%2$s]", selectedAttribute, 
						parentNode.getRetrievalDataSource().getAttributes().get(selectedAttribute).getName()));
			}
			JSONArray jNewAttributes = new JSONArray(new_attr);
			for (int i = 0; i < jNewAttributes.length(); i++) {
				JSONObject jAttr = jNewAttributes.getJSONObject(i);
				Attribute newAttr = new Attribute(jAttr.getString("new_attr_name"),
						                          jAttr.getString("new_attr_name_en"),
						                          jAttr.getString("new_attr_desc"),
						                          jAttr.getString("new_attr_image"));
				JSONArray jAttrUserfields = jAttr.getJSONArray("new_attr_user_field");
				newAttr.setUserFields(UserField.parse(jAttrUserfields));
				logger.info("����ӵ����ԣ�" + newAttr);
				attrSelector.addNewAttribute(newAttr, true);
			}
			nodeDao.addNode(newNode, parentNode, attrSelector);
			
			this.message = "Success, o(��_��)o...";
			return ActionSupport.SUCCESS;
			
		} catch (JSONException e) {
			this.message = "�ͻ��˳��������ݸ�ʽ������ʹ�����µĿͻ��˳���";
			return ActionSupport.ERROR;
		} catch (AttributeSelectedWrongException e) {
			this.message = "�����������ĸ��ڵ����ԣ�";
			return ActionSupport.ERROR;
		} catch (NumberFormatException ex) {
			this.message = "���ڵ����Ը�ʽ����";
			return ActionSupport.ERROR;
		} catch (Exception ex) {
			this.message = ex.getMessage();
			return ActionSupport.ERROR;
		}
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setNode_name_en(String name_en) {
		this.node_name_en = name_en;
	}

	public void setNode_name(String name) {
		this.node_name = name;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public void setUser_field(String user_field) {
		this.user_field = user_field;
	}

	public void setParent_attr(String parent_attr) {
		this.parent_attr = parent_attr;
	}

	public void setNew_attr(String new_attr) {
		this.new_attr = new_attr;
	}

	public String getMessage() {
		return message;
	}

	public void setPost_user_name(String post_user_name) {
		this.post_user_name = post_user_name;
	}

	public void setPost_user_password(String post_user_password) {
		this.post_user_password = post_user_password;
	}
}
