package com.zj.retrieval.master.entity;


public class NodeFeature {

	public static final int YES    = 2;
	public static final int NO     = 1;
	public static final int UNKNOW = 3;
	
	private String id;
	private String nodeId;
	private int inNodeIndex;
	private String desc;
	private String cname;
	private String ename;
	private int creatorId;
	private int modifierId;
	
	public NodeFeature() { 
	}
	
	public NodeFeature(String name) {
		this();
		this.cname = name;
	}
	
	public NodeFeature(String name, String id) {
		this(name);
		this.id = id;
	}
	
	public NodeFeature(String name, String enName, String desc) {
		this.desc = desc;
		this.cname = name;
		this.ename = enName;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return cname;
	}

	public void setName(String name) {
		this.cname = name;
	}

	public String getEnglishName() {
		return ename;
	}

	public void setEnglishName(String englishName) {
		this.ename = englishName;
	}

	public int getIndex() {
		return inNodeIndex;
	}

	public void setIndex(int index) {
		this.inNodeIndex = index;
	}

	public String getEnName() {
		return ename;
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

	public int getInNodeIndex() {
		return inNodeIndex;
	}

	public void setInNodeIndex(int inNodeIndex) {
		this.inNodeIndex = inNodeIndex;
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
