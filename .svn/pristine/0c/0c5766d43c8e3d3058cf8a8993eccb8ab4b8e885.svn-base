package com.qbao.sdk.server.controller;

import com.qbao.sdk.server.bo.PayKeepRateBo;
import com.qbao.sdk.server.bo.UserKeepRateBo;
import com.qbao.sdk.server.service.statistic.KeepRateService;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.view.AjaxResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by chenwei on 2015/12/2
 * 留存率查询
 */
@Controller
@RequestMapping("/keepRate")
public class KeepRateController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private KeepRateService keepRateService;

    /**
     * 新增用户留存率
     * @param appCode
     * @param start
     * @param end
     * @return
     */
    @RequestMapping("/getUserKeepRate")
    @ResponseBody
    public AjaxResult getUserKeepRate(String appCode, String start, String end) {
        List<UserKeepRateBo> list = keepRateService.getUserKeepRate(appCode, start, end);
        return AjaxResult.success(list);
    }

    /**
     * 付费用户留存率
     * @param appCode
     * @param start
     * @param end
     * @return
     */
    @RequestMapping("/getPayKeepRate")
    @ResponseBody
    public AjaxResult getPayKeepRate(String appCode, String start, String end) {
        List<PayKeepRateBo> list = keepRateService.getPayKeepRate(appCode, start, end);
        return AjaxResult.success(list);
    }


    /**
     * 获取次日留存率
     *
     * @param appCode
     * @param date
     * @return
     */
    @RequestMapping("/getUserKeepRateNextDay")
    @ResponseBody
    public AjaxResult getUserKeepRateNextDay(String appCode, String date) {
    	SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
    	try {
    		// 查询的是昨天的一天后留存率
			Date d = sdf.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DATE, -1);
					
			date = sdf.format(c.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String rate = keepRateService.getUserKeepRateNextDay(appCode, date);
        return AjaxResult.success(rate);
    }

    /**
     * 月平均留存率
     * @param appCode
     * @param month
     * @return
     */
    @RequestMapping("/getUsrKeepRtMonthAverage")
    @ResponseBody
    public AjaxResult getUsrKeepRtMonthAverage(String appCode, String month) {
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        Date mon;
        try {
            mon = df.parse(month);

        } catch (ParseException e) {
            e.printStackTrace();
            return AjaxResult.failed("日期格式错误");
        }

        //每个月第一天和最后一天各减一天
        log.info("查询月份:{}",df.format(mon));
        Date start = getLastDayOfLastMon(mon);
        Date end = getLastButOneOfMon(mon);
        log.info("开始日期：{}，结束日期：{}",df.format(start),df.format(end));

        double sum = keepRateService.getUsrKeepRtMonthSum(appCode,start,end);
        //获取当月天数
        int days = getDaysOfMonth(mon);
        double avg = sum/days;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return AjaxResult.success(decimalFormat.format(avg));
    }

    /**
     * 当月天数
     * @param mon
     * @return
     */
    private int getDaysOfMonth(Date mon) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mon);
        
        // 如果当前月份没有过完或日期<=3号，取-3天的日期
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        if (c.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
        	return c.get(Calendar.DATE);
        } else if (c.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)){
        	return 1;
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取本月倒数第二天
     * @param mon
     * @return
     */
    private Date getLastButOneOfMon(Date mon) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mon);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
        
        // 如果当前月份没有过完或日期<=3号，取-3天的日期
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        if (c.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
        	return c.getTime();
        }
        return calendar.getTime();
    }

    /**
     * 获取上月最后一天
     *
     * @param mon
     * @return
     */
    private Date getLastDayOfLastMon(Date mon) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mon);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }


}
