/**
 * 
 */
package com.qbao.sdk.server.service.impl.pay;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qbao.sdk.server.bo.pay.PayConfigInfo;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayConfigMapper;
import com.qbao.sdk.server.metadata.entity.pay.PayConfigEntity;
import com.qbao.sdk.server.service.pay.OpenPayConfigService;

/**
 * @author mazhongmin
 *
 */
@Service("openPayConfigService")
public class OpenPayConfigServiceImpl implements OpenPayConfigService {

	@Resource
	PayConfigMapper payConfigMapper;
	
	@Transactional
	@Override
	public void insertOpenPayConfig(PayConfigInfo payConfig) {
		payConfigMapper.deleteByAppCode(payConfig.getAppCode());
		PayConfigEntity entity = new PayConfigEntity();
		//对象转换成实体对象
		BeanUtils.copyProperties(payConfig, entity);
	    payConfigMapper.insertPayConfig(entity);
	}

}
