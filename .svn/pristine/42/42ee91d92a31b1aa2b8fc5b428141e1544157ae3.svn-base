package com.qbao.sdk.server.controller.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.bo.pay.UserInfo;
import com.qbao.sdk.server.controller.BaseController;
import com.qbao.sdk.server.login.domain.request.AuthorizationRequest;
import com.qbao.sdk.server.login.domain.user.User;
import com.qbao.sdk.server.login.enumeration.AccessTypeEnum;
import com.qbao.sdk.server.login.enumeration.ErrorCodeEnum;
import com.qbao.sdk.server.metadata.entity.login.AuthorizationAccessTokenEntity;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.service.login.AuthorizationAccessTokenService;
import com.qbao.sdk.server.service.login.LoginResultNotifyService;
import com.qbao.sdk.server.service.login.SdkAuthorizationService;
import com.qbao.sdk.server.service.login.ThridRequestService;
import com.qbao.sdk.server.service.redis.RedisService;
import com.qbao.sdk.server.util.IPUtil;
import com.qbao.sdk.server.util.RandomValueStringGenerator;
import com.qbao.sdk.server.util.RedisUtil;
import com.qbao.sdk.server.view.AjaxResult;
import com.qbao.sdk.server.view.SdkOauthResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sdklogin/v10/")
public class SdkAuthorizeController extends BaseController {

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    @Autowired
    private SdkAuthorizationService sdkAuthorizationService;

    @Autowired
    private ThridRequestService thridRequestService;

    @Autowired
    private AuthorizationAccessTokenService authorizationAccessTokenService;

    @Autowired
    private LoginResultNotifyService loginResultNotifyService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisService redisService;

    /**
     * 获取授权code接口
     *
     * @param appCode 应用app编码
     * @return 授权code
     */
    @RequestMapping(value = "getAuthCode")
    @ResponseBody
    public SdkOauthResult getAuthCode(String appCode) {
//		log.info(">>>>>>>>sdk获取授权code接口请求:appCode={}", appCode);

        String code = null;
        try {
            SdkOauthResult result = checkClient(appCode);
            if (!result.isSuccess()) {
                return result;
            }

            code = generator.generate();

            String redisKey = "sdkserver_" + appCode + code;
            redisUtil.set(redisKey, JSON.toJSONString(code));
            redisUtil.expire(redisKey, 10 * 60);
        } catch (Exception e) {
            log.error(">>>>>>>>获取授权code失败:", e);
            return SdkOauthResult.failed(ErrorCodeEnum.获取授权code失败.code, ErrorCodeEnum.获取授权code失败.name());
        }

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("authCode", code);

        return SdkOauthResult.success(resultMap);
    }

