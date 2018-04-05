package com.qbao.sdk.server.service.impl.login;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qbao.sdk.server.metadata.dao.mapper.login.AuthorizationAccessTokenMapper;
import com.qbao.sdk.server.metadata.entity.login.AuthorizationAccessTokenEntity;
import com.qbao.sdk.server.service.login.AuthorizationAccessTokenService;
import com.qbao.sdk.server.util.DateUtils;

@Service("authorizationAccessTokenService")
public class AuthorizationAccessTokenServiceImpl implements AuthorizationAccessTokenService {

	@Autowired
	private AuthorizationAccessTokenMapper authorizationAccessTokenMapper;

	@Value("${AccessToken.ValiditySeconds}")
	private int expiredSeconds;


	@Value("${app_whitelist}")
	private String appWhiteList;
	
	
	public AuthorizationAccessTokenEntity getAccessTokenByTokenId(String tokenId) {
		return authorizationAccessTokenMapper.getAccessTokenById(tokenId);
	}

	@Transactional(rollbackFor = Exception.class)
	public AuthorizationAccessTokenEntity getAccessToken(String appCode, String userName, int userId, Long mobile,
			int accessType) {
		AuthorizationAccessTokenEntity accessTokenEntity = new AuthorizationAccessTokenEntity();
		accessTokenEntity.setClientId(appCode);
		accessTokenEntity.setUserName(userName);
		accessTokenEntity.setAccessType(accessType);

		AuthorizationAccessTokenEntity existingAccessToken = authorizationAccessTokenMapper
				.getAccessToken(accessTokenEntity);

		if (null != existingAccessToken) {
			if (existingAccessToken.isExpired()) {
				authorizationAccessTokenMapper.remove(existingAccessToken.getId());
			} else {
				return existingAccessToken;
			}
		}

		AuthorizationAccessTokenEntity accessToken = createAccessToken(appCode, userName, userId, mobile, accessType);
		authorizationAccessTokenMapper.add(accessToken);

		return accessToken;
	}

	/**
	 * 创建accessToken信息
	 * 
	 * @param clientId
	 *            app编码
	 * @param userName
	 *            用户名
	 * @param userId
	 *            用户id
	 * @param mobile
	 *            手机号
	 * @param accessType
	 *            接入类型
	 * @return accessToken对象信息
	 */
	private AuthorizationAccessTokenEntity createAccessToken(String clientId, String userName, Integer userId,
			Long mobile, int accessType) {
		AuthorizationAccessTokenEntity token = new AuthorizationAccessTokenEntity();
		String tokenId = UUID.randomUUID().toString();
		token.setAccessToken(tokenId);
		token.setClientId(clientId);
		token.setUserName(userName);
		token.setUserId(userId);
		token.setExpiration(DateUtils.getInternalDateBySecond(DateUtils.now(), expiredSeconds));
		token.setMobile(mobile);
		token.setAccessType(accessType);
		return token;
	}

	@Override
	public boolean checkWhiteAppCode(String appCode) {
		if(appWhiteList.contains(appCode)){
			return true;
		}else{
			return false;
		}
	}

}