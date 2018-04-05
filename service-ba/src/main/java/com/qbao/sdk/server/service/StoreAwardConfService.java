package com.qbao.sdk.server.service;

import java.util.List;

import com.qbao.sdk.server.metadata.entity.StoreAwardConfEntity;


/**
 * 游戏活动奖励规则
 * @author xuefeihu
 *
 */
public interface StoreAwardConfService {
	
	/**
	 * 查询配置
	 * @param time 发放时间
	 * @param appCode 
	 * @return
	 */
	List<StoreAwardConfEntity> queryConf(String time, String appCode);
	
}