    /**
     * 获取登录token
     *
     * @param appCode    应用app编码
     * @param loginName  登录名
     * @param password   密码
     * @param accessType 接入类型
     * @param request
     * @return accessToken
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "getLoginToken")
    @ResponseBody
    public SdkOauthResult getLoginToken(String appCode, String loginName, String password, String accessType, HttpServletRequest request) {
//		log.info(">>>>>>>>sdk获取登录token接口请求:" + JSON.toJSONString(request.getParameterMap()));

        AuthorizationRequest authorizationRequest = genrAuthorizationRequest(appCode, null, accessType, request);
        String authCode = "";

        try {
            SdkOauthResult result = checkClient(appCode);
            if (!result.isSuccess()) {
                return result;
            }

            // 校验其他相关请求参数并获取密码
            result = sdkAuthorizationService.checkAuthorizeRequest(appCode, loginName, password);
            String srcPassword = "";
            if (!result.isSuccess()) {
                return result;
            } else {
                srcPassword = ((Map<String, String>) result.getData()).get("srcPassword");
                authCode = ((Map<String, String>) result.getData()).get("authCode");
            }
            UserInfo _userInfo = new UserInfo();
            _userInfo.setLoginName(loginName);
            _userInfo.setPassword(srcPassword);
            //验证宝玩用户与密码
            result = checkUserInfo(_userInfo);
            String username = loginName;

            if (!result.isSuccess()) {
                request.setAttribute("authUserFail", "1");
                return result;
            } else {
                _userInfo = (UserInfo) result.getData();
                User user = new User(Long.valueOf(_userInfo.getUserId()), _userInfo.getUsername(),_userInfo.getMobile());
                authorizationRequest.setUser(user);
                request.setAttribute("authUserOk", "1");
            }

            // 获取accessToken
            AuthorizationAccessTokenEntity accessToken = authorizationAccessTokenService.getAccessToken(appCode, username, Integer.valueOf(_userInfo.getUserId()),
                   Long.valueOf(_userInfo.getMobile()),
                    StringUtils.isNotBlank(accessType) ? Integer.valueOf(accessType) : AccessTypeEnum.WAP.f);

            authorizationRequest.setTokenId(accessToken.getAccessToken());
            authorizationRequest.setAccessType(accessToken.getAccessType());

            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("authToken", accessToken.getAccessToken());

            request.setAttribute("result", "1");
            return SdkOauthResult.success(resultMap);
        } catch (Exception e) {
            log.error(">>>>>>>>非预期错误", e);
            return SdkOauthResult.failed(ErrorCodeEnum.非预期错误.code, ErrorCodeEnum.非预期错误.name());
        } finally {
            if (StringUtils.isNotBlank(authCode)) {
                String redisKey = "sdkserver_" + appCode + authCode;
                redisUtil.del(redisKey);
            }
        }
    }

    /**
     * 验证宝玩用户与密码
     * @param _userInfo
     * @return
     */
    private SdkOauthResult checkUserInfo(UserInfo _userInfo) {
        // 校验用户名密码
        UserInfo qbUserInfo = null;
        //根据登录名获取钱宝username
        qbUserInfo = thridRequestService.qurUameNameByAccount(_userInfo.getLoginName());
        if (qbUserInfo == null || StringUtils.isBlank(qbUserInfo.getUsername()))
            return SdkOauthResult.failed(ErrorCodeEnum.无效的用户名.code, ErrorCodeEnum.无效的用户名.name());
        //根据钱宝username获取钱宝userid
        String qbaoUserId = thridRequestService.queryQBUserName(qbUserInfo.getUsername());
        if (StringUtils.isBlank(qbaoUserId))
            return SdkOauthResult.failed(ErrorCodeEnum.无效的用户名.code, ErrorCodeEnum.无效的用户名.name());
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        String signPwd = md5PasswordEncoder.encodePassword(_userInfo.getPassword(), qbUserInfo.getUsername()).toLowerCase();
        //校验钱宝用户密码
        JSONObject jsonObject = thridRequestService.checkQbaoAccount(qbaoUserId, signPwd);
        if (jsonObject == null || !jsonObject.getBoolean("data")) {
            return SdkOauthResult.failed("用户名或密码不正确");
        }
        qbUserInfo = thridRequestService.loadUserInfo(Long.valueOf(qbaoUserId));
        return qbUserInfo == null ? SdkOauthResult.failed("没有该用户信息") : SdkOauthResult.success(qbUserInfo);
    }

