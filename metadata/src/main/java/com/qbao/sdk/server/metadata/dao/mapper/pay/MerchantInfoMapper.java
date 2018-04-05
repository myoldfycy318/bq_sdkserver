package com.qbao.sdk.server.metadata.dao.mapper.pay;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;



@Repository
public interface MerchantInfoMapper extends IBaseMapperDao<OpenMerchantEntity, Long> {
	
	/**
	 * 根据钱宝账户ID和appCode查询APP
	 * @param merchantUserId
	 * @return
	 */
	List<OpenMerchantEntity> queryMerchantByUserId(@Param("merchantUserId")String merchantUserId, @Param("appCode") String appCode);
	
	/**
	 * 查询所有APPcode
	 * @return
	 */
	List<String> queryAllAppCode();
	
	/**
	 * 插入开放平台商户信息
	 * 
	 * @param openMerEntity
	 */
	void insertOpenMerchant(@Param("entity")OpenMerchantEntity openMerEntity);
	
	/**
	 * 删除商户数据
	 * 
	 * @param appCode
	 */
	void deleteMerchantByAppCode(@Param("appCode")String appCode);
	
	/**
	 * 根据应用编码获取开放平台商户信息
	 * 
	 * @param appCode
	 * @return
	 */
	OpenMerchantEntity getOpenMerchantEntityByAppCode(@Param("appCode")String appCode);

	/**
	 * 获取所有应用
	 * @return
	 */
	List<OpenMerchantEntity> getAllApp();
	
}
