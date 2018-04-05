package com.qbao.sdk.server.job;

import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.PayKeepRate;
import com.qbao.sdk.server.metadata.entity.statistic.UserKeepRate;
import com.qbao.sdk.server.service.statistic.PayKeepRateService;
import com.qbao.sdk.server.service.statistic.UserKeepRateService;
import com.qbao.sdk.server.util.PropertiesUtil;
import com.qbao.sdk.server.util.ReportUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chenwei on 2016/1/26
 */
public class HistoryDataProcessTask {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private PayKeepRateService payKeepRateService;

    @Resource
    private UserKeepRateService userKeepRateService;
    
	@Resource
	private PropertiesUtil payConfig;
	
	private String userKeepStartDate;
	private String payKeepStartDate;

	private void loadConfData(){
		userKeepStartDate = payConfig.getString("quartztask.keeprate.startdate");
		payKeepStartDate = payConfig.getString("quartztask.payrate.startdate");
	}
	
    public void statisticHistoryData(){
    	final List<OpenMerchantEntity> appList = userKeepRateService.getAllApp();
        if (appList.isEmpty()) {
            log.info("应用列表为空！");
            return;
        }
        loadConfData();
        if (StringUtils.isEmpty(userKeepStartDate) || StringUtils.isEmpty(payKeepStartDate)) {
        	log.error("Properties content don't config \"quartztask.keeprate.startdate\", \"quartztask.payrate.startdate\"");
        	return;
        }
        delInaccurateData();
        new Thread() {
			public void run() {
				boolean isUserKeepTaskSuccess = false;
				try {
			    	processPayKeepRate(appList);
			    	isUserKeepTaskSuccess = true;

				} catch (Exception e) {
					log.error("历史数据用户留存率任务出错", e);
				} finally {
					if (!isUserKeepTaskSuccess) {
						log.error("历史数据用户留存率任务需要重新执行");
					}
				}
			};
		}.start();
		new Thread() {
			public void run() {
				boolean isPayKeepTaskSuccess = false;
				try {
			    	processAddUserKeepRate(appList);
			    	isPayKeepTaskSuccess = true;

				} catch (Exception e) {
					log.error("历史数据付费留存率任务出错", e);
				} finally {
					if (!isPayKeepTaskSuccess) {
						log.error("历史数据付费留存率任务需要重新执行");
					}
				}
			};
		}.start();
    }
    
    /**
     * 删除之前没有执行成功和不完整的留存率数据
     */
    private void delInaccurateData(){
    	String endDate = DateFormatUtils.format(getEndDate(), "yyyy-MM-dd");
    	userKeepRateService.delInaccurateData(userKeepStartDate, endDate);
    	payKeepRateService.delInaccurateData(payKeepStartDate, endDate);
    }
    
    private void processPayKeepRate(List<OpenMerchantEntity> appList) {
    	log.info("历史数据付费留存率统计开始...");
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            
            // 结束时间为前两天
            PayKeepRate payKeepRate = null;
            String date = payKeepStartDate;
            Calendar c = Calendar.getInstance();
            c.setTime(df.parse(date));
            String endDate = df.format(getEndDate());
            String keepDate = null;
            while (date.compareTo(endDate) <= 0) {
                for (OpenMerchantEntity merchant : appList) {
                	String appCode = merchant.getAppCode();
                	int num = payKeepRateService.countPayUserNumber(date, appCode);
                	if (num == 0) continue;
                    payKeepRate = new PayKeepRate();
                    payKeepRate.setKeepDate(date);
                    //today付费用户总数
                    
                    payKeepRate.setAppCode(appCode);
                    payKeepRate.setMerchantCode(merchant.getMerchantCode());
                    
                    payKeepRate.setPayUserTotal(num);
                    calendar.setTime(c.getTime());
                    for (int i = 1; i < 8; i++) {
                    	calendar.add(Calendar.DATE, 1);
                        keepDate = df.format(calendar.getTime());
                        
                        //keepDate付费用户登录总数
                        int keepNum = payKeepRateService.countKeepUserNum(appCode, date, keepDate);
                        String rate = ReportUtils.calculateRate(keepNum, num);
                        switch (i) {
                        case 1: {
                        	payKeepRate.setDay1(rate);
                        	break;
                        }
                        case 2: {
                        	payKeepRate.setDay2(rate);
                        	break;
                        }
                        case 3: {
                        	payKeepRate.setDay3(rate);
                        	break;
                        }
                        case 4: {
                        	payKeepRate.setDay4(rate);
                        	break;
                        }
                        case 5: {
                        	payKeepRate.setDay5(rate);
                        	break;
                        }
                        case 6: {
                        	payKeepRate.setDay6(rate);
                        	break;
                        }
                        case 7: {
                        	payKeepRate.setDay7(rate);
                        	break;
                        }
                        
                        }
                        

                    }
                    payKeepRateService.insert(payKeepRate, null);
                   
                }
                c.add(Calendar.DATE, 1);
                date = df.format(c.getTime());

            }
            log.info("历史数据付费留存率统计结束...");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        

    }

