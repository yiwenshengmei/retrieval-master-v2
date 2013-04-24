package com.zj.retrieval.master.interceptors;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.zj.retrieval.master.actions.LoginAction;

public class PermissionInterceptor implements Interceptor {

	private static final long serialVersionUID = 8962371733637736930L;
	private final static Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);
	public static final String LOGIN_KEY = "LOGIN";

	@Override
	public void destroy() {
	}

	@Override
	public void init() {
	}

	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		logger.debug("begin check login interceptor!");
		Object action = ai.getAction();
		if (action instanceof LoginAction) {
			logger.debug("exit check login, because this is login action.");
			return ai.invoke();
		}

		Map<String, Object> sessionMap = ai.getInvocationContext().getSession();
		String login = (String) sessionMap.get(LOGIN_KEY);
		if (StringUtils.isNotBlank(login)) {
			logger.debug("already login!");
			return ai.invoke();
		}
		else {
			logger.debug("no login, forward login page!");
			return "login";
		}
	}
}
