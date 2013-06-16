package com.zj.retrieval.master.entity;

import java.util.List;

public class Node {

	public final static String VIRTUAL_NODE_NAME = "VIRTUAL_NODE";
	public final static String VIRTUAL_NODE_ID   = "VIRTUAL_NODE";
	
	private String id;
	private String uri;
	private String cname;
	private String ename;
	private String parentId;
	private int ofParentIndex;
	private String desc;
	private String owl;
	private int creatorId;
	private int modifierId;
	private String remark;
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	private List<NodeAttribute> attributes;

	public List<NodeAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<NodeAttribute> attributes) {
		this.attributes = attributes;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public Node() {
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getOwl() {
		return owl;
	}
	public void setOwl(String owl) {
		this.owl = owl;
	}
	public int getOfParentIndex() {
		return ofParentIndex;
	}
	public void setOfParentIndex(int inParentIndex) {
		this.ofParentIndex = inParentIndex;
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
