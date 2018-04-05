/**
 * 
 */
package com.qbao.sdk.server.metadata.dao.mapper.pay;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.ChargePointEntity;

/**
 * @author mazhongmin
 *
 */
@Repository
public interface ChargePointMapper extends IBaseMapperDao<ChargePointEntity, Long> {
	
	/**
	 * 根据APPCode查询所有计费点信息
	 * @param appCode
	 * @return
	 */
	List<ChargePointEntity> queryPointByAppCode(@Param("appCode")String appCode);

	/**
	 * 新增计费点信息
	 * @param entity
	 */
	void insert(@Param("entity") ChargePointEntity entity);
	
	/**
	 * 根据计费点编码删除计费点信息
	 * @param code
	 */
	void deleteByCode(@Param("code") String code);
	
	/**
	 * 根据计费点编码和appCode查询计费点信息
	 * @param code
	 * @param appCode
	 * @return
	 */
	ChargePointEntity selectByCode(@Param("code") String code,@Param("appCode")String appCode);
}
