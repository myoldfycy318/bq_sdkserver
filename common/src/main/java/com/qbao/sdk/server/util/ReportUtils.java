package com.qbao.sdk.server.util;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenwei on 2016/1/6
 */
public class ReportUtils {

    public static Date generateDateBefore(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -i - 1);
        return calendar.getTime();
    }

    private static DecimalFormat df = new DecimalFormat("0.00");
    
    public static String calculateRate(int keepNum, int num) {
        
        if(num==0||keepNum==0){
            return "0";
        }
        double result = (double)keepNum/num;
        return df.format(result*100);
    }

    public static String getZoom(String yesterday) {
        return yesterday.substring(0, 7).replaceAll("-", "");
    }

}
