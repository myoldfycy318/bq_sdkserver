/**
 * 
 */
package com.qbao.sdk.server.service.impl.pay;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qbao.sdk.server.bo.pay.ChargePointInfo;
import com.qbao.sdk.server.metadata.dao.mapper.pay.ChargePointMapper;
import com.qbao.sdk.server.metadata.entity.pay.ChargePointEntity;
import com.qbao.sdk.server.service.pay.OpenChargingPointService;

/**
 * @author mazhongmin
 *
 */
@Service("openChargingPointService")
public class OpenChargingPointServiceImpl implements OpenChargingPointService {

	@Resource
	ChargePointMapper openChargePointMapper; 
	
	@Transactional
	@Override
	public void insertChargingPoint(ChargePointInfo chargePointInfo) {
		openChargePointMapper.deleteByCode(chargePointInfo.getChargePointCode());
		ChargePointEntity entity = new ChargePointEntity();
		BeanUtils.copyProperties(chargePointInfo, entity);
		openChargePointMapper.insert(entity);
	}

}
