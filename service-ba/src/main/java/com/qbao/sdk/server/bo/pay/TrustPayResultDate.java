package com.qbao.sdk.server.bo.pay;

import java.math.BigDecimal;

/**
 * 信任支付返回参数
 * @author liuxingyue
 *
 */
public class TrustPayResultDate {

	private long sdkFlowId;  
	
	private String orderNo;
	
	private String transDate;
	
	private int qbAmount;
	
	private int bqAmount;

	public TrustPayResultDate() {
		
	}

	public TrustPayResultDate(long sdkFlowId, String orderNo, String transDate, int qbAmount,
			int bqAmount) {
		this.sdkFlowId = sdkFlowId;
		this.orderNo = orderNo;
		this.transDate = transDate;
		this.qbAmount = qbAmount;
		this.bqAmount = bqAmount;
	}

	public long getSdkFlowId() {
		return sdkFlowId;
	}

	public void setSdkFlowId(long sdkFlowId) {
		this.sdkFlowId = sdkFlowId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public int getQbAmount() {
		return qbAmount;
	}

	public void setQbAmount(int qbAmount) {
		this.qbAmount = qbAmount;
	}

	public int getBqAmount() {
		return bqAmount;
	}

	public void setBqAmount(int bqAmount) {
		this.bqAmount = bqAmount;
	}
	
	
	
}
