package com.qbao.sdk.server.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.qbao.sdk.server.security.role.CasUserDetail;

public abstract class BaseController {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected CasUserDetail getCurrentUserInfo() {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return null;
		}

		Authentication authentication = context.getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CasUserDetail) {
				return (CasUserDetail) principal;
			}
		}
		return null;
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
}
