package com.qbao.sdk.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.qbao.sdk.server.constants.AppVerStatusEnum;
import com.qbao.sdk.server.metadata.dao.mapper.AppVerStatusMapper;
import com.qbao.sdk.server.metadata.dao.mapper.pay.MerchantInfoMapper;
import com.qbao.sdk.server.metadata.entity.AppVerStatusEntity;
import com.qbao.sdk.server.metadata.entity.Paginator;
import com.qbao.sdk.server.metadata.entity.VerStatusEntity;
import com.qbao.sdk.server.metadata.entity.pay.OpenMerchantEntity;
import com.qbao.sdk.server.service.AppVerStatusService;
import com.qbao.sdk.server.util.RedisUtil;

@Service
public class AppVerStatusServiceImpl implements AppVerStatusService {
	
	private static final String APPLIST_PREFIX = "sdkserver:applist";
//	
//	private static final String APPSTATUS_PREFIX = "sdkserver:appstatus:";
	
	@Resource
	private AppVerStatusMapper appVerStatusMapper;
	
	@Resource
	private MerchantInfoMapper merchantInfoMapper;
	
	@Resource
	private RedisUtil redisUtil;
	
	@Override
	public void add(AppVerStatusEntity entity) {
		// TODO Auto-generated method stub
//		String key = APPSTATUS_PREFIX + entity.getAppCode() + ":" + entity.getAppVer();
//		redisUtil.setex(key, 60 * 60, String.valueOf(entity.getStatus()));
		appVerStatusMapper.add(entity);;
	}

	@Override
	public int del(AppVerStatusEntity entity) {
		// TODO Auto-generated method stub
//		String key = APPSTATUS_PREFIX + entity.getAppCode() + ":" + entity.getAppVer();
//		redisUtil.del(key);
		return appVerStatusMapper.delete(entity);
	}

	@Override
	public List<AppVerStatusEntity> query(AppVerStatusEntity entity, Paginator p) {
		List<AppVerStatusEntity> appList = appVerStatusMapper.query(entity, p);
		
		AppVerStatusEntity temp = null;
		List<AppVerStatusEntity> newAppList = new ArrayList<AppVerStatusEntity>();
		List<VerStatusEntity> list = null;
		VerStatusEntity ver = null;
		for (AppVerStatusEntity app : appList) {
			// 清除appid属性等
			app.setAppId(0);
			ver = new VerStatusEntity();
			ver.setAppCode(app.getAppCode());
			ver.setAppVer(app.getAppVer());
			ver.setStatus(AppVerStatusEnum.getFromKey(app.getStatus()).desc);
			if (temp == null || !app.getAppCode().equals(temp.getAppCode())) {
				temp = app;
				list = new ArrayList<VerStatusEntity>();
				list.add(ver);
				temp.setList(list);
				newAppList.add(temp);
			} else {
				list.add(ver);
			}
			
			
		}
		return newAppList;
	}

	@Override
	public void update(AppVerStatusEntity entity) {
		// TODO Auto-generated method stub
//		String key = APPSTATUS_PREFIX + entity.getAppCode() + ":" + entity.getAppVer();
//		redisUtil.setex(key, 60 * 60, String.valueOf(entity.getStatus()));
		appVerStatusMapper.update(entity);
	}

	@Override
	public List<OpenMerchantEntity> queryAppList() {
		String s = redisUtil.get(APPLIST_PREFIX);
		List<OpenMerchantEntity> appList = null;
		if (StringUtils.isEmpty(s)){
			appList = merchantInfoMapper.getAllApp();
			List<OpenMerchantEntity> newAppList = new ArrayList<OpenMerchantEntity>();
			OpenMerchantEntity temp = null;
			for (OpenMerchantEntity app : appList){
				temp = new OpenMerchantEntity();
				// 将不用的字段屏蔽掉
				temp.setAppCode(app.getAppCode());
				temp.setAppName(app.getAppName());
				newAppList.add(temp);
				
				appList = newAppList;
			}
			redisUtil.setex(APPLIST_PREFIX, 60 * 60, JSONArray.toJSONString(appList));
		} else {
			appList = JSONArray.parseArray(s, OpenMerchantEntity.class);
		}
		return appList;
	}

	@Override
	public int queryCount(AppVerStatusEntity entity) {
		// TODO Auto-generated method stub
		return appVerStatusMapper.queryCount(entity);
	}

}
