package com.qbao.sdk.server.service.login;

import com.qbao.sdk.server.metadata.entity.login.OauthAccessRecordEntity;

public interface OauthAccessRecordService {
	/**
	 * 添加oauth访问记录
	 * 
	 * @param accessRecord
	 *            访问记录对象
	 */
	void addOauthAccessRecord(OauthAccessRecordEntity accessRecord);
}