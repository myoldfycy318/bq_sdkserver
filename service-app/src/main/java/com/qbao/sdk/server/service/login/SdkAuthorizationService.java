package com.qbao.sdk.server.service.login;

import com.qbao.sdk.server.login.domain.request.AuthorizationRequest;
import com.qbao.sdk.server.view.AjaxResult;
import com.qbao.sdk.server.view.SdkOauthResult;

public interface SdkAuthorizationService {

	/**
	 * 检查授权相关请求参数
	 * 
	 * @param appCode
	 *            应用的app编码
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	SdkOauthResult checkAuthorizeRequest(String appCode, String userName, String password);

	/**
	 * 根据token获取用户信息
	 * 
	 * @param authorizationRequest
	 *            授权请求信息
	 * @return 用户信息
	 */
	SdkOauthResult getUserInfoByToken(AuthorizationRequest authorizationRequest);

	/**
	 * 登录解锁接口
	 * 
	 * @param ip
	 *            请求访问ip
	 * @param loginName
	 *            登录用户名
	 * @return
	 */
	AjaxResult relieveUserLoginLock(String ip, String loginName);

}