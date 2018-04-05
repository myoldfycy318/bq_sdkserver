package com.qbao.sdk.server.service.impl.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.service.login.LoginResultNotifyService;
import com.qbao.sdk.server.util.ApiConnector;
import com.qbao.sdk.server.util.HttpClientUtil;
import com.qbao.sdk.server.util.LoginResultNotifyExecutor;

/**
 * 登录结果异步通知
 * 
 * @author
 *
 */
@Service("loginResultNotifyService")
public class LoginResultNotifyServiceImpl implements LoginResultNotifyService {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public void startNotify(long userId, String key, String clientId, String notifyUrl) {
		executeStartNotifyThread(userId, key, clientId, notifyUrl);
	}

	/**
	 * 启动异步通知线程
	 * 
	 * @param userId
	 *            用户id
	 * @param key
	 *            客户端唯一key
	 * @param clientId
	 *            应用app编码
	 * @param notifyUrl
	 *            异步回调地址
	 */
	private void executeStartNotifyThread(final long userId, final String key, final String clientId,
			final String notifyUrl) {
		LoginResultNotifyExecutor.executePayResultNotify(new Runnable() {
			@Override
			public void run() {
				try {
					sendAsyncNotifyRequest(userId, key, clientId, notifyUrl);
				} catch (Exception e) {
					log.error("启动支付结果异步通知线程异常! userId=" + userId + ",key=" + key + ",clientId=" + clientId
							+ ",notifyUrl=" + notifyUrl, e);
				}
			}
		});
	}

	/**
	 * 发送异步通知请求
	 * 
	 * @param userId
	 *            用户id
	 * @param key
	 *            客户端为key
	 * @param clientId
	 *            应用app编码
	 * @param notifyUrl
	 *            异步回调地址
	 * @return 通知是否成功
	 */
	private boolean sendAsyncNotifyRequest(long userId, String key, String clientId, String notifyUrl) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		paramList.add(new BasicNameValuePair("key", key));
		paramList.add(new BasicNameValuePair("userId", String.valueOf(userId)));
		paramList.add(new BasicNameValuePair("appCode", clientId));

		boolean isSuccess = true;
		try {
			log.info("异步通知URL：{},参数：{}", notifyUrl, JSON.toJSONString(paramList));
			ApiConnector.get(notifyUrl, paramList);
		} catch (Exception e) {
			log.error("发送异步通知失败(Exception)!userId=" + userId + ",key=" + key + ",clientId=" + clientId, e);
			isSuccess = false;
		}

		log.info("异步通知结果：{}", isSuccess);
		return isSuccess;
	}
}
