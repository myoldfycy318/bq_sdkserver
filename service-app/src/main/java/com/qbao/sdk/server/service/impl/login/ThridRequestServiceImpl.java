package com.qbao.sdk.server.service.impl.login;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.service.login.ThridRequestService;
import com.qbao.sdk.server.util.ApiConnector;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户中心接口
 */
@Service("thridRequestService")
public class ThridRequestServiceImpl implements ThridRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThridRequestServiceImpl.class);


    @Value("${qbao.qur.uame.by.acount.url}")
    private String QUR_USERNAME_URL;

    @Value("${qbao.sdk.server.appid}")
    private String QBAO_APPID;

    @Value("${qbao.qur.userid.url}")
    private String QUR_USERID_URL;

    @Value("${qbao.qur.user.detail.url}")
    private String QUR_USER_DETAIL_URL;

    @Value("${qbao.user.check.password.url}")
    private String QBAO_CHECK_PW_URL;

    /**
     * 校验钱宝用户账户
     *
     * @param qbUserId
     * @param password
     * @return
     */
    @Override
    public JSONObject checkQbaoAccount(String qbUserId, String password) {
        JSONObject jsonObject = null;
        String result = null;
        try {
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("appId",QBAO_APPID));
            paramsList.add(new BasicNameValuePair("userId", qbUserId));
            paramsList.add(new BasicNameValuePair("password", password));
            String url = QBAO_CHECK_PW_URL;
            result = ApiConnector.post(url, paramsList);
            LOGGER.info("查询用户cas信息，请求url:{}，请求参数：{},响应报文：{}", url, JSONObject.toJSONString(paramsList), result);
            if (StringUtils.isNotBlank(result) && (jsonObject = JSONObject.parseObject(result)) != null
                    && 1 == jsonObject.getInteger("code") && StringUtils.isNotBlank(jsonObject.getString("data"))) {
                return jsonObject;
            }
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>查询用户cas信息异常:", e);
        }
        return null;
    }

    /**
     * 根据userId查找用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfo loadUserInfo(long userId) {
        String url = QUR_USER_DETAIL_URL;
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        paramsList.add(new BasicNameValuePair("appId", QBAO_APPID));
        String response = ApiConnector.post(url, paramsList);
        LOGGER.info("查询钱宝用户信息,请求url:{},请求参数：{},响应报文：{}", url, JSONObject.toJSONString(paramsList), response);
        if (StringUtils.isBlank(response)) {
            return null;
        }
        JSONObject obj = JSON.parseObject(response);
        if (1 != obj.getIntValue("code") || obj == null) {
            return null;
        }
        return JSON.parseObject(obj.getString("data"), UserInfo.class);
    }

    /**
     * 根据userName获取钱宝用户userid
     *
     * @param userName
     * @return
     */
    @Override
    public String queryQBUserName(String userName) {
        JSONObject jsonObject = null;
        String url = QUR_USERID_URL;
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userName", userName));
        paramsList.add(new BasicNameValuePair("appId", QBAO_APPID));
        String response = ApiConnector.post(url, paramsList);
        LOGGER.info("根据userName获取钱宝用户userid,请求url:{},请求参数：{},响应报文：{}", url, JSONObject.toJSONString(paramsList), response);
        if (StringUtils.isNotBlank(response) && (jsonObject = JSONObject.parseObject(response)) != null
                && 1 == jsonObject.getInteger("code") && StringUtils.isNotBlank(jsonObject.getString("data"))) {
            return jsonObject.getString("data");
        }
        return null;
    }

    /**
     * 根据账户获取钱宝username
     *
     * @param account
     * @return
     */
    @Override
    public UserInfo qurUameNameByAccount(String account) {
        JSONObject obj = null;
        String url = QUR_USERNAME_URL;
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("account", account));
        paramsList.add(new BasicNameValuePair("appId", QBAO_APPID));
        String response = ApiConnector.post(url, paramsList);
        LOGGER.info("根据账户获取钱宝username,请求url:{},请求参数：{},响应报文：{}", url, JSONObject.toJSONString(paramsList), response);
        if (StringUtils.isBlank(response) || (obj = JSON.parseObject(response)) == null
                || 1 != obj.getIntValue("code") || null == obj.getString("data")) {
            return null;
        }
        return JSON.parseObject(obj.getString("data"), UserInfo.class);
    }
}
