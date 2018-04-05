/**
 * 
 */
package com.qbao.sdk.server.metadata.dao.mapper.pay;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.PayConfigEntity;


/**
 * @author mazhongmin
 * 支付配置 mapper
 */
@Repository
public interface PayConfigMapper extends IBaseMapperDao<PayConfigEntity, Long>{	
	
	/**
	 * 根据appCode删除 指定支付配置(逻辑删除)
	 * @param appCode  应用编码
	 */
	void deleteByAppCode(@Param("appCode") String appCode);
	
	
	/**
	 * 新增支付配置信息  
	 * @param entity  支付配置 实体对象
	 */
	void insertPayConfig(@Param("entity") PayConfigEntity entity);
	
	
	/**
	 * 根据应用编码查询 支付配置信息
	 * @param appCode  应用编码
	 * @return
	 */
	PayConfigEntity queryByAppCode(@Param("appCode") String appCode);

}
