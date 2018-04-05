package com.qbao.sdk.server.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qbao.sdk.server.metadata.entity.AppVerStatusEntity;
import com.qbao.sdk.server.metadata.entity.Paginator;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.service.AppVerStatusService;
import com.qbao.sdk.server.service.statistic.UserStatisticService;
import com.qbao.sdk.server.view.AjaxResult;

/**
 * 应用状态后台管理
 * @author lilongwei
 *
 */
@Controller
@RequestMapping("/appver/status")
public class AppVerStatusController extends BaseController{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AppVerStatusService appVerStatusServiceImpl;
	
	@Resource
	private UserStatisticService userStatisticServiceImpl;
	
	public AjaxResult addAppVerStatus(AppVerStatusEntity entity){
		// 参数校验
		if (entity == null || StringUtils.isBlank(entity.getAppCode())
				|| StringUtils.isBlank(entity.getAppVer())
				|| entity.getStatus() < 1 || entity.getStatus() >= 100) {
			return AjaxResult.failed("提供的参数不完整");
		}
		// appid和app_name查询open_merchant
		OpenMerchantEntity app = userStatisticServiceImpl.getMerchantInfo(entity.getAppCode());
		if (app == null) {
			return AjaxResult.failed("没有查询到应用信息");
		} else {
			entity.setAppId(app.getAppId());
			entity.setAppName(app.getAppName());
		}
		try {
			Paginator p = new Paginator();
			p.setStart(0);
			p.setPageSize(10);
			List<AppVerStatusEntity> list = appVerStatusServiceImpl.query(entity, p);
			if (!CollectionUtils.isEmpty(list)) {
				log.error("查询出来的应用状态记录：{}", list);
				return AjaxResult.failed("应用状态已存在，请先删除后再添加");
			}
			appVerStatusServiceImpl.add(entity);
			return AjaxResult.success("success");
		} catch (Exception e) {
			log.error("添加一条应用状态失败", e);
		}
		return AjaxResult.failed("添加失败");
	}
	
	@RequestMapping("/main")
	@ResponseBody
	public AjaxResult handle(String method, AppVerStatusEntity entity, Paginator p){
		if ("add".equals(method)){
			return addAppVerStatus(entity);
		} else if ("delete".equals(method)){
			return delAppVerStatus(entity);
		} else if ("update".equals(method)){
			return updateAppVerStatus(entity);
		} else if ("query".equals(method)){
			return query(entity, p);
		} else if ("queryCount".equals(method)){
			return queryCount(entity);
		} else {
			log.error("调用应用状态后台管理接口提供参数不正确,method:{}");
		}
		
		return AjaxResult.failed("Error");
	}
	

	public AjaxResult delAppVerStatus(AppVerStatusEntity entity){
		
		if (entity == null || StringUtils.isBlank(entity.getAppCode())
				|| StringUtils.isBlank(entity.getAppVer())) {
			return AjaxResult.failed("提供的参数不完整");
		}
		// appid和app_name查询open_merchant
		OpenMerchantEntity app = userStatisticServiceImpl.getMerchantInfo(entity.getAppCode());
		if (app == null) {
			return AjaxResult.failed("没有查询到应用信息");
		} else {
			entity.setAppId(app.getAppId());
			//entity.setAppName(app.getAppName());
		}
		try {
			int line = appVerStatusServiceImpl.del(entity);
			if (line == 1) return AjaxResult.success("success");
		} catch (Exception e) {
			log.error("删除一条应用状态失败", e);
		}
		return AjaxResult.failed("删除失败");
	}
	

	public AjaxResult updateAppVerStatus(AppVerStatusEntity entity){
		if (entity == null || StringUtils.isBlank(entity.getAppCode())
				|| StringUtils.isBlank(entity.getAppVer())
				|| entity.getStatus() < 1 || entity.getStatus() >= 100) {
			return AjaxResult.failed("提供的参数不完整");
		}
		// appid和app_name查询open_merchant
		OpenMerchantEntity app = userStatisticServiceImpl.getMerchantInfo(entity.getAppCode());
		if (app == null) {
			return AjaxResult.failed("没有查询到应用信息");
		} else {
			entity.setAppId(app.getAppId());
			//entity.setAppName(app.getAppName());
		}
		try {
			 appVerStatusServiceImpl.update(entity);
			return AjaxResult.success("success");
		} catch (Exception e) {
			log.error("修改一条应用状态失败", e);
		}
		return AjaxResult.failed("修改失败");
	}
	
	/**
	 * 提供应用版本信息，返回结果只有一条记录；否则会有多条记录
	 * @param entity
	 * @return
	 */
	public AjaxResult query(AppVerStatusEntity entity, Paginator p){
		// entity == null查询全部应用，不为null时必须提供应用编码
		//if (entity != null && StringUtils.isBlank(entity.getAppCode())) return AjaxResult.failed("提供的参数不完整");
		if (entity != null && !StringUtils.isBlank(entity.getAppCode())
				//|| StringUtils.isBlank(entity.getAppVer()) || entity.getStatus() < 1
				) {
			// appid和app_name查询open_merchant
			OpenMerchantEntity app = userStatisticServiceImpl.getMerchantInfo(entity.getAppCode());
			if (app == null) {
				return AjaxResult.failed("没有查询到应用信息");
			} else {
				entity.setAppId(app.getAppId());
				//entity.setAppName(app.getAppName());
			}
			
		}
		
		try {
			List<AppVerStatusEntity> list = appVerStatusServiceImpl.query(entity, p);
			if (CollectionUtils.isEmpty(list)) {
				return AjaxResult.failed("没有查询到数据");
			} else {
				
				return AjaxResult.success(list);
			}
		} catch (Exception e) {
			log.error("查询失败", e);
		}
		return AjaxResult.failed("查询失败");
	}
	
	public AjaxResult queryCount(AppVerStatusEntity entity){
		int count = 0;
		try {
			count= appVerStatusServiceImpl.queryCount(entity);
			return AjaxResult.success(count);
		} catch (Exception e) {
			log.error("获取应用总数失败", e);;
		}
		
		return AjaxResult.failed("获取应用总数失败");
	}
	@RequestMapping("/appList")
	@ResponseBody
	public AjaxResult getAppList(){
		List<OpenMerchantEntity> appList = appVerStatusServiceImpl.queryAppList();
		if (CollectionUtils.isEmpty(appList)) {
			return AjaxResult.failed("没有应用列表");
		} else {
			return AjaxResult.success(appList);
		}
	}
}
