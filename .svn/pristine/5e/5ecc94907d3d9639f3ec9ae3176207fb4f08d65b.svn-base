package com.qbao.sdk.server.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.util.ApiConnector;
import com.qbao.sdk.server.util.RedisUtil;
import com.qbao.sdk.server.util.rsa.RSACoder;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
	
	
	@Resource
	MerchantInfoMapper merchantInfoMapper;
	
	//获取应用信息key前缀
	static final String MERCHANT_PREFIX = "sdkServer:merchant_";

	@RequestMapping("/testPage")
	public String testJson() {
		
		return "index.page";
	}
	
//	
//	@RequestMapping("/dotrustpay")
//	public void dotrustpay(HttpServletRequest request,HttpServletResponse response){
//		String transType = request.getParameter("transType");
//		String appCode = request.getParameter("appCode");
//		String orderNo = request.getParameter("orderNo");
//		String userId = request.getParameter("userId");
//		String transAmount = request.getParameter("transAmount");
//		String appSource = request.getParameter("appSource");
//		String transIntro = request.getParameter("transIntro");
//		String payType = request.getParameter("payType");
//		String payNotifyUrl = request.getParameter("payNotifyUrl");
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append("transType=").append(transType).append(",")
//		.append("appCode=").append(appCode).append(",")
//		.append("orderNo=").append(orderNo).append(",")
//		.append("userId=").append(userId).append(",")
//		.append("transAmount=").append(transAmount).append(",")
//		.append("appSource=").append(appSource).append(",")
//		.append("payType=").append(payType).append(",")
//		.append("payNotifyUrl=").append(payNotifyUrl);
//		OpenMerchantEntity entity = getMerchantInfo(appCode);
//		String signCode = "";
//		try {
//			signCode = RSACoder.sign(sb.toString().getBytes(), entity.getPrivateKey());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
//		paramsList.add(new BasicNameValuePair("transType", transType));
//		paramsList.add(new BasicNameValuePair("appCode", appCode));
//		paramsList.add(new BasicNameValuePair("orderNo", orderNo));
//		paramsList.add(new BasicNameValuePair("userId", userId));
//		paramsList.add(new BasicNameValuePair("transAmount", transAmount));
//		paramsList.add(new BasicNameValuePair("appSource", appSource));
//		paramsList.add(new BasicNameValuePair("transIntro", transIntro));
//		paramsList.add(new BasicNameValuePair("payType", payType));
//		paramsList.add(new BasicNameValuePair("payNotifyUrl", payNotifyUrl));
//		paramsList.add(new BasicNameValuePair("signType", "RSA"));
//		paramsList.add(new BasicNameValuePair("signCode", signCode));
//		
//		String result = ApiConnector.post("http://sdkserver.qbao.com/sdkpay/v10/dotrustpay.html", paramsList);
//		log.info("response:{}",result);
//		PrintWriter out = null;
//		try {
//			response.setContentType("text/html;charset=utf-8");
//			 out = response.getWriter();
//			out.println(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(out != null){
//				out.flush();
//			    out.close();
//			}
//		}
//	}
//	
//	
//	private OpenMerchantEntity getMerchantInfo(String appCode) {
//		return merchantInfoMapper.getOpenMerchantEntityByAppCode(appCode);
//	}
//	

}
