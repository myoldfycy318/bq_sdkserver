package com.qbao.sdk.server.controller.statistic;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qbao.sdk.server.controller.BaseController;
import com.qbao.sdk.server.metadata.entity.statistic.AppDownloadStatisticInfo;
import com.qbao.sdk.server.metadata.entity.statistic.SdkStatisticOverviewEntity;
import com.qbao.sdk.server.request.statistic.SopOverviewRequest;
import com.qbao.sdk.server.service.statistic.SdkTransStatisticService;
import com.qbao.sdk.server.service.statistic.UserStatisticService;
import com.qbao.sdk.server.view.AjaxResult;

/**
 * 流水查询相关  统计概况、用户产出
 * @author xuefeihu
 *
 */
@Controller
@RequestMapping("trans/statistic")
public class SdkTransStatisticController extends BaseController {
	
	@Resource
	private SdkTransStatisticService sdkTransStatisticServiceImpl;

	/**
	 * 开放平台-统计概况
	 */
	@RequestMapping("/overview")
	@ResponseBody
	public AjaxResult overview(SopOverviewRequest sopOverviewRequest){
		
		List<SdkStatisticOverviewEntity> dataList = null;
		try {
			dataList = sdkTransStatisticServiceImpl.overview(sopOverviewRequest);
		} catch (Exception e) {
			log.error("查询统计概况数据出错", e);
			return AjaxResult.failed(e.getMessage());
		}
		if (CollectionUtils.isEmpty(dataList)) {
			return AjaxResult.failed("没有查询到数据");
		}
		return AjaxResult.success(dataList);
	}
	
	/**
	 * 开放平台-用户产出
	 */
	@RequestMapping("/userOutput")
	@ResponseBody
	public AjaxResult userOutput(){
		
		return null;
	}
	
	@Resource
	private UserStatisticService userStatisticServiceImpl;
	
	@RequestMapping("/queryUserPayData")
	@ResponseBody
	public AjaxResult queryUserPayData(String appCode, String date){
		// 请求IP校验
		
		AppDownloadStatisticInfo day = null;
		try {
			day = sdkTransStatisticServiceImpl.queryUserPayData(appCode, date);
		} catch (Exception e) {
			log.error("查询用户某天的支出数据出错", e);
		}
		
		if (day != null) {
			return AjaxResult.success(day);
		}
		return AjaxResult.failed("查询用户某天的支出数据出错");
	}
	
	@RequestMapping("/queryUserPayRangeData")
	@ResponseBody
	public AjaxResult queryUserPayRangeData(String appCode, String fromDate, String toDate){

		List<AppDownloadStatisticInfo> dayList = null;
		try {
			dayList = sdkTransStatisticServiceImpl.queryUserPayRangeDate(appCode, fromDate, toDate);
		} catch (Exception e) {
			log.error("查询某个日期范围的用户产出数据出错", e);
		}
		if (dayList!= null && !dayList.isEmpty()) {
			
			
			return AjaxResult.success(dayList);
		}
		
		return AjaxResult.failed("查询某个日期范围的用户产出数据出错");
	}
}
