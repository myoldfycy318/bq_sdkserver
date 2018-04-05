package com.qbao.sdk.server.metadata.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.StoreAwardConfEntity;


/**
 * 游戏活动奖励规则
 * @author xuefeihu
 *
 */
@Repository
public interface StoreAwardConfMapper extends IBaseMapperDao<StoreAwardConfEntity, Integer> {
	
	int insert(StoreAwardConfEntity entity);
	
	int updateById(StoreAwardConfEntity entity);
	
	/**
	 * 上下架
	 * @param storeConfId 应用市场配置ID
	 * @param isPublish 0 下架、1 上架
	 * @param type 规则类型
	 * @return
	 */
	int publish(@Param("storeConfId")String storeConfId, @Param("isPublish")Integer isPublish, @Param("type")Integer type);
	
	/**
	 * 查询配置
	 * @param date 日期
	 * @param time 时间
	 * @param appCode SDK应用编码
	 * @return
	 */
	List<StoreAwardConfEntity> queryConf(@Param("date")String date, @Param("time")String time, @Param("appCode") String appCode);
	
	/**
	 * 根据应用市场配置ID获取配置
	 * @param storeConfId 应用市场配置ID
	 * @param type 配置类型
	 * @return
	 */
	List<StoreAwardConfEntity> queryConfByStoreConfId(@Param("storeConfId") String storeConfId, @Param("type" )Integer type);
	
	/**
	 * 删除配置
	 * @param storeConfId 应用市场配置ID
	 * @param type 配置类型
	 * @return
	 */
	int deleteByStoreConfId(@Param("storeConfId")String storeConfId, @Param("type") Integer type);
	

}
