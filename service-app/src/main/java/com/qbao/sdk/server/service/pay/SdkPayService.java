/**
 * 
 */
package com.qbao.sdk.server.service.pay;

import com.qbao.sdk.server.bo.pay.SdkDopayRequest;
import com.qbao.sdk.server.bo.pay.SdkPayRequest;
import com.qbao.sdk.server.bo.pay.SdkPayResponse;
import com.qbao.sdk.server.bo.pay.SdkTrustpayRequest;
import com.qbao.sdk.server.bo.pay.SmsSend;

/**
 * @author mazhongmin
 *
 */
public interface SdkPayService {
	
	/** 认证超时时间（秒） */
	static final int AUTH_TIME_OUT = 600;
	
	//SDK支付业务类型
	static final String  BIZ_TYPE = "800001";
	
	//宝券 业务类型
	static final String BQ_BIZ_TYPE = "48";



	/**
	 * SDK支付
	 * @param sdkDopayBo
	 * @return
	 */
	SdkPayResponse dopay(SdkDopayRequest sdkDopayBo);
	
	/**
	 * SDK获取用户信息
	 * @param payTransEntity
	 * @return
	 */
	SdkPayResponse dealWithPayRequest(SdkPayRequest payRequest);
	
	/**
	 * 发送短信验证码
	 * @param smsSend
	 * @return
	 */
	SdkPayResponse sendSms(SmsSend smsSend);
	
	/**
	 * 信任支付
	 * @param trustPayBo
	 * @return
	 */
	SdkPayResponse dotrustpay(SdkTrustpayRequest trustPayBo);
}
