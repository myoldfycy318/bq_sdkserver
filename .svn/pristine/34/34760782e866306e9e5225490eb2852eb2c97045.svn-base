package com.qbao.sdk.server.service.impl.statistic;

import com.qbao.sdk.server.bo.PayKeepRateBo;
import com.qbao.sdk.server.bo.UserKeepRateBo;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.PayKeepRateMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserKeepRateMapper;
import com.qbao.sdk.server.service.statistic.KeepRateService;
import com.qbao.sdk.server.util.DateUtils;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chenwei on 2015/12/7
 */
@Service
public class KeepRateServiceImpl implements KeepRateService {

    @Resource
    private UserKeepRateMapper userKeepRateMapper;

    @Resource
    private PayKeepRateMapper payKeepRateMapper;

    @Override
    public List<UserKeepRateBo> getUserKeepRate(String appCode, String start, String end) {
    	Calendar c = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
    	try {
			c.setTime(sdf.parse(start));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	List<UserKeepRateBo> list = userKeepRateMapper.getUserKeepRateByApp(appCode,start,end);
    	UserKeepRateBo rate = null;
    	if (CollectionUtils.isEmpty(list)){
    		rate = createZeroUserKeepRateBo(appCode, start);
    		list.add(rate);
    	}
    	String date = start;
    	for (int i = 0; i <list.size(); i++){
    		rate = list.get(i);
    		if (rate.getKeepDate().compareTo(date) > 0){
    			rate = createZeroUserKeepRateBo(appCode, date);
    			list.add(i, rate);
    		}
    		c.add(Calendar.DATE, 1);
    		date = sdf.format(c.getTime());
    	}
    	rate = list.get(list.size() -1);
    	date = rate.getKeepDate();
    	while (date.compareTo(end) < 0) {
    		date = sdf.format(c.getTime());
    		rate = createZeroUserKeepRateBo(appCode, date);
    		list.add(rate);
    		c.add(Calendar.DATE, 1);
    		
    	}
        return list;
    }

	private UserKeepRateBo createZeroUserKeepRateBo(String appCode,
			 String date) {
		UserKeepRateBo rate;
		rate = new UserKeepRateBo();
		rate.setKeepDate(date);
		rate.setAddUserTotal(0);
		rate.setAppCode(appCode);
		rate.setDay1("0%");
		rate.setDay2("0%");
		rate.setDay3("0%");
		rate.setDay4("0%");
		rate.setDay5("0%");
		rate.setDay6("0%");
		rate.setDay7("0%");
		return rate;
	}


	private PayKeepRateBo createZeroPayKeepRateBo(String appCode,
			 String date) {
		PayKeepRateBo rate = new PayKeepRateBo();
		rate.setKeepDate(date);
		rate.setPayUserTotal(0);
		rate.setAppCode(appCode);
		rate.setDay1("0%");
		rate.setDay2("0%");
		rate.setDay3("0%");
		rate.setDay4("0%");
		rate.setDay5("0%");
		rate.setDay6("0%");
		rate.setDay7("0%");
		return rate;
	}
	
    @Override
    public List<PayKeepRateBo> getPayKeepRate(String appCode, String start, String end) {
    	Calendar c = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
    	try {
			c.setTime(sdf.parse(start));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	List<PayKeepRateBo> list = payKeepRateMapper.getPayKeepRateByAppCode(appCode,start,end);
    	PayKeepRateBo rate = null;
    	if (CollectionUtils.isEmpty(list)){
    		rate = createZeroPayKeepRateBo(appCode, start);
    		
    		list.add(rate);
    	}
    	String date = start;
    	for (int i = 0; i <list.size(); i++){
    		rate = list.get(i);
    		if (rate.getKeepDate().compareTo(date) > 0){
    			rate = createZeroPayKeepRateBo(appCode, date);
    			list.add(i, rate);
    		}
    		c.add(Calendar.DATE, 1);
    		date = sdf.format(c.getTime());
    	}
    	rate = list.get(list.size() -1);
    	date = rate.getKeepDate();
    	while (date.compareTo(end) < 0) {
    		date = sdf.format(c.getTime());
    		rate = createZeroPayKeepRateBo(appCode, date);
    		list.add(rate);
    		c.add(Calendar.DATE, 1);
    		
    	}
        return list;
    }

    @Override
    public String getUserKeepRateNextDay(String appCode, String date) {

    	String rate = userKeepRateMapper.getUserKeepRateNextDay(appCode,date);
    	if (rate == null){
    		rate = "0";
    	}
        return rate;
    }

    @Override
    public double getUsrKeepRtMonthSum(String appCode, Date start, Date end) {
        String sumStr = userKeepRateMapper.getUsrKeepRtMonthSum(appCode,start,end);
        return null==sumStr?0:Double.parseDouble(sumStr);
    }
}
