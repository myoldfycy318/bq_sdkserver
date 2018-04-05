package com.qbao.sdk.server.service.statistic;

import java.util.Map;

/**
 * 历史流水数据处理
 * @author xuefeihu
 *
 */
public interface TransProcessService {
	
	/**
	 * 处理每月月结流水
	 */
	void process2MonthSum();
	
	/**
	 * 处理每日日切流水
	 */
	void process2DaySum();
	
	///////////////////////////////////////////////////////////////////////////
	//以下接口只跑一次
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * 处理旧日切流水
	 */
	void processPastDaySum();
	
	/**
	 * 处理旧月结流水
	 */
	void processPastMonthSum();

}
