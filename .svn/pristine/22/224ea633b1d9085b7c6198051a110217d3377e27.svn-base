package com.qbao.sdk.server.controller.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qbao.sdk.server.controller.BaseController;
import com.qbao.sdk.server.metadata.entity.statistic.AppUserDayStatisticInfo;
import com.qbao.sdk.server.service.statistic.UserStatisticService;
import com.qbao.sdk.server.view.AjaxResult;

/**
 * 应用趋势、用户获取、用户活跃界面入口类
 * @author lilongwei
 *
 */
@Controller
@RequestMapping("/statistic")
public class SdkUserStatisticController extends BaseController{

	
	@Resource
	private UserStatisticService userStatisticServiceImpl;
	
	@RequestMapping("/queryUserStatisticInfo")
	@ResponseBody
	public AjaxResult queryUserStatisticInfo(
			String merchantCode, String appCode, String monthStr, String dateStr){
		// 请求IP校验
		
		AppUserDayStatisticInfo statisticInfo = null;
		try {
			statisticInfo = userStatisticServiceImpl.queryUserStatisticInfo(merchantCode, appCode, monthStr, dateStr);
		} catch (Exception e) {
			log.error("查询用户统计数据出错", e);
		}
		
		if (statisticInfo != null) {
			return AjaxResult.success(statisticInfo);
		}
		return AjaxResult.failed("没有查询到用户统计数据");
	}
	
	@RequestMapping("/queryRangeUserStatisticInfo")
	@ResponseBody
	public AjaxResult queryRangeUserStatisticInfo(
			String merchantCode, String appCode, String fromDate, String toDate){

		List<AppUserDayStatisticInfo> statisticInfos = null;
		try {
			statisticInfos = userStatisticServiceImpl.queryRangeUserStatisticInfo(merchantCode, appCode, fromDate, toDate);
		} catch (Exception e) {
			log.error("查询某个日期范围的用户统计数据出错", e);
		}
		if (statisticInfos!= null && !statisticInfos.isEmpty()) {
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < statisticInfos.size(); i++) {
				AppUserDayStatisticInfo app = statisticInfos.get(i);
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("date", app.getDate());
				m.put("loginUserCount", app.getLoginUsersSum());
				m.put("registerUserCount", app.getRegisterUsersSum());
				m.put("userCount", app.getUsersSum());
				dataList.add(m);
			}
			
			return AjaxResult.success(dataList);
		}
		// 请求IP校验
		return AjaxResult.failed("没有查询到日期段的用户统计数据");
	}
	
}
