package com.qbao.sdk.server.service.statistic;

import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.UserKeepRate;

import java.util.Date;
import java.util.List;

/**
 * Created by chenwei
 */
public interface UserKeepRateService {

    /**
     * 获取keepDate注册用户在today登录的用户总数
     * @param appCode
     * @param keepDate 注册日期
     * @param today 用户登录日期
     * @return
     */
    int countKeepUserNum(String appCode, String keepDate, String today);

    List<OpenMerchantEntity> getAllApp();

    void insert(UserKeepRate userKeepRate, Date date);

    UserKeepRate selectRateByAppAndDate(String keepDate, String appCode);

    void updateUserKeepRate(UserKeepRate userKeepRate, int i);

    /**
     * 当天新增用户数
     * @param keepDate 当天
     * @param appCode 用户编码
     * @return
     */
    int countAddUserNum(String keepDate, String appCode);
    
    void delInaccurateData(String startDate, String endDate);
}
