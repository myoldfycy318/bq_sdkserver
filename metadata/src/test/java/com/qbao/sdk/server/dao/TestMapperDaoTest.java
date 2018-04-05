package com.qbao.sdk.server.dao;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qbao.sdk.server.metadata.dao.mapper.TestMapper;
import com.qbao.sdk.server.metadata.entity.TestEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/datasource-config.xml")
public class TestMapperDaoTest {
	
	@Resource
	private TestMapper testMapper;
	
	@Test
	public void testAdd() {
		TestEntity en = new TestEntity();
		en.setUsername("wxt");
		testMapper.add(en);
		Assert.assertTrue(en.getId() > 0);
		
	}

}
