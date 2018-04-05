/**
 * 
 */
package com.qbao.sdk.server.service.impl.pay;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.bo.pay.*;
import com.qbao.sdk.server.constants.*;
import com.qbao.sdk.server.constants.stastics.Constants.BusinessType;
import com.qbao.sdk.server.metadata.dao.mapper.pay.*;
import com.qbao.sdk.server.metadata.entity.pay.*;
import com.qbao.sdk.server.service.login.ThridRequestService;
import com.qbao.sdk.server.service.pay.SdkPayService;
import com.qbao.sdk.server.service.redis.RedisService;
import com.qbao.sdk.server.shangsu.TradeItem;
import com.qbao.sdk.server.shangsu.TradeResponse;
import com.qbao.sdk.server.util.*;
import com.qbao.sdk.server.util.rsa.RSACoder;
import com.qbao.sdk.server.util.shangsu.RSAUtil;
import com.qbao.sdk.server.util.shangsu.YanSuCommonUtil;
import com.qbao.sdk.server.view.SmsSendResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author mazhongmin
 * SDK支付服务接口
 *
 */
@Service("sdkPayService")
public class SdkPayServiceImpl implements SdkPayService{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	PropertiesUtil payConfig;
	
	@Resource
	private PayTransMapper payTransMapper;
	
	@Resource
	private PayConfigMapper payConfigMapper;
	
	@Resource
	private ChargePointMapper chargePointMapper;
	
	@Resource
	private PayAsyncNoticeMapper payAsyncNoticeMapper;
	
	@Resource
	private TurntableRuleMapper turntableRuleMapper;
	
	@Resource
	private RedisUtil redisUtil;
	
	@Resource
	private SMSUtils smsUtils;
	
	@Resource
	RedisService redisService;
	
	@Resource
	private PropertiesUtil domainConfig;
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Autowired
	private CurrencyEntity currency;

    @Autowired
    private ThridRequestService thridRequestService;
	
	//获得钱宝流水id前缀
	static final String SDK_FLOWID = "sdkServer:paying_";
	
	/** 应用市场VIP等级 */
	public static final String SDK_STORE_VIP = "SdkServer:Store:Vip";
	
	public static final String SDK_PAY_TRANS_QUEUE_KEY="sdk_pay_trans_queue_key";
	
	@Override
	public SdkPayResponse dopay(SdkDopayRequest sdkDopayBo) {
		
		/*参数非空校验*/
		if(isBlank(sdkDopayBo)){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PARAM_BLANK.getCode(),PayResEnum.PARAM_BLANK.getMsg());
		}
		
		//判断商户是否存在
		OpenMerchantEntity merchantEntity = redisService.getMerchantInfo(sdkDopayBo.getAppCode());
		if(merchantEntity == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getMsg(), null);
		}
		
		/**参数解密，解密结果为流水id、交易密码、交易日期***/
		String transPassWord = sdkDopayBo.getTransPassWord();
		String privateKey = payConfig.getString("client.private.key");
		byte[] rsaData = null;
		try {
			rsaData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(transPassWord), privateKey);
		} catch (Exception e) {
			logger.error("参数{}解密异常:{}",transPassWord,e);
		}
		String result = rsaData == null ? null : new String(rsaData);
//		logger.info("解密后参数:{}",JSON.toJSONString(result));
		
		String sdkflowId = ""; //流水id
		String password = "";  //交易密码
		String transDate = ""; //接单日期
		try{
			String[] array = result.split(SignConstants.COMMA);
			sdkflowId = array[0];
			password = array[1];
			transDate = array[2];
		}catch(NullPointerException e) {
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.DECRYPT_ERROR.getCode(),PayResEnum.DECRYPT_ERROR.getMsg());
		}catch(ArrayIndexOutOfBoundsException e) {
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PARAM_ILLEGAL.getCode(),PayResEnum.PARAM_ILLEGAL.getMsg());
		}
		 
		String suffix = transDate.substring(0, 6);

		/*根据流水id查询流水信息*/
		PayTransEntity entity = queryEntityById(sdkflowId,suffix);
		if(entity == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
					PayResEnum.TRANS_NO_EXISTS.getCode(), PayResEnum.TRANS_NO_EXISTS.getMsg());
		}

		// 支付信息超时验证
		if(!valAuthTime(new Date(),entity.getCreateTime(),AUTH_TIME_OUT)){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
					PayResEnum.TIMESTAMP_ILLEGAL.getCode(), PayResEnum.TIMESTAMP_ILLEGAL.getMsg());
		}
		
		/******短信验证*******/
		String varValue = redisService.getGlobalVarByType("10");	
		BigDecimal totalAmount = entity.getTransAmount();
		BigDecimal top = new BigDecimal(varValue);
        UserInfo userInfo = thridRequestService.loadUserInfo(entity.getPayUserId());
        if(null == userInfo) {
            //不存在该用户
            return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
                    , PayResEnum.USE_NOT_EXIST.getCode()
                    , PayResEnum.USE_NOT_EXIST.getMsg(), null);
        }
		if(top.compareTo(totalAmount) != 1){
			String verifyCode = sdkDopayBo.getMsgCode();
			if(StringUtils.isBlank(verifyCode)){
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
						PayResEnum.INPUT_VERIFY_CODE.getCode(), PayResEnum.INPUT_VERIFY_CODE.getMsg());
			}

			SmsSendResult smsResult = smsUtils.verifySmsCode(userInfo.getMobile(), verifyCode);
			if(!SmsStatusEnum.SEND_SMS_OK.getStatus().equals(smsResult.getStatus())){
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						smsResult.getStatus(),smsResult.getDesc());
			}
		}
		
		long payUserId = entity.getPayUserId();  //用户id
		String appCode = entity.getAppCode(); //应用编码
		//根据应用编码查询支付配置
		PayConfigEntity payConfig = getPayConfig(appCode);
		String bqFlag = sdkDopayBo.getUseBqFlag(); //是否支持宝券  0-不支持  1-支持
		
		Long voucher = null; //宝券余额
		if("1".equals(bqFlag)){
			if(payConfig.getBqFlag() != 1){
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.CANT_NOT_USE_VOUCHER.getCode(),PayResEnum.CANT_NOT_USE_VOUCHER.getMsg());
			}
			
			/*******查询宝券余额*************/
