/**
 * 
 */
package com.qbao.sdk.server.metadata.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.BqAutoSendEntity;


/**
 * @author mazhongmin
 *
 */
@Repository
public interface AutoSendBqMapper extends IBaseMapperDao<BqAutoSendEntity, Long>{

	/**
	 * 每个应用下每个用户一天消费的宝币、宝券汇总
	 * @param status  流水状态
	 * @param transDate  交易日期
	 * @param start 查询记录开始位置
	 * @param size 查询记录数量
	 * @return
	 */
	List<BqAutoSendEntity> sumTransConsume(@Param("seq") String seq, @Param("status") String status,@Param("transDate") String transDate,
			@Param("start") int start,@Param("size") int size);
	
	
	/**
	 * 记录总数
	 * @param status  流水状态
	 * @param transDate  交易日期
	 * @return
	 */
	int getTransConsumeTotal(@Param("seq") String seq, @Param("status") String status,@Param("transDate") String transDate);
	
	
	/**
	 * 批量插入汇总记录
	 * @param entity  汇总记录实体对象
	 */
	void insertSumTrans(@Param("entity") BqAutoSendEntity entity);
	
	/**
	 * 根据id查询数据总量
	 * @param id
	 * @return
	 * 
	 */
	int getTransConsumeById(@Param("id") String id);
	
	/**
	 * 查询指定天的每个游戏下每个用户宝币消费总数
	* @Title: sumTransConsumeV2 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param  @param seq
	* @param  @param transDate
	* @param  @return
	* @return List<BqAutoSendEntity>    返回类型 
	* @throws
	 */
	List<BqAutoSendEntity> sumTransConsumeV2(@Param("seq") String seq,@Param("transDate") String transDate);
}
