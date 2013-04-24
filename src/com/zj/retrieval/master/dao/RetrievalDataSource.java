package com.zj.retrieval.master.dao;

import java.util.ArrayList;
import java.util.List;

import com.zj.retrieval.master.entity.Attribute;
import com.zj.retrieval.master.entity.Matrix;

public class RetrievalDataSource {
	// key����������֤�е��кţ�value������кŶ�Ӧ����������
	private List<Attribute> attributes;
	// key�������������е��кţ�value�����ӽ�������ݿ��е�id
	private List<String> childNodes;
	private Matrix matrix;
	
	public RetrievalDataSource() {
		attributes = new ArrayList<Attribute>();
		childNodes = new ArrayList<String>();
		matrix = new Matrix();
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public List<String> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(List<String> childNodes) {
		this.childNodes = childNodes;
	}
	public Matrix getMatrix() {
		return matrix;
	}
	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}
}