//			voucher = queryVoucher(payUserId);
            voucher = queryUserVoucher(payUserId);
			if(voucher == null){
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.VOUCHER_QUERY_ERROR.getCode(),PayResEnum.VOUCHER_QUERY_ERROR.getMsg());
			}
		}
		
		/************非认证商家，判断绑定银行卡**********/
		int certifiedStatus = payConfig.getCertifiedStatus();  //是否认证商家  0-否  1-是
		if(0 == certifiedStatus) {
			if(!isBindBankCard(payUserId)) {
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.NO_BIND_BANK_CARD.getCode(),PayResEnum.NO_BIND_BANK_CARD.getMsg());
			}
		}
		
		/********设置交易密码校验*********/
		if(!existTradePwd(payUserId)){
			return  new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PAY_PSW_NO_EXISTS.getCode(),PayResEnum.PAY_PSW_NO_EXISTS.getMsg());
		}
		
		/********手机绑定校验***********/
		if(StringUtils.isBlank(userInfo.getMobile())){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.NO_BIND_MOBILE.getCode(),PayResEnum.NO_BIND_MOBILE.getMsg());
		}

		/*******交易密码校验*********/
		Integer pwdCode = checkPwd(payUserId,password,entity.getLoginName());
		if(pwdCode == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PWD_CHECK_NET_ERROR.getCode(),PayResEnum.PWD_CHECK_NET_ERROR.getMsg());
		}else if(0 == pwdCode){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PWD_CHECK_ERROR.getCode(),PayResEnum.PWD_CHECK_ERROR.getMsg());
		}else if(2 == pwdCode){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PWD_CHECK_DEADLINE.getCode(),PayResEnum.PWD_CHECK_DEADLINE.getMsg());
		}
		if(pwdCode != 1){  //返回code只有1才是成功
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.PWD_CHECK_UNKNOW.getCode(),PayResEnum.PWD_CHECK_UNKNOW.getMsg());
		}

		String sdkflowIdJson = redisUtil.get(SDK_FLOWID + sdkflowId);
		/*******查询该笔交易是否正在支付中**********/
		if(StringUtils.isNotBlank(sdkflowIdJson)){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), PayResEnum.SDKFLOWID_IS_EXIST.getCode(), PayResEnum.SDKFLOWID_IS_EXIST.getMsg());
		}
		
		//放入缓存
		redisUtil.setex(SDK_FLOWID + sdkflowId, 30, "正在支付中......");
		
		/** 校验结束,进行支付**/
		SdkPayResponse response = dopayBiz(entity, voucher, certifiedStatus, suffix);
		
		redisUtil.del(SDK_FLOWID + sdkflowId);
		
		//异步通知
		asyncNotice(response, entity, suffix);
		
		//应用市场大转盘单笔充值返回抽奖次数异步通知
		String responseCode = response.getResponseCode();
		if(PayResEnum.SUCCESS_CODE.getCode().equals(responseCode)){
			//若支付成功
			try {
				this.resRuleTimes(entity);
			} catch (Exception e) {
				logger.error("单笔充值规则查询异常，异常原因：{}",e);
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.TURNTABLE_RULE_QUERY_ERROR.getCode(),PayResEnum.TURNTABLE_RULE_QUERY_ERROR.getMsg());
			}
			
		}
		return response;
			
	}
	private void resRuleTimes(PayTransEntity entity){
		//查询规则
		TurntableRuleEntity rule = null;
		rule = turntableRuleMapper.getRulesBySingle(entity.getUpdateTime(),entity.getAppCode());
//		logger.info("单笔充值规则：{},appCode={},支付时间：{}",rule,entity.getAppCode(),entity.getCreateTime());
		//异步通知应用市场
		doAscyNotice(rule,entity);
	}
	
	@Override
	public SdkPayResponse dealWithPayRequest(SdkPayRequest payRequest) {
		String status = "";
		
		OpenMerchantEntity merchantEntity = redisService.getMerchantInfo(payRequest.getAppCode());
		
		if(merchantEntity == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getMsg(), null);
		}
		
		Date now = new Date();
		//根据appcode和bizcode确定该流水是否存在
		PayTransEntity entity = payTransMapper.getPayTransReqByMertCodeTransCode(payRequest.getAppCode(), payRequest.getOrderNo(),DateUtils.toDateText(now, "yyyyMM"));
		if(entity != null){
			//该流水已存在
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.TRANS_INFO_IS_EXIST.getCode()
					, PayResEnum.TRANS_INFO_IS_EXIST.getMsg(), null);
		}
		
		//签名校验
		boolean signCodeFlag = false;
		if(SignTypeEnum.SIGN_RSA.getValue().equals(payRequest.getSignType())){//RSA
			String sign = this.appendParam(payRequest);
			String signCode = payRequest.getSignCode();
			
			String publicKey = merchantEntity.getPublicKey();
			
			//RSA公钥验签
			try {
				signCodeFlag = RSACoder.verify(sign.getBytes(), publicKey, signCode);
			} catch (Exception e) {
				logger.error("签名校验异常:{}",e);
			}
		}
		if(!signCodeFlag){
			//签名信息不合法
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
						, PayResEnum.SIGN_CODE_ILLEGAL.getCode()
						, PayResEnum.SIGN_CODE_ILLEGAL.getMsg()
						, null);
		}
		
		//查询计费点
		ChargePointEntity chargeEntity = chargePointMapper.selectByCode(payRequest.getBillingCode(),payRequest.getAppCode());
		if(chargeEntity == null){
//			logger.error("该计费点不存在，计费点ID：{},应用编码：{}",payRequest.getBillingCode(),payRequest.getAppCode());
			 return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
						, PayResEnum.CHARGE_POINT_NO_EXISTS.getCode()
						, PayResEnum.CHARGE_POINT_NO_EXISTS.getMsg()
						, null);
		}
		
		//判断是否存在该用户
		UserInfo userInfo =  thridRequestService.loadUserInfo(payRequest.getUserId());
		if(null == userInfo){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
										, PayResEnum.USE_NOT_EXIST.getCode()
										, PayResEnum.USE_NOT_EXIST.getMsg(), null);
		}
		
		//校验通过,支付请求受理成功
		status = TransStatusEnum.PAY_TRANS_ACCEPT_SUCCESS.getStatus();
		
		BigDecimal chargeAmount = chargeEntity.getChargePointAmount();
		if(StringUtils.isBlank(payRequest.getTransIntro())){
			payRequest.setTransIntro(chargeEntity.getChargePointName());
		}
		PayTransEntity payTransEntity = this.addPayTrans(payRequest,status,userInfo.getUsername(),chargeAmount,merchantEntity.getMerchantUserId(),now);
		
		//是否短信验证
		String globalVarValue = redisService.getGlobalVarByType("10");	
		BigDecimal top = new BigDecimal(globalVarValue);
		String msgFlag = (top.compareTo(chargeAmount) == 1)
						? PayResEnum.NO_SEND_MSG.getCode() : PayResEnum.SEND_MSG.getCode();
						
	    //是否可以使用宝券
		PayConfigEntity payConfigEntity = getPayConfig(payRequest.getAppCode());
		Integer useBqFlag = payConfigEntity.getBqFlag();
		
		//获取用户可用宝券余额
//		Long bqAmount = queryVoucher(payTransEntity.getPayUserId());
		Long bqAmount = queryUserVoucher(payTransEntity.getPayUserId());
		if(bqAmount == null){
			logger.error("支付调用宝券系统查询宝券余额异常,支付用户ID：{}",payTransEntity.getPayUserId());
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.VOUCHER_QUERY_ERROR.getCode()
					, PayResEnum.VOUCHER_QUERY_ERROR.getMsg(), null);
		}
		//获取用户可用宝币余额
