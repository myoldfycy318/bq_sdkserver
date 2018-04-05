package com.qbao.sdk.server.service.impl.statistic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserStatisticMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.RegisterUser;
import com.qbao.sdk.server.service.statistic.UserStatisticService;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.util.RedisUtil;
import com.qbao.sdk.server.util.StatisticUtils;

@Service
public class UserStatisticServiceImpl implements UserStatisticService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	//获取应用信息key前缀
	private static final String APPCODE_PREFIX = "sdkServer:appcode_";
	
	@Resource
	private UserStatisticMapper userStatisticMapper;
	
	@Resource
	private MerchantInfoMapper merchantInfoMapper;
	
	@Resource
	private RedisUtil redisUtil;
	@Override
	public void insertRegisterUser() {
		
		dayInsertRegiserUser(DateUtils.getStatisticDate(), DateUtils.getStatisticMonth());
		
	}

	@Override
	public void statisticUserSum() {
		String monthStr = DateUtils.getStatisticMonth();
		String dateStr = DateUtils.getStatisticDate();
		// 按日应用统计
		statisticDayAppData(monthStr, dateStr);
		// 按日商户统计
		statisticDayMerchantData(monthStr, dateStr);
		// 按日运营统计
		statisticDayAllData(monthStr, dateStr);
		
		// 按月应用统计
		statisticMonthAppData(monthStr, dateStr);
		// 按月商户统计
		statisticMonthMerchantData(monthStr, dateStr);
		// 按月运营统计
		statisticMonthAllData(monthStr, dateStr);
	}

	/**
	 * 计算总用户数和存库
	 * @param dateStr
	 * @param dayStatisticsMap 存放登陆用户数和新增用户数数据
	 */
	private void countUsersAndSave(String dateStr,
			Map<String, AppUserDayStatisticInfo> dayStatisticsMap) {
		// 更新总用户数。 总用户数等于上一天的总用户数+新增用户数（减轻查注册用户表的压力）
		String prevDateStr = DateUtils.getPrevDayStr(dateStr);
		// 更新商户编码
		AppUserDayStatisticInfo app = null;
		for (Iterator<Map.Entry<String, AppUserDayStatisticInfo>> it = dayStatisticsMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, AppUserDayStatisticInfo> entry = it.next();
			AppUserDayStatisticInfo dayStatisticInfo = entry.getValue();
			// 根据应用编码查询商户编码
			OpenMerchantEntity merchantInfo = getMerchantInfo(entry.getKey());
			app = userStatisticMapper.getRecentStatisticInfo(merchantInfo.getMerchantCode(), entry.getKey(), prevDateStr);
			if (app == null) {
				dayStatisticInfo.setMerchantCode(merchantInfo.getMerchantCode());
			} else {
				dayStatisticInfo.setMerchantCode(app.getMerchantCode());
			}
			
			dayStatisticInfo.setDate(dateStr);
			// 总用户数=前一天的总用户数+今天新增的用户数
			dayStatisticInfo.setUsersSum((app == null? 0 :app.getUsersSum()) + dayStatisticInfo.getRegisterUsersSum());
			userStatisticMapper.saveAppUserDayStatistic(dayStatisticInfo);
		}
	}

	private List<AppUserDayStatisticInfo> getRegisteUsers(String monthStr, String dateStr, int type){
		List<AppUserDayStatisticInfo> RegisteUsers = new ArrayList<AppUserDayStatisticInfo>();
		List<AppUserDayStatisticInfo> tempRegisteUsers = null;
		String tableName = null;
		String tableNamePostfix = null;
		for (int i = 0; i<100; i++) {
			if (i < 10){
				tableNamePostfix = "0" + String.valueOf(i);
			} else {
				tableNamePostfix = String.valueOf(i);
			}
			tableName = StatisticUtils.REGISTER_USER_TABLE_PREFIX + tableNamePostfix;
			switch (type) {
			
			case 0 : // 日 应用
			{
				tempRegisteUsers =  userStatisticMapper.countAppDayRegisteUsers(monthStr, dateStr, tableName);
				break;
			}
			case 1: // 日 商户
			{
				tempRegisteUsers =  userStatisticMapper.countMerchantDayRegisteUsers(monthStr, dateStr, tableName);
				break;
			}
			case 2: // 日 运营
			{
				// 日运营每次查询10张表，分10次查询。对每次查询出的user_id去重
				Set<Long> userIdSet = new HashSet<Long>();
				List<RegisterUser> registerUserList = null;
				for (int j = 0; j<10; j++) {
					registerUserList =userStatisticMapper.statisticRegisterUsers(dateStr, j);
					for (RegisterUser user : registerUserList){
						userIdSet.add(user.getUserId());
					}
				}
				
				String tableName2 = null;
				String tableNamePostfix2 = null;
				int addCount = 0;
				Iterator<Long> it = userIdSet.iterator();
				while (it.hasNext()){
					long userId = it.next();
					for (int j = 0; j<100; j++) {
						if (j < 10){
							tableNamePostfix2 = "0" + String.valueOf(j);
						} else {
							tableNamePostfix2 = String.valueOf(j);
						}
						tableName2 = StatisticUtils.REGISTER_USER_TABLE_PREFIX + tableNamePostfix2;
						int count = userStatisticMapper.checkUserFirstLogin(userId, tableName2, dateStr);
						if (count > 0){
							break;
						}
						if (j == 99) addCount++;
					}
				}
				AppUserDayStatisticInfo appStatisticInfo = new AppUserDayStatisticInfo();
				appStatisticInfo.setRegisterUsersSum(addCount);
				RegisteUsers.add(appStatisticInfo);
				return RegisteUsers;
			}
			}
			RegisteUsers.addAll(tempRegisteUsers);
		}
		return RegisteUsers;
	}
	
	private void statisticDayAppData(String monthStr, String dateStr) {
		
		List<AppUserDayStatisticInfo> loginUsers = userStatisticMapper.countAppDayLoginUsers(monthStr, dateStr);
		Map<String, AppUserDayStatisticInfo> dayStatisticsMap = new HashMap<String, AppUserDayStatisticInfo>();
		int i = 0;
		
		AppUserDayStatisticInfo countDayStatistic = null;
		// 遍历登陆用户数记录
		int size = loginUsers.size();
		for ( ; i < size; i++ ) {
			countDayStatistic = loginUsers.get(i);
			if (StringUtils.isBlank(countDayStatistic.getAppCode())) continue;
			dayStatisticsMap.put(countDayStatistic.getAppCode(), countDayStatistic);
			
		}
		// 更新注册用户数
		List<AppUserDayStatisticInfo> registerUsers = getRegisteUsers(monthStr, dateStr, 0);
		size = registerUsers.size();
		AppUserDayStatisticInfo dayStatistic = null;
		for ( i = 0; i < size; i++ ) {
			countDayStatistic = registerUsers.get(i);
			if (StringUtils.isBlank(countDayStatistic.getAppCode())) continue;
			dayStatistic = dayStatisticsMap.get(countDayStatistic.getAppCode());
			if (dayStatistic == null) {
				dayStatisticsMap.put(countDayStatistic.getAppCode(), countDayStatistic);
			} else {
				dayStatistic.setRegisterUsersSum(countDayStatistic.getRegisterUsersSum());
			}
		}
		countUsersAndSave(dateStr, dayStatisticsMap);
	}

	private void statisticDayMerchantData(String monthStr, String dateStr) {
		List<AppUserDayStatisticInfo> loginUsers = userStatisticMapper.countMerchantDayLoginUsers(monthStr, dateStr);
		// key是merchantCode
		Map<String, AppUserDayStatisticInfo> dayStatisticsMap = new HashMap<String, AppUserDayStatisticInfo>();
		int i = 0;
		
		AppUserDayStatisticInfo countDayStatistic = null;
		// 遍历登陆用户数记录
		int size = loginUsers.size();
		for ( ; i < size; i++ ) {
			countDayStatistic = loginUsers.get(i);
			if (StringUtils.isBlank(countDayStatistic.getMerchantCode())) continue;
			dayStatisticsMap.put(countDayStatistic.getMerchantCode(), countDayStatistic);
			
		}
		// 更新注册用户数
		List<AppUserDayStatisticInfo> registerUsers = getRegisteUsers(monthStr, dateStr, 1);
		size = registerUsers.size();
		AppUserDayStatisticInfo dayStatistic = null;
		for ( i = 0; i < size; i++ ) {
			countDayStatistic = registerUsers.get(i);
			if (StringUtils.isBlank(countDayStatistic.getMerchantCode())) continue;
			dayStatistic = dayStatisticsMap.get(countDayStatistic.getMerchantCode());
			if (dayStatistic == null) {
				dayStatisticsMap.put(countDayStatistic.getMerchantCode(), countDayStatistic);
			} else {
				dayStatistic.setRegisterUsersSum(countDayStatistic.getRegisterUsersSum());
			}
		}
		String prevDateStr = DateUtils.getPrevDayStr(dateStr);
		// 更新日期字段
		String appCode = ALL_APP_CODE;
		for (Iterator<Map.Entry<String, AppUserDayStatisticInfo>> it = dayStatisticsMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, AppUserDayStatisticInfo> entry = it.next();
			// 根据应用编码查询商户编码
			AppUserDayStatisticInfo dayStatisticInfo = entry.getValue();
			AppUserDayStatisticInfo app = userStatisticMapper.getRecentStatisticInfo(entry.getKey(), appCode, prevDateStr);
			dayStatisticInfo.setUsersSum((app==null ? 0 : app.getUsersSum()) + dayStatisticInfo.getRegisterUsersSum());
			dayStatisticInfo.setDate(dateStr);
			// app_code
			dayStatisticInfo.setAppCode(appCode);
			userStatisticMapper.saveAppUserDayStatistic(dayStatisticInfo);
		}
	}

	private void statisticDayAllData(String monthStr, String dateStr) {
		// 登陆用户数
		AppUserDayStatisticInfo loginUsers = userStatisticMapper.countAllDayLoginUsers(monthStr, dateStr);
		if (loginUsers == null) {
			loginUsers = new AppUserDayStatisticInfo();
		}
		// 更新注册用户数
		AppUserDayStatisticInfo registerUsers = getRegisteUsers(monthStr, dateStr, 2).get(0);
		if (registerUsers != null) {
			loginUsers.setRegisterUsersSum(registerUsers.getRegisterUsersSum());
		}
		String prevDateStr = DateUtils.getPrevDayStr(dateStr);
		// 更新总用户数
		String merchantCode = YUNYING_MERCHANT_CODE;
		String appCode = ALL_APP_CODE;
		AppUserDayStatisticInfo app = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode,  prevDateStr);
		loginUsers.setUsersSum((app ==null ? 0 : app.getUsersSum())+loginUsers.getRegisterUsersSum());
		

		loginUsers.setDate(dateStr);
		// app_code和merchant_code
		loginUsers.setMerchantCode(merchantCode);
		loginUsers.setAppCode(appCode);
		userStatisticMapper.saveAppUserDayStatistic(loginUsers);
		
	}

	private void statisticMonthAppData(String monthStr, String dateStr) {
		List<AppUserDayStatisticInfo> loginUsers = userStatisticMapper.countAppMonthLoginUsers(monthStr, dateStr);
		int i = 0;
		
		AppUserDayStatisticInfo countDayStatistic = null;
		// 遍历登陆用户数记录
		int size = loginUsers.size();
		for ( ; i < size; i++ ) {
			countDayStatistic = loginUsers.get(i);
			// 根据应用编码查询商户编码
			OpenMerchantEntity merchantInfo = getMerchantInfo(countDayStatistic.getAppCode());
			countDayStatistic.setMerchantCode(merchantInfo.getMerchantCode());
			countDayStatistic.setDate(dateStr.substring(0, 7));  // 仅保存了登陆用户数统计信息
			userStatisticMapper.saveAppUserMonthStatistic(countDayStatistic);
			
		}
	}

	private void statisticMonthMerchantData(String monthStr, String dateStr) {
		List<AppUserDayStatisticInfo> loginUsers = userStatisticMapper.countMerchantMonthLoginUsers(monthStr, dateStr);
		int i = 0;
		
		AppUserDayStatisticInfo countDayStatistic = null;
		// 遍历登陆用户数记录
		int size = loginUsers.size();
		for ( ; i < size; i++ ) {
			countDayStatistic = loginUsers.get(i);
			// 根据应用编码查询商户编码
			countDayStatistic.setMerchantCode(countDayStatistic.getMerchantCode());
			countDayStatistic.setDate(dateStr.substring(0, 7));
			countDayStatistic.setAppCode(ALL_APP_CODE);
			userStatisticMapper.saveAppUserMonthStatistic(countDayStatistic);
			
		}
	}

	private void statisticMonthAllData(String monthStr, String dateStr) {
		// 登陆用户数
		AppUserDayStatisticInfo loginUsers = userStatisticMapper.countAllMonthLoginUsers(monthStr, dateStr);
		if (loginUsers == null) {
			return;
		}
		

		loginUsers.setDate(dateStr.substring(0, 7));
		// app_code和merchant_code
		loginUsers.setMerchantCode(YUNYING_MERCHANT_CODE);
		loginUsers.setAppCode(ALL_APP_CODE);
		userStatisticMapper.saveAppUserMonthStatistic(loginUsers);
		
	}

	/**
	 * 上线时需要做已上架游戏的后台数据统计
	 */
	@Override
	public void onlineStatisticInitial() {
		String beginDate = "2015-11-01";
		final SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -2);
		String endDate = dateSdf.format(c.getTime());
		String monthStr;
		
		try {
			c.setTime(dateSdf.parse(beginDate));
		} catch (ParseException e) {
			log.error("user statistic parse beginDate failed", e);
			return;
		}
		while (beginDate.compareTo(endDate) <= 0) {
			monthStr = beginDate.substring(0, 7).replaceAll("-", "");
			dayInsertRegiserUser(beginDate, monthStr);
			// 按日应用统计
			statisticDayAppData(monthStr, beginDate);
			// 按日商户统计
			statisticDayMerchantData(monthStr, beginDate);
			// 按日运营统计
			statisticDayAllData(monthStr, beginDate);
			
			// 按月应用统计
			statisticMonthAppData(monthStr, beginDate);
			// 按月商户统计
			statisticMonthMerchantData(monthStr, beginDate);
			// 按月运营统计
			statisticMonthAllData(monthStr, beginDate);
			c.add(Calendar.DATE, 1);
			beginDate = dateSdf.format(c.getTime());
		}
		
	}

	private void dayInsertRegiserUser(String date, String monthStr) {
		String tableNo = null;
		String tableName = null;
		for (int i =0; i<100; i++){
			if (i < 10){
				tableNo = "0" + String.valueOf(i);
			} else {
				tableNo = String.valueOf(i);
			}
			
			tableName = StatisticUtils.REGISTER_USER_TABLE_PREFIX + tableNo;
			// 截止日期（不包含）
			Calendar c = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
				Date date2 = sdf.parse(date);
				
				c.setTime(date2);
				c.add(Calendar.DATE, 1);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userStatisticMapper.insertRegisteUser(monthStr, date, DateFormatUtils.format(c, "yyyy-MM-dd"), tableName, i);
		}
	}
	
	@Override
	public AppUserDayStatisticInfo queryUserStatisticInfo(
			String merchantCode, String appCode, String monthStr, String dateStr) {
		// 日查询
		AppUserDayStatisticInfo app = null;
		if (!StringUtils.isBlank(dateStr)) {
			app = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode, dateStr);
			if (app==null) return null;
			// 查询出来的结果中日期小于传进来的日期，说明这一天没有用户登陆数据（表中没有这天记录）
			if (dateStr.compareTo(app.getDate())> 0) {
				//app.setDate(dateStr);
				app.setLoginUsersSum(0);
				app.setRegisterUsersSum(0);
			}
			
		} else { // 月查询
			String recentDayStr = DateUtils.getRecentDayStr(monthStr);
			app = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode, recentDayStr);
			if (app==null) return null;
			if (!app.getDate().substring(0, 7).equals(monthStr)) { // 查询到的不是当前月数据，说明后面日期没有登录用户和新增用户，当前月份的总用户也等于这一天的
				app.setLoginUsersSum(0);
				app.setRegisterUsersSum(0);
			} else {
				// 更新新增用户数
				String prevRecentDayStr = DateUtils.getPrevMonthRecentDayStr(monthStr);
				AppUserDayStatisticInfo prevApp = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode, prevRecentDayStr);
				if (prevApp!= null){
					// 新增用户数=当月用户数-上月的用户数
					app.setRegisterUsersSum(app.getUsersSum()-prevApp.getUsersSum());
				} else {
					app.setRegisterUsersSum(app.getUsersSum());
				}
				// 月查询更新登陆用户数
				AppUserDayStatisticInfo app2 = userStatisticMapper.queryMonthUserStatisticInfo(merchantCode, appCode, monthStr);
				if (app2 !=null && app2.getDate().equals(monthStr)){
					app.setLoginUsersSum(app2.getLoginUsersSum());
				} else {
					app.setLoginUsersSum(0);
				}
			}

		}
		
		
		return app;
	}

	@Override
	public List<AppUserDayStatisticInfo> queryRangeUserStatisticInfo(
			String merchantCode, String appCode, String fromDate, String toDate) {
		
		Calendar c = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
			Date d = sdf.parse(fromDate);
			c.setTime(d);
		} catch (ParseException e) {
			log.error("解析日期出错", e);
			throw new RuntimeException(e);
		}
		List<AppUserDayStatisticInfo> apps = userStatisticMapper.queryRangeUserStatisticInfo(merchantCode, appCode, fromDate, toDate);
		if (!CollectionUtils.isEmpty(apps)){
			AppUserDayStatisticInfo app = apps.get(0);
			if (app.getDate().compareTo(fromDate)>0){
				AppUserDayStatisticInfo app2 = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode, fromDate);
				if (app2 == null) {
					app2 = new AppUserDayStatisticInfo();
					app2.setDate(fromDate);
				} else {
					app2.setDate(fromDate);
					app2.setLoginUsersSum(0);
					app2.setRegisterUsersSum(0);
				}
				apps.add(0, app2);
			}
			AppUserDayStatisticInfo app2 = null;
			AppUserDayStatisticInfo lastApp = apps.get(0);
			for (int i = 1; i<apps.size(); i++ ){
				app = apps.get(i);
				c.add(Calendar.DATE, 1);
				String date = DateFormatUtils.format(c, "yyyy-MM-dd");
				if (app.getDate().compareTo(date)>0){
					app2 = new AppUserDayStatisticInfo();
					app2.setDate(date);
					app2.setUsersSum(lastApp.getUsersSum());
					apps.add(i, app2);
				} else {
					lastApp = app;
				}
				
				
				
			}
			app = apps.get(apps.size()-1);
			String date = app.getDate();
			while (date.compareTo(toDate)<0){
				c.add(Calendar.DATE, 1);
				date = DateFormatUtils.format(c, "yyyy-MM-dd");
				app2 = new AppUserDayStatisticInfo();
				app2.setLoginUsersSum(0);
				app2.setRegisterUsersSum(0);
				app2.setUsersSum(app.getUsersSum());
				app2.setDate(date);
				apps.add(app2);
				
			}
		} else {
			AppUserDayStatisticInfo app2 = userStatisticMapper.getRecentStatisticInfo(merchantCode, appCode, fromDate);
			if (app2 == null) return null; //没有数据
			apps = new ArrayList<AppUserDayStatisticInfo>();
			AppUserDayStatisticInfo app = new AppUserDayStatisticInfo();
			app.setLoginUsersSum(0);
			app.setRegisterUsersSum(0);
			app.setUsersSum(app2.getUsersSum());
			app.setDate(fromDate);
			apps.add(app);
			String date = fromDate;
			while (date.compareTo(toDate)<0){
				c.add(Calendar.DATE, 1);
				date = DateFormatUtils.format(c, "yyyy-MM-dd");
				app = new AppUserDayStatisticInfo();
				app.setLoginUsersSum(0);
				app.setRegisterUsersSum(0);
				app.setUsersSum(app2.getUsersSum());
				app.setDate(date);
				apps.add(app);
				
			}
		}


		return apps;
	}
	
	public OpenMerchantEntity getMerchantInfo(String appCode) {
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
