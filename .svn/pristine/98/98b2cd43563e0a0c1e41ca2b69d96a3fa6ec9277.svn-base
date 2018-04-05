package com.qbao.sdk.server.service.impl.statistic;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.qbao.sdk.server.constants.stastics.SettleTypeEnum;
import com.qbao.sdk.server.metadata.dao.mapper.pay.ChargePointMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayTransMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.DaySumMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.MonthSumMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserStatisticMapper;
import com.qbao.sdk.server.metadata.entity.pay.ChargePointEntity;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkDaySumEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkMonthSumEntity;
import com.qbao.sdk.server.service.statistic.TransProcessService;
import com.qbao.sdk.server.util.ApiConnector;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.util.MathUtils;
import com.qbao.sdk.server.util.PropertiesUtil;
import com.qbao.sdk.server.util.RedisUtil;

/**
 * 历史流水数据处理
 * @author xuefeihu
 *
 */
@Service("transProcessService")
public class TransProcessServiceImpl implements TransProcessService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransProcessServiceImpl.class);
	
	final BigDecimal zeroDecimal = new BigDecimal(0.0);
	
	//获取应用信息key前缀
	private static final String APPCODE_PREFIX = "sdkServer:appcode_";
	
	@Resource
	private MerchantInfoMapper merchantInfoMapper;
	
	@Resource
	private ChargePointMapper chargePointMapper;
	
	@Resource
	private PayTransMapper payTransMapper;
	
	@Resource
	private DaySumMapper sdkDaySumEntityMapper;
	
	@Resource
	private MonthSumMapper sdkMonthSumEntityMapper;
	
	@Resource
	private UserStatisticMapper userStatisticMapper;

	@Resource
	private PropertiesUtil payConfig;
	/**
	 * 开始月份
	 */
	public static final String START_MONTH = "201511";
	
	@Resource
	private RedisUtil redisUtil;

	private BigDecimal qbbRate;
	
	private BigDecimal bqRate;
	
	private static NumberFormat percentFormat = NumberFormat.getPercentInstance();

	static {
		percentFormat.setMaximumIntegerDigits(3);
		percentFormat.setMaximumFractionDigits(2);
	}
	
	private void initCashRate() {
		if (qbbRate == null) {
			qbbRate= new BigDecimal(payConfig.getInt("qbb.cash.rate"));
		}
		if (bqRate == null) {
			bqRate = new BigDecimal(payConfig.getInt("bq.cash.rate"));
		}
	}
	/**
	 * 处理每月月结流水（每月2号凌晨执行）
	 */
	@Override
	public void process2MonthSum() {
		initCashRate();
		//int currentInt = Integer.parseInt(DateUtils.getLastMonthyyyyMM());
		Calendar c =  Calendar.getInstance();
		// 仅每个月的1号会统计
		if (c.get(Calendar.DATE) > 1) {
			return;
		}
		c.add(Calendar.DATE, -1);
		int currentInt = Integer.parseInt(DateUtils.formatToyyyyMM(c.getTime()));
		//获取所有APP信息
		List<String> appCodes = merchantInfoMapper.queryAllAppCode();
		if(CollectionUtils.isEmpty(appCodes)) {
			LOGGER.error("查不到任何APP信息");
			return ;
		}
		for(String appCode : appCodes) {
			//LOGGER.info("开始统计应用{}的历史{}月结记录...", appCode, currentInt);
			try {
				processMonthSum(currentInt, appCode);
			} catch (Exception e) {
				LOGGER.error("统计月份：{}，APPCode：{}出错{}", currentInt, appCode, e);
			}
			//LOGGER.info("统计完成{}应用{}的历史月结记录", currentInt, appCode);
		}

	}
	
	/**
	 * 处理每日日切流水(每天凌晨执行)
	 */
	@Override
	public void process2DaySum() {
		initCashRate();
		int currentInt = Integer.parseInt(DateUtils.getYesterdayyyyMMdd());
		//获取所有APP信息
		List<String> appCodes = merchantInfoMapper.queryAllAppCode();
		if(CollectionUtils.isEmpty(appCodes)) {
			LOGGER.error("查不到任何APP信息");
			return ;
		}
		for(String appCode : appCodes) {
			//LOGGER.info("开始统计应用{}的历史{}日切记录...", appCode, currentInt);
			try {
				processDaySum(currentInt, appCode);
			} catch (Exception e) {
				LOGGER.error("统计日期：{}，APPCode：{}出错{}", currentInt, appCode, e);
			}
			//LOGGER.info("统计完成{}应用{}的历史日切记录", currentInt, appCode);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//以下接口只跑一次
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * 日切旧数据批量处理
	 */
	@Override
	public void processPastDaySum() {
		initCashRate();
		//获取所有APP信息
		List<String> appCodes = merchantInfoMapper.queryAllAppCode();
		if(CollectionUtils.isEmpty(appCodes)) {
			LOGGER.error("查不到任何APP信息");
			return ;
		}
		
		Calendar start = Calendar.getInstance();
		start.set(2015, 10, 1);//设置开始时间为20151101
		int startInt = Integer.parseInt(DateUtils.formatToyyyyMMdd(start.getTime()));
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, -2);//结束时间为前天
		int endInt = Integer.parseInt(DateUtils.formatToyyyyMMdd(end.getTime()));
		int currentInt = startInt;
		
		while( currentInt <= endInt ) {
			for(String appCode : appCodes) {
				//LOGGER.info("开始统计应用{}的历史{}日切记录...", appCode, currentInt);
				try {
					processDaySum(currentInt, appCode);
				} catch (Exception e) {
					LOGGER.error("统计日期：{}，APPCode：{}出错{}", currentInt, appCode, e);
					LOGGER.error("日流水统计出错", e);
				}
				//LOGGER.info("统计完成{}应用{}的历史日切记录", currentInt, appCode);
			}
			start.add(Calendar.DAY_OF_MONTH, 1);
			currentInt = Integer.parseInt(DateUtils.formatToyyyyMMdd(start.getTime()));
		}
	}

	/**
	 * 处理单个APP日切流水
	 * @param currentDate
	 * @param appCode
	 */
	private void processDaySum(int currentDate, String appCode) {
		//获取APPCode的计费点
		List<ChargePointEntity> chargePoints = chargePointMapper.queryPointByAppCode(appCode);
		if(CollectionUtils.isEmpty(chargePoints)) {
			LOGGER.error("appcode:{}查不到任何计费点信息", appCode);
			return ;
		}
		OpenMerchantEntity merchantInfo = getMerchantInfo(appCode);
		if (merchantInfo == null) {
			LOGGER.error("查询不到appCode: {}的应用信息", appCode);
			return;
		}
		int appSource = 0;
		for(ChargePointEntity chargePoint : chargePoints) {
			SdkDaySumEntity sdkDaySumEntity = payTransMapper.queryDaySumTrans(String.valueOf(currentDate).substring(0, 6), 
					currentDate+"", appCode, chargePoint.getChargePointCode());
			if (sdkDaySumEntity == null || sdkDaySumEntity.getAppCode() == null){ // select语句中count()没有记录，也new一个对象
				LOGGER.debug("没有查询到该计费点的流水， currentDate: {}, appCode: {}, chargePoint: {}", currentDate,  appCode, chargePoint.getChargePointCode());
				continue; // 没有计费点流水不统计
			} else {
				// 宝币折现、宝券折现和总折现计算
				double qbbCash = (sdkDaySumEntity.getBbTotalAmount() ==null ? zeroDecimal
						: sdkDaySumEntity.getBbTotalAmount()).divide(qbbRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sdkDaySumEntity.setQbbCash(qbbCash);
				
				double bqCash = (sdkDaySumEntity.getBqTotaAmount()== null ? zeroDecimal
						: sdkDaySumEntity.getBqTotaAmount()).divide(bqRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sdkDaySumEntity.setBqCash(bqCash);
				sdkDaySumEntity.setCashSum(new BigDecimal(qbbCash).add(new BigDecimal(bqCash)).doubleValue());
				
			}
			appSource = sdkDaySumEntity.getAppSource();
			// 当没有流水时查出来的都是null，因此一些字段做了覆盖（重复set）
			sdkDaySumEntity.setMerchantCode(merchantInfo.getMerchantCode());
			sdkDaySumEntity.setMerchantName(merchantInfo.getMerchantName());
			sdkDaySumEntity.setAppCode(merchantInfo.getAppCode());
			sdkDaySumEntity.setAppName(merchantInfo.getAppName());
			sdkDaySumEntity.setChargingPointCode(chargePoint.getChargePointCode());
			sdkDaySumEntity.setChargingPointName(chargePoint.getChargePointName());
			sdkDaySumEntity.setTradeDate(currentDate+"");
			sdkDaySumEntity.setSettleStatus(SettleTypeEnum.NOT_SETTLE.getType()+"");
			sdkDaySumEntity.setTradeMonth(String.valueOf(currentDate).substring(0, 6));
			
			// 付费用户数、付费次数统计（仅限道具的付费用户数，一个应用的付费用户数有重复数据，不是相加得到，付费次数可以相加)
			
			sdkDaySumEntityMapper.insert(sdkDaySumEntity);
		}
		
		// 统计下载用户数
		AppDownloadStatisticInfo info = queryAppDownloadData(appCode, String.valueOf(currentDate), null);
		if (info == null) {
			LOGGER.debug("查询不到应用下载数据，返回null");
			info = new AppDownloadStatisticInfo();
			
		}
		info.setDateCol(String.valueOf(currentDate));
		info.setMerchantCode(merchantInfo.getMerchantCode());
		info.setMerchantName(merchantInfo.getMerchantName());
		info.setAppCode(merchantInfo.getAppCode());
		info.setAppName(merchantInfo.getAppName());
		info.setAppSource(appSource);
		// 不选择道具代码和道具名称情况下统计付费用户数和付费次数
		AppDownloadStatisticInfo info2 = sdkDaySumEntityMapper.statisticDayAllPayUser(String.valueOf(currentDate).substring(0, 6),
				String.valueOf(currentDate), appCode);
		info.setPayUserCount(info2.getPayUserCount());
		info.setPayCount(info2.getPayCount());
		info.setQbbFlow(info2.getQbbFlow());
		info.setBqFlow(info2.getBqFlow());
		// 宝券、宝币折现计算
		double qbbCash =  new BigDecimal(info2.getQbbFlow()).divide(qbbRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		info.setQbbCash(qbbCash);
		
		double bqCash = new BigDecimal(info2.getBqFlow()).divide(bqRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		info.setBqCash(bqCash);
		double cashSum = new BigDecimal(qbbCash).add(new BigDecimal(bqCash)).doubleValue();
		info.setCashSum(cashSum);

		// 付费转化率，arpu, arppu
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateCol = null;
		try {
			Date d = sdf.parse(String.valueOf(currentDate));
			dateCol = DateFormatUtils.format(d, "yyyy-MM-dd");
		} catch (ParseException e) {
			LOGGER.error("解析日期出错", e);
		}
		AppUserDayStatisticInfo statisticInfo = userStatisticMapper.getRecentStatisticInfo(merchantInfo.getMerchantCode(), appCode, dateCol);
		if (statisticInfo == null) {
			LOGGER.debug("没有查询到用户统计数据, appCode: {}, dateStr: {}", appCode, dateCol);
			//throw new RuntimeException("没有查询到用户统计数据");
			statisticInfo = new AppUserDayStatisticInfo(); //没有用户统计数据（值全取0）
		} else {
			if (statisticInfo.getDate().compareTo(dateCol)<0){
				AppUserDayStatisticInfo tempApp = new AppUserDayStatisticInfo();
				tempApp.setDate(dateCol);
				tempApp.setUsersSum(statisticInfo.getUsersSum());
				statisticInfo = tempApp;
			}
		}
		double payRate =0.0, arpu = 0.0, arppu=0.0;
		if (statisticInfo.getLoginUsersSum() > 0){
			payRate = MathUtils.divideExact(info2.getPayUserCount(), statisticInfo.getLoginUsersSum());
			info.setPayRate(payRate); // 付费转化率=付费用户数/登陆用户数
		} else {
			info.setPayRate(0.0);
		}
		if (statisticInfo.getLoginUsersSum() > 0){
			arpu = MathUtils.divideExact(cashSum, statisticInfo.getLoginUsersSum());
			info.setArpu(arpu); // 每用户平均收入 = 折现收入比上启动用户数
		} else {
			info.setArpu(0.00);
		}
		if (info.getPayUserCount()>0){
			arppu = MathUtils.divideExact(cashSum, info.getPayUserCount());
			info.setArppu(arppu); // 平均每付费用户收入  =折现比上付费用户数
		} else {
			info.setArppu(0.00);
		}
		userStatisticMapper.insertAppDownloadStatisticInfo(info);
	}

	/**
	 * 月结旧数据批量处理
	 */
	@Override
	public void processPastMonthSum() {
		initCashRate();
		//获取所有APP信息
		List<String> appCodes = merchantInfoMapper.queryAllAppCode();
		if(CollectionUtils.isEmpty(appCodes)) {
			LOGGER.error("查不到任何APP信息");
			return ;
		}
		
		Calendar start = Calendar.getInstance();
		start.set(2015, 10, 1);//设置开始时间为20151101
		int startInt = Integer.parseInt(DateUtils.formatToyyyyMM(start.getTime()));
		Calendar end = Calendar.getInstance();
		if(end.get(Calendar.DAY_OF_MONTH) >= 2) {//取上一个月为止
			end.add(Calendar.MONTH, -1);
		}else {
			end.add(Calendar.MONTH, -2);//取上上一个月为止
		}
		int endInt = Integer.parseInt(DateUtils.formatToyyyyMM(end.getTime()));
		int currentInt = startInt;
		
		while( currentInt <= endInt ) {
			for(String appCode : appCodes) {
				//LOGGER.info("开始统计应用{}的历史{}月结记录...", appCode, currentInt);
				try {
					processMonthSum(currentInt, appCode);
				} catch (Exception e) {
					LOGGER.error("月流水统计出错", e);
					LOGGER.error("统计月份：{}，APPCode：{}出错{}", currentInt, appCode, e);
				}
				//LOGGER.info("统计完成{}应用{}的历史月结记录", currentInt, appCode);
			}
			start.add(Calendar.MONTH, 1);
			currentInt = Integer.parseInt(DateUtils.formatToyyyyMM(start.getTime()));
		}
		
	}

	/**
	 * 处理app的月结流水
	 * @param currentMonth
	 * @param appCode
	 */
	private void processMonthSum(int currentMonth, String appCode) {
		//获取APPCode的计费点
		List<ChargePointEntity> chargePoints = chargePointMapper.queryPointByAppCode(appCode);
		if(CollectionUtils.isEmpty(chargePoints)) {
			LOGGER.error("appcode:{}查不到任何计费点信息", appCode);
			return ;
		}
		OpenMerchantEntity merchantInfo = getMerchantInfo(appCode);
		if (merchantInfo == null) {
			LOGGER.error("查询不到appCode: {}的应用信息", appCode);
			return;
		}
		int appSource = 0;
		for(ChargePointEntity chargePoint : chargePoints) {
			SdkMonthSumEntity sdkMonthSumEntity = sdkDaySumEntityMapper.queryMonthTrans(String.valueOf(currentMonth), 
					appCode, chargePoint.getChargePointCode());
			// 优化，没有查到该计费点记录，不存入数据库
			if (sdkMonthSumEntity == null || sdkMonthSumEntity.getAppCode()==null) continue;
			// 宝币折现、宝券折现和总折现计算
			double qbbCash = (sdkMonthSumEntity.getBbTotalAmount()).divide(qbbRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
			sdkMonthSumEntity.setQbbCash(qbbCash);
			
			double bqCash = sdkMonthSumEntity.getBqTotaAmount().divide(bqRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
			sdkMonthSumEntity.setBqCash(bqCash);
			sdkMonthSumEntity.setCashSum(new BigDecimal(qbbCash).add(new BigDecimal(bqCash)).doubleValue());
					
			appSource = sdkMonthSumEntity.getAppSource();
			// 当没有流水时查出来的都是null，因此一些字段做了覆盖（重复set）
			sdkMonthSumEntity.setMerchantCode(merchantInfo.getMerchantCode());
			sdkMonthSumEntity.setMerchantName(merchantInfo.getMerchantName());
			sdkMonthSumEntity.setAppCode(merchantInfo.getAppCode());
			sdkMonthSumEntity.setAppName(merchantInfo.getAppName());
			sdkMonthSumEntity.setChargingPointCode(chargePoint.getChargePointCode());
			sdkMonthSumEntity.setChargingPointName(chargePoint.getChargePointName());
			sdkMonthSumEntity.setSettleMonth(currentMonth+"");
			sdkMonthSumEntity.setSettleStatus(SettleTypeEnum.NOT_SETTLE.getType()+"");
			
			// 月结流水，付费次数可以相加。而付费用户数需要单独查询出来
			sdkMonthSumEntity.setPayUserCount(sdkMonthSumEntityMapper.queryMonthPayUserCount(appCode, Integer.toString(currentMonth),
					chargePoint.getChargePointCode()));
			sdkMonthSumEntityMapper.insert(sdkMonthSumEntity);
		}
		
		// 统计下载用户数
		AppDownloadStatisticInfo info = queryAppDownloadData(appCode, null, Integer.toString(currentMonth));
		if (info == null) {
			LOGGER.debug("查询不到应用下载数据，返回null");
			info = new AppDownloadStatisticInfo();

		}
		info.setDateCol(String.valueOf(currentMonth));
		info.setMerchantCode(merchantInfo.getMerchantCode());
		info.setMerchantName(merchantInfo.getMerchantName());
		info.setAppCode(merchantInfo.getAppCode());
		info.setAppName(merchantInfo.getAppName());
		info.setAppSource(appSource);
		// 不选择道具代码和道具名称情况下统计付费用户数和付费次数
		AppDownloadStatisticInfo info2 = sdkMonthSumEntityMapper.statisticMonthAllPayUser(String.valueOf(currentMonth), appCode);
		info.setPayUserCount(info2.getPayUserCount());
		info.setPayCount(info2.getPayCount());
		
		info.setQbbFlow(info2.getQbbFlow());
		info.setBqFlow(info2.getBqFlow());
		// 宝券、宝币折现计算
		double qbbCash =  new BigDecimal(info2.getQbbFlow()).divide(qbbRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		info.setQbbCash(qbbCash);
		
		double bqCash = new BigDecimal(info2.getBqFlow()).divide(bqRate, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		info.setBqCash(bqCash);
		double cashSum = new BigDecimal(qbbCash).add(new BigDecimal(bqCash)).doubleValue();
		info.setCashSum(cashSum);

		// 付费转化率，arpu, arppu
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String dateCol = null;
		try {
			Date d = sdf.parse(String.valueOf(currentMonth));
			dateCol = DateFormatUtils.format(d, "yyyy-MM");
		} catch (ParseException e) {
			LOGGER.error("解析日期出错", e);
		}
		AppUserDayStatisticInfo statisticInfo = userStatisticMapper.getRecentStatisticInfo(merchantInfo.getMerchantCode(), appCode, dateCol);
		if (statisticInfo == null) {
			LOGGER.debug("没有查询到用户统计数据, appCode: {}, dateStr: {}", appCode, dateCol);
			//throw new RuntimeException("没有查询到用户统计数据");
			statisticInfo = new AppUserDayStatisticInfo(); //没有用户统计数据（值全取0）
		} else {
			if (statisticInfo.getDate().compareTo(dateCol)<0){
				AppUserDayStatisticInfo tempApp = new AppUserDayStatisticInfo();
				tempApp.setDate(dateCol);
				tempApp.setUsersSum(statisticInfo.getUsersSum());
				statisticInfo = tempApp;
			}
		}
		double payRate =0.0, arpu = 0.0, arppu=0.0;
		if (statisticInfo.getLoginUsersSum() > 0){
			payRate = (double)info2.getPayUserCount()/statisticInfo.getLoginUsersSum();
			info.setPayRate(payRate); // 付费转化率=付费用户数/登陆用户数
		} else {
			info.setPayRate(0.0);
		}
		if (statisticInfo.getLoginUsersSum() > 0){
			arpu = MathUtils.divideExact(cashSum, statisticInfo.getLoginUsersSum());
			info.setArpu(arpu); // 每用户平均收入 = 折现收入比上启动用户数
		} else {
			info.setArpu(0.00);
		}
		if (info.getPayUserCount()>0){
			arppu = MathUtils.divideExact(cashSum, info.getPayUserCount());
			info.setArppu(arppu); // 平均每付费用户收入  =折现比上付费用户数
		} else {
			info.setArppu(0.00);
		}
		userStatisticMapper.insertAppDownloadStatisticInfo(info);
	}

	/**
	 * 查询应用用户下载数据
	 * 日统计date不为null
	 * 月统计date要求为null，month不为null
	 * @param appCode
	 * @param date
	 * @param month
	 * @return
	 */
	private AppDownloadStatisticInfo queryAppDownloadData(String appCode, String date, String month){
		
		String url = payConfig.getString(date == null ? "statistic.month.app.download.data" : "statistic.day.app.download.data");
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		pairs.add(new BasicNameValuePair("appCodes", appCode));
		if (date == null){
			pairs.add(new BasicNameValuePair("month", month));
		} else {
			pairs.add(new BasicNameValuePair("date", date));
		}
		

		//pairs.add(new BasicNameValuePair("pageNo", "1"));
		//pairs.add(new BasicNameValuePair("pageSize", "10"));
		String reponse = ApiConnector.get(url, pairs);
		if (reponse != null && !"".equals(reponse)) {
			JSONObject obj = JSON.parseObject(reponse);
			if (obj.getIntValue("responseCode")==1000) {
				String data = obj.getString("data");
				List<AppDownloadStatisticInfo> dataList = JSON.parseObject(data, new TypeReference<List<AppDownloadStatisticInfo>>(){});
				if (CollectionUtils.isEmpty(dataList)) {
					LOGGER.error("查询一天的应用下载数据，没有查询到数据");
					return null;
				}
				AppDownloadStatisticInfo info = dataList.get(0);
				
				 
				return info;
			}
		}
		return null;
	}
	
	private OpenMerchantEntity getMerchantInfo(String appCode) {
		String merchantJson = redisUtil.get(APPCODE_PREFIX + appCode);
		OpenMerchantEntity  opMerchent = null;
		if(StringUtils.isBlank(merchantJson)){
			//获取数据库中的商户信息
			opMerchent = merchantInfoMapper.getOpenMerchantEntityByAppCode(appCode);
			if(opMerchent != null){
				redisUtil.setex(APPCODE_PREFIX + appCode, 60*60, JSON.toJSONString(opMerchent));
			}
		}else{
			opMerchent =  JSONObject.parseObject(merchantJson, OpenMerchantEntity.class);
		}
		return opMerchent;
	}
}
