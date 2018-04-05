package com.qbao.sdk.server.service.statistic;

import java.util.List;
import java.util.Map;

import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkDaySumEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkStatisticOverviewEntity;
import com.qbao.sdk.server.request.PageDto;
import com.qbao.sdk.server.request.statistic.SopOverviewRequest;
import com.qbao.sdk.server.response.statistic.SopUserOutputResponse;

/**
 * 流水查询相关  统计概况、用户产出
 * @author xuefeihu
 *
 */
public interface SdkTransStatisticService { 

	/**
	 * SOP 统计概况
	 * @param sopOverviewRequest
	 * @return
	 */
	List<SdkStatisticOverviewEntity> overview(SopOverviewRequest sopOverviewRequest );
	
	/**
	 * SOP 用户产出
	 * @param appCode
	 * @return
	 */
	SopUserOutputResponse output(String appCode);

	/**
	 * 查询应用宝币和宝券流水
	 * 查询某天的提供date（格式yyyyMMdd)，month要求为null；查询某月的提供month（格式yyyyMM)，date要求为null
	 * 返回值Map中宝券流水key为bqFlow，宝币流水key为qbbFlow
	 * @param appCode
	 * @param date
	 * @param month
	 * @return
	 */
	Map<String, Object> queryAppTrans(String appCode, String date, String month);
	
	AppDownloadStatisticInfo queryUserPayData(String appCode, String date);
	
	List<AppDownloadStatisticInfo> queryUserPayRangeDate(String appCode, String fromDate, String toDate);
	
	
}
