package com.qbao.sdk.server.controller.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.PayRequestResponse;
import com.qbao.sdk.server.bo.pay.QueryParam;
import com.qbao.sdk.server.constants.BusResponeEnum;
import com.qbao.sdk.server.constants.TransStatusEnum;
import com.qbao.sdk.server.constants.TransTypeEnum;
import com.qbao.sdk.server.service.pay.QueryService;
import com.qbao.sdk.server.util.IPUtil;

@Controller
@RequestMapping("/api")
public class SdkApiController {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	QueryService queryService;
	/**
	 * 单笔查询接口
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryrequest")
	@ResponseBody
	public Object queryRequest(HttpServletRequest request){
		String orderNo = request.getParameter("orderNo");
		String appCode = request.getParameter("appCode");
		String queryType = request.getParameter("queryType");
		String transType = request.getParameter("transType");
		String transDate = request.getParameter("transDate");
		String signType = request.getParameter("signType");
		String signCode = request.getParameter("signCode");
		
		QueryParam queryParam = new QueryParam();
		queryParam.setOrderNo(orderNo);
		queryParam.setAppCode(appCode);
		queryParam.setQueryType(queryType);
		queryParam.setTransType(transType);
		queryParam.setTransDate(transDate);
		queryParam.setSignType(signType);
		queryParam.setSignCode(signCode);
		queryParam.setRequestIp(IPUtil.getIpAddr(request));
		
		log.info("商户查询的参数为:{}",JSON.toJSON(queryParam));
		
		Map<String ,Object> map = new HashMap<String, Object>(); 
		
		//支付
		if(TransTypeEnum.TRANS_TYPE_PAY.getTransCode().equals(queryParam.getQueryType())){
			PayRequestResponse payRequest = queryService.getPayRequest(queryParam);
			if(!payRequest.isSuccess()){
				map = getQueryNullMap(queryParam,payRequest.getCode(),payRequest.getMsg());
			}else{
				map = queryPayMap(payRequest);
			}
		}
		
		log.info("商户查询的参数为:{}",JSON.toJSON(map));
		
		 return map;
	}
	
	/**
	 * 获取支付交易的返回值
	 * 
	 * @param refundRequest
	 * @return
	 */
	private Map<String, Object> queryPayMap(PayRequestResponse payRequest){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", BusResponeEnum.QUERY_IS_OK.getResponeCode());
		map.put("msg", BusResponeEnum.QUERY_IS_OK.getResponeMsg());
		map.put("qbTransNum", payRequest.getQbTransNum());
		map.put("transType", payRequest.getTransType());
		map.put("appCode", payRequest.getAppCode());
		map.put("orderNo", payRequest.getOrderNo());
		map.put("transTime", payRequest.getTransTime());
		map.put("transIntro", payRequest.getTransIntro());
		map.put("transAmount", payRequest.getTransAmount());
		if(payRequest.getStatus().equals(TransStatusEnum.PAY_TRANS_ACCEPT_SUCCESS.getStatus())){
			map.put("status", "受理中");
		}else if(payRequest.getStatus().equals(TransStatusEnum.PAY_TRANS_SUCCESS.getStatus())){
			map.put("status", "支付成功");
		}else{
			map.put("status", "支付失败");
		}
		return map;
		
	}
	
	/**
	 * 查询结果为空的返回值
	 * 
	 * @return
	 */
	private Map<String, Object> getQueryNullMap(QueryParam queryParam,String code,String msg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("signType", queryParam.getSignType());
		map.put("signCode", queryParam.getSignCode());
		map.put("tansType", queryParam.getTransType());
		map.put("queryType", queryParam.getQueryType());
		map.put("orderNo", queryParam.getOrderNo());
		map.put("appCode", queryParam.getAppCode());
		map.put("transDate", queryParam.getTransDate());
		map.put("code", code);
		map.put("msg", msg);
		return map;
	}

}
