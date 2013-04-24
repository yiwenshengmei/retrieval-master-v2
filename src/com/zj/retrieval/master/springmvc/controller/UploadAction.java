package com.zj.retrieval.master.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.zj.retrieval.master.Util;
import com.zj.retrieval.master.dao.UserDao;
import com.zj.retrieval.master.entity.Node;

public class UploadAction {
	private File image;
	
	private String message;
	private boolean isError;
	
	public boolean getIsError() {
		return isError;
	}
	
	private String post_user_name;
	private String post_user_password;
	
	private static Logger logger = LoggerFactory.getLogger(Node.class);
	
	public String execute() {
		try {
			UserDao userDao = Util.getUserDao();
			if (!userDao.verifyUser(post_user_name, post_user_password)) {
				this.isError = true;
				this.message = "用户名或密码错误！";
				
				return ActionSupport.ERROR;
			}
						
			String filename = null;
			List<String> images_path = new ArrayList<String>();
			String realpath = ServletActionContext.getServletContext().getRealPath("/images");
			File folder = new File(realpath);
			if(!folder.exists()) {
				folder.mkdirs();
				logger.debug("用于存放图片的文件夹不存在，现已创建。" + folder.toString());
			}
			if (null != image) {
				filename = UUID.randomUUID().toString() + ".jpg";
				File destfile = new File(folder, filename);
				FileUtils.copyFile(image, destfile);
				String fullName = "images/" + filename;
				images_path.add(fullName);
				logger.debug("保存文件成功: " + destfile.getPath());
			} else {
				throw new IllegalArgumentException("服务端没有接收到文件！");
			}
			this.message = "文件id: " + filename;
			return ActionSupport.SUCCESS;
		} catch (IOException e) {
			logger.error("服务端保存文件时失败", e);
			this.message = "服务端保存文件时失败";
			return ActionSupport.ERROR;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			this.message = e.getMessage();
			return ActionSupport.ERROR;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public void setPost_user_name(String post_user_name) {
		this.post_user_name = post_user_name;
	}

	public void setPost_user_password(String post_user_password) {
		this.post_user_password = post_user_password;
	}
}
