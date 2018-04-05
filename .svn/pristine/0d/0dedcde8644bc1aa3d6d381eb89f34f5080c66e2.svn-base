package com.qbao.sdk.server.util;

/**
 * 数据统计功能工具类
 * @author lilongwei
 *
 */
public class StatisticUtils {

	public static final String REGISTER_USER_TABLE_PREFIX = "sdk_register_users";
	
	/**
	 * 注册用户表（根据商户id后两位分表）
	 * @param merchantId
	 * @return
	 */
	public static String getRegisterUserTableName(long merchantId){
		
		int mod = (int) (merchantId % 100);
		String tableNamePostfix = null;
		if (mod < 10){
			tableNamePostfix = "0" + String.valueOf(mod);
		} else {
			tableNamePostfix = String.valueOf(mod);
		}
		
		return REGISTER_USER_TABLE_PREFIX + tableNamePostfix;
	}
}
