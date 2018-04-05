package com.qbao.sdk.server.service.impl.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.qbao.sdk.server.metadata.dao.mapper.pay.ChargePointMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayTransMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.DaySumMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.MonthSumMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserStatisticMapper;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkDaySumEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkMonthSumEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkStatisticOverviewEntity;
import com.qbao.sdk.server.request.statistic.SopOverviewRequest;
import com.qbao.sdk.server.response.statistic.SopUserOutputResponse;
import com.qbao.sdk.server.service.statistic.SdkTransStatisticService;


/**
 * 流水查询相关 统计概况、用户产出
 * 
 * @author xuefeihu
 *
 */
@Service("sdkTransStatisticService")
public class SdkTransStatisticServiceImpl implements SdkTransStatisticService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SdkTransStatisticServiceImpl.class);

	@Resource
	private MerchantInfoMapper merchantInfoMapper;

	@Resource
	private ChargePointMapper chargePointMapper;

	@Resource
	private PayTransMapper payTransMapper;

	@Resource
	private DaySumMapper daySumMapper;

	@Resource
	private MonthSumMapper monthSumMapper;
	
	@Resource
	private UserStatisticMapper userStatisticMapper;

	@Override
	public List<SdkStatisticOverviewEntity> overview(SopOverviewRequest request) {
		
		
		String appCode = request.getAppCode();
		String merchantCode = request.getMerchantCode();
		List<SdkStatisticOverviewEntity> dataList = null;
		List<AppDownloadStatisticInfo> infos = null;
		switch (request.getType()) {
			case 0: // 日流水
			{
				String date = request.getDate();
				List<SdkDaySumEntity> trans = null;
				switch (request.getChildType()) {
					case 0:  // 有道具代码和道具名称
					{
						trans = daySumMapper.queryDayTransByAppcode(appCode, merchantCode, date);
						
						dataList = handleDayStatisticData(trans);
						break;
					}
					case 1: // 没有道具代码和道具名称
					{
						// 合并宝券流水和宝币流水
						trans = daySumMapper.queryDayTransSumByAppcode(appCode, merchantCode, date);
						// 需要覆盖付费用户数
						infos = userStatisticMapper.queryAppDownloadStatisticInfo(appCode, merchantCode, date, null);
						
						dataList = handleDayStatisticData(trans, infos);
						break;
					}
					case 2: // 下载用户数和下载次数
					{
						// 合并宝券流水和宝币流水
//						trans = daySumMapper.queryDayTransSumByAppcode(appCode, merchantCode, date);
						infos = userStatisticMapper.queryAppDownloadStatisticInfo(appCode, merchantCode, date, null);
						dataList = handleDayStatisticData(trans, infos);
						break;
					}
				
				}
				break;
			}
			case 1: // 月流水
			{
				String month=request.getMonth();
				List<SdkMonthSumEntity> trans = null;
				switch (request.getChildType()) {
					case 0:  // 有道具代码和道具名称
					{
						trans = monthSumMapper.queryMonthTransByAppcode(appCode, merchantCode, month);
						
						dataList = handleMonthStatisticData(trans);
						break;
					}
					case 1: // 没有道具代码和道具名称
					{
						// 按应用合并道具宝券流水和宝币流水
						trans = monthSumMapper.queryMonthTransSumByAppcode(appCode, merchantCode, month);
						// 需要覆盖付费用户数
						infos = userStatisticMapper.queryAppDownloadStatisticInfo(appCode, merchantCode, month, Boolean.TRUE);
						dataList = handleMonthStatisticData(trans, infos);
						break;
					}
					case 2: // 下载用户数和下载次数
					{
						// 按应用合并道具宝券流水和宝币流水
//						trans = monthSumMapper.queryMonthTransSumByAppcode(appCode, merchantCode, month);
						infos = userStatisticMapper.queryAppDownloadStatisticInfo(appCode, merchantCode, month, Boolean.TRUE);
						dataList = handleMonthStatisticData(trans, infos);
						break;
					}
				}
				break;
	
			}
		}
		
		return dataList;
	}

	private static List<SdkStatisticOverviewEntity> handleDayStatisticData(List<SdkDaySumEntity> trans,
			List<AppDownloadStatisticInfo> infos) {
		List<SdkStatisticOverviewEntity> views = new ArrayList<SdkStatisticOverviewEntity>();
		if (CollectionUtils.isEmpty(infos)) {
			LOGGER.info("没有查询到下载统计数据");
			return views;
		}
		SdkStatisticOverviewEntity view;
		
		for (int i = 0; i< infos.size(); i++) {
			view = new SdkStatisticOverviewEntity();
			AppDownloadStatisticInfo info = infos.get(i);
			convertDownloadSumToStatisticOverview(view, info);
			
			views.add(view);
		}
		return views;
	}
	
	private static List<SdkStatisticOverviewEntity> handleMonthStatisticData(List<SdkMonthSumEntity> trans,
			List<AppDownloadStatisticInfo> infos) {
		List<SdkStatisticOverviewEntity> views = new ArrayList<SdkStatisticOverviewEntity>();
		if (CollectionUtils.isEmpty(infos)) {
			LOGGER.info("没有查询到下载统计数据");
			return views;
		}
		SdkStatisticOverviewEntity view;
		
		for (int i = 0; i< infos.size(); i++) {
			view = new SdkStatisticOverviewEntity();
			
			AppDownloadStatisticInfo info = infos.get(i);
			convertDownloadSumToStatisticOverview(view, info);
			
			views.add(view);
		}
		return views;
	}
	
	private static List<SdkStatisticOverviewEntity> handleDayStatisticData(List<SdkDaySumEntity> trans) {
		if (CollectionUtils.isEmpty(trans)) {
			LOGGER.error("道具支出流水统计数据没有查询到");
			throw new RuntimeException("没有查询到道具支出流水统计数据");
		}
		SdkStatisticOverviewEntity view;
		List<SdkStatisticOverviewEntity> views = new ArrayList<SdkStatisticOverviewEntity>();
		for (int i = 0; i< trans.size(); i++) {
			view = new SdkStatisticOverviewEntity();
			
			SdkDaySumEntity day = trans.get(i);
			convertDaySumToStatisticOverview(view, day);
			
			views.add(view);
		}
		return views;
	}

	private static List<SdkStatisticOverviewEntity> handleMonthStatisticData(List<SdkMonthSumEntity> trans) {
		if (CollectionUtils.isEmpty(trans)) {
			LOGGER.error("没有查询到下载统计数据");
			throw new RuntimeException("没有查询到下载统计数据");
		}
		SdkStatisticOverviewEntity view;
		List<SdkStatisticOverviewEntity> views = new ArrayList<SdkStatisticOverviewEntity>();
		for (int i = 0; i< trans.size(); i++) {
			view = new SdkStatisticOverviewEntity();
			
			SdkMonthSumEntity month = trans.get(i);
			convertMonthSumToStatisticOverview(view, month);
			
			views.add(view);
		}
		return views;
	}
	
	private static void convertDaySumToStatisticOverview(
			SdkStatisticOverviewEntity view, SdkDaySumEntity day) {
		view.setMerchantCode(day.getMerchantCode());
		view.setMerchantName(day.getMerchantName());
		view.setAppCode(day.getAppCode());
		view.setAppName(day.getAppName());
		//view.setAppType(day.getAppSource());
		view.setChargingPointCode(day.getChargingPointCode());
		view.setChargingPointName(day.getChargingPointName());
		// 宝币流水
		view.setAccountAmount(day.getBbTotalAmount().longValue());
		// 宝券流水
		view.setBqAccountAmount(day.getBqTotaAmount().longValue());
		
		// 宝币折现、宝券折现和总折现
		view.setBbTotalAmount(day.getQbbCash());
		view.setBqTotaAmount(day.getBqCash());
		view.setTotalAmount(day.getCashSum());
		
		// 付费用户数和付费次数
		view.setPayCount(day.getPayCount());
		view.setPayUserCount(day.getPayUserCount());
	}

	private static void convertMonthSumToStatisticOverview(
			SdkStatisticOverviewEntity view, SdkMonthSumEntity month) {
		view.setMerchantCode(month.getMerchantCode());
		view.setMerchantName(month.getMerchantName());
		view.setAppCode(month.getAppCode());
		view.setAppName(month.getAppName());
		//view.setAppType(day.getAppSource());
		view.setChargingPointCode(month.getChargingPointCode());
		view.setChargingPointName(month.getChargingPointName());
		// 宝币流水
		view.setAccountAmount(month.getBbTotalAmount().longValue());
		// 宝券流水
		view.setBqAccountAmount(month.getBqTotaAmount().longValue());
		
		// 宝币折现、宝券折现和总折现
		view.setBbTotalAmount(month.getQbbCash());
		view.setBqTotaAmount(month.getBqCash());
		view.setTotalAmount(month.getCashSum());
		
		// 付费用户数和付费次数
		view.setPayCount(month.getPayCount());
		view.setPayUserCount(month.getPayUserCount());
	}

	private static void convertDownloadSumToStatisticOverview(
			SdkStatisticOverviewEntity view, AppDownloadStatisticInfo info) {
		view.setMerchantCode(info.getMerchantCode());
		view.setMerchantName(info.getMerchantName());
		view.setAppCode(info.getAppCode());
		view.setAppName(info.getAppName());
		view.setPayUserCount(info.getPayUserCount());
		view.setPayCount(info.getPayCount());
		view.setDownCount(info.getDownTimes());
		view.setDownUserCount(info.getUserCounts());
		
		// 宝币和宝券流水、折现以下载统计表中数据为准
		// 宝币流水
		view.setAccountAmount(info.getQbbFlow());
		// 宝券流水
		view.setBqAccountAmount(info.getBqFlow());
		
		// 宝币折现、宝券折现和总折现
		view.setBbTotalAmount(info.getQbbCash());
		view.setBqTotaAmount(info.getBqCash());
		view.setTotalAmount(info.getCashSum());
	}

	@Override
	public SopUserOutputResponse output(String appCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> queryAppTrans(String appCode, String date,
			String month) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppDownloadStatisticInfo queryUserPayData(String appCode, String date) {
		AppDownloadStatisticInfo download = userStatisticMapper.queryDayAppDownloadStatistic(appCode, date);
		return download;
	}

	@Override
	public List<AppDownloadStatisticInfo> queryUserPayRangeDate(String appCode,
			String fromDate, String toDate) {
		List<AppDownloadStatisticInfo> downloadList = userStatisticMapper.queryDayRangeAppDownloadStatistic(appCode, fromDate, toDate);
		return downloadList;
	}

}
