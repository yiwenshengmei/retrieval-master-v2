package com.zj.retrieval.master.entity;

import java.util.ArrayList;
import java.util.List;



public class RetrievalDataSource {
	private Matrix matrix;
	private String id;
	private Node node;
	private List<NodeFeature> features;
	
	public RetrievalDataSource() { 
		features = new ArrayList<NodeFeature>();
		matrix = new Matrix();
	}
	
	public RetrievalDataSource(Node node) {
		this();
		this.node = node;
	}
	
	public Matrix getMatrix() {
		return matrix;
	}
	
	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public List<NodeFeature> getFeatures() {
		return features;
	}

	public void setFeatures(List<NodeFeature> features) {
		this.features = features;
	}
}
