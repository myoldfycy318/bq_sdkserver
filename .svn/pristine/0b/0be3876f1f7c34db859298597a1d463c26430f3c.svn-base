package com.qbao.sdk.server.metadata.dao.mapper.pay;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.GlobalVarEntity;

@Repository
public interface GlobalVarMapper  extends IBaseMapperDao<GlobalVarEntity, Long>{
	
	/**
	 * 获得发送短信验证码的交易金额临界点
	 * @return
	 */	
	String getGlobalVarByType(@Param("varType") String varType);
	

}
