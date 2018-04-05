package com.qbao.sdk.server.metadata.dao.mapper.statistic;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkMonthSumEntity;

@Repository
public interface MonthSumMapper extends IBaseMapperDao<SdkMonthSumEntity, Long> {
	/**
	 * 根据APPCode查询计费点流水
	 * @param appCode
	 * @param date
	 * @return
	 */
	List<SdkMonthSumEntity> queryDayTransByAppcode(@Param("appCode")String appCode, @Param("month")String month);
    /**
     * 通过id物理删除sdk_month_sum的数据.
     */
    int deleteById(Integer monthSumId);

    /**
     * 向表sdk_month_sum中插入数据.
     */
    int insert(SdkMonthSumEntity record);

    /**
     * 通过id查询表sdk_month_sum.
     */
    SdkMonthSumEntity selectById(Integer monthSumId);

    /**
     * 通过id修改表sdk_month_sum.
     */
    int updateById(SdkMonthSumEntity record);
    
    int queryMonthPayUserCount(@Param("appCode")String appCode, @Param("month")String month, @Param("chargePointCode") String chargePointCode);
    
    /**
     * 月流水统计所有道具的付费用户数和付费次数
     * @return
     */
    AppDownloadStatisticInfo statisticMonthAllPayUser(@Param("suffix")String month, 
			@Param("appCode") String appCode);
    
	/**
	 * 根据APPCode查询计费点流水
	 * @param appCode
	 * @param date
	 * @return
	 */
	List<SdkMonthSumEntity> queryMonthTransByAppcode(@Param("appCode")String appCode, @Param("merchantCode")String merchantCode, @Param("month")String month);
	
	/**
	 * 根据APPCode查询流水，宝券流水和宝币流水求和
	 * @param appCode
	 * @param date
	 * @return
	 */
	List<SdkMonthSumEntity> queryMonthTransSumByAppcode(@Param("appCode")String appCode, @Param("merchantCode")String merchantCode, @Param("month")String month);
}