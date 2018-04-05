/**
 * 
 */
package com.qbao.sdk.server.service.pay;

import com.qbao.sdk.server.bo.pay.PayConfigInfo;



/**
 * @author mazhongmin
 * 开放平台 支付配置信息
 */
public interface OpenPayConfigService {
	
	
	/**
	 * 插入开放平台支付配置
	 * @param entity  支付配置 实体对象
	 */
	void insertOpenPayConfig(PayConfigInfo payConfig);


}
