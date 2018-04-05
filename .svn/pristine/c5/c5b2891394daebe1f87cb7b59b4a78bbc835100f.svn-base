package com.qbao.sdk.server.util;

import java.math.BigDecimal;

public class MathUtils {

	/**
	 * 精确除，结果四舍五入，保留两位小数
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double divideExact(double d1, double d2){
		BigDecimal b = new BigDecimal(d1).divide(new BigDecimal(d2), 2, BigDecimal.ROUND_HALF_UP);
		
		return b.doubleValue();
	}
	
	public static void main(String[] args) {
		int a =1, b=3;
		System.out.println("result: " + divideExact(1, 3));
	}
}
