package com.qbao.sdk.server.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qbao.sdk.server.service.pay.TurntableAsyncNoticeService;


/**
 * 支付异步通知
 * @author liuxingyue
 *
 */
public class TurntableAscyNoticeTask {
protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	TurntableAsyncNoticeService turntableAsyncNoticeService;
	
	public void asyncNoticeAday(){
		log.info("发送大转盘抽奖次数异步通知-每日 start..");
		turntableAsyncNoticeService.asyncNoticeAday();
		log.info("发送大转盘抽奖次数异步通知-每日   end..");
	}
	
	public void asyncNoticeAweek(){
		log.info("发送大转盘抽奖次数异步通知-每周  start..");
		turntableAsyncNoticeService.asyncNoticeAweek();
		log.info("发送大转盘抽奖次数异步通知-每周   end..");
	}
	
	public void asyncNoticeAmonth(){
		log.info("发送大转盘抽奖次数异步通知-每月  start..");
		turntableAsyncNoticeService.asyncNoticeAmonth();
		log.info("发送大转盘抽奖次数异步通知-每月   end..");
	}
	
	public void asyncNoticeAperiod(){
		log.info("发送大转盘抽奖次数异步通知-自定义时间段  start..");
		turntableAsyncNoticeService.asyncNoticeAperiod();
		log.info("发送大转盘抽奖次数异步通知-自定义时间段   end..");
	}

}
