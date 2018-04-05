package com.qbao.sdk.server.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.qbao.sdk.server.metadata.dao.mapper.TestMapper;
import com.qbao.sdk.server.metadata.entity.TestEntity;
import com.qbao.sdk.server.service.TestService;

@Service
public class TestServiceImpl implements TestService {
	
	@Resource
	private TestMapper testMapper;

	@Override
	public void add(String username) {
		
		TestEntity en = new TestEntity();
		en.setUsername(username);
		testMapper.add(en);
	}

}
