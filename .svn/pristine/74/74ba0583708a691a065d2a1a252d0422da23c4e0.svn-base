/**
 * 
 */
package com.qbao.sdk.server.controller.pay;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.ChargePointInfo;
import com.qbao.sdk.server.bo.pay.OpenMerchant;
import com.qbao.sdk.server.bo.pay.PayConfigInfo;
import com.qbao.sdk.server.controller.BaseController;
import com.qbao.sdk.server.service.pay.OpenChargingPointService;
import com.qbao.sdk.server.service.pay.OpenMerchantService;
import com.qbao.sdk.server.service.pay.OpenPayConfigService;
import com.qbao.sdk.server.util.RedisUtil;

/**
 * @author mazhongmin
 * 接收开放平台信息API
 */
@Controller
@RequestMapping("open/recive/api")
public class OpenReciveApiController extends BaseController{

	@Resource
	OpenPayConfigService openPayConfigService;
	
	@Resource
	OpenMerchantService openMerchantService;
	
	@Resource
	OpenChargingPointService openChargingPointService;
	
	@Resource
	private RedisUtil redisUtil;
	
	//获取应用信息key前缀
	static final String MERCHANT_PREFIX = "sdkServer:merchant_";
	
	
	/**
	 * 接受开放平台商户信息
	 * 
	 * @param response
	 * @param openMerchant
	 */
	@RequestMapping("/merchant")
	@ResponseBody
	public void receOpenMerchant(HttpServletResponse response,OpenMerchant openMerchant){
		log.info("接受开放平台的商户信息:{}",JSON.toJSON(openMerchant));
		boolean flag = true;
		//插入开放平台商户信息
		try {
			openMerchantService.insertOpenMerchant(openMerchant);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("插入开放平台商户信息失败:{}",e);
			flag = false;
		}
		PrintWriter out = null;
		if(flag){
			try {
				redisUtil.set(MERCHANT_PREFIX+openMerchant.getAppCode(),JSON.toJSONString(openMerchant));
				out = response.getWriter();
				log.info("success");
				out.println("success");
			} catch (Exception e) {
				log.info("将商户放入开放平台失败:{}",e);
				log.info("fail");
				out.println("fail");;
			}finally{
				if (out != null) {
					out.flush();
				    out.close();
				}
			}
		}
	}
	
	
	/**
	 * 接收开放平台支付配置信息
	 * @param request
	 */
	@RequestMapping("/payconfig")
	@ResponseBody
	public Map<String,Object> payConfig(HttpServletResponse response,PayConfigInfo payConfig){
		boolean flag = true;
		try{
			openPayConfigService.insertOpenPayConfig(payConfig);
		}catch(Exception e){
			log.error("插入开放平台支付配置信息失败:{}",e);
			flag = false;
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(flag){
			resultMap.put("success", true);
		}else{
			resultMap.put("success", false);
		}
		return resultMap;
	}
	
	
	/**
	 * 接收开放平台计费点信息
	 * @param chargePointInfo
	 * @return
	 */
	@RequestMapping("/chargingPoint")
	@ResponseBody
	public Map<String,Object> chargingPoint(ChargePointInfo chargePointInfo){
		boolean flag = true;
		try{
			openChargingPointService.insertChargingPoint(chargePointInfo);
		}catch(Exception e){
			e.printStackTrace();
			log.error("接收开放平台计费点信息失败:{}",e);
			flag = false;
		}
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(flag){
			resultMap.put("success", true);
		}else{
			resultMap.put("success", false);
		}
		return resultMap;
	}
}
