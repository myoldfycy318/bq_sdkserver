package com.qbao.sdk.server.metadata.dao.mapper;

import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.TestEntity;

@Repository
public interface TestMapper extends IBaseMapperDao<TestEntity, Long> {

}
