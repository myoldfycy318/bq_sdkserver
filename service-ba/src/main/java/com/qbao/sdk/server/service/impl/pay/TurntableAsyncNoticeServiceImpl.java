package com.qbao.sdk.server.service.impl.pay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.qbao.sdk.server.constants.RechargeConditionEnum;
import com.qbao.sdk.server.constants.RechargeTypeEnum;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayTransMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.TurntableRuleMapper;
import com.qbao.sdk.server.metadata.entity.pay.TurntablePayTransEntity;
import com.qbao.sdk.server.metadata.entity.pay.TurntableRuleEntity;
import com.qbao.sdk.server.service.pay.TurntableAsyncNoticeService;
import com.qbao.sdk.server.util.ApiConnector;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.util.PropertiesUtil;
/**
 * 应用市场大转盘根据规则和支付流水返回抽奖次数实现类
 * @author liuxingyue
 *
 */
@Service
public class TurntableAsyncNoticeServiceImpl implements TurntableAsyncNoticeService {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private TurntableRuleMapper turntableRuleMapper;
	
	@Resource
	private PayTransMapper payTransMapper;
	
	@Resource
	private PropertiesUtil domainConfig;
	
	@Override
	public void asyncNoticeAday() {
		Date date = new Date();
		String settleDate = getDateBefore(date,-1);//结算日期
		
		List<TurntableRuleEntity> rules = new ArrayList<TurntableRuleEntity>();
		rules = turntableRuleMapper.getRulesByDay(settleDate);
		log.info("应用市场大转盘规则(每日)：{}，时间：{}",JSON.toJSON(rules),settleDate);
		//执行异步通知
		this.doAscyNotice(rules,settleDate,settleDate);
		
	}


	@Override
	public void asyncNoticeAweek() {
		Date date = new Date();
		String endDate = getDateBefore(date,-1);
		String startDate = getDateBefore(date,-7);
		
		List<TurntableRuleEntity> rules = turntableRuleMapper.getRulesByPeriod(startDate,endDate,RechargeTypeEnum.EVERY_WEEK.getStatus());
		log.info("应用市场大转盘规则(每周)：{},时间{}-{}",JSON.toJSON(rules),startDate,endDate);
		//执行异步通知
		this.doAscyNotice(rules,startDate,endDate);
	}

	@Override
	public void asyncNoticeAmonth() {
		Date date = new Date();
		String endDate = getDateBefore(date,-1);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = DateUtils.toDateText(calendar.getTime(), "yyyyMMdd");
		
		List<TurntableRuleEntity> rules = turntableRuleMapper.getRulesByPeriod(startDate,endDate,RechargeTypeEnum.EVERY_MONTH.getStatus());
		log.info("应用市场大转盘规则(每月)：{},时间{}-{}",JSON.toJSON(rules),startDate,endDate);
		//执行异步通知
		this.doAscyNotice(rules,startDate,endDate);
		
	}


	@Override
	public void asyncNoticeAperiod() {
		Date date = new Date();
		String endDate = getDateBefore(date,-1);
		List<TurntableRuleEntity> rules = turntableRuleMapper.getRulesByTimePeriod(endDate,RechargeTypeEnum.TIME_PERIOD.getStatus());
		log.info("应用市场大转盘规则(每周)：{},时间{}",JSON.toJSON(rules),endDate);
		//执行异步通知
		this.doAscyNotice(rules,"","");
		
	}
	
	private static String getDateBefore(Date date,int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, day);
		date = calendar.getTime();
		String dateBefore = DateUtils.toDateText(date, "yyyyMMdd");
		return dateBefore;
	}
	
	/**
	 * 将信息发送到应用市场
	 * @param userId 用户id
	 * @param times 赠送的抽奖次数
	 */
	private boolean sendToStore(String userId,int times){
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userId", userId));
		pairs.add(new BasicNameValuePair("times", times+""));
		log.info("异步请求发送到应用市场，userId={},times={}",userId,times);
		String response = ApiConnector.post(domainConfig.getString("store.turntable.rule"), pairs);
		log.info("发送用户{}的抽奖次数到应用市场返回结果：{}",userId,response);
		if(StringUtils.isBlank(response)){
			return false;
		}
		return true;
	}
	
	/**
	 * 执行异步通知
	 * @param rules
	 * @param startDate
	 * @param endDate
	 */
	private void doAscyNotice(List<TurntableRuleEntity> rules,String startDate,String endDate){
		int number = 0;
		int coinVal = 0;
		int bqVal = 0;
		if(!CollectionUtils.isEmpty(rules)){
			for (TurntableRuleEntity rule : rules) {
				String appCode = rule.getRechargeGameIds();
				if(rule.getRechargeType().toString().equals(RechargeTypeEnum.TIME_PERIOD.getStatus())){
					startDate = DateUtils.toDateText(rule.getRechargeStartTime(), "yyyyMMdd");
					endDate = DateUtils.toDateText(rule.getRechargeEndTime(), "yyyyMMdd");
				}else if(rule.getRechargeType().toString().equals(RechargeTypeEnum.EVERY_MONTH.getStatus())){//每月
					String startRuleDate = DateUtils.toDateText(rule.getStartTime(), "yyyyMMdd");
					String endRuleDate = DateUtils.toDateText(rule.getEndTime(), "yyyyMMdd");
					startDate = (Integer.valueOf(startDate) >= Integer.valueOf(startRuleDate))?startDate:startRuleDate;
					endDate = (Integer.valueOf(endDate) <= Integer.valueOf(endRuleDate))?endDate:endRuleDate;
				}
				List<TurntablePayTransEntity> entities = payTransMapper.getTurntablePayRequest(appCode, startDate, endDate, startDate.substring(0,6));
			
				BigDecimal coinAmount = rule.getRechargeCoinAmount()== null ? new BigDecimal("0"):rule.getRechargeCoinAmount();
				BigDecimal bqAmount = rule.getRechargeBqAmount()==null ? new BigDecimal("0"):rule.getRechargeBqAmount();			
				String condition = rule.getRechargeCondition().toString();//条件类型：0 >=、1 =、2 >.
				for(TurntablePayTransEntity entity : entities){
					BigDecimal accountAmount = entity.getAccountAmount();
					BigDecimal bqAccountAmount = entity.getBqAccoutAmount();
					number = rule.getNumber();
					coinVal = accountAmount.compareTo(coinAmount);
					bqVal = bqAccountAmount.compareTo(bqAmount);
					BigDecimal zero = new BigDecimal(0);
					if(condition.equals(RechargeConditionEnum.GREATER_THAN.getStatus())){// >
						if(coinAmount.compareTo(zero)!=0){
							if(coinVal != 1){
								number = 0;
							}
						}
						if(bqAmount.compareTo(zero)!=0){
							if(bqVal != 1){
								number = 0;
							}
						}
					}else if(condition.equals(RechargeConditionEnum.EQUAL_TO.getStatus())){// =
						if(coinAmount.compareTo(zero)!=0){
							if(coinVal != 0){
								number = 0;
							}
						}
						if(bqAmount.compareTo(zero)!=0){
							if(bqVal != 0){
								number = 0;
							}
						}		
					}else if(condition.equals(RechargeConditionEnum.GREATER_THAN_OR_EQUAL_TO.getStatus())){// >=
						if(coinVal == -1 || bqVal == -1){
							number = 0;
						}
					}
					
					//结果发送给应用市场
					this.sendToStore(entity.getPayUserId(),number);
				}
			}
		}
	}

}
