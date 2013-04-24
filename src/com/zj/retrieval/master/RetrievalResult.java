package com.zj.retrieval.master;

import java.util.ArrayList;
import java.util.List;

import com.zj.retrieval.master.entity.NodeFeature;

public class RetrievalResult {
	private boolean hasResult;
	private NodeFeature nextFeature;
	private List<Node> result;
	private List<Integer> lastState;
	
	public RetrievalResult() {
		hasResult = false;
		nextFeature = null;
		result = new ArrayList<Node>();
		lastState = new ArrayList<Integer>();
	}
	
	public boolean hasResult() {
		return hasResult;
	}
	public void hasResult(boolean hasResult) {
		this.hasResult = hasResult;
	}
	public NodeFeature getNextFeature() {
		return nextFeature;
	}
	public void setNextFeature(NodeFeature nextFeature) {
		this.nextFeature = nextFeature;
		hasResult = false;
	}

	public List<Integer> getLastState() {
		return lastState;
	}

	public void setLastState(List<Integer> lastState) {
		this.lastState = lastState;
	}

	public List<Node> getResult() {
		return result;
	}

	public void setResult(List<Node> result) {
		this.result = result;
	}
	
	
}
