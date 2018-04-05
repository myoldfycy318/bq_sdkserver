package com.qbao.sdk.server.service.statistic;

import com.qbao.sdk.server.bo.PayKeepRateBo;
import com.qbao.sdk.server.bo.UserKeepRateBo;

import java.util.Date;
import java.util.List;

/**
 * Created by chenwei on 2015/12/7
 */
public interface KeepRateService {


    /**
     * 查询app 开始时间到结束时间内的注册用户留存率
     * @param appCode
     * @param start
     * @param end
     */
    List<UserKeepRateBo> getUserKeepRate(String appCode, String start, String end);

    /**
     * 查询app 开始到结束时间内付费用户留存率
     * @param appCode
     * @param start
     * @param end
     * @return
     */
    List<PayKeepRateBo> getPayKeepRate(String appCode, String start, String end);

    /**
     * 获取次日留存率
     * @param appCode
     * @param date
     * @return
     */
    String getUserKeepRateNextDay(String appCode, String date);

    /**
     * 查询当月用户留存率总数
     * @param appCode
     * @param start
     * @param end
     * @return
     */
    double getUsrKeepRtMonthSum(String appCode, Date start, Date end);
}
