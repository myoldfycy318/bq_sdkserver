package com.qbao.sdk.server.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qbao.sdk.server.service.pay.PayAsyncNoticeService;


/**
 * 支付异步通知
 * @author liuxingyue
 *
 */
public class PayAsyncNoticeTask {
protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	PayAsyncNoticeService payAsyncNoticeService;
	
	public void payAsyncNotice(){
		log.info("发送支付异步通知 start..");
		payAsyncNoticeService.payAsyncNotice();
		log.info("发送支付异步通知  end..");
	}

}
