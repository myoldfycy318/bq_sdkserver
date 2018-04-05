package com.qbao.sdk.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * @author Shengzhao Li
 */
public abstract class DateUtils {
	
	// 精确到秒
	public static final String DATATIMEF_STR_SEC = "yyyyMMddHHmmss";

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DEFAULT_CUR_MONTH_FORMAT = "yyyyMM";
	
	public static final String YYYYMMDD = "yyyyMMdd";
	
	public static final String YYYYMM = "yyyyMM";
	
//	public static final SimpleDateFormat monthSdf = new SimpleDateFormat(DEFAULT_CUR_MONTH_FORMAT);
	
	/**
	 * SimpleDateFormat中含有一个calendar对象，存在并发问题
	 */
//	public static final SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	
	
	/**
	 * 获取上个月的yyyyMM格式日期
	 * @return
	 */
	public static String getLastMonthyyyyMM(){
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);
		SimpleDateFormat format = new SimpleDateFormat(YYYYMM);
		return format.format(lastMonth.getTime());
	}

	/**
	 * 获取昨天的yyyyMMdd格式日期
	 * @return
	 */
	public static String getYesterdayyyyMMdd(){
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD);
		return format.format(yesterday.getTime());
	}
	
	/**
	 * 格式化日期为yyyyMM
	 * @param date
	 * @return
	 */
	public static String formatToyyyyMM(Date date) {
		if(null == date) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(YYYYMM);
		return format.format(date);
	}
	
	/**
	 * 格式化日期为yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String formatToyyyyMMdd(Date date) {
		if(null == date) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD);
		return format.format(date);
	}
	
    /**
     * Private constructor
     */
    private DateUtils() {
    }

    public static Date now() {
        return new Date();
    }


    //Create new  SimpleDateFormat
    private static SimpleDateFormat newDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.SIMPLIFIED_CHINESE);
    }

    public static String toDateTime(Date date) {
        return toDateText(date, DEFAULT_DATE_TIME_FORMAT);
    }


    public static String toDateText(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        SimpleDateFormat dateFormat = newDateFormat(pattern);
        return dateFormat.format(date);
    }
    
	public static String getCurDateFormatStr(String formatStr)
	{
		return toDateText(Calendar.getInstance().getTime(), formatStr);
	}
	
	/**
	 * 获取到次日凌晨的秒数
	 * @return
	 */
	public static Long getSecondFromMorning() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		long range = (end.getTime().getTime() - start.getTime().getTime())/1000;
		return range;
	}

	/**
	 * 比较当前日期是否在指定的日期之前
	 * 
	 * @param d
	 * @param endTime
	 * @return
	 */
	public static boolean compareCurrDateToDate(Date d, String endTime)
	{
		boolean flag = false;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date end = ft.parse(endTime);
			if (d.before(end))
			{
				flag = true;
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	public static boolean compareCurrDateToDate(Date d, Date endTime)
	{
		boolean flag = false;
		try
		{
			if (d.before(endTime))
			{
				flag = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	public static Date getInternalDateBySecond(Date d, int Second)
	{
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(d);
		now.add(Calendar.SECOND, Second);
		return now.getTime();
	}
	
	/**
	 * 按照默认formatStr的格式，转化dateTimeStr为Date类型 dateTimeStr必须是formatStr的形式
	 * 
	 * @param dateTimeStr
	 * @param formatStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr, String formatStr) {
		try {
			if (dateTimeStr == null || dateTimeStr.equals("")) {
				return null;
			}
			java.text.DateFormat sdf = new SimpleDateFormat(formatStr);
			java.util.Date d = sdf.parse(dateTimeStr);
			return d;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 统计数据对应日期所在的月份，格式yyyyMM
	 * @return
	 */
	public static String getStatisticMonth(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return DateFormatUtils.format(c, DEFAULT_CUR_MONTH_FORMAT);
		//return monthSdf.format(c.getTime());
	}
	
	/**
	 * 统计数据对应日期，格式yyyy-MM-dd
	 * @return
	 */
	public static String getStatisticDate(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return DateFormatUtils.format(c, DATE_PATTERN);
		//return dateSdf.format(c.getTime());
	}
	
	/**
	 * 当前日期减几天
	 * @previousDayCount 负的天数
	 * @return
	 */
	public static String getStatisticPreviousDate(int previousDayCount){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, previousDayCount);
		return DateFormatUtils.format(c, DATE_PATTERN);
		//return dateSdf.format(c.getTime());
	}
	
	/**
	 * 折线图开始日期
	 * @return
	 */
	public static String getStatisticFromDate(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -2);
		c.add(Calendar.MONTH, -1);
		return DateFormatUtils.format(c, DATE_PATTERN);
		//return dateSdf.format(c.getTime());
	}
	
	/**
	 * 获取给定日期的前一天日期
	 * @param dateStr
	 * @return
	 */
	public static String getPrevDayStr(String dateStr){ // 日期格式yyyy-MM-dd
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateSdf = new SimpleDateFormat(DATE_PATTERN);
		try {
			Date d = dateSdf.parse(dateStr);
			c.setTime(d);
			c.add(Calendar.DATE, -1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateSdf.format(c.getTime());
	}

//	private static final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM");
	/**
	 * 获取给定月份的最后一天日期
	 * @param dateStr
	 * @return
	 */
	public static String getPrevMonthEndDayStr(String monthStr){ // 日期格式yyyy-MM-dd
		Calendar c = Calendar.getInstance();
		SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM");
		try {
			Date d = mFormat.parse(monthStr);
			c.setTime(d);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return DateFormatUtils.format(c, "yyyy-MM-dd");
	}
	
	/**
	 * 根据给定的月份来确定最新有统计数据的日期
	 * 上个月份，取最后一天
	 * 否则取最新有统计数据的日期
	 * @param monthStr
	 * @return
	 */
	public static String getRecentDayStr(String monthStr){
		String prevDayStr = getPrevMonthEndDayStr(monthStr);
		String statisticDayStr = getStatisticPreviousDate(-2);
		if (prevDayStr.compareTo(statisticDayStr)>0){
			return statisticDayStr;
		}
		return prevDayStr;
	}
	
	/**
	 * 获取上个月最后一天的日期
	 * @param monthStr
	 * @return
	 */
	public static String getPrevMonthRecentDayStr(String monthStr){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM");
		try {
			Date d = mFormat.parse(monthStr);
			c.setTime(d);
			c.add(Calendar.MONTH, -1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return getPrevMonthEndDayStr(DateFormatUtils.format(c, "yyyy-MM"));
	}
	
	public static void main(String[] args) {
		String s = getPrevMonthRecentDayStr("2015-12");
		System.out.println("result: " + s);
		s = getRecentDayStr("2015-12");
		System.out.println("result: " + s);
		s = getPrevMonthEndDayStr("2015-12");
		System.out.println("result: " + s);
	}
} 