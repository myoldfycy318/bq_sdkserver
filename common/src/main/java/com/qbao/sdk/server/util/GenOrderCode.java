package com.qbao.sdk.server.util;

import java.util.Date;
import java.util.Random;

/**
 * 生成唯一ID
 * 
 * @author Administrator
 *
 */
public class GenOrderCode {
	
	private static Date date = new Date();
	private static StringBuilder buf = new StringBuilder();

	public static synchronized String next() {
		
		buf.delete(0, buf.length());
		date.setTime(System.currentTimeMillis());
		String str = String.format("%1$tY%1$tm%1$td%1$tk%1$tM%1$tS%2$05d",
				date, genSixNum());
		return str;
	}
	
	/**
	 *  随机生成6位数
	 *  
	 * @return
	 */
	private static int genSixNum(){
		int[] array = {0,1,2,3,4,5,6,7,8,9};
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
		    int index = rand.nextInt(i);
		    int tmp = array[index];
		    array[index] = array[i - 1];
		    array[i - 1] = tmp;
		}
		int result = 0;
		for(int i = 0; i < 6; i++)
		    result = result * 10 + array[i];
		
		return result;
	}
	
}