    /**
     * 根据token获取登录授权的用户信息
     *
     * @param key       应用客户端生成的唯一标识
     * @param appCode   应用编码
     * @param authToken accesstoken
     * @param request
     * @return 登录授权的用户信息
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "getUserInfoByToken")
    @ResponseBody
    public SdkOauthResult getUserInfo(String key, String appCode, String authToken, HttpServletRequest request) {
//		log.info(">>>>>>>>sdk获取用户信息请求:" + JSON.toJSONString(request.getParameterMap()));

        AuthorizationRequest authorizationRequest = genrAuthorizationRequest(appCode, authToken, null, request);
        OpenMerchantEntity clientDetails = null;
        try {
            SdkOauthResult result = checkClient(appCode);
            if (!result.isSuccess()) {
                return result;
            } else {
                clientDetails = (OpenMerchantEntity) result.getData();
            }

            if (StringUtils.isBlank(authToken) || StringUtils.isBlank(key)) {
//				log.error(">>>>>>>>>有必填参数为空");
                return SdkOauthResult.failed(ErrorCodeEnum.有必填参数为空.code, ErrorCodeEnum.有必填参数为空.name());
            }

            if (key.length() > 32) {
//				log.error(">>>>>>>>>key的长度超过32位");
                return SdkOauthResult.failed(ErrorCodeEnum.key的长度超过32位.code, ErrorCodeEnum.key的长度超过32位.name());
            }

            // 根据token获取用户信息
            Map<String, Object> resultMap = new HashMap<String, Object>();
            result = sdkAuthorizationService.getUserInfoByToken(authorizationRequest);
            if (!result.isSuccess()) {
                return result;
            } else {
                resultMap = (Map<String, Object>) result.getData();
            }

            if (StringUtils.isNotBlank(clientDetails.getLoginRedirectUrl())) {
                loginResultNotifyService.startNotify(Long.valueOf(resultMap.get("userId").toString()), key, appCode, clientDetails.getLoginRedirectUrl());
            }
            // 判断是否是白名单游戏,如果是白名单游戏,客户端就返回username给游戏
            if (authorizationAccessTokenService.checkWhiteAppCode(appCode)) {
                resultMap.put("isWhite", "1");
            } else {
                resultMap.put("isWhite", "0");
            }
            request.setAttribute("result", "1");
            return SdkOauthResult.success(resultMap);

        } catch (Exception e) {
            log.error(">>>>>>>>>非预期错误", e);
            return SdkOauthResult.failed(ErrorCodeEnum.非预期错误.code, ErrorCodeEnum.非预期错误.name());
        }

    }

    /**
     * 生成授权请求对象
     *
     * @param appCode     应用app编码
     * @param accessToken access token
     * @param accessType  接入类型
     * @param request
     * @return
     */
    private AuthorizationRequest genrAuthorizationRequest(String appCode, String accessToken, String accessType, HttpServletRequest request) {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setClientId(appCode);
        authorizationRequest.setTokenId(accessToken);
        authorizationRequest.setAccessType(StringUtils.isNotBlank(accessType) ? Integer.valueOf(accessType) : AccessTypeEnum.WAP.f);
        request.setAttribute("authorizationRequest", authorizationRequest);
        return authorizationRequest;
    }

    /**
     * 校验客户端应用信息
     *
     * @param appCode 应用编码
     * @return
     */
    private SdkOauthResult checkClient(String appCode) {
        if (StringUtils.isBlank(appCode)) {
//			log.error(">>>>>>>>请求参数appCode为空");
            return SdkOauthResult.failed(ErrorCodeEnum.appCode为空.code, ErrorCodeEnum.appCode为空.name());
        }
        OpenMerchantEntity clientDetails = redisService.getMerchantInfo(appCode);
        if (clientDetails == null) {
//			log.error(">>>>>>>>>>>无效的appCode");
            return SdkOauthResult.failed(ErrorCodeEnum.无效的appCode.code, ErrorCodeEnum.无效的appCode.name());
        }

        return SdkOauthResult.success(clientDetails);
    }

    /**
     * 解除登录锁定
     *
     * @param loginName 登录用户名
     */
    @RequestMapping(value = "relieveLoginLock")
    @ResponseBody
    public AjaxResult relieveUserLoginLock(String loginName, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info(">>>>登录解锁请求,登录用户名：{}" + loginName);
            String ip = IPUtil.getIpAddr(request);

            return sdkAuthorizationService.relieveUserLoginLock(ip, loginName);
        } catch (Exception e) {
            log.error("登录解锁失败");
            return AjaxResult.failed("登录解锁失败");
        }
    }
}
