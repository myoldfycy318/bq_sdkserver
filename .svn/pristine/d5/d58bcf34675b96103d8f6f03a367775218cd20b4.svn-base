package com.qbao.sdk.server.service.impl.statistic;

import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserKeepRateMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.UserStatisticMapper;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.UserKeepRate;
import com.qbao.sdk.server.service.statistic.UserKeepRateService;
import com.qbao.sdk.server.service.statistic.UserStatisticService;
import com.qbao.sdk.server.util.ReportUtils;
import com.qbao.sdk.server.util.StatisticUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenwei
 */
@Service
public class UserKeepRateServiceImpl implements UserKeepRateService {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Resource
    private UserKeepRateMapper userKeepRateMapper;

    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    private final String TABLE_NAME_PREFIX = "oauth_access_record";
    
	@Resource
	private UserStatisticMapper userStatisticMapper;
	
	@Resource
	private UserStatisticService userStatisticServiceImpl;

    @Override
    public int countKeepUserNum(String appCode, String keepDate,String yesterday) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("appCode",appCode);
        param.put("keepDate", keepDate);
        param.put("yesterday", yesterday);
        //表名
        String zoom = ReportUtils.getZoom(yesterday);
        String tableName = TABLE_NAME_PREFIX + zoom;

        param.put("tableName",tableName);
        OpenMerchantEntity merchant = userStatisticServiceImpl.getMerchantInfo(appCode);
        if (merchant == null) {
        	log.error("应用{}没有查询到对应的信息", appCode);
        	return 0;
        }
        String registerTableName = StatisticUtils.getRegisterUserTableName(merchant.getMerchantUserId());
        param.put("registerTableName",registerTableName);
        Integer result = userKeepRateMapper.countKeepUserNum(param);
        return result==null?0:result;
    }


    @Override
    public List<OpenMerchantEntity> getAllApp() {
        return merchantInfoMapper.getAllApp();
    }

    @Override
    @Transactional
    public void insert(UserKeepRate userKeepRate,Date date) {
//        String zoom = ReportUtils.getZoom(date);
        userKeepRateMapper.insertUserKeepRate(userKeepRate,null);
    }

    @Override
    public UserKeepRate selectRateByAppAndDate(String keepDate, String appCode) {
//        String zoom = ReportUtils.getZoom(keepDate);
        return userKeepRateMapper.selectRateByAppAndDate(keepDate,appCode,null);
    }

    @Override
    @Transactional
    public void updateUserKeepRate(UserKeepRate userKeepRate, int i) {
//        String zoom =ReportUtils.getZoom(userKeepRate.getKeepDate());
        userKeepRateMapper.updateUserKeepRate(userKeepRate,i,null);
    }

    @Override
    public int countAddUserNum(String keepDate, String appCode) {
    	// 仅日查询
    	OpenMerchantEntity merchant = userStatisticServiceImpl.getMerchantInfo(appCode);
    	if (merchant == null) return 0;
		AppUserDayStatisticInfo app = userStatisticMapper.getRecentStatisticInfo(merchant.getMerchantCode(), appCode, keepDate);
		if (app==null) return 0;
		// 查询出来的结果中日期小于传进来的日期，说明这一天没有用户登陆数据（表中没有这天记录）
		if (keepDate.compareTo(app.getDate())> 0) {
			//app.setDate(dateStr);
			app.setLoginUsersSum(0);
			app.setRegisterUsersSum(0);
		}
        //Integer result = userKeepRateMapper.countAddUserNum(DateFormatUtils.format(keepDate,"yyyy-MM-dd"), appCode);
        //return result==null?0:result;
		
		return app.getRegisterUsersSum();
    }


	@Override
	public void delInaccurateData(String startDate, String endDate) {
		userKeepRateMapper.del(startDate, endDate);
		
	}
}
