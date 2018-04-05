package com.qbao.sdk.server.service.login;

import com.qbao.sdk.server.metadata.entity.login.AuthorizationAccessTokenEntity;

public interface AuthorizationAccessTokenService {

	/**
	 * 获取accessToken信息
	 * 
	 * @param appCode
	 *            应用app编码
	 * @param userName
	 *            用户名
	 * @param userId
	 *            用户id
	 * @param mobile
	 *            用户手机号
	 * @param accessType
	 *            接入类型
	 * @return accessToken
	 */
	AuthorizationAccessTokenEntity getAccessToken(String appCode, String userName, int userId, Long mobile,
			int accessType);

	/**
	 * 根据token获取accessToken相关信息
	 * 
	 * @param tokenId
	 *            accessToken
	 * @return accessToken相关信息
	 */
	AuthorizationAccessTokenEntity getAccessTokenByTokenId(String tokenId);
	
	/**
	 * 校验AppCode是否在白名单内
	* @Title: checkWhiteAppCode 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param  @param appCode
	* @param  @return
	* @return boolean    返回类型 
	* @throws
	 */
	boolean checkWhiteAppCode(String appCode);
}