    /**
     * 结束时间为当前日期-2天
     * @return
     */
    private static Date getEndDate(){
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, -2);
    	return c.getTime();
    }
    
    private void processAddUserKeepRate(List<OpenMerchantEntity> appList) {
    	log.info("历史数据用户留存率开始...");
        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();

            
            // 当前统计日期
            String date = userKeepStartDate;
            calendar.setTime(df.parse(date));
            String endDate = df.format(getEndDate());
            Calendar c = Calendar.getInstance();
            //当前日期N天后的日期
            String keepDate = null;
            while (date.compareTo(endDate) <= 0) {

                for (OpenMerchantEntity merchant : appList) {
                	c.setTime(calendar.getTime());
                    String appCode = merchant.getAppCode();
                    int num = userKeepRateService.countAddUserNum(date, appCode);
                    if (num == 0) continue;
                    UserKeepRate userKeepRate = new UserKeepRate();
                    userKeepRate.setKeepDate(date);
                    userKeepRate.setAppCode(appCode);
                    userKeepRate.setMerchantCode(merchant.getMerchantCode());
                    //当前日期新增用户总数
                    
                    userKeepRate.setAddUserTotal(num);
                    for (int i = 1; i < 8; i++) {
                    	c.add(Calendar.DATE, 1);
                        keepDate = df.format(c.getTime());
                        //几天后keepDate用户登录总数
                        int keepNum = userKeepRateService.countKeepUserNum(appCode, date, keepDate);

                        String rate = ReportUtils.calculateRate(keepNum, num);
                        switch (i) {
                        case 1: {
                        	userKeepRate.setDay1(rate);
                        	break;
                        }
                        case 2: {
                        	userKeepRate.setDay2(rate);
                        	break;
                        }
                        case 3: {
                        	userKeepRate.setDay3(rate);
                        	break;
                        }
                        case 4: {
                        	userKeepRate.setDay4(rate);
                        	break;
                        }
                        case 5: {
                        	userKeepRate.setDay5(rate);
                        	break;
                        }
                        case 6: {
                        	userKeepRate.setDay6(rate);
                        	break;
                        }
                        case 7: {
                        	userKeepRate.setDay7(rate);
                        	break;
                        }
                        
                        }
                    }
                    userKeepRateService.insert(userKeepRate, null);
                }
                calendar.add(Calendar.DATE, 1);
                date = df.format(calendar.getTime());
                
            }
            log.info("历史数据用户留存率结束...");
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }


    /**
     * 获取当日新增用户数
     *
     * @param today
     * @param appCode
     * @return
     */
//    private int getAddUserNumber(Date today, String appCode) {
//        
//        return userKeepRateService.countAddUserNum(today, appCode);
//    }


//    public Date generateDateAfter(Date date, int i) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.DAY_OF_MONTH, i);
//        return calendar.getTime();
//    }


}
