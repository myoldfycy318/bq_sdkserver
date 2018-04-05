package com.qbao.sdk.server.metadata.entity.pay;

import java.math.BigDecimal;

/**
 * 根据appCode和payUserId统计充值金额
 * @author liuxingyue
 *
 */
public class TurntablePayTransEntity {
	private String payTransCode;
	
	private String payUserId;
	
	private String appCode;
	
	private BigDecimal accountAmount;
	
	private BigDecimal bqAccoutAmount;

	public String getPayTransCode() {
		return payTransCode;
	}

	public void setPayTransCode(String payTransCode) {
		this.payTransCode = payTransCode;
	}

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public BigDecimal getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(BigDecimal accountAmount) {
		this.accountAmount = accountAmount;
	}

	public BigDecimal getBqAccoutAmount() {
		return bqAccoutAmount;
	}

	public void setBqAccoutAmount(BigDecimal bqAccoutAmount) {
		this.bqAccoutAmount = bqAccoutAmount;
	}
	
	

}
