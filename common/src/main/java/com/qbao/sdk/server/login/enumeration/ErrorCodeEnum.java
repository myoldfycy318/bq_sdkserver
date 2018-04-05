package com.qbao.sdk.server.login.enumeration;

public enum ErrorCodeEnum {
	appCode为空(2001), 
	无效的appCode(2002), 
	该应用也经停用(2003),
	有必填参数为空(2004),
	字符串解密失败(2005),
	无效的密码或authCode(2006), 
	无效的authCode(2007), 
	无效的用户名(2008),
	密码不正确(2009),
	无效的token(2010),
	tonken对应的客户端信息不匹配(2011),
	tonken已经过期(2012), 
	获取授权code失败(2013), 
	token为空(2014),
	key的长度超过32位(2015),
	非预期错误(9999);
	
	
	public final int code;

	private ErrorCodeEnum(int code) {
		this.code = code;
	}
}
