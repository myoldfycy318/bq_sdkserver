/**
 * 
 */
package com.qbao.sdk.server.service.impl.pay;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.qbao.sdk.server.bo.pay.DopayResult;
import com.qbao.sdk.server.bo.pay.TrustPayResultDate;
import com.qbao.sdk.server.constants.PayAsyncNoticeEnum;
import com.qbao.sdk.server.constants.SignConstants;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayAsyncNoticeMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.PayTransMapper;
import com.qbao.sdk.server.metadata.entity.pay.PayAsyncNoticeEntity;
import com.qbao.sdk.server.metadata.entity.pay.PayTransEntity;
import com.qbao.sdk.server.service.pay.PayAsyncNoticeService;
import com.qbao.sdk.server.util.ApiConnector;
import com.qbao.sdk.server.util.PropertiesUtil;
import com.qbao.sdk.server.util.rsa.RSACoder;

/**
 * 支付异步通知商户失败情况下重新发送定时任务
 * @author liuxingyue
 *
 */
@Service
public class PayAsyncNoticeServiceImpl implements PayAsyncNoticeService {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private PayAsyncNoticeMapper payAsyncNoticeMapper;
	
	@Resource
	private PayTransMapper payTransMapper;
	
	@Resource
	PropertiesUtil payConfig;
	

	/* (non-Javadoc)
	 * @see com.qbao.opensdk.service.PayAsyncNoticeService#payAsyncNotice()
	 */

	@Override
	public void payAsyncNotice() {
		List<PayAsyncNoticeEntity> entities = payAsyncNoticeMapper.getPayAsyncNoticeByStatus(Calendar.getInstance().getTime());
		
		if(!CollectionUtils.isEmpty(entities)){
			payAsyncNoticeMapper.updateStatus(entities, PayAsyncNoticeEnum.IN_SENDING.getStatus());
			String privateKey = payConfig.getString("async.private.key");
			String mzAppCode = payConfig.getString("mz.app.code");
			for (PayAsyncNoticeEntity p : entities) {
				try{
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					String responseCode = p.getResponseCode();
					String errorCode = p.getErrorCode();
		        	pairs.add(new BasicNameValuePair("responseCode",responseCode));
		         	pairs.add(new BasicNameValuePair("errorCode",errorCode));
		         	pairs.add(new BasicNameValuePair("errorMsg",p.getErrorMsg()));
		         	String sdkFlowId = "";
		         	String dateAndId = String.valueOf(p.getSdkflowId());
		         	try{ // 老数据字段sdkflow_id没有年月前缀，下一版本可以去掉
		        		sdkFlowId = dateAndId.substring(6);
		         	}catch(Exception e){
		         		sdkFlowId = dateAndId;
		         	}
		        	if(mzAppCode.equals(p.getAppCode())) { //魔足
		        		String suffix = dateAndId.substring(0, 6);
		        		long sdkFlowIdL = Long.valueOf(sdkFlowId);
		        		PayTransEntity payTransEntity = payTransMapper.getPayTransReqById(sdkFlowIdL, suffix);
		        		TrustPayResultDate data = new TrustPayResultDate(sdkFlowIdL, p.getBizCode(), suffix, payTransEntity.getAccountAmount().intValue(), payTransEntity.getBqAccountAmount().intValue());
		        		pairs.add(new BasicNameValuePair("data", JSON.toJSONString(data)));
		        	}else {
		        		DopayResult data = new DopayResult(Long.valueOf(sdkFlowId),p.getBizCode());
			    		pairs.add(new BasicNameValuePair("data", JSON.toJSONString(data)));
		        	}
		         
		    		StringBuffer sb = new StringBuffer();
		    		sb.append("responseCode=").append(responseCode).append(SignConstants.COMMA)
		    		.append("errorCode=").append(errorCode).append(SignConstants.COMMA)
		    		.append("sdkflowId=").append(sdkFlowId).append(SignConstants.COMMA)
		    		.append("bizCode=").append(p.getBizCode());
		    		
		    		String signCode = "";
		    		try {
		    			signCode = RSACoder.sign(sb.toString().getBytes(), privateKey);
		    		} catch (Exception e) {
		    			log.error("异步回调加签失败:{}",e);
		    		}
		    		
		    		pairs.add(new BasicNameValuePair("signCode", signCode));
		         	String response = ApiConnector.post(p.getCallbackUrl(), pairs);
		         	//异步通知返回结果
		         	PayAsyncNoticeEntity asyncNoticeEntity = new PayAsyncNoticeEntity();
		         	asyncNoticeEntity.setId(p.getId());
		         	asyncNoticeEntity.setSdkflowId(p.getSdkflowId());
		         	asyncNoticeEntity.setTimes(p.getTimes()+1);
		         	//下次发送时间
		         	Calendar date = Calendar.getInstance();
		         	date.add(Calendar.MINUTE, p.getTimes()*2);
		         	asyncNoticeEntity.setNextSendTime(date.getTime());
		         	String status;
		    		Boolean isSuccess = null;
		    		if(StringUtils.isBlank(response)) {
		    			status = PayAsyncNoticeEnum.NET_UNUSUAL.getStatus();
		    		}else{
		    			isSuccess = JSON.parseObject(response).getBoolean("isSuccess");
		    			if(isSuccess) {
		    				status = PayAsyncNoticeEnum.SEND_SUCCESS.getStatus();
		    			}else {
		    				status = PayAsyncNoticeEnum.MER_RES_FAIL.getStatus();
		    			}
		    		}
		    		asyncNoticeEntity.setStatus(status);
		    		asyncNoticeEntity.setIsSuccess(isSuccess);
		         	payAsyncNoticeMapper.updatePayAsyncNotice(asyncNoticeEntity);
		         	
		         	log.info("钱宝流水:" + p.getSdkflowId() + "异步通知返回结果:{}",response);
				}catch(Exception e){
					log.error("商户通知失败:钱宝流水:" + p.getSdkflowId() + ".原因:{}", e);
				}
			}
		}
	}
	

}
