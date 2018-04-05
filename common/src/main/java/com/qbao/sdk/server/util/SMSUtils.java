package com.qbao.sdk.server.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.sms.client.message.api.SMSSendApi;
import com.api.sms.common.exception.MessageSendException;
import com.api.sms.common.exception.SmsFailedException;
import com.qbao.sdk.server.constants.SmsStatusEnum;
import com.qbao.sdk.server.view.SmsSendResult;

public class SMSUtils {

	protected final Logger logger = LoggerFactory.getLogger(SMSUtils.class);

	@Resource
	RedisUtil redisUtil;

	private String SMS_REDIS_KEY_PREFIX = "sms.pay.";
	/**
	 * 生成手机验证码
	 * 
	 * @param mobile
	 * @param request
	 * @return
	 * @throws SmsFailedException
	 * @throws PayOrderException
	 */
	public SmsSendResult generateAndSaveVerifyCode(String mobile, String ip) {

		// 判断单个ip一分钟内发送次数判断
		String ipCount = redisUtil.get(ip);

		int count = 1;
		if (StringUtils.isNotBlank(ipCount)) {

			count = Integer.parseInt(ipCount);

			if (count >= 9) {
				logger.info("ip:" + ip + "一分钟内发送短信超过10次");
				return new SmsSendResult(SmsStatusEnum.SEND_SMS_OFTEN.getStatus(),
						SmsStatusEnum.SEND_SMS_OFTEN.getDesc());
			}

			count++;
		}

		// 判断手机号码
		String mobileCatche = redisUtil.get(mobile);

		if (StringUtils.isNotBlank(mobileCatche)) {

			logger.info("mobile:" + mobile + "一分钟内发送短信,暂停发送");
			return new SmsSendResult(SmsStatusEnum.SEND_SMS_ONCE_MIN.getStatus(), 
					SmsStatusEnum.SEND_SMS_ONCE_MIN.getDesc());
		}

		String verifyCode = "";
		String format = "";

		// 验证码
		verifyCode = generateVerifyCode();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		format = df.format(new Date());
		int busId = 39;
		try {
			String ret = SMSSendApi.sendSmsSignleCode(busId, mobile,
					"欢迎使用钱宝网，您此次操作的验证码是：" + verifyCode, verifyCode, format, 2);
			logger.info(mobile + "验证码短信发送成功:" + verifyCode);
			logger.info("发送短信结果:" + ret);
		} catch (IOException e) {
			logger.error("支付验证码发送失败", e);
		} catch (MessageSendException em) {
			logger.error("支付验证码发送失败", em);
		}

		logger.info("======发送的手机验证码为" + verifyCode + "=======");
		
		redisUtil.setex(mobile, 90, mobile);
		redisUtil.setex(ip,90, String.valueOf(count));
		redisUtil.setex(mobile + "payverifyCode",90, verifyCode);
		redisUtil.setex(SMS_REDIS_KEY_PREFIX + mobile,90, verifyCode + "$" + format);
		
		
		return new SmsSendResult(SmsStatusEnum.SEND_SMS_OK.getStatus(), 
				SmsStatusEnum.SEND_SMS_OK.getDesc(),verifyCode);
	}

	/**
	 * 验证手机验证码
	 * 
	 * @param mobile
	 * @param request
	 * @return
	 * @throws SmsFailedException
	 * @throws PayOrderException
	 */
	public SmsSendResult verifySmsCode(String mobile, String verifyCode) {
		String value = redisUtil.get(SMS_REDIS_KEY_PREFIX + mobile);
		if (StringUtils.isBlank(value)) {
			return new SmsSendResult(SmsStatusEnum.SMS_IS_EXPIRE.getStatus(),SmsStatusEnum.SMS_IS_EXPIRE.getDesc());
		}

		String valueArray[] = value.split("\\$");
		if(!valueArray[0].equals(verifyCode)){
			return new SmsSendResult(SmsStatusEnum.SMS_ERROR.getStatus(),SmsStatusEnum.SMS_ERROR.getDesc());
		}
		
		return new SmsSendResult(SmsStatusEnum.SEND_SMS_OK.getStatus(),SmsStatusEnum.SEND_SMS_OK.getDesc());

	}

	private String generateVerifyCode() {
		return String.valueOf(new Random().nextInt(899999) + 100000);
	}

}
