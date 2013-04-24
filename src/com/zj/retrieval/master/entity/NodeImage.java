package com.zj.retrieval.master.entity;

import java.util.ArrayList;
import java.util.List;


public class NodeImage {
	private String id;
	private String path;
	private Node node;
	private String url;
	
	public static List<NodeImage> batchCreate(List<String> paths, Node node) {
		List<NodeImage> ret = new ArrayList<NodeImage>();
		for (String path : paths) {
			ret.add(new NodeImage(path, node));
		}
		return ret;
	}
	
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public NodeImage(String path) {
		this(path, null);
	}
	
	public NodeImage(String path, Node node) {
		this.path = path;
		this.node = node;
	}
	
	public NodeImage() {}

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
}
