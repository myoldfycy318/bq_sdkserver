package com.qbao.sdk.server.metadata.entity.login;

import java.io.Serializable;
import java.util.Date;

import com.qbao.sdk.server.util.DateUtils;

public class OauthAccessRecordEntity implements Serializable {
	private static final long serialVersionUID = 2278918884951668974L;

	/**
	 * 访问时间
	 */
	private Date accessTime = DateUtils.now();

	/**
	 * 接入类型
	 */
	private int accessType;

	/**
	 * accessToken
	 */
	private String tokenId;

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
	private Long userId;

	/**
	 * 访问结果
	 */
	private String result = "0";

	/**
	 * 表名后缀
	 */
	private String curMonth;

	/**
	 * 请求地址url
	 */
	private String requestUrl;

	/**
	 * 请求地址url类型
	 */
	private int requestUrlType;

	public Date getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}

	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getRequestUrlType() {
		return requestUrlType;
	}

	public void setRequestUrlType(int requestUrlType) {
		this.requestUrlType = requestUrlType;
	}

	public String getCurMonth() {
		return curMonth;
	}

	public void setCurMonth(String curMonth) {
		this.curMonth = curMonth;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
