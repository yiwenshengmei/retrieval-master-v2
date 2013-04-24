package com.zj.retrieval.master.entity;


public class MatrixItem {
	private Integer value;
	private MatrixRow row;
	private String id;
	
	public static MatrixItem Yes(MatrixRow row) {
		return new MatrixItem(NodeFeature.YES, row);
	}
	
	public static MatrixItem No(MatrixRow row) {
		return new MatrixItem(NodeFeature.NO, row);
	}
	
	public static MatrixItem Unknow(MatrixRow row) {
		return new MatrixItem(NodeFeature.UNKNOW, row);
	}
	
	public MatrixItem() { }
	
	public MatrixItem(Integer value, MatrixRow row) {
		this();
		this.value = value;
		this.row = row;
	}
	
	public MatrixItem(Integer value) {
		this(value, null);
	}

	public MatrixRow getRow() {
		return row;
	}

	public void setRow(MatrixRow row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public Integer getValue() {
		return value;
	}
	
	public String getTextValue() {
		return NodeFeature.textValue(value);
	}
	
	public String getShortTextValue() {
		return NodeFeature.shotTextValue(value);
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
