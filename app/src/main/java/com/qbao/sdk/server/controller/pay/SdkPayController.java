/**
 * 
 */
package com.qbao.sdk.server.controller.pay;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.SdkDopayRequest;
import com.qbao.sdk.server.bo.pay.SdkPayRequest;
import com.qbao.sdk.server.bo.pay.SdkPayResponse;
import com.qbao.sdk.server.bo.pay.SdkTrustpayRequest;
import com.qbao.sdk.server.bo.pay.SmsSend;
import com.qbao.sdk.server.service.pay.SdkPayService;
import com.qbao.sdk.server.util.IPUtil;

/**
 * @author mazhongmin
 * 应用市场SDK支付
 *
 */
@Controller
@RequestMapping("/sdkpay/v10")
public class SdkPayController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private SdkPayService sdkPayService;

	/**
	 * 获得用户支付信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/payrequest")
	@ResponseBody
	public SdkPayResponse payRequest(HttpServletRequest request){
		//用户支付信息参数解析
		SdkPayRequest payRequest = payRequestParam(request);
//		logger.info("获取用户支付信息请求参数：{}",payRequest);
		SdkPayResponse sdkPayResponse = sdkPayService.dealWithPayRequest(payRequest);
//		logger.info("获取支付信息返回结果:{}",JSON.toJSONString(sdkPayResponse));
		return sdkPayResponse;
		
	}
	
	/**
	 * SDK支付
	 * @param request
	 * @param sdkDopayBo
	 * @return
	 */
	@RequestMapping(value = "/dopay",method = RequestMethod.POST)
	@ResponseBody
	public SdkPayResponse dopay(HttpServletRequest request,SdkDopayRequest sdkDopayBo){
//		logger.info("SDK支付请求参数：{}",sdkDopayBo);
		SdkPayResponse response = sdkPayService.dopay(sdkDopayBo);
//		logger.info("支付返回结果:{}",JSON.toJSONString(response));
		return response;
	}
	
	
	/**
	 * SDK信任支付
	 * @param request
	 * @param sdkDopayBo
	 * @return
	 */
	@RequestMapping(value = "/dotrustpay",method = RequestMethod.POST)
	@ResponseBody
	public SdkPayResponse dotrustpay(HttpServletRequest request,SdkTrustpayRequest trustPayBo){
		String requestIp = IPUtil.getIpAddr(request);
		trustPayBo.setRequestIp(requestIp);
		logger.info("信任支付请求参数:{}",JSON.toJSONString(trustPayBo));
		
		SdkPayResponse response = sdkPayService.dotrustpay(trustPayBo);
		logger.info("信任支付返回结果:{}",JSON.toJSONString(response));
		return response;
	}
	
	/**
	 * 发送短信验证码
	 * @param smsSend
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/sendSms")
	@ResponseBody
	public SdkPayResponse sendSms(SmsSend smsSend,HttpServletRequest request){
		String ip = IPUtil.getIpAddr(request);
		smsSend.setRequstIp(ip);
		logger.info("发送短信验证参数:{}",JSON.toJSONString(smsSend));
		SdkPayResponse response = sdkPayService.sendSms(smsSend);
		logger.info("发送短信验证返回结果",JSON.toJSONString(response));
		return response;
	}
	
	
	/**
	 * 用户支付信息参数解析
	 * @param request
	 * @return
	 */
	public SdkPayRequest payRequestParam(HttpServletRequest request){
		//接收参数
		String transType = request.getParameter("transType");//交易类型 默认 2012
		String appCode  = request.getParameter("appCode");//应用编码
		String orderNo = request.getParameter("orderNo");//业务流水号
		String userId = request.getParameter("userId");//用户ID
		String billingCode = request.getParameter("payCode");//计费点编码
		String appSource = request.getParameter("appSource");//应用渠道 0：IOS 1：Web端 2：Android
		String transIntro = request.getParameter("transIntro");//交易简介
		String payCallbackUrl = request.getParameter("payNotifyUrl");//异步通知URL
		String signCode = request.getParameter("signCode");//签名
		String signType = request.getParameter("signType");//签名
		
		//封装参数
		SdkPayRequest payRequest = new SdkPayRequest();
		payRequest.setTransType(transType);
		payRequest.setAppCode(appCode);
		payRequest.setOrderNo(orderNo);
		payRequest.setUserId(Long.valueOf(userId));
		payRequest.setBillingCode(billingCode);
		payRequest.setAppSource(Integer.parseInt(appSource));
		payRequest.setTransIntro(transIntro);
		payRequest.setPayCallbackUrl(payCallbackUrl);
		payRequest.setSignCode(signCode);
		payRequest.setSignType(signType);
		logger.info("获取支付信息参数:{}",JSON.toJSONString(payRequest));
		return payRequest;
				
				
	}

}
