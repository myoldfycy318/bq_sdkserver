package com.qbao.sdk.server.service.impl.statistic;

import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.dao.mapper.statistic.PayKeepRateMapper;
import com.qbao.sdk.server.metadata.entity.statistic.PayKeepRate;
import com.qbao.sdk.server.service.statistic.PayKeepRateService;
import com.qbao.sdk.server.util.ReportUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;

/**
 * Created by chenwei on 2016/1/5
 */
@Service
public class PayKeepRateServiceImpl implements PayKeepRateService {

    @Resource
    private PayKeepRateMapper payKeepRateMapper;

	@Resource
	private MerchantInfoMapper merchantInfoMapper;
	
    @Override
    @Transactional
    public void insert(PayKeepRate payKeepRate, Date yesterday) {
//        String zoom = ReportUtils.getZoom(yesterday);
        payKeepRateMapper.insert(payKeepRate,null);
    }

    @Override
    public int countKeepUserNum(String appCode, String keepDate, String yesterday) {
        String oauthZoom = ReportUtils.getZoom(yesterday);
        String payZoom = ReportUtils.getZoom(keepDate);
        String keepDateStr = keepDate.replaceAll("-", "");
        Integer result = payKeepRateMapper.countKeepUserNum(appCode, keepDateStr, yesterday,oauthZoom,payZoom);
        return result==null?0:result;
    }

    @Override
    public PayKeepRate selectByAppCodeAndDate(String keepDate, String appCode) {
//        String zoom = ReportUtils.getZoom(keepDate);
        return payKeepRateMapper.selectByAppCodeAndDate(keepDate,appCode,null);
    }

    @Override
    @Transactional
    public void updatePayKeepRate(PayKeepRate payKeepRate,int i) {
//        String zoom = ReportUtils.getZoom(payKeepRate.getKeepDate());
        payKeepRateMapper.updatePayKeepRate(payKeepRate,null,i);
    }

    @Override
    public int countPayUserNumber(String date, String appCode) {
        String zoom = ReportUtils.getZoom(date);
        Integer result = payKeepRateMapper.countPayUserNumber(date.replaceAll("-", ""),zoom,appCode);
        return result==null?0:result;
    }

	@Override
	public void delInaccurateData(String startDate, String endDate) {
		payKeepRateMapper.del(startDate, endDate);
		
	}

}
