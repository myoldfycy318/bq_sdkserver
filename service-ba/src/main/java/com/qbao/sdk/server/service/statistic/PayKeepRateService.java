package com.qbao.sdk.server.service.statistic;

import com.qbao.sdk.server.metadata.entity.statistic.PayKeepRate;

import java.util.Date;

/**
 * Created by chenwei on 2016/1/5
 */
public interface PayKeepRateService {
    void insert(PayKeepRate payKeepRate, Date yesterday);

    /**
     * 查询keepDate付费用户在yesterday登录的总数
     * @param appCode
     * @param keepDate 统计日期 付费日期
     * @param yesterday 统计日期N天后 登陆日期
     * @return
     */
    int countKeepUserNum(String appCode, String keepDate, String yesterday);

    PayKeepRate selectByAppCodeAndDate(String keepDate, String appCode);

    void updatePayKeepRate(PayKeepRate payKeepRate, int i);

    int countPayUserNumber(String date, String appCode);
    
    void delInaccurateData(String startDate, String endDate);
}
