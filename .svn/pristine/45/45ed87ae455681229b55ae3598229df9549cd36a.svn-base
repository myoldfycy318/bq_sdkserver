package com.qbao.sdk.server.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.qbao.sdk.server.security.role.CasUserDetail;

public abstract class BaseController {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected String getCurrentUsername() {
		String username = "";
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return username;
		}

		Authentication authentication = context.getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				username = ((UserDetails) principal).getUsername();
			}
		}
		return username;
	}

	protected long getCurrentUserId() {
		long id = 0l;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return id;
		}
		Authentication authentication = context.getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CasUserDetail) {
				id = ((CasUserDetail) principal).getId();
			}
		}
		return id;
	}

	/**
	 * 获取用户手机号
	 * 
	 * @return
	 */
	protected String getCurrentUserMobile() {
		String mobile = null;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return mobile;
		}
		Authentication authentication = context.getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CasUserDetail) {
				mobile = ((CasUserDetail) principal).getMobilePhone();
			}
		}
		return mobile;
	}

	protected long getCasUserId(HttpServletRequest request) {
		long id = 0L;
		try {
			CasAuthenticationToken token = (CasAuthenticationToken) request
					.getUserPrincipal();
			if (token != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> attributes = token.getAssertion()
						.getPrincipal().getAttributes();
				id = Long.valueOf(attributes.get("id").toString());
			}
		} catch (Exception e) {
			log.error("获取用户登录信息错误 , {}" ,e.getMessage());
		}
		return id;
	}

}

