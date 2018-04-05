package com.qbao.sdk.server.metadata.entity.login;

import java.io.Serializable;
import java.util.Date;

import com.qbao.sdk.server.util.DateUtils;

public class AuthorizationAccessTokenEntity implements Serializable {
	private static final long serialVersionUID = -3341254038578468961L;

	private long id;

	/**
	 * 创建时间
	 */
	private Date createTime = DateUtils.now();

	/**
	 * accessToken
	 */
	private String accessToken;

	/**
	 * 应用编码
	 */
	private String clientId;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 用户id
	 */
	private Integer userId;

	/**
	 * 失效时间
	 */
	private Date expiration;

	/**
	 * 手机号码
	 */
	private Long mobile;

	/**
	 * 接入类型
	 */
	private int accessType;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}

	public boolean isExpired() {
		return expiration != null && expiration.before(DateUtils.now());
	}

	public int getExpiresIn() {
		return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue()
				: 0;
	}

}