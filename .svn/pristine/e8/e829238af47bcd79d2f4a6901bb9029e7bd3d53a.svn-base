package com.qbao.sdk.server.service.impl.pay;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qbao.sdk.server.bo.pay.OpenMerchant;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.service.pay.OpenMerchantService;


/**
 * 
 * @author Frank.Zhou
 *
 */
@Service("openMerchantService")
public class OpenMerchantServiceImpl implements OpenMerchantService {
	
	@Resource
	MerchantInfoMapper merchantInfoMapper;

	/*
	 * 
	 * @see com.qbao.opensdk.service.OpenMerchantService#insertOpenMerchant(com.qbao.opensdk.bo.OpenMerchant)
	 */
	@Transactional
	@Override
	public void insertOpenMerchant(OpenMerchant openMerchant) {
		//先删除在插入
		merchantInfoMapper.deleteMerchantByAppCode(openMerchant.getAppCode());
		OpenMerchantEntity openMerchantEntity = new OpenMerchantEntity();
		BeanUtils.copyProperties(openMerchant, openMerchantEntity);
		openMerchantEntity.setStatus(openMerchant.getStatus());
		merchantInfoMapper.insertOpenMerchant(openMerchantEntity);
	}

}
