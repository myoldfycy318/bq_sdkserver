/**
 * 
 */
package com.qbao.sdk.server.bo.pay;

/**
 * @author mazhongmin
 *
 */
public class DopayResult {

	private long sdkflowId;
	
	private String bizCode;
	
	public DopayResult(long sdkflowId,String bizCode){
		this.sdkflowId = sdkflowId;
		this.bizCode = bizCode;
	}

	public long getSdkflowId() {
		return sdkflowId;
	}

	public void setSdkflowId(long sdkflowId) {
		this.sdkflowId = sdkflowId;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	
}
