package com.qbao.sdk.server.service.pay;

import com.qbao.sdk.server.bo.pay.BaseResponse;

/**
 * 
 * 描述：
 * RSA服务接口
 * @author hexiaoyi
 */
public interface RsaUtilService {
	/**
	 * rsa公钥验签
	 * @param data
	 * @param publicKey
	 * @param signCode
	 * @return
	 */
	BaseResponse valRsaSign(String data,String publicKey,String signCode);
	
	/**
	 * 私钥加签
	 * @param data
	 * @param privateKey
	 * @return
	 */
	String rsaSign(String data,String privateKey); 
}
