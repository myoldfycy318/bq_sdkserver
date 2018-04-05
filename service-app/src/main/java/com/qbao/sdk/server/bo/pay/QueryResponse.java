package com.qbao.sdk.server.bo.pay;
/**
 * 查询返回结果
 * @author liuxingyue
 *
 */
public class QueryResponse extends BaseResponse{
	public QueryResponse(){}
	
	public QueryResponse(boolean isSuccess){
		this.setSuccess(isSuccess);
	}
	
	public QueryResponse(boolean isSuccess,String code, String msg){
		this.setSuccess(isSuccess);
		this.setCode(code);
		this.setMsg(msg);
	}

}
