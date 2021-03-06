package com.qbao.sdk.server.service.impl.login;

import com.qbao.sdk.server.login.domain.request.AuthorizationRequest;
import com.qbao.sdk.server.login.domain.user.User;
import com.qbao.sdk.server.login.enumeration.ErrorCodeEnum;
import com.qbao.sdk.server.metadata.dao.mapper.login.AuthorizationAccessTokenMapper;
import com.qbao.sdk.server.metadata.entity.login.AuthorizationAccessTokenEntity;
import com.qbao.sdk.server.service.login.SdkAuthorizationService;
import com.qbao.sdk.server.util.RedisUtil;
import com.qbao.sdk.server.util.rsa.RSACoder;
import com.qbao.sdk.server.view.AjaxResult;
import com.qbao.sdk.server.view.SdkOauthResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("sdkAuthorizationService")
public class SdkAuthorizationServiceImpl implements SdkAuthorizationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SdkAuthorizationServiceImpl.class);

	@Autowired
	private AuthorizationAccessTokenMapper authorizationAccessTokenMapper;

	@Value("${rsa_private_key_4_sdk}")
	private String RSA_PRIVATE_KEY_4_SDK;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Value("${opensdk.server.ip}")
	private String openSdkServerIp;

	@Override
	public SdkOauthResult checkAuthorizeRequest(String appCode, String loginName, String password) {
		if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password)) {
//			LOGGER.error(">>>>>>>>>>>有必填参数为空");
			return SdkOauthResult.failed(ErrorCodeEnum.有必填参数为空.code, ErrorCodeEnum.有必填参数为空.name());
		}

		String srcPasswordAndCode = "";
		try {
			srcPasswordAndCode = new String(
					RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), RSA_PRIVATE_KEY_4_SDK));
		} catch (Exception e) {
			LOGGER.error(">>>>>>>>>>>字符串解密失败");
			return SdkOauthResult.failed(ErrorCodeEnum.字符串解密失败.code, ErrorCodeEnum.字符串解密失败.name());
		}

		if (srcPasswordAndCode.length() < 12) {
//			LOGGER.error(">>>>>>>>>>>无效的密码或authCode");
			return SdkOauthResult.failed(ErrorCodeEnum.无效的密码或authCode.code, ErrorCodeEnum.无效的密码或authCode.name());
		}

		String srcPassword = srcPasswordAndCode.substring(0, srcPasswordAndCode.length() - 6);

		String authCode = srcPasswordAndCode.substring(srcPasswordAndCode.length() - 6);

		String redisKey = "sdkserver_" + appCode + authCode;
		if (StringUtils.isBlank(redisUtil.get(redisKey))) {
//			LOGGER.error(">>>>>>>>>>>无效的authCode");
			return SdkOauthResult.failed(ErrorCodeEnum.无效的authCode.code, ErrorCodeEnum.无效的authCode.name());
		}

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("srcPassword", srcPassword);
		resultMap.put("authCode", authCode);
		return SdkOauthResult.success(resultMap);
	}

	@Override
	public SdkOauthResult getUserInfoByToken(AuthorizationRequest authorizationRequest) {
		String accessToken = authorizationRequest.getTokenId();
		String clientId = authorizationRequest.getClientId();

		AuthorizationAccessTokenEntity tokenInfo = authorizationAccessTokenMapper.getAccessTokenById(accessToken);

		if (null == tokenInfo) {
			LOGGER.error(">>>>>>>>>>>无效的 token:{}", accessToken);
			return SdkOauthResult.failed(ErrorCodeEnum.无效的token.code, ErrorCodeEnum.无效的token.name());
		}

		if (null == tokenInfo.getClientId() || !clientId.equals(tokenInfo.getClientId())) {
			LOGGER.error(">>>>>>>>>>>tonken 对应的客户端信息不匹配:{} ", accessToken);
			return SdkOauthResult.failed(ErrorCodeEnum.tonken对应的客户端信息不匹配.code, ErrorCodeEnum.tonken对应的客户端信息不匹配.name());
		}

		if (tokenInfo.isExpired()) {
			LOGGER.error(">>>>>>>>>>>tonken 已经过期:{} ", accessToken);
			return SdkOauthResult.failed(ErrorCodeEnum.tonken已经过期.code, ErrorCodeEnum.tonken已经过期.name());
		}

		Integer userId = tokenInfo.getUserId();
		String userName = tokenInfo.getUserName();
		User user = new User(userId, userName, null);
		authorizationRequest.setUser(user);
		authorizationRequest.setAccessType(tokenInfo.getAccessType());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("userId", userId);
		resultMap.put("userName", userName);

		return SdkOauthResult.success(resultMap);
	}

	@Override
	public AjaxResult relieveUserLoginLock(String ip, String loginName) {
		if (openSdkServerIp.indexOf(ip) == -1) {
			LOGGER.error(">>>>>>>登录解锁请求IP不合法,请求ip为{},允许访问的ip为{}", ip, openSdkServerIp);
			return AjaxResult.failed("请求ip不合法");
		}

		if (StringUtils.isBlank(loginName)) {
			LOGGER.error("请求用户名为空");
			return AjaxResult.failed("请求用户名为空");
		}

		redisTemplate.delete("open_sdkserver_login_error_" + loginName.trim());
		redisTemplate.delete("open_sdkserver_login_error_flag_" + loginName.trim());
		return AjaxResult.success();
	}
}
