package com.qbao.sdk.server.controller.pay;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.TurnableRuleRequest;
import com.qbao.sdk.server.bo.pay.TurnableRuleResponse;
import com.qbao.sdk.server.constants.ResultConstants;
import com.qbao.sdk.server.constants.TurnableRuleResponseEnum;
import com.qbao.sdk.server.service.pay.TurntableRuleService;
import com.qbao.sdk.server.util.IPUtil;

/**
 * 应用市场大转盘
 * @author liuxingyue
 *
 */
@Controller
@RequestMapping("/store/rule")
public class StoreTurntableRuleController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private TurntableRuleService turntableRuleService;
	
	@RequestMapping("/saveOrUpdateRule")
	@ResponseBody
	public TurnableRuleResponse saveOrUpdateRule(HttpServletRequest request,TurnableRuleRequest turnableRuleRequest){
		turnableRuleRequest.setRequestIp(IPUtil.getIpAddr(request));
		logger.info("应用市场大转盘请求参数：{}",JSON.toJSON(turnableRuleRequest));
		TurnableRuleResponse turnableRuleResponse = null;
		try {
			turnableRuleResponse = turntableRuleService.addOrUpdateRule(turnableRuleRequest);
		} catch (Exception e) {
			logger.error("添加或更新应用市场大转盘规则异常：id={},{}",turnableRuleRequest.getId(),e);
			turnableRuleResponse = new TurnableRuleResponse(ResultConstants.FAILED_BOOL,TurnableRuleResponseEnum.TURNTABLE_RULE_UPDATE_ERROR.getCode(),TurnableRuleResponseEnum.TURNTABLE_RULE_UPDATE_ERROR.getMsg());
		}
		return turnableRuleResponse;
	}
	
	@RequestMapping("/deleteRule")
	@ResponseBody
	public TurnableRuleResponse delRule(HttpServletRequest request,int id){
		String requestIp = IPUtil.getIpAddr(request);
		logger.info("删除规则id为==={}===的大转盘规则",id);
		TurnableRuleResponse turnableRuleResponse = turntableRuleService.delTurntableRule(id,requestIp);
		return turnableRuleResponse;
	}

}
