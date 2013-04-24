package com.zj.retrieval.master.actions;


public class UserAction {
	private String msg;
	private String name;
	
	public String execute() {
		this.msg = "Hello " + name;
		return "jsonRet";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
