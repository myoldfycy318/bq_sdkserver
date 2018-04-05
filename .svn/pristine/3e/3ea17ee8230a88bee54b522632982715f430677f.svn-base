package com.qbao.sdk.server.job;

import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.PayKeepRate;
import com.qbao.sdk.server.metadata.entity.statistic.UserKeepRate;
import com.qbao.sdk.server.service.statistic.PayKeepRateService;
import com.qbao.sdk.server.service.statistic.UserKeepRateService;
import com.qbao.sdk.server.util.DateUtils;
import com.qbao.sdk.server.util.ReportUtils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chenwei on 2016/1/4 17:17.
 * 留存率
 */
public class UserKeepRateTask {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserKeepRateService userKeepRateService;

    public void process() {
    	final List<OpenMerchantEntity> appList = userKeepRateService.getAllApp();

        if (appList.isEmpty()) {
            log.info("应用列表为空！");
            return;
        }
        // 如果今天执行出错，记录下日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final String date = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        //两个留存率可以并发执行
        
		new Thread() {
			public void run() {
				boolean isUserKeepTaskSuccess = false;
				try {
					// 新增用户留存率
					addUserKeepRateProcess(appList);
					isUserKeepTaskSuccess = true;

				} catch (Exception e) {
					log.error("用户留存率任务出错", e);
				} finally {
					if (!isUserKeepTaskSuccess) {
						log.error("用户留存率任务{}需要重新执行", date);
					}
				}
			};
		}.start();

		new Thread() {
			public void run() {
				boolean isPayKeepTaskSuccess = false;
				try {
					// 付费留存率
					statisticPayKeepRate(appList);
					isPayKeepTaskSuccess = true;

				} catch (Exception e) {
					log.error("付费留存率任务出错", e);
				} finally {
					if (!isPayKeepTaskSuccess) {
						log.error("付费留存率任务{}需要重新执行", date);
					}
				}
			};
		}.start();
        
    }

    /**
     * 新增用户留存率
     */
    private void addUserKeepRateProcess(List<OpenMerchantEntity> appList) {
        log.info("用户留存率任务开始...");

        String keepDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN);
        String yesterday = sdf.format(calendar.getTime());
        Calendar c = Calendar.getInstance();
        for (OpenMerchantEntity merchant : appList) {
            String appCode = merchant.getAppCode();
            //添加昨天的留存率记录
            addUserKeepRate(yesterday, merchant);

            c.setTime(calendar.getTime());
            for (int i = 1; i < 8; i++) {
            	c.add(Calendar.DATE, -1);
                keepDate = sdf.format(c.getTime());
                UserKeepRate userKeepRate = userKeepRateService.selectRateByAppAndDate(keepDate, appCode);
                if(null==userKeepRate){
                	continue;
                }
                int keepNum = userKeepRateService.countKeepUserNum(appCode, keepDate, yesterday);
                String rate = ReportUtils.calculateRate(keepNum, userKeepRate.getAddUserTotal());
                
                userKeepRate.setRate(rate);
                userKeepRateService.updateUserKeepRate(userKeepRate, i);
            }
        }
        log.info("用户留存率结束...");
    }

    private void addUserKeepRate(String yesterday, OpenMerchantEntity merchant) {
        String appCode = merchant.getAppCode();
        int yestdayNum = userKeepRateService.countAddUserNum(yesterday, appCode);
        // 若付费用户数为0，不往数据库中添加记录
        if (yestdayNum == 0) return ;
        UserKeepRate userKeepRate = new UserKeepRate();
        userKeepRate.setAppCode(appCode);
        userKeepRate.setKeepDate(yesterday);
        userKeepRate.setAddUserTotal(yestdayNum);
        userKeepRate.setMerchantCode(merchant.getMerchantCode());
        userKeepRateService.insert(userKeepRate, null);
    }

    @Resource
    private PayKeepRateService payKeepRateService;



    public void statisticPayKeepRate(List<OpenMerchantEntity> appList){
        log.info("付费用户留存率任务开始...");

        String keepDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String yesterday = DateFormatUtils.format(calendar, DateUtils.DATE_PATTERN);

        Calendar c = Calendar.getInstance();
        
        for (OpenMerchantEntity merchant : appList) {
            String appCode = merchant.getAppCode();
            //添加昨天的留存率记录
            
            addPayKeepRate(yesterday, merchant);

            c.setTime(calendar.getTime());
            for (int i = 1; i < 8; i++) {
            	c.add(Calendar.DATE, -1);
                keepDate = DateFormatUtils.format(c, DateUtils.DATE_PATTERN); //DateUtils.dateSdf.format(c.getTime())
                PayKeepRate payKeepRate = payKeepRateService.selectByAppCodeAndDate(keepDate, appCode);
                if(null==payKeepRate){
                	continue;
                }
                int keepNum = payKeepRateService.countKeepUserNum(appCode, keepDate, yesterday);
                String rate = ReportUtils.calculateRate(keepNum, payKeepRate.getPayUserTotal());
                
                
                payKeepRate.setRate(rate);
                payKeepRateService.updatePayKeepRate(payKeepRate, i);
            }
        }
        log.info("付费用户留存率任务结束...");
    }

    private void addPayKeepRate(String yesterday, OpenMerchantEntity merchant) {
            String appCode = merchant.getAppCode();
            int yestdayNum = payKeepRateService.countPayUserNumber(yesterday, appCode);
            // 若付费用户数为0，不往数据库中添加记录
            if (yestdayNum == 0) return ;
            PayKeepRate payKeepRate = new PayKeepRate();
            payKeepRate.setAppCode(appCode);
            payKeepRate.setKeepDate(yesterday);
            payKeepRate.setPayUserTotal(yestdayNum);
            payKeepRate.setMerchantCode(merchant.getMerchantCode());
            payKeepRateService.insert(payKeepRate, null);
    }

}
