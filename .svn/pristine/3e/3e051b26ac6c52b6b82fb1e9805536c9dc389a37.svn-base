package com.qbao.sdk.server.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qbao.sdk.server.service.AutoSendBqService;


/**
 * @author mazhongmin
 * 自动发放宝券
 *
 */
public class AutoSendBqTask {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	AutoSendBqService autoSendBqService;
	
	public void sumConsume(){
		log.info("自动发放宝券 start..");
//		autoSendBqService.sumTransConsume();
		autoSendBqService.sumTransConsumeV2();
		log.info("自动发放宝券  end..");
	}
}
