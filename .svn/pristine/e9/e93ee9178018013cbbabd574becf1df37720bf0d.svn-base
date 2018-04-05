package com.qbao.sdk.server.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qbao.sdk.server.service.statistic.TransProcessService;

/**
 * 日切月结流水统计
 * @author xuefeihu
 *
 */
public class DayAndMonthSumTask {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private TransProcessService transProcessService;

	/**
	 * 处理每月月结流水
	 */
	public void process2MonthSum() {
		
	}

	/**
	 * 处理每日日切流水
	 */
	public void process2DaySum() {
		long start = System.currentTimeMillis();
		log.info("处理每日日切流水  start..");
		long used;
		try {
			transProcessService.process2DaySum();
			used = System.currentTimeMillis() - start;
			log.info("处理每日日切流水  end.用时：" + used/1000 + " 秒");
		} catch (Exception e) {
			log.error("处理每日日切流水出错", e);
		}
		
		start = System.currentTimeMillis();
		log.info("处理每月月结流水 start...");
		try {
			transProcessService.process2MonthSum();
			used = System.currentTimeMillis() - start;
			log.info("处理每月月结流水  end.用时：" + used/1000 + " 秒");
		} catch (Exception e) {
			log.error("处理每日月结流水出错", e);
		}
	}

	
	///////////////////////////////////////////////////////////////////////////
	//以下接口只跑一次
	///////////////////////////////////////////////////////////////////////////	
	
	/**
	 * 处理旧日切流水
	 */
	public void processPastDaySum() {
		long start = System.currentTimeMillis();
		log.info("处理旧日切流水  start..");
		transProcessService.processPastDaySum();
		long used = System.currentTimeMillis() - start;
		log.info("处理旧日切流水   end.用时：" + used/1000 + " 秒");
		
		start = System.currentTimeMillis();
		log.info("处理旧月结流水  start..");
		transProcessService.processPastMonthSum();
		used = System.currentTimeMillis() - start;
		log.info("处理旧月结流水   end.用时：" + used/1000 + " 秒");
	}

	/**
	 * 处理旧月结流水
	 */
	public void processPastMonthSum() {
		
	}

}