//		Long qbAmount = queryBalance(payTransEntity,payConfigEntity.getCertifiedStatus());
		Long qbAmount = queryUserBalance(payTransEntity, payConfigEntity.getCertifiedStatus());
		if(qbAmount == null){
			logger.error("支付调用账户系统查询可用宝币余额异常,支付用户ID：{}",payTransEntity.getPayUserId());
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.ACCOUNT_QUERY_ERROR.getCode()
					, PayResEnum.ACCOUNT_QUERY_ERROR.getMsg(), null);
		}
		
		SdkPayRequestData bo = new SdkPayRequestData();
		bo.setSdkflowId(payTransEntity.getPayTransId().toString());
		bo.setBqAmount(bqAmount);
		bo.setQbAmount(qbAmount);
		bo.setMsgFlag(Integer.parseInt(msgFlag));
		bo.setUseBqFlag(useBqFlag);
		bo.setTransDate(payTransEntity.getTransDate());
		bo.setPropName(chargeEntity.getChargePointName());
		bo.setPropPrice(chargeEntity.getChargePointAmount().intValue());
		bo.setCurrency(currency);
		return new SdkPayResponse(PayResEnum.SUCCESS_CODE.getCode(), "", "", bo);
	}
	
	
	@Override
	public SdkPayResponse sendSms(SmsSend smsSend) {
		
		if(isBlank(smsSend)){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
					PayResEnum.PARAM_BLANK.getCode(), PayResEnum.PARAM_BLANK.getMsg());
		}
		/*根据流水id查询流水信息*/
		String bizId = smsSend.getSdkflowId();
		String transDate = smsSend.getTransDate();
		PayTransEntity entity = queryEntityById(bizId,transDate.substring(0,6));
		if(entity == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
					PayResEnum.TRANS_NO_EXISTS.getCode(), PayResEnum.TRANS_NO_EXISTS.getMsg());
		}
		
		String globalVarValue = redisService.getGlobalVarByType("10");	
		BigDecimal top = new BigDecimal(globalVarValue);
		if(top.compareTo(entity.getTransAmount()) == 1){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(), 
					PayResEnum.NO_SEND_SMS.getCode(), PayResEnum.NO_SEND_SMS.getMsg());
		}
        UserInfo userInfo =thridRequestService.loadUserInfo(entity.getPayUserId());
        //验证是否绑定手机号
		if(userInfo == null || StringUtils.isBlank(userInfo.getMobile())){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.NO_BIND_MOBILE.getCode(),PayResEnum.NO_BIND_MOBILE.getMsg());
		}
		String ip = smsSend.getRequstIp();
		SmsSendResult smsResult = smsUtils.generateAndSaveVerifyCode(userInfo.getMobile(), ip);
		if(!SmsStatusEnum.SEND_SMS_OK.getStatus().equals(smsResult.getStatus())){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					smsResult.getStatus(),smsResult.getDesc());
		}
		
		return new SdkPayResponse(PayResEnum.SUCCESS_CODE.getCode());
	}
	
	/**
	 * 调用宝券、账户系统进行扣减
	 * @param entity
	 * @param voucher
	 * @param certifiedStatus
	 * @param suffix
	 * @return
	 */
	private SdkPayResponse dopayBiz(PayTransEntity entity,Long voucher,int certifiedStatus,String suffix){
		//声明支付返回结果
		DopayResult data = new DopayResult(entity.getPayTransId(),entity.getBizCode());
		String status = entity.getStatus(); // 支付状态
//		logger.info("当前流水的交易状态:" + status);
		//流水已经支付成功
		if(TransStatusEnum.PAY_TRANS_SUCCESS.getStatus().equals(status)) {
			return new SdkPayResponse(PayResEnum.SUCCESS_CODE.getCode(),null,null,data);
		}
		
		//流水受理成功状态才能支付
		if(TransStatusEnum.PAY_TRANS_ACCEPT_SUCCESS.getStatus().equals(status)) {
			this.calculate(entity, voucher);
			SdkPayResponse response = null;
//			AccountResult trade = doBizTrade(entity,certifiedStatus);
            TradeResponse tradeResponse = doTrade(entity,certifiedStatus);
            status = TransStatusEnum.PAY_TRANS_FAILED.getStatus();
			if(tradeResponse == null){
				response = new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.TRADE_NET_ERROR.getCode(),PayResEnum.TRADE_NET_ERROR.getMsg(),data);
			}else{
				 if("100000".equals(tradeResponse.getRespCode())){
					entity.setAccountFlowId(tradeResponse.getPayTradeNo());
                     status = TransStatusEnum.PAY_TRANS_SUCCESS.getStatus();
					response = new SdkPayResponse(PayResEnum.SUCCESS_CODE.getCode(),null,null,data);
				}else{
					response = new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
							PayResEnum.TRADE_UNKNOW.getCode(),tradeResponse.getRespMsg(),data);
				}
			}
			entity.setStatus(status);
			
			//设置会员等级
			String memberLevel = queryVipFromStore(entity.getPayUserId());
			if(StringUtils.isNotEmpty(memberLevel)){
				entity.setMemberLevel(memberLevel);
			}
		
			
			if(!updatePayTrans(entity,suffix)){ //更新流水状态失败
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.UPDATE_TRANS_ERROR.getCode(),PayResEnum.UPDATE_TRANS_ERROR.getMsg());
			}
			//将成功的消费流水发送到mq队列
			if(entity.getStatus().equals(TransStatusEnum.PAY_TRANS_SUCCESS.getStatus())){
				try{
				amqpTemplate.convertAndSend(SDK_PAY_TRANS_QUEUE_KEY,entity);
//				logger.info("成功发送到"+SDK_PAY_TRANS_QUEUE_KEY+"队列:" + JSON.toJSONString(entity));
				}catch(Exception e){
					logger.error("发送到"+SDK_PAY_TRANS_QUEUE_KEY+"队列出现异常:" + JSON.toJSONString(entity)+"{异常:"+e+"}");
				}
			}
			return response;
		}else {
			logger.error("流水状态异常:" + status);
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.STATUS_UNKNOW.getCode(),PayResEnum.STATUS_UNKNOW.getMsg());
		}
	}
	
	/**
	 * 查询应用市场会员等级
	 * @param payUserId
	 */
	private String queryVipFromStore(Long payUserId) {
		//优先从Redis中获取
//		String data = redisUtil.get(SDK_STORE_VIP + ":" + payUserId);
//		if(StringUtils.isNotEmpty(data)) {
//			return data;
//		}
		
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("userId", String.valueOf(payUserId)));

		String vipLevelUrl = payConfig.getString("store.viplevel.url");
//		logger.info("查询用户{}会员等级URL:{}", payUserId, vipLevelUrl);
		String responseBody = ApiConnector.post(vipLevelUrl, paramsList);
//		logger.info("查询用户{}会员等级返回信息:{}", payUserId, responseBody);
		if(StringUtils.isNotEmpty(responseBody)){
			JSONObject jObj = JSONObject.parseObject(responseBody);
			if(jObj.getInteger("responseCode") == 1000) {
				JSONObject dataObj = jObj.getJSONObject("data");
				if(dataObj!=null){
					String memberLevel = dataObj.getString("memberLevel");
					return memberLevel;
				}
			}
		}
