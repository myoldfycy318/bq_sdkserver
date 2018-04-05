/**
 * 
 */
package com.qbao.sdk.server.service.impl.redis;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.metadata.dao.mapper.pay.GlobalVarMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.service.redis.RedisService;
import com.qbao.sdk.server.util.RedisUtil;

/**
 * @author mazhongmin 
 *
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {
	
	@Resource
	MerchantInfoMapper merchantInfoMapper;
	
	@Resource
	private GlobalVarMapper globalVarMapper;
	
	@Resource
	private RedisUtil redisUtil;

	@Override
	public OpenMerchantEntity getMerchantInfo(String appCode) {
		String merchantJson = redisUtil.get(MERCHANT_PREFIX + appCode);
		OpenMerchantEntity  opMerchent = null;
		if(StringUtils.isBlank(merchantJson)){
			//获取数据库中的商户信息
			opMerchent = merchantInfoMapper.getOpenMerchantEntityByAppCode(appCode);
			if(opMerchent != null){
				redisUtil.set(MERCHANT_PREFIX + appCode, JSON.toJSONString(opMerchent));
			}
		}else{
			opMerchent =  JSONObject.parseObject(merchantJson, OpenMerchantEntity.class);
		}
		return opMerchent;
	}
	
	@Override
	public String getGlobalVarByType(String varType) {

		String value = redisUtil.get(GLOBAL_VAR+varType);
		if(StringUtils.isBlank(value)){
			//获取数据库中的信息
			value = globalVarMapper.getGlobalVarByType(varType);	
			if(StringUtils.isNotBlank(value)){
				//放入缓存
				redisUtil.set(GLOBAL_VAR, value);
			}
		}
		return value;
		
	}
}
