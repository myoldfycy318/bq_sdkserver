package com.qbao.sdk.server.login.domain.user;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 5350047399015049613L;

	/**
	 * 用户id
	 */
	private long userId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 手机号
	 */
	private String phone;

	public User() {
	}

	public User(long userId, String username, String phone) {
		this.userId = userId;
		this.username = username;
		this.phone = phone;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [userId=");
		builder.append(userId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", phone=");
		builder.append(phone);
		builder.append("]");
		return builder.toString();
	}
}