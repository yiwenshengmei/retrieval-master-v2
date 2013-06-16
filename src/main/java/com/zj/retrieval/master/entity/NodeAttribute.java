package com.zj.retrieval.master.entity;

public class NodeAttribute {
	
	private String key;
	private String value;
	private String id;
	private String nodeId;
	private int creatorId;
	private int modifierId;
	private String remark;
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public NodeAttribute() { }
	
	public NodeAttribute(String key, String value, String nodeId) {
		this.key = key;
		this.value = value;
		this.nodeId = nodeId;
	}
	
	public NodeAttribute(String key, String value) {
		this(key, value, null);
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

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getModifierId() {
		return modifierId;
	}

	public void setModifierId(int modifierId) {
		this.modifierId = modifierId;
	}
}
