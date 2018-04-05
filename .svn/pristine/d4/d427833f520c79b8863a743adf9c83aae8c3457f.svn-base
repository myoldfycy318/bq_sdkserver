/**
 * 
 */
package com.qbao.sdk.server.bo.pay;

/**
 * @author mazhongmin
 * SDK返回信息
 */
public class SdkPayResponse {

	// 1000 成功 1005 失败
	private String responseCode;
	
	//错误码
	private String errorCode;
	
	//错误信息
	private String errorMsg;
	
	//返回数据
	private Object data;
	
	public SdkPayResponse(String responseCode){
		this.responseCode = responseCode;
	}
	
	public SdkPayResponse(String responseCode,String errorCode,String errorMsg){
		this.responseCode = responseCode;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public SdkPayResponse(String responseCode, String errorCode, String errorMsg, Object data) {
		super();
		this.responseCode = responseCode;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.data = data;
	}
	

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
