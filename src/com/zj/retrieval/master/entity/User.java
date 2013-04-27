package com.zj.retrieval.master.entity;

public class User {

	private String id;
	private String name;
	private String password;
	private String remark;
	private int limitRootNodeCreate;
	private int limitChildNodeCreate;
	private int limitRootNodeEdit;
	private int limitChildNodeEdit;
	private int limitRootNodeDelete;
	private int limitChildNodeDelete;
	private int limitUserManager;
	private int isActive;
	
	public User() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getLimitRootNodeCreate() {
		return limitRootNodeCreate;
	}

	public void setLimitRootNodeCreate(int limitRootNodeCreate) {
		this.limitRootNodeCreate = limitRootNodeCreate;
	}

	public int getLimitChildNodeCreate() {
		return limitChildNodeCreate;
	}

	public void setLimitChildNodeCreate(int limitChildNodeCreate) {
		this.limitChildNodeCreate = limitChildNodeCreate;
	}

	public int getLimitRootNodeEdit() {
		return limitRootNodeEdit;
	}

	public void setLimitRootNodeEdit(int limitRootNodeEdit) {
		this.limitRootNodeEdit = limitRootNodeEdit;
	}

	public int getLimitChildNodeEdit() {
		return limitChildNodeEdit;
	}

	public void setLimitChildNodeEdit(int limitChildNodeEdit) {
		this.limitChildNodeEdit = limitChildNodeEdit;
	}

	public int getLimitRootNodeDelete() {
		return limitRootNodeDelete;
	}

	public void setLimitRootNodeDelete(int limitRootNodeDelete) {
		this.limitRootNodeDelete = limitRootNodeDelete;
	}

	public int getLimitChildNodeDelete() {
		return limitChildNodeDelete;
	}

	public void setLimitChildNodeDelete(int limitChildNodeDelete) {
		this.limitChildNodeDelete = limitChildNodeDelete;
	}

	public int getLimitUserManager() {
		return limitUserManager;
	}

	public void setLimitUserManager(int limitUserManager) {
		this.limitUserManager = limitUserManager;
	}	
}
