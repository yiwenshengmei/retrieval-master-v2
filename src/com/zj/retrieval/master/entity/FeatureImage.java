package com.zj.retrieval.master.entity;

import java.util.ArrayList;
import java.util.List;



public class FeatureImage {
	private String id;
	private String path;
	private NodeFeature feature;
	private String url;
	
	public static List<FeatureImage> batchCreate(List<String> paths, NodeFeature feature) {
		List<FeatureImage> ret = new ArrayList<FeatureImage>();
		for (String path : paths) {
			ret.add(new FeatureImage(path, feature));
		}
		return ret;
	}
	
	public FeatureImage(String path, NodeFeature feature) {
		this.path = path;
		this.feature = feature;
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

	public NodeFeature getFeature() {
		return feature;
	}

	public void setFeature(NodeFeature feature) {
		this.feature = feature;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
