package com.zj.retrieval.master.actions;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zj.retrieval.master.AbstractAction;
import com.zj.retrieval.master.User;
import com.zj.retrieval.master.service.BizUser;

public abstract class PermissionValidateAction extends AbstractAction {
	private HttpServletRequest request;
	private ServletContext servletContext;
	private HttpServletResponse response;
	
	private final static Logger logger = LoggerFactory.getLogger(PermissionValidateAction.class);
	
	private String loginName;
	private String loginPassword;
	
	protected void preExecute() throws PermissionValidateFailException, NoPermissionInfoException {
		if (!needPermissionValidate()) { 
			logger.debug(String.format("ActionName: %1$s 不需要验证权限", getActionName()));
			return; 
		};
		HttpSession httpSession = request.getSession(true);
		boolean hasLogin = httpSession.getAttribute("hasLogin") == null ? 
				false : (Boolean) request.getSession(true).getAttribute("hasLogin");
		if (!hasLogin) {
			// 说明用户未登陆
			if (StringUtils.isBlank(loginName) && StringUtils.isBlank(loginPassword)) {
				logger.debug("没有登陆信息，请转向登陆页面");
				throw new NoPermissionInfoException("没有登陆信息，请转向登陆页面");
			}

			User user = null;
			if ((user = BizUser.login(loginName, loginPassword)) == null) {
				logger.debug(String.format("登录失败: LoginName=%1$s, LoginPassword=%2$s", loginName, loginPassword));
				throw new PermissionValidateFailException("登陆失败");
			}
			else {
				logger.debug(String.format("登录成功: LoginName=%1$s", loginName));
				httpSession.setAttribute("hasLogin", "true");
				httpSession.setAttribute("user", user);
			}
		}
	}
	
	protected boolean needPermissionValidate() {
		return true;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
}
