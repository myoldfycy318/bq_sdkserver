package com.qbao.sdk.server.metadata.dao.mapper.statistic;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkDaySumEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkMonthSumEntity;

@Repository
public interface DaySumMapper extends IBaseMapperDao<SdkMonthSumEntity, Long> {
	
	/**
	 * 根据APPCode查询计费点流水
	 * @param appCode
	 * @param date
	 * @return
	 */
	List<SdkDaySumEntity> queryDayTransByAppcode(@Param("appCode")String appCode, @Param("merchantCode")String merchantCode, @Param("date")String date);
	
	/**
	 * 根据APPCode查询流水，计费点求和
	 * @param appCode
	 * @param date
	 * @return
	 */
	List<SdkDaySumEntity> queryDayTransSumByAppcode(@Param("appCode")String appCode, @Param("merchantCode")String merchantCode, @Param("date")String date);
	
	List<SdkDaySumEntity> queryDayRangeTransSumByAppcode(@Param("appCode")String appCode, @Param("fromDate")String fromDate, @Param("toDate")String toDate);
	/**
	 * 统计月结数据
	 * @param month
	 * @param appCode
	 * @param chargePointCode
	 * @return
	 */
	SdkMonthSumEntity queryMonthTrans(@Param("month")String month, 
			@Param("appCode") String appCode, @Param("chargePointCode") String chargePointCode);
	
    /**
     * 通过id物理删除sdk_day_sum的数据.
     */
    int deleteById(Integer daySumId);

    /**
     * 向表sdk_day_sum中插入数据.
     */
    int insert(SdkDaySumEntity record);

    /**
     * 通过id查询表sdk_day_sum.
     */
    SdkDaySumEntity selectById(Integer daySumId);

    /**
     * 通过id修改表sdk_day_sum.
     */
    int updateById(SdkDaySumEntity record);
    
    /**
     * 日流水统计所有道具的付费用户数和付费次数
     * @return
     */
    AppDownloadStatisticInfo statisticDayAllPayUser(@Param("suffix")String month, @Param("transDate")String transDate,
			@Param("appCode") String appCode);
   
}