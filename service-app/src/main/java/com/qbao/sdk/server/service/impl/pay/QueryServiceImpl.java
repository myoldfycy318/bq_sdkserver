package com.qbao.sdk.server.service.impl.pay;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.qbao.sdk.server.bo.pay.BaseResponse;
import com.qbao.sdk.server.bo.pay.PayRequestResponse;
import com.qbao.sdk.server.bo.pay.QueryParam;
import com.qbao.sdk.server.bo.pay.QueryResponse;
import com.qbao.sdk.server.constants.BusResponeEnum;
import com.qbao.sdk.server.constants.ResultConstants;
import com.qbao.sdk.server.constants.SafeCheckResponeEnum;
import com.qbao.sdk.server.constants.SignConstants;
import com.qbao.sdk.server.constants.SignTypeEnum;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayTransMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.pay.PayTransEntity;
import com.qbao.sdk.server.metadata.entity.pay.QueryParamEntity;
import com.qbao.sdk.server.service.pay.QueryService;
import com.qbao.sdk.server.service.pay.RsaUtilService;
import com.qbao.sdk.server.service.redis.RedisService;

/**
 * 交易明细查询实现
 * @author liuxingyue
 *
 */
@Service("QueryService")
public class QueryServiceImpl implements QueryService {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	public PayTransMapper payTransMapper;
	
	@Resource
	public MerchantInfoMapper merchantInfoMapper;
	
	@Resource
	private RsaUtilService rsaUtilService;
	
	@Resource
	private RedisService redisService;
	@Override
	public PayRequestResponse getPayRequest(QueryParam queryParam) {

		//加签类型校验
		String signType = queryParam.getSignType();
		if(SignTypeEnum.getFromKey(signType) == null){
			return new PayRequestResponse(ResultConstants.FAILED_BOOL
					,BusResponeEnum.SIGN_TYPE_NULL.getResponeCode()
					,BusResponeEnum.SIGN_TYPE_NULL.getResponeMsg());
		}
		
		QueryParamEntity queryParamEntity = new QueryParamEntity();
		BeanUtils.copyProperties(queryParam, queryParamEntity);
		//校验
		QueryResponse response = val(queryParamEntity);
		if(!response.isSuccess()){
			return new PayRequestResponse(ResultConstants.FAILED_BOOL, response.getCode(), response.getMsg()) ;
		}
		
		PayTransEntity payTransEntity = payTransMapper.getPayRequest(queryParamEntity,queryParam.getTransDate().substring(0,6));
		if(null == payTransEntity){
			response.setCode(BusResponeEnum.QUERY_IS_NULL.getResponeCode());
			response.setMsg(BusResponeEnum.QUERY_IS_NULL.getResponeMsg());
			return new PayRequestResponse(ResultConstants.FAILED_BOOL, response.getCode(), response.getMsg()) ;
			
		}
		PayRequestResponse payRequest = new PayRequestResponse();
		payRequest.setQbTransNum(payTransEntity.getPayTransId().toString());
		payRequest.setTransType(payTransEntity.getTransType());
		payRequest.setAppCode(payTransEntity.getAppCode());
		payRequest.setOrderNo(payTransEntity.getPayTransCode());
		payRequest.setTransTime(payTransEntity.getTransDate()+payTransEntity.getTransTime());
		payRequest.setTransIntro(payTransEntity.getTransIntro());
		payRequest.setTransAmount(payTransEntity.getTransAmount().toString());
		payRequest.setStatus(payTransEntity.getStatus());
		payRequest.setSuccess(response.isSuccess());
		return payRequest;
	}
	
	/**
	 * 校验结果
	 * @return
	 */
	public QueryResponse val(QueryParamEntity queryParamEntity){
		/*
		 * 1.根据商户号获取redis数据
		 * 2.若redis不存在，则获取数据库数据
		 * 3.都不存在返回失败
		 */
		OpenMerchantEntity openMerchant = redisService.getMerchantInfo(queryParamEntity.getAppCode());
		//参数鉴权
		QueryResponse paramResult = paramAuthentication(queryParamEntity,openMerchant);
		if(!paramResult.isSuccess()){
			return paramResult;
		}
			
		return new QueryResponse(ResultConstants.SUCCESS_BOOL);
		
	}
	
	private QueryResponse paramAuthentication(QueryParamEntity queryParamEntity, OpenMerchantEntity openMerchant){
		if(openMerchant == null){
			//商户不存在
			return new QueryResponse(ResultConstants.FAILED_BOOL
					,SafeCheckResponeEnum.MERCHANT_CODE_NOT_GRANT.getResponeCode()
					,SafeCheckResponeEnum.MERCHANT_CODE_NOT_GRANT.getResponeMsg());
		}
		
		
//		String ipStr = "";
		String publicKey = "";
		
//		ipStr = openMerchant.getWhiteIp();
		publicKey = openMerchant.getPublicKey();
		
//		//白名单验证
//		if(ipStr.indexOf(queryParamEntity.getRequestIp()) == -1){
//			return new QueryResponse(ResultConstants.FAILED_BOOL
//					,SafeCheckResponeEnum.REQUEST_IP_ILLEGAL.getResponeCode()
//					,SafeCheckResponeEnum.REQUEST_IP_ILLEGAL.getResponeMsg());
//		}
		
		//签名认证
		if(!vilidateSign(queryParamEntity,openMerchant,publicKey)){
			return new QueryResponse(ResultConstants.FAILED_BOOL
					,SafeCheckResponeEnum.SIGN_CODE_ILLEGAL.getResponeCode()
					,SafeCheckResponeEnum.SIGN_CODE_ILLEGAL.getResponeMsg());
		}
		
		return new QueryResponse(ResultConstants.SUCCESS_BOOL);
	}

	/**
	 * 验证授权的签名信息
	 * @param grantRequest
	 * @param openMerchant
	 * @return
	 */
	private boolean vilidateSign(QueryParamEntity queryParamEntity, OpenMerchantEntity openMerchant, String publicKey) {
		String data = this.appendParam(queryParamEntity);
		String signCode = queryParamEntity.getSignCode();
		String signType = queryParamEntity.getSignType();
		if(SignTypeEnum.SIGN_RSA.getValue().equals(signType)){
			//RSA签名
			BaseResponse valRsaSignResult = rsaUtilService.valRsaSign(data, publicKey, signCode);
			if(!valRsaSignResult.isSuccess()){
				return false;
			}
		}else{
			return false;
		}
		return true;

	}
	
	/**
	 * 组装参数
	 * @return
	 */
	private String appendParam(QueryParamEntity queryParamEntity){
		StringBuffer buffer = new StringBuffer();
		String orderNo = queryParamEntity.getOrderNo();
		String appCode  = queryParamEntity.getAppCode();
		String queryType = queryParamEntity.getQueryType();
		String transType = queryParamEntity.getTransType();
		String tansDate = queryParamEntity.getTransDate();
		buffer.append("orderNo=").append(orderNo).append(SignConstants.COMMA)
		    	.append("appCode=").append(appCode).append(SignConstants.COMMA)
		    	.append("queryType=").append(queryType).append(SignConstants.COMMA)
		    	.append("transType=").append(transType).append(SignConstants.COMMA)
		    	.append("transDate=").append(tansDate);
		return buffer.toString();
	}

}
