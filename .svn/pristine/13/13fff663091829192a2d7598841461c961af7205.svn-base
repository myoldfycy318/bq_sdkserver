package com.qbao.sdk.server.service.impl.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qbao.sdk.server.bo.pay.BaseResponse;
import com.qbao.sdk.server.service.pay.RsaUtilService;
import com.qbao.sdk.server.util.rsa.RSACoder;

/**
 * 
 * 描述：
 * rsa服务实现
 * @author hexiaoyi
 */
@Service("rsaUtilService")
public class RsaUtilServiceImpl implements RsaUtilService {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public BaseResponse valRsaSign(String data, String publicKey,
			String signCode) {
		// 验证签名
		boolean status = false;
		try {
			status = RSACoder.verify(data.getBytes(), publicKey, signCode);
		} catch (Exception e) {
			log.error("公钥验签失败:" + e);
		}
		return new BaseResponse(status);
	}
	
	@Override
	public String rsaSign(String data, String privateKey) {
		// 验证签名
		String status = "";
		try {
			status = RSACoder.sign(data.getBytes(), privateKey);
		} catch (Exception e) {
			log.error("私钥加签失败:" + e);
		}
		return status;
	}
}
