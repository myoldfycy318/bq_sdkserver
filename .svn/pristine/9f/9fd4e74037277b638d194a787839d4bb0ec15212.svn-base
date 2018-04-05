package com.qbao.sdk.server.metadata.dao.mapper.statistic;


import com.qbao.sdk.server.bo.UserKeepRateBo;
import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.statistic.UserKeepRate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public interface UserKeepRateMapper extends IBaseMapperDao<UserKeepRate, Long> {
    int insert(UserKeepRate record);

    int insertUserKeepRate(@Param("record") UserKeepRate record, @Param("zoom") String zoom);

    Integer countKeepUserNum(Map<String, Object> param);

    UserKeepRate selectRateByAppAndDate(@Param("keepDate") String keepDate, @Param("appCode")
    String appCode, @Param("zoom") String zoom);

    void updateUserKeepRate(@Param("record") UserKeepRate userKeepRate, @Param("i") int i,
                            @Param("zoom") String zoom);

    Integer countAddUserNum(@Param("keepDate") String keepDate, @Param("appCode") String appCode);

    List<UserKeepRateBo> getUserKeepRateByApp(@Param("appCode") String appCode, @Param("start") String
            start, @Param("end") String end);

    String getUserKeepRateNextDay(@Param("appCode") String appCode, @Param("date") String date);

    String getUsrKeepRtMonthSum(@Param("appCode") String appCode, @Param("start") Date
            start, @Param("end") Date end);
    
    void del(@Param("startDate") String startDate, @Param("endDate") String endDate);
}