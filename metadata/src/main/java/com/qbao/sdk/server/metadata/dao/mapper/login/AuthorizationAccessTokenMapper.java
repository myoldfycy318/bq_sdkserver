package com.qbao.sdk.server.metadata.dao.mapper.login;

import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.login.AuthorizationAccessTokenEntity;

@Repository
public interface AuthorizationAccessTokenMapper extends IBaseMapperDao<AuthorizationAccessTokenEntity, Long> {

	/**
	 * 获取accessToken
	 * 
	 * @param authorizationAccessToken
	 * @return
	 */
	AuthorizationAccessTokenEntity getAccessToken(AuthorizationAccessTokenEntity authorizationAccessToken);

	/**
	 * 根据access_token获取token信息
	 * 
	 * @param tokenId
	 * @return
	 */
	AuthorizationAccessTokenEntity getAccessTokenById(String accessToken);
}
