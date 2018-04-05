package com.qbao.sdk.server.service.impl.pay;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.bo.pay.TurnableRuleRequest;
import com.qbao.sdk.server.bo.pay.TurnableRuleResponse;
import com.qbao.sdk.server.constants.ResultConstants;
import com.qbao.sdk.server.constants.SignConstants;
import com.qbao.sdk.server.constants.TurnableRuleResponseEnum;
import com.qbao.sdk.server.metadata.dao.mapper.pay.TurntableRuleMapper;
import com.qbao.sdk.server.metadata.entity.pay.TurntableRuleEntity;
import com.qbao.sdk.server.service.pay.TurntableRuleService;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.util.PropertiesUtil;
/**
 * 应用市场大转盘规则服务接口实现类
 * @author liuxingyue
 *
 */
@Service
public class TurntableRuleServiceImpl implements TurntableRuleService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private TurntableRuleMapper turntableRuleMapper;
	
	@Resource
	PropertiesUtil domainConfig;
	
	@Override
	public TurnableRuleResponse delTurntableRule(int id,String requestIp) {
		//ip白名单校验
		TurnableRuleResponse turnableRuleResponse = valRequestIp(requestIp);
		if(!turnableRuleResponse.isSuccess()){
			return turnableRuleResponse;
		}
		try {
			turntableRuleMapper.deleteTurntableRuleById(id);
			logger.info("应用市场大转盘规则删除成功,规则id:{}",id);
			return new TurnableRuleResponse(ResultConstants.SUCCESS_BOOL,TurnableRuleResponseEnum.GET_TURNTABLE_RULE_SUCCESS.getCode(),TurnableRuleResponseEnum.GET_TURNTABLE_RULE_SUCCESS.getMsg());
		} catch (Exception e) {
			logger.info("应用市场大转盘规则删除失败,规则id:{},失败原因:{}",id,e);
			return new TurnableRuleResponse(ResultConstants.FAILED_BOOL,TurnableRuleResponseEnum.TURNTABLE_RULE_DEL_ERROR.getCode(),TurnableRuleResponseEnum.TURNTABLE_RULE_DEL_ERROR.getMsg());
		}

	}

	@Transactional
	@Override
	public TurnableRuleResponse addOrUpdateRule(TurnableRuleRequest turnableRuleRequest) {
		//ip白名单校验
		TurnableRuleResponse turnableRuleResponse = valRequestIp(turnableRuleRequest.getRequestIp());
		if(!turnableRuleResponse.isSuccess()){
			return turnableRuleResponse;
		}
		//参数校验
		
		if(!paramVal(turnableRuleRequest)){
			return new TurnableRuleResponse(ResultConstants.FAILED_BOOL,TurnableRuleResponseEnum.TURNTABLE_RULE_PARAM_ILLEGAL.getCode(),TurnableRuleResponseEnum.TURNTABLE_RULE_PARAM_ILLEGAL.getMsg());
		}
		turntableRuleMapper.deleteTurntableRuleById(turnableRuleRequest.getId());
		turnableRuleResponse = addRule(turnableRuleRequest);
		return turnableRuleResponse;
	}
	
	private TurnableRuleResponse addRule(TurnableRuleRequest turnableRuleRequest){
		String[] appCode = turnableRuleRequest.getRechargeGameIds().split(SignConstants.COMMA);
		
		List<TurntableRuleEntity> list = new ArrayList<TurntableRuleEntity>();
		for (int i = 0; i < appCode.length; i++) {
			TurntableRuleEntity turntableRuleEntity = new TurntableRuleEntity();
			BeanUtils.copyProperties(turnableRuleRequest, turntableRuleEntity);
			turntableRuleEntity.setStartTime(DateUtils.getDate(turnableRuleRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			turntableRuleEntity.setEndTime(DateUtils.getDate(turnableRuleRequest.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			turntableRuleEntity.setRechargeStartTime(DateUtils.getDate(turnableRuleRequest.getRechargeStartTime(), "yyyy-MM-dd HH:mm:ss"));
			turntableRuleEntity.setRechargeEndTime(DateUtils.getDate(turnableRuleRequest.getRechargeEndTime(), "yyyy-MM-dd HH:mm:ss"));
			turntableRuleEntity.setRechargeGameIds(appCode[i]);
			list.add(turntableRuleEntity);
		}
		try {
			turntableRuleMapper.insertTurntableRule(list);
			return new TurnableRuleResponse(ResultConstants.SUCCESS_BOOL,TurnableRuleResponseEnum.GET_TURNTABLE_RULE_SUCCESS.getCode(),TurnableRuleResponseEnum.GET_TURNTABLE_RULE_SUCCESS.getMsg());
		} catch (Exception e) {
			logger.info("应用市场大转盘规则操作数据库失败,规则:{},失败原因:{}",JSON.toJSON(turnableRuleRequest),e);
			throw new RuntimeException("规则添加异常");
		}
	}
	
	/**
	 * ip白名单校验
	 * @param requestIp
	 * @return
	 */
	private TurnableRuleResponse valRequestIp(String requestIp){
		/* 注释操作应用市场大转盘规则ip白名单校验
		String ips = domainConfig.getString("store.ips");
		if(ips.indexOf(requestIp) == -1){
			return new TurnableRuleResponse(ResultConstants.FAILED_BOOL,TurnableRuleResponseEnum.IP_ERROR.getCode(),TurnableRuleResponseEnum.IP_ERROR.getMsg());
		}
		*/
		return new TurnableRuleResponse(ResultConstants.SUCCESS_BOOL,"","");
	}
	
	/**
	 * 参数校验
	 * @param turnableRuleRequest
	 * @return
	 */
	private boolean paramVal(TurnableRuleRequest turnableRuleRequest){
		boolean flag = true;
		if(turnableRuleRequest.getId()==null){
			return false;
		}
		if(turnableRuleRequest.getRechargeType()==null || turnableRuleRequest.getRechargeCondition() == null){
			return false;
		}
		if(StringUtils.isBlank(turnableRuleRequest.getName()) || StringUtils.isBlank(turnableRuleRequest.getRechargeCondition().toString())){
			return false;
		}
		if(StringUtils.isBlank(turnableRuleRequest.getEndTime()) || StringUtils.isBlank(turnableRuleRequest.getStartTime())){
			return false;
		}
		return flag;
	}

}
