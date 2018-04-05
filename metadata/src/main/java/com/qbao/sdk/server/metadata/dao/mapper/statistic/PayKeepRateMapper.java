package com.qbao.sdk.server.metadata.dao.mapper.statistic;

import com.qbao.sdk.server.bo.PayKeepRateBo;
import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.statistic.PayKeepRate;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayKeepRateMapper extends IBaseMapperDao<PayKeepRate, Long> {


    Integer countKeepUserNum(@Param("appCode") String appCode, @Param("keepDate")String keepDate,
                             @Param("yesterday") String yesterday,
                             @Param("oauthZoom") String oauthZoom, @Param("payZoom") String
                                     payZoom);

    void insert(@Param("record") PayKeepRate payKeepRate, @Param("zoom") String zoom);

    PayKeepRate selectByAppCodeAndDate(@Param("keepDate") String keepDate, @Param("appCode") String
            appCode, @Param("zoom") String zoom);

    void updatePayKeepRate(@Param("record") PayKeepRate payKeepRate, @Param("zoom") String zoom,
                           @Param("i") int i);

    Integer countPayUserNumber(@Param("keepDate") String date, @Param("zoom") String zoom, @Param
            ("appCode") String appCode);

    List<PayKeepRateBo> getPayKeepRateByAppCode(@Param("appCode") String appCode, @Param("start")
    String start, @Param("end") String end);


    void del(@Param("startDate") String startDate, @Param("endDate") String endDate);
}