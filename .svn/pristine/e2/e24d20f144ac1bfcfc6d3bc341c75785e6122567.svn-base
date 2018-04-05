package com.qbao.sdk.server.service.statistic;

import java.util.List;

import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;

public interface UserStatisticService {
	
	public static final String YUNYING_MERCHANT_CODE = "M0000000";
	
	public static final String ALL_APP_CODE ="A0000000";
	/**
	 * 将新注册的用户（首次登陆应用）保存到表中
	 * 每个应用下一个user_id对应一条记录
	 */
	void insertRegisterUser();
	
	/**
	 * 统计用户某天的新增用户数、登陆用户数、总用户数
	 * 每个应用的一天对应一条记录
	 */
	void statisticUserSum();
	
	void onlineStatisticInitial();
	
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
	AppUserDayStatisticInfo queryUserStatisticInfo(String merchantCode, 
			String appCode, String monthStr, String dateStr);
	
	List<AppUserDayStatisticInfo> queryRangeUserStatisticInfo(String merchantCode, 
			String appCode, String fromDate, String toDate);
	
	public OpenMerchantEntity getMerchantInfo(String appCode);
}
