package com.zj.retrieval.master.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.zj.retrieval.master.AbstractNodeCRUDAction;
import com.zj.retrieval.master.DALService;
import com.zj.retrieval.master.IDALAction;
import com.zj.retrieval.master.AttributeSelectedWrongException;
import com.zj.retrieval.master.AttributeSelector;
import com.zj.retrieval.master.DetailType;
import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.dao.NodeDao;
import com.zj.retrieval.master.dao.UserDao;
import com.zj.retrieval.master.entity.NodeAttribute;
import com.zj.retrieval.master.entity.NodeFeature;
import com.zj.retrieval.master.entity.NodeImage;
import com.zj.retrieval.master.entity.NodeType;
import com.zj.retrieval.master.service.BizNode;

public class AddNodeAction extends AbstractNodeCRUDAction {
	private static Logger logger = LoggerFactory.getLogger(AddNodeAction.class);
	private String parent_id, parent_attr, new_attr;
	private List<File> newFeatureImages;
	private List<String> newFeatureImagesFileName;
	
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public void setParent_attr(String parent_attr) {
		this.parent_attr = parent_attr;
	}

	public void setNew_attr(String new_attr) {
		this.new_attr = new_attr;
	}

	@Override
	protected void beforeSaveNode(Node node, Session sess) {
		
		Node parentNode = (Node) sess.get(Node.class, parent_id);
		parentNode.getChildNodes().add(node);
		node.setParentNode(parentNode);
		
		
		
		NodeDao nodeDao =  Configuration.getNodeDao();
		
		logger.info("找到父节点：" + parentNode);
		AttributeSelector attrSelector = BizNode.getAttributeSelector(parentNode);
		String[] selectedAttributes = parent_attr.isEmpty() ?
			new String[0] : parent_attr.split(" ");
		for (int i = 0; i < selectedAttributes.length; i++) {
			int selectedAttribute = Integer.valueOf(selectedAttributes[i]);
			attrSelector.select(selectedAttribute, true);
			logger.info(String.format("选择父节点属性[id=%1$s, name=%2$s]", selectedAttribute, 
					parentNode.getRetrievalDataSource().getFeatures().get(selectedAttribute).getName()));
		}
		JSONArray jNewAttributes = new JSONArray(new_attr);
		for (int i = 0; i < jNewAttributes.length(); i++) {
			JSONObject jAttr = jNewAttributes.getJSONObject(i);
			NodeFeature newAttr = new NodeFeature(jAttr.getString("new_attr_name"),
					                          jAttr.getString("new_attr_name_en"),
					                          jAttr.getString("new_attr_desc"),
					                          jAttr.getString("new_attr_image"));
			JSONArray jAttrUserfields = jAttr.getJSONArray("new_attr_user_field");
			newAttr.setUserFields(NodeAttribute.parse(jAttrUserfields));
			logger.info("新添加的属性：" + newAttr);
			attrSelector.addNewAttribute(newAttr, true);
		}
		nodeDao.addNode(node, parentNode, attrSelector);
	}

	public void setNewFeatureImages(File[] newFeatureImages) {
		this.newFeatureImages = newFeatureImages;
	}

	public void setNewFeatureImages(List<File> newFeatureImages) {
		this.newFeatureImages = newFeatureImages;
	}

	public void setNewFeatureImagesFileName(List<String> newFeatureImagesFileName) {
		this.newFeatureImagesFileName = newFeatureImagesFileName;
	}

	@Override
	protected String getActionName() {
		return "AddNodeAction";
	}

	@Override
	protected void preExecute() throws Exception {
	}

	@Override
	protected void postExecute() throws Exception {
	}

	@Override
	protected String getSuccessfulMesssage() {
		return null;
	}

	@Override
	protected String getActionResult() {
		return null;
	}
}
