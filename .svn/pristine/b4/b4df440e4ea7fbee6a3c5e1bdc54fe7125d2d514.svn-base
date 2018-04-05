package com.qbao.sdk.server.service.impl.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qbao.sdk.server.metadata.dao.mapper.login.OauthAccessRecordMapper;
import com.qbao.sdk.server.metadata.entity.login.OauthAccessRecordEntity;
import com.qbao.sdk.server.service.login.OauthAccessRecordService;

@Service("oauthAccessRecordService")
public class OauthAccessRecordServiceImpl implements OauthAccessRecordService {

	@Autowired
	private OauthAccessRecordMapper oauthAccessRecordMapper;

	@Override
	public void addOauthAccessRecord(OauthAccessRecordEntity accessRecord) {
		oauthAccessRecordMapper.add(accessRecord);
	}
}