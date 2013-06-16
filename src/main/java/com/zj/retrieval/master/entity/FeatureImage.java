package com.zj.retrieval.master.entity;

public class FeatureImage {
	private String id;
	private String path;
	private String featureId;
	private int creatorId;
	private int modifierId;
	private String url;
	
	public FeatureImage(String path, String featureId) {
		this.path = path;
		this.featureId = featureId;
	}
	
	public FeatureImage() {}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
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

	@Override
	public String toString() {
		return "FeatureImage [id=" + id + ", path=" + path + "]";
	}
}
