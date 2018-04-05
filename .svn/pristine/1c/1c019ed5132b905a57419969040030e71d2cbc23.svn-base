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
	 * 插入规则
	 * @param entity
	 * @return
	 */
	int save(StoreAwardConfEntity entity);
	
	/**
	 * 上下架
	 * @param isPublish 0 下架、1 上架
	 * @return
	 */
	int publish(String storeConfId, int isPublish, int type);
	
	/**
	 * 查询配置
	 * @param time 发放时间
	 * @param appCode 
	 * @return
	 */
	List<StoreAwardConfEntity> queryConf(String time, String appCode);
	
	/**
	 * 删除配置
	 * @param storeConfId 应用市场配置ID
	 * @return
	 */
	int deleteByStoreConfId(String storeConfId, Integer type);

}