//		logger.info("{}会员等级信息为空",payUserId);
		return null;
	}
	
	/**
	 * 入库
	 * @param payTransEntity
	 * @param status
	 */
	private PayTransEntity addPayTrans(SdkPayRequest payRequest, String status, String userName,BigDecimal chargeAmount,Integer merchantUserId,Date now){
		PayTransEntity entity = covent(payRequest);
		entity.setTransDate(DateUtils.toDateText(now, DateUtils.DATATIMEF_STR_SEC).substring(0, 8));
		entity.setTransTime(DateUtils.toDateText(now, DateUtils.DATATIMEF_STR_SEC).substring(8));
		entity.setCreateTime(now);
		entity.setLoginName(userName);
		entity.setStatus(status);
		entity.setTransAmount(chargeAmount);
		entity.setPayTransCode(GenOrderCode.next());
		entity.setMerchantUserId(merchantUserId);
		String suffix = DateUtils.toDateText(now, "yyyyMM");
		payTransMapper.addPayTransRequest(entity,suffix);
		return entity;
	}
    
	/**
	 * 根据userId查找用户信息
	 * @param userId
	 * @return
	 */
	public UserInfo loadUserInfo(long userId) {

		String response = ApiConnector.get(payConfig.getString("usercentre.url")
						+ String.format(payConfig.getString("query.user.info"),userId), null);
		if (StringUtils.isBlank(response)) {
//			logger.error("url={},params={},result={}",payConfig.getString("usercentre.url")
//					+ String.format(payConfig.getString("query.user.info"),userId), null, response);
			
			return null;
		}	
		
		JSONObject obj = JSON.parseObject(response);
		if (1 != obj.getIntValue("code")) {
			return null;
		}
		
		return JSON.parseObject(obj.getString("data"), UserInfo.class);
	}
	
	
	/**
	 * 根据username查询用户信息
	 * @param userName
	 * @return
	 */
	private UserInfo loadUserInfo(String userName) { 
		String response = ApiConnector.get(payConfig.getString("usercentre.url")
				+ String.format(payConfig.getString("query.user.mobile"),userName), null);
		if (StringUtils.isBlank(response)) {
//			logger.error("url={},params={},result={}",payConfig.getString("usercentre.url")
//							+ String.format(payConfig.getString("query.user.mobile"),userName), null, response);
			return null;
		}	
		
		JSONObject obj = JSON.parseObject(response);
		if (1 != obj.getIntValue("code")) {
			return null;
		}
		
		return JSON.parseObject(obj.getString("data"), UserInfo.class);
	}
	
	/**
	 * 获取用户可用宝币余额
	 * @param payTransEntity
	 * @return
	 */
	private Long queryBalance(PayTransEntity payTransEntity,Integer certifiedStatus){
		
		int deductType = 0;//对应的扣款方式
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		String userId = String.valueOf(payTransEntity.getPayUserId());
		paramsList.add(new BasicNameValuePair("userId", userId));
		
		if (0 == certifiedStatus) {
			logger.info("用户{},购买非认证商家应用，应用编码:{},业务流水号:{},支付金额:{}",payTransEntity.getPayUserId(),payTransEntity.getAppCode()
					,payTransEntity.getBizCode(),payTransEntity.getTransAmount());
			deductType = QbAccountDeductType.DEDUCT_TYPE_QB_5;
			
		}else{
			logger.info("用户{},购买认证商家应用，应用编码:{},业务流水号:{},支付金额:{}",payTransEntity.getPayUserId(),payTransEntity.getAppCode()
					,payTransEntity.getBizCode(),payTransEntity.getTransAmount());
			
			deductType = QbAccountDeductType.DEDUCT_TYPE_QB_3;
		}
		paramsList.add(new BasicNameValuePair("deductType", deductType+""));
		String responseBody = ApiConnector.get(payConfig.getString("account.query.url"), paramsList, "utf-8");
		logger.info("获取钱宝用户可用余额信息:{}",responseBody);
		
		if(StringUtils.isBlank(responseBody)){
			logger.error("查询用户{}宝币余额异常",userId);
			return null;
		}
		JSONObject obj = JSON.parseObject(responseBody);
		if(obj.getIntValue("result") != 0){
			return null;
		}
		return Long.parseLong(obj.getString("data"));
	}

    /**
     * 调用商肃的个人宝币（人民币）账户可用余额查询接口
     *
     * @param payTransEntity
     * @return
     */
    private Long queryUserBalance(PayTransEntity payTransEntity, Integer certifiedStatus) {
        final String userId = String.valueOf(payTransEntity.getPayUserId());
        final String tradeTime = DateUtils.getCurDateFormatStr("yyyyMMddHHmmss");
        Map<String, String> map = new TreeMap<String, String>() {{
            put("userId", userId);
            put("inputCharset", "UTF-8");
            put("groupCode", "G009");
            put("busiType", BusinessType.QUERY_DEBIT_CARD_ACCOUNT.getType());
            put("tradeTime", tradeTime);
        }};
        String map2Stirng = YanSuCommonUtil.map2String(map);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        paramsList.add(new BasicNameValuePair("signType", "RSA"));
        try {
            String accountQueryUrl = payConfig.getString("shangsu.person.pay.query.rmb.url");
//            logger.info("查询用户余额url{},请求参数{}", accountQueryUrl, map2Stirng);
            //MD5算法摘要
            String md5Sign = YanSuCommonUtil.md5Sign(map2Stirng);
            //使用RSA算法私钥加密
            String signCode = RSAUtil.sign(md5Sign, RSAUtil.getPrivateKey(payConfig.getString("shangsu.rsa.private.key")));
            paramsList.add(new BasicNameValuePair("signCode", signCode));
            String responseBody = ApiConnector.post(accountQueryUrl, paramsList, "UTF-8");
            logger.info("用户{}获取钱宝用户可用余额信息:{}", userId, responseBody);
            if (StringUtils.isBlank(responseBody)) {
                logger.info("查询用户{}账户余额异常", userId);
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            if (!"100000".equals(JSONObject.parseObject(responseBody).getString("respCode")))
                return null;
            return jsonObject.getLong("rmbAmount");
        } catch (Exception e) {
            logger.error("queryUserBalance error", e);
            return null;
        }
    }

	/**
	 * 验证交易密码是否存在
	 * @param payUserId
	 * @return
	 */
    protected boolean existTradePwd(long payUserId) {
        String url = payConfig.getString("psw.exists.url");
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", String.valueOf(payUserId)));
        paramsList.add(new BasicNameValuePair("appId", payConfig.getString("qbao.sdk.server.appid")));
        String responseBody = ApiConnector.post(url, paramsList);
        logger.info("查询用户交易密码,请求url:{},请求参数：{},响应报文：{}", url, JSONObject.toJSONString(paramsList), responseBody);
        if (StringUtils.isBlank(responseBody)) {
            logger.error("查询用户{}是否设置支付密码失败", payUserId);
            return false;
        }
        JSONObject jObj = JSONObject.parseObject(responseBody);
        JSONObject data = null;
        int code = jObj.getInteger("code"); //"code": 1,//1成功0失败 , "tradePassword": 0//交易密码 0未设置1已设置
        if (code == 1 && (data = jObj.getJSONObject("data")) != null && 1 == data.getInteger("tradePassword")) {
            return true;
        }
        return false;
    }
	
	/**
	 * 判断是否绑定手机号
	 * @param payUserId
	 * @return
	 */
	private boolean isBindMobile(long payUserId){
		String requestUrl = payConfig.getString("bind.mobile.url") + payUserId;
//		logger.info("校验用户{}是否绑定手机号URL:{}",payUserId,requestUrl);
		String responseBody = ApiConnector.get(requestUrl, null);
//		logger.info("校验用户{}是否绑定手机号返回结果:{}",payUserId,responseBody);
		
		if(StringUtils.isBlank(responseBody)){
			return false;
		}
		
		JSONObject jObj = JSONObject.parseObject(responseBody);
		String code = jObj.getString("code");
		if("1".equals(code)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 查询用户是否绑定银行卡号
	 * @param payUserId
	 * @return
	 */
	private boolean isBindBankCard(long payUserId){

		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("userId", String.valueOf(payUserId)));

		String requestUrl = payConfig.getString("bind.bankcard.url");
		logger.info("查询用户{}是否绑定银行卡URL:{}",payUserId,requestUrl);
		String responseBody = ApiConnector.post(requestUrl,paramsList);
		logger.info("查询用户{}绑定银行卡返回信息:{}",payUserId,responseBody);
		
		if(StringUtils.isBlank(responseBody)){
			return false;
		}
		
		JSONObject jObj = JSONObject.parseObject(responseBody);
		return jObj.getBoolean("data");
	}
	
	
	/**
	 * 交易密码MD5加密
	 * @param payUserId  用户id
	 * @param password   交易密码
	 * @param loginId    用户名称
	 * @return
	 */
	private Integer checkPwd(long payUserId,String password,String loginId){
		Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
		//MD5加盐加密
		String signPwd = md5PasswordEncoder.encodePassword(password, loginId).toLowerCase();
		
		String url = payConfig.getString("pwd.check.url");
//		logger.info("用户{}密码校验参数:{}",payUserId,signPwd);
		String response = ApiConnector.post(String.format(url,payUserId, signPwd), null);
//		logger.info("用户{}密码校验返回结果:{}",payUserId,response);
		
		if(StringUtils.isBlank(response)){
			return null;
		}
		
		JSONObject obj = JSON.parseObject(response);
		return obj.getIntValue("code");
	}
	
	/**
	 * 判断请求参数是否为空
	 * @param sdkDopayBo
	 * @return
	 */
	private boolean isBlank(SdkDopayRequest sdkDopayBo){
		logger.info("SDK支付参数:{}",JSON.toJSONString(sdkDopayBo));
		if(StringUtils.isBlank(sdkDopayBo.getUseBqFlag()) 
				|| StringUtils.isBlank(sdkDopayBo.getTransPassWord())
				|| StringUtils.isBlank(sdkDopayBo.getAppCode())){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断发送短信校验码请求参数是否为空
	 * @param smsSend
	 * @return
	 */
	private boolean isBlank(SmsSend smsSend){
		if(StringUtils.isBlank(smsSend.getSdkflowId()) 
				|| StringUtils.isBlank(smsSend.getTransDate())){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户可用宝券余额
	 * @param payUserId
	 * @return
	 */
	private Long queryVoucher(long payUserId){
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("userId", String.valueOf(payUserId)));
		paramsList.add(new BasicNameValuePair("type", "voucher"));

		String responseBody = ApiConnector.post(
				payConfig.getString("quan.query.url"), paramsList,"utf-8");
		logger.info("查询用户{}宝券余额返回信息:{}",payUserId,responseBody);
		
		if(StringUtils.isBlank(responseBody)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(responseBody);
		if(!"1000".equals(obj.getString("code"))){
			return null;
		}
		return Long.parseLong(obj.getString("data"));
	}

    /**
     * 获取用户可用宝券余额
     *
     * @param payUserId
     * @return
     */
    private Long queryUserVoucher(final long payUserId) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        final String tradeTime = DateUtils.getCurDateFormatStr("yyyyMMddHHmmss");
        Map<String, String> map = new HashMap<String, String>() {{
            put("userId", String.valueOf(payUserId));
            put("inputCharset", "UTF-8");
            put("groupCode", "G009");
            put("busiType", BusinessType.QUERY_BQ_ACCOUNT.getType());
            put("tradeTime", tradeTime);
        }};
        for (Map.Entry<String, String> entry : map.entrySet()) {
            paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        paramsList.add(new BasicNameValuePair("signType", "RSA"));
        String map2Stirng = YanSuCommonUtil.map2String(map);
        try {
            String quanQueryUrl = payConfig.getString("shangsu.person.pay.query.bq.url");
//            logger.info("查询用户宝券余额url{},请求参数{}", quanQueryUrl, map2Stirng);
            //MD5算法摘要
            String md5Sign = YanSuCommonUtil.md5Sign(map2Stirng);
            String signCode = RSAUtil.sign(md5Sign, RSAUtil.getPrivateKey(payConfig.getString("shangsu.rsa.private.key")));
            paramsList.add(new BasicNameValuePair("signCode", signCode));
            String responseBody = ApiConnector.post(quanQueryUrl, paramsList, "UTF-8");
            logger.info("查询用户{}宝券余额返回信息:{}", payUserId, responseBody);
            if (StringUtils.isBlank(responseBody)) {
                return null;
            }
            JSONObject obj = JSON.parseObject(responseBody);
            if (!"100000".equals(obj.getString("respCode")) || StringUtils.isEmpty(obj.getString("bqAmount"))) {
                return null;
            }
            return Long.parseLong(obj.getString("bqAmount"));
        } catch (Exception e) {
            logger.error("queryUserVoucher error=", e);
            return null;
        }
    }

    /**
	 * 调用账户系统扣减宝币
	 * @param entity
	 * @return
	 */
	private AccountResult doBizTrade(PayTransEntity entity,int certifiedStatus) {
		String domain = payConfig.getString("pay.dobiztrade.url");

		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("createrUid",String.valueOf(entity.getPayUserId())));
		paramsList.add(new BasicNameValuePair("bizType",BIZ_TYPE));
		paramsList.add(new BasicNameValuePair("bizId","sdkServer_" +entity.getTransDate().substring(0,6) 
				+ entity.getPayTransId()));
		paramsList.add(new BasicNameValuePair("bizSnapshot",entity.getTransIntro()));
		
		List<AcountDetail> detailList = new ArrayList<AcountDetail>();
		BigDecimal zero = new BigDecimal(0);
		
		//扣减宝币
		if(entity.getAccountAmount().compareTo(zero) == 1){
			AcountDetail bb = new AcountDetail();
			int changeTypeBB;
			if(0 == certifiedStatus){
				changeTypeBB = AccountTypeEnum.CHANGE_TYPE_BB_NO.getType();
			}else{
				changeTypeBB = AccountTypeEnum.CHANGE_TYPE_BB_YES.getType();
			}
			bb.setAccountChangeType(changeTypeBB);
			bb.setAccountType(AccountTypeEnum.ACCOUNT_TYPE_BB.getType());
			bb.setAmount(entity.getAccountAmount().longValue());
			bb.setUserId(entity.getPayUserId());
			detailList.add(bb);
		}
		
		//扣减宝券
		if(entity.getBqAccountAmount().compareTo(zero) == 1){
			AcountDetail bq = new AcountDetail();
			int changeTypeBQ;
			if(0 == certifiedStatus){
				changeTypeBQ = AccountTypeEnum.CHANGE_TYPE_BQ_NO.getType();
			}else{
				changeTypeBQ = AccountTypeEnum.CHANGE_TYPE_BQ_YES.getType();
			}
			bq.setAccountChangeType(changeTypeBQ);
			bq.setAccountType(AccountTypeEnum.ACCOUNT_TYPE_BQ.getType());
			bq.setAmount(entity.getBqAccountAmount().longValue());
			bq.setUserId(entity.getPayUserId());
			detailList.add(bq);
		}
		
		//商户增加宝币
		AcountDetail mer = new AcountDetail();
		mer.setAccountChangeType(AccountTypeEnum.CHANGE_TYPE_MER.getType());
		mer.setAccountType(AccountTypeEnum.ACCOUNT_TYPE_MER.getType());
		mer.setAmount(entity.getTransAmount().longValue());
		mer.setRelatedId("30"); //冻结天数
		mer.setUserId(entity.getMerchantUserId());
		detailList.add(mer);
		paramsList.add(new BasicNameValuePair("detailList",JSON.toJSONString(detailList)));
		logger.info("订单{}扣减宝币参数:{}", entity.getPayTransId(),JSON.toJSONString(paramsList));
		
		String responseBody = ApiConnector.get(domain,paramsList);
		logger.info("订单{}扣减宝币返回结果:{}", entity.getPayTransId(), responseBody);
		
		if(StringUtils.isBlank(responseBody)){
			return null;
		}
		
		return JSON.parseObject(responseBody,AccountResult.class);
	}


    /**
     * 调用商肃的交易类接口扣除人民币、宝券
     *
     * @param entity
     * @return
     */
    private TradeResponse doTrade(final PayTransEntity entity, int certifiedStatus) {
        try {
            final String tradeTime = DateUtils.getCurDateFormatStr("yyyyMMddHHmmss");
            final String tradeDesc = Base64.encodeBase64URLSafeString("游戏充值".getBytes("UTF-8"));
            //支付方式 0：组合支付，1：人民币，2：宝券
            final String payType = YanSuCommonUtil.getPayType(entity.getAccountAmount(), entity.getBqAccountAmount());
            Map<String, String> map = new HashMap<String, String>() {{
                put("inputCharset", "UTF-8");
                put("groupCode", "G009");
                put("sellerId", entity.getMerchantUserId() + "");//卖家ID
                put("buyerId", entity.getPayUserId() + "");//买家ID
                put("busiType", BusinessType.GAME_RECHARGE.getType());
                put("tradeTime", tradeTime);
                put("outTradeNo", entity.getBizCode());
                put("payType", payType); //0：组合支付，1：人民币，2：宝券
                put("payTotalAmount", entity.getTransAmount().longValue() + "");//交易总金额
                put("rmbAmount", entity.getAccountAmount().longValue() + "");//人民币金额
                put("bqAmount", entity.getBqAccountAmount().longValue() + "");//宝券金额
                put("feeAmount", "0");
                put("tradeDesc", tradeDesc);
            }};
            String map2Stirng = YanSuCommonUtil.map2String(map);
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            paramsList.add(new BasicNameValuePair("signType", "RSA"));
            String domain = payConfig.getString("shangsu.person.pay.url");
//            logger.info("调用商肃的交易类接口url{},请求参数{}", domain, map2Stirng);
            //MD5算法摘要
            String md5Sign = YanSuCommonUtil.md5Sign(map2Stirng);
            //使用RSA算法私钥加密
            String signCode = RSAUtil.sign(md5Sign, RSAUtil.getPrivateKey(payConfig.getString("shangsu.rsa.private.key")));
            paramsList.add(new BasicNameValuePair("signCode", signCode));
            String responseBody = ApiConnector.post(domain, paramsList, "UTF-8");
            logger.info("流水号{}调用商肃的交易类接口返回结果:{}", entity.getPayTransId(), responseBody);
            if (StringUtils.isBlank(responseBody))
                return null;
            return JSON.parseObject(responseBody, TradeResponse.class);
        } catch (Exception e) {
            logger.error("doTrade error", e);
            return null;
        }
    }

    /**
     * 构建交易行项数据
     *
     * @param entity
     * @return
     */
    private List<TradeItem> buildTradeItem(PayTransEntity entity) {
        List<TradeItem> tradeItems = new ArrayList<TradeItem>();
        //人民币
        TradeItem bbTradeItem = new TradeItem();
        bbTradeItem.setItemTradeNo(entity.getPayTransId() + "");
        bbTradeItem.setAmount(entity.getBqAccountAmount().longValue());
        bbTradeItem.setItemBusiType("P090201");
        bbTradeItem.setPayType("1");
        tradeItems.add(bbTradeItem);
         //宝劵
        TradeItem bqTradeItem = new TradeItem();
        bqTradeItem.setItemTradeNo(entity.getPayTransId() + "");
        bqTradeItem.setAmount(entity.getAccountAmount().longValue());
        bqTradeItem.setItemBusiType("P090202");
        bqTradeItem.setPayType("2");
        tradeItems.add(bqTradeItem);
        return tradeItems;
    }


    /**
	 * 根据流水id查询流水
	 * @param sdkflowId
	 */
	private PayTransEntity queryEntityById(String sdkflowId,String suffix){
		PayTransEntity entity = null;
		try{
			entity = payTransMapper.getPayTransReqById(Long.valueOf(sdkflowId),suffix);
		}catch(Exception e) {
			logger.error("根据流水{}查询异常:{}",sdkflowId,e);
		}
//		logger.info("流水{}查询结果为:{}",sdkflowId,JSON.toJSONString(entity));
		return entity;
	}
	

	
	
	/**
	 * 验证是否可以使用宝券
	 * @param appCode
	 * @return
	 */
	private PayConfigEntity getPayConfig(String appCode){
		PayConfigEntity payConfigEntity = payConfigMapper.queryByAppCode(appCode);
		if(payConfigEntity == null){
			payConfigEntity = new PayConfigEntity();
			payConfigEntity.setBqFlag(payConfig.getInt("bqFlag"));
			payConfigEntity.setCertifiedStatus(payConfig.getInt("certifiedStatus"));
		}
		return payConfigEntity;
	}

	private PayTransEntity calculate(PayTransEntity entity,Long voucher){
		voucher = voucher == null ? 0 : voucher;  //null时 不支持宝券，设置为0
		BigDecimal bqTotal = new BigDecimal(voucher);
		BigDecimal amount = entity.getTransAmount();
		if(bqTotal.compareTo(amount) != -1){
			entity.setBqAccountAmount(amount);
			entity.setAccountAmount(new BigDecimal(0));
		}else{
			entity.setAccountAmount(amount.subtract(bqTotal));
			entity.setBqAccountAmount(bqTotal);
		}
		return entity;
	}
	
	/**
	 * 更新流水
	 * @param entity
	 */
	private boolean updatePayTrans(PayTransEntity entity,String suffix){
//		entity.setStatus(TransStatusEnum.PAY_TRANS_SUCCESS.getStatus());
		try{
			payTransMapper.updatePayTransByPayTransId(entity,suffix);
		}catch(Exception e){
			logger.error("SDK流水号{}修改状态失败:{}", entity.getPayTransId(), e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 授权时间超时校验
	 * @param now
	 * @param createTime
	 * @param timeoutSecond 超时时间（秒）
	 * @return 当前时间>=授权时间+超时时间 false;当前时间<授权时间+超时时间 true
	 */
	private boolean valAuthTime(Date now,Date createTime,int timeoutSecond){
		//授权时间+超时时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(createTime);
		calendar.add(Calendar.SECOND, timeoutSecond);
		Date afterGrantDate = calendar.getTime();
		//当前时间>=授权时间+超时时间 false;当前时间<授权时间+超时时间 true
		return DateUtils.compareCurrDateToDate(now, afterGrantDate);
	}
	
	/**
	 * 签名参数组装
	 * @param
	 * 
	 * 签名规则：appCode+"&"+bizCode +"&"+billingCode+"&"+payCallbackUrl
	 * @return
	 */
	private String appendParam(SdkPayRequest payRequest){
		StringBuffer sb = new StringBuffer();
		String appCode = payRequest.getAppCode();
		String bizCode = payRequest.getOrderNo();
		String chargingPointCode = payRequest.getBillingCode();
		String callbackUrl = payRequest.getPayCallbackUrl();
		
		sb.append("appCode=").append(appCode).append(SignConstants.AND)
		.append("orderNo=").append(bizCode).append(SignConstants.AND)
		.append("payCode=").append(chargingPointCode).append(SignConstants.AND)
		.append("payNotifyUrl=").append(callbackUrl);
		return sb.toString();
	}
	
	/**
	 * 异步通知
	 * @param
	 */
	private void asyncNotice(SdkPayResponse response,PayTransEntity entity, String suffix) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("responseCode", response.getResponseCode()));
		pairs.add(new BasicNameValuePair("errorCode", response.getErrorCode()));
		pairs.add(new BasicNameValuePair("errorMsg", response.getErrorMsg()));
		pairs.add(new BasicNameValuePair("data", JSON.toJSONString(response.getData())));
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("responseCode=").append(response.getResponseCode()).append(SignConstants.COMMA)
		.append("errorCode=").append(response.getErrorCode()).append(SignConstants.COMMA)
		.append("sdkflowId=").append(entity.getPayTransId()).append(SignConstants.COMMA)
		.append("bizCode=").append(entity.getBizCode());
		
		/**加签**/
		String privateKey = payConfig.getString("async.private.key");
		String signCode = "";
		try {
			signCode = RSACoder.sign(sb.toString().getBytes(), privateKey);
		} catch (Exception e) {
			logger.error("异步回调加签失败:{}",e);
		}
		
		pairs.add(new BasicNameValuePair("signCode", signCode));
		
		String res = ApiConnector.post(entity.getCallbackUrl(), pairs);
		String status;
		Boolean isSuccess = null;
		if(StringUtils.isBlank(res)) {
			status = PayAsyncNoticeEnum.NET_UNUSUAL.getStatus();
		}else{
			isSuccess = JSON.parseObject(res).getBoolean("isSuccess");
			if(isSuccess){
				status = PayAsyncNoticeEnum.SEND_SUCCESS.getStatus();
			}else{
				status = PayAsyncNoticeEnum.MER_RES_FAIL.getStatus();
			}
		}
		if(!status.equals(PayAsyncNoticeEnum.SEND_SUCCESS.getStatus())){
			insertAsync(response, entity,status,isSuccess,suffix);
		}
	}
	
	
	/**
	 * 插入补单数据
	 * @param response
	 * @param payEntity
	 * @param status
	 * @param isSuccess
	 */
	private void insertAsync(SdkPayResponse response,PayTransEntity payEntity,String status,Boolean isSuccess,String suffix){
		PayAsyncNoticeEntity asyncEntity = new PayAsyncNoticeEntity();
		asyncEntity.setBizCode(payEntity.getBizCode());
		String payTransId = suffix + payEntity.getPayTransId();
		asyncEntity.setSdkflowId(Long.valueOf(payTransId));
		asyncEntity.setAppCode(payEntity.getAppCode());
		asyncEntity.setTransDate(payEntity.getTransDate());
		asyncEntity.setTransTime(payEntity.getTransTime());
		asyncEntity.setCallbackUrl(payEntity.getCallbackUrl());
		asyncEntity.setResponseCode(response.getResponseCode());
		asyncEntity.setErrorCode(response.getErrorCode());
		asyncEntity.setErrorMsg(response.getErrorMsg());
		asyncEntity.setTimes(1);
		asyncEntity.setStatus(status);
		asyncEntity.setIsSuccess(isSuccess);
		Calendar date = Calendar.getInstance();
     	date.add(Calendar.MINUTE, 1);
     	asyncEntity.setNextSendTime(date.getTime());
		
		try {
			payAsyncNoticeMapper.addPayAsyncNotice(asyncEntity);
		} catch (Exception e) {
			logger.error("支付异步通知插入数据异常：{}",e); 
		}
	}
	
	
	private PayTransEntity covent(SdkPayRequest payRequest){
		PayTransEntity entity = new PayTransEntity();
		entity.setAppCode(payRequest.getAppCode());
		entity.setTransType(payRequest.getTransType());
		entity.setBizCode(payRequest.getOrderNo());
		entity.setPayUserId(payRequest.getUserId());
		entity.setChargingPointCode(payRequest.getBillingCode());
		entity.setAppSource(payRequest.getAppSource());
		entity.setTransIntro(payRequest.getTransIntro());
		entity.setCallbackUrl(URLDecoder.decode(payRequest.getPayCallbackUrl()));
		entity.setSignCode(payRequest.getSignCode());
		entity.setSignType(payRequest.getSignType());
		return entity;
	}
	
	/**
	 * 将信息发送到应用市场
	 * @param userId 用户id
	 * @param times 赠送的抽奖次数
	 */
	private void sendToStore(Long userId,int times ,String payTransCode){
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userId", userId.toString()));
		pairs.add(new BasicNameValuePair("times", times+""));
		pairs.add(new BasicNameValuePair("payTransCode", payTransCode));
		logger.info("异步请求发送到应用市场，userId={},times={},payTransCode={}",userId,times,payTransCode);
		ApiConnector.asyncGet(domainConfig.getString("store.turntable.rule"), pairs);
	}
	
	/**
	 * 执行异步通知
	 * @param
	 * @param
	 * @param
	 */
	private void doAscyNotice(TurntableRuleEntity rule,PayTransEntity entity){
		if(rule != null){
			
			BigDecimal coinAmount = rule.getRechargeCoinAmount()== null ? new BigDecimal("0"):rule.getRechargeCoinAmount();
			BigDecimal bqAmount = rule.getRechargeBqAmount()==null ? new BigDecimal("0"):rule.getRechargeBqAmount();			
			String condition = rule.getRechargeCondition().toString();//条件类型：0 >=、1 =、2 >.
			BigDecimal accountAmount = entity.getAccountAmount();
			BigDecimal bqAccountAmount = entity.getBqAccountAmount();
			BigDecimal zero = new BigDecimal(0);
			int coinVal = accountAmount.compareTo(coinAmount);
			int bqVal = bqAccountAmount.compareTo(bqAmount);
			if(condition.equals(RechargeConditionEnum.GREATER_THAN.getStatus())){// >
				if(coinAmount.compareTo(zero)!=0){
					if(coinVal != 1){
						rule.setNumber(0);
					}
				}
				if(rule.getNumber() != 0 && bqAmount.compareTo(zero)!=0){
					if(bqVal != 1){
						rule.setNumber(0);
					}
				}
			}
			if(condition.equals(RechargeConditionEnum.EQUAL_TO.getStatus())){// =
				if(coinAmount.compareTo(zero)!=0){
					if(coinVal != 0){
						rule.setNumber(0);
					}
				}
				if(rule.getNumber() != 0 && bqAmount.compareTo(zero)!=0){
					if(bqVal != 0){
						rule.setNumber(0);
					}
				}			
			}
			if(condition.equals(RechargeConditionEnum.GREATER_THAN_OR_EQUAL_TO.getStatus())){// >=
				if(coinVal == -1 && bqVal == -1){
					rule.setNumber(0);
				}
			}
			
			//结果发送给应用市场
			this.sendToStore(entity.getPayUserId(),rule.getNumber(),entity.getPayTransCode());
			
		}
	}
	
	
	@Override
	public SdkPayResponse dotrustpay(SdkTrustpayRequest trustPayBo) {
		String appCode = trustPayBo.getAppCode();
		//校验当前应用是否存在
		OpenMerchantEntity merchantEntity = redisService.getMerchantInfo(appCode);
		if(merchantEntity == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getCode()
					, PayResEnum.APP_CODE_NO_EXISTS.getMsg());
		}
		//校验ip白名单
		if(!isWhiteIp(trustPayBo.getRequestIp(),merchantEntity.getWhiteIp())){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.REQUEST_IP_ERROR.getCode()
					, PayResEnum.REQUEST_IP_ERROR.getMsg());

		}

		//签名方式校验 默认RSA
		String signType = trustPayBo.getSignType();
		if(SignTypeEnum.getFromKey(signType) == null){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.SIGN_TYPE_ERROR.getCode()
					, PayResEnum.SIGN_TYPE_ERROR.getMsg());
		}

		//签文校验
		if(!validSignature(trustPayBo,merchantEntity)){
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode()
					, PayResEnum.SIGN_CODE_ILLEGAL.getCode()
					, PayResEnum.SIGN_CODE_ILLEGAL.getMsg());
		}

		//是否支持宝券支付
		long payUserId = trustPayBo.getUserId();
		PayConfigEntity payConfig = getPayConfig(appCode);
		String payType = trustPayBo.getPayType();
		Long voucher = null; //宝券余额
		if("0".equals(payType)) { //0-组合支付  只有认证商家才能组合支付
			if(payConfig.getBqFlag() != 1) {
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.CANT_NOT_USE_VOUCHER.getCode(),PayResEnum.CANT_NOT_USE_VOUCHER.getMsg());
			}

			/*******查询宝券余额*************/
//			voucher = queryVoucher(payUserId);
            voucher = queryUserVoucher(payUserId);
            if (voucher == null) {
				return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
						PayResEnum.VOUCHER_QUERY_ERROR.getCode(),PayResEnum.VOUCHER_QUERY_ERROR.getMsg());
			}
		}

		String orderNo = trustPayBo.getOrderNo();
		String curDate = DateUtils.getCurDateFormatStr("yyyyMMddHHmmss");
		String suffix = curDate.substring(0, 6);
		PayTransEntity entity = payTransMapper.getPayTransReqByMertCodeTransCode(appCode,orderNo,suffix);
		if(entity != null){ //当前流水已存在
			return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
					PayResEnum.TRANS_INFO_IS_EXIST.getCode(),PayResEnum.TRANS_INFO_IS_EXIST.getMsg());
		}else{ //当前流水不存在
			String status = TransStatusEnum.PAY_TRANS_ACCEPT_SUCCESS.getStatus();
			entity = addPayTrans(trustPayBo, status, merchantEntity.getMerchantUserId(), curDate);
			SdkPayResponse response = null;
			status = TransStatusEnum.PAY_TRANS_FAILED.getStatus();
			if(entity != null){
				this.calculate(entity, voucher);
				TrustPayResultDate data = new TrustPayResultDate(entity.getPayTransId(), orderNo, curDate.substring(0, 8), entity.getAccountAmount(), entity.getBqAccountAmount());
                //AccountResult trade = doBizTrade(entity,payConfig.getCertifiedStatus());
                TradeResponse tradeResponse = doTrade(entity, payConfig.getCertifiedStatus());
                if (tradeResponse == null) {
					response = new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
							PayResEnum.TRADE_NET_ERROR.getCode(),PayResEnum.TRADE_NET_ERROR.getMsg(),data);
				}else{
                    if ("100000".equals(tradeResponse.getRespCode())) {
                        entity.setAccountFlowId(tradeResponse.getPayTradeNo());
                        status = TransStatusEnum.PAY_TRANS_SUCCESS.getStatus();
                        response = new SdkPayResponse(PayResEnum.SUCCESS_CODE.getCode(), null, null, data);
                    } else {
                        response = new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
                                PayResEnum.TRADE_UNKNOW.getCode(), PayResEnum.TRADE_UNKNOW.getMsg(), data);
                    }
                }
                entity.setStatus(status);

                //设置会员等级 TODO
                String memberLevel = queryVipFromStore(entity.getPayUserId());
                entity.setMemberLevel(memberLevel);

                if (!updatePayTrans(entity, suffix)) { //更新流水状态失败
                    return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
                            PayResEnum.UPDATE_TRANS_ERROR.getCode(), PayResEnum.UPDATE_TRANS_ERROR.getMsg());
                }
                asyncNotice(response, entity, suffix);
                if (PayResEnum.SUCCESS_CODE.getCode().equals(response.getResponseCode())) {
                    resRuleTimes(entity);
                }
                return response;
            }
        }

        return new SdkPayResponse(PayResEnum.FAIL_CODE.getCode(),
                PayResEnum.UPDATE_TRANS_ERROR.getCode(), PayResEnum.UPDATE_TRANS_ERROR.getMsg());
    }

    private boolean validSignature(SdkTrustpayRequest trustPayBo,
			OpenMerchantEntity merchantEntity){
		String sign = this.appendParam(trustPayBo);
		String signCode = trustPayBo.getSignCode();
		
		String publicKey = merchantEntity.getPublicKey();
		boolean flag = false;
		//RSA公钥验签
		try {
			flag = RSACoder.verify(sign.getBytes(), publicKey, signCode);
		} catch (Exception e) {
			logger.error("信任支付签名校验异常:{}",e);
		}
		return flag;
	}
	
	private String appendParam(SdkTrustpayRequest trustPayBo){
		StringBuffer sb = new StringBuffer();
		sb.append("transType=").append(trustPayBo.getTransType()).append(SignConstants.COMMA)
		.append("appCode=").append(trustPayBo.getAppCode()).append(SignConstants.COMMA)
		.append("orderNo=").append(trustPayBo.getOrderNo()).append(SignConstants.COMMA)
		.append("userId=").append(trustPayBo.getUserId()).append(SignConstants.COMMA)
		.append("transAmount=").append(trustPayBo.getTransAmount()).append(SignConstants.COMMA)
		.append("appSource=").append(trustPayBo.getAppSource()).append(SignConstants.COMMA)
		.append("payType=").append(trustPayBo.getPayType()).append(SignConstants.COMMA)
		.append("payNotifyUrl=").append(trustPayBo.getPayNotifyUrl());
		logger.info("加签字符串{}",sb.toString());
		
		return sb.toString();
	}
	
	
	private PayTransEntity addPayTrans(SdkTrustpayRequest trustPayBo,String status,Integer merchantUserId,String curTime){
		PayTransEntity entity = new PayTransEntity();
		entity.setAppCode(trustPayBo.getAppCode());
		entity.setTransType(trustPayBo.getTransType());
		entity.setBizCode(trustPayBo.getOrderNo());
		entity.setPayUserId(trustPayBo.getUserId());
		entity.setLoginName(trustPayBo.getLoginName());
		entity.setAppSource(trustPayBo.getAppSource());
		entity.setTransIntro(trustPayBo.getTransIntro());
		entity.setCallbackUrl(URLDecoder.decode(trustPayBo.getPayNotifyUrl()));
		entity.setSignCode(trustPayBo.getSignCode());
		entity.setSignType(trustPayBo.getSignType());
		entity.setTransAmount(trustPayBo.getTransAmount());
		entity.setTransDate(curTime.substring(0,8));
		entity.setTransTime(curTime.substring(8));
		entity.setPayTransCode(GenOrderCode.next());
		entity.setStatus(status);
		entity.setMerchantUserId(merchantUserId);
		String suffix = curTime.substring(0,6);
		try{
			payTransMapper.addPayTransRequest(entity,suffix);
		}catch(Exception e){
			logger.error("信任支付新增流水异常:{}",e);
			entity = null;
		}
		
		return entity;
	}
	
	/**
	 * 请求ip白名单校验
	 * @param ip
	 * @param ips
	 * @return
	 */
	private boolean isWhiteIp(String ip,String ips){
		String[] ipArr = ips.split(SignConstants.COMMA);
		List<String> ipList = Arrays.asList(ipArr);
		if(ipList.contains(ip)){
			return true;
		}
		
		return false;
	}

}
