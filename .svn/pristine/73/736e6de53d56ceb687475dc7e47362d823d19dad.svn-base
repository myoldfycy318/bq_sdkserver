package com.qbao.sdk.server.metadata.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.AppVerStatusEntity;
import com.qbao.sdk.server.metadata.entity.Paginator;

public interface AppVerStatusMapper extends IBaseMapperDao<AppVerStatusEntity, Long>{

	int delete(AppVerStatusEntity entity);
	
	List<AppVerStatusEntity> query(@Param("entity")AppVerStatusEntity entity, @Param("p")Paginator p);
	
	int queryCount(AppVerStatusEntity entity);
}
