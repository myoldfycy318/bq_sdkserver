package com.qbao.sdk.server.metadata.dao.mapper.statistic;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.TurntableRuleEntity;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.RegisterUser;

@Repository
public interface UserStatisticMapper extends IBaseMapperDao<TurntableRuleEntity, Long>{
	
	/**
	 * 查询新增的用户数据写入到注册用户表中
	 * @param monthStr
	 * @param dateStr
	 */
	void insertRegisteUser(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr, @Param("endDateStr")String endDateStr,
			@Param("tableName")String tableName, @Param("postfix")int tableNameIndex);
	
	/**
	 * 写入用户统计数据（新增用户数、登陆用户数、总用户数等）
	 * @param dayStatisticInfo
	 */
	void saveAppUserDayStatistic(@Param("dayStatisticInfo")AppUserDayStatisticInfo dayStatisticInfo);
	
	void saveAppUserMonthStatistic(@Param("dayStatisticInfo")AppUserDayStatisticInfo dayStatisticInfo);
	
	/**
	 * 按日统计应用的新增用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countAppDayRegisteUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr,
			@Param("tableName")String tableName);
	
	/**
	 * 按日统计商家下的所有应用的新增用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countMerchantDayRegisteUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr,
			@Param("tableName")String tableName);

	/**
	 * 统计某天的新增用户，一次只能查询10张表sdk_register_users00-sdk_register_users09
	 * @param dateStr
	 * @param tableNameLine 
	 * @return
	 */
	List<RegisterUser> statisticRegisterUsers(@Param("dateStr")String dateStr, @Param("tableNameLine")int tableNameLine);
	
	
	/**
	 * 按日统计应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countAppDayLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	/**
	 * 按日统计商家下的所有应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countMerchantDayLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	/**
	 * 按日运营统计所有商家下应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	AppUserDayStatisticInfo countAllDayLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);

	/**
	 * 查询应用用户统计数据
	 * 若不提供商户编码，查询全部数据，需要校验是运营角色。一般商户必须要提供商户编码
	 * 若提供了商户编码，没有应用编码，查询商户下的全部应用。若提供了应用编码，就查询这款应用的用户统计数据
	 * 
	 * 按月查询，需要提供monthStr（格式yyyy-MM)，日期必须为null
	 * 按日查询，需要提供dateStr（格式yyyy-MM-dd)，月份字符串为null
	 * @param merchantCode
	 * @param appCode
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
//	AppUserDayStatisticInfo queryUserStatisticInfo(@Param("merchantCode")String merchantCode, 
//			@Param("appCode")String appCode, @Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	List<AppUserDayStatisticInfo> queryRangeUserStatisticInfo(@Param("merchantCode")String merchantCode, 
			@Param("appCode")String appCode, @Param("fromDate")String fromDate, @Param("toDate")String toDate);
	
	/**
	 * 按月统计应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countAppMonthLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	/**
	 * 按月统计商家下的所有应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	List<AppUserDayStatisticInfo> countMerchantMonthLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	/**
	 * 按月运营统计所有商家下应用的登陆用户数
	 * @param monthStr
	 * @param dateStr
	 * @return
	 */
	AppUserDayStatisticInfo countAllMonthLoginUsers(@Param("monthStr")String monthStr, @Param("dateStr")String dateStr);
	
	void insertAppDownloadStatisticInfo(@Param("info")AppDownloadStatisticInfo info);
	
	/**
	 * 查询单个应用的下载统计数据和付费用户数、付费次数（统计的是所有道具）
	 * @param appCode
	 * @param dateCol
	 * @return
	 */
	List<AppDownloadStatisticInfo> queryAppDownloadStatisticInfo(@Param("appCode")String appCode,
			@Param("merchantCode")String merchantCode, @Param("dateCol")String dateCol, @Param("isMonth")Boolean isMonth);
	
	AppDownloadStatisticInfo queryDayAppDownloadStatistic(@Param("appCode")String appCode,
			@Param("date")String date);
	
	List<AppDownloadStatisticInfo> queryDayRangeAppDownloadStatistic(@Param("appCode")String appCode,
			@Param("fromDate")String fromDate, @Param("toDate")String toDate);
	
	AppUserDayStatisticInfo getRecentStatisticInfo(@Param("merchantCode")String merchantCode, 
			@Param("appCode")String appCode, @Param("dateStr")String dateStr);
	
	AppUserDayStatisticInfo queryMonthUserStatisticInfo(@Param("merchantCode")String merchantCode, 
			@Param("appCode")String appCode, @Param("monthStr")String monthStr);
	
	/**
	 * 判断用户是否为首次登陆，需要执行100次，遍历所有表。
	 * 返回值>0说明过去已登陆过
	 * @param userId
	 * @param tableName
	 * @param date
	 * @return
	 */
	int checkUserFirstLogin(@Param("userId")long userId, @Param("tableName")String tableName, @Param("date")String date);
}
