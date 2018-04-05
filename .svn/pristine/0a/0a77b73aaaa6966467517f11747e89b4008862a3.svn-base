package com.qbao.sdk.server.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qbao.sdk.server.service.statistic.UserStatisticService;

/**
 * 开放平台数据统计功能中数据预处理
 * 譬如今天是1月5号，1月4号凌晨2点开始统计1月3号的数据，然后今天能查看1月3号的数据。
 * @author lilongwei
 *
 */
public class SdkUserStatisticTask {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	UserStatisticService userStatisticServiceImpl;
	public void userStatistic() {
		log.info("user data statistic start...");
		try {
			
			userStatisticServiceImpl.insertRegisterUser();
			userStatisticServiceImpl.statisticUserSum();
			log.info("user data statistic end...");
		} catch (Exception e) {
			log.error("开发平台统计注册用户和登陆用户出错", e);
		}
		
	}
	
	public void onlineStatisticInitial() {
		log.info("开发平台数据统计功能上线统计数据初始化开始...");
		try {
			userStatisticServiceImpl.onlineStatisticInitial();
			log.info("开发平台数据统计功能上线统计数据初始化结束...");
		} catch (Exception e) {
			log.error("开发平台数据统计功能上线统计数据初始化出错", e);
		}
		
	}
}
