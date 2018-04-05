package com.qbao.sdk.server.service.login;

import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.bo.pay.UserInfo;

public interface ThridRequestService {

    /**
     * 根据用户名获取钱宝用户信息
     * @param userId
     * @return
     */
    UserInfo loadUserInfo(long userId);

    /**
     * 校验钱宝用户账户
     * @param qbUserId
     * @param password
     * @return
     */
    JSONObject checkQbaoAccount(String qbUserId, String password);


    String queryQBUserName(String userName);

    /**
     * 根据账户获取钱宝username
     *
     * @param account
     * @return
     */
    UserInfo qurUameNameByAccount(String account);

}
