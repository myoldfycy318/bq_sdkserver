package com.qbao.sdk.server.metadata.dao.mapper.pay;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.TurntableRuleEntity;

/**
 * 应用市场大转盘规则mapper
 * @author liuxingyue
 *
 */
@Repository
public interface TurntableRuleMapper  extends IBaseMapperDao<TurntableRuleEntity, Long>{
	
	/**
	 * 新增
	 * @param turntableRuleEntity
	 */
	void insertTurntableRule(@Param("entities") List<TurntableRuleEntity> turntableRuleEntities);
	
	/**
	 * 根据id删除相应规则
	 * @param id
	 */
	void deleteTurntableRuleById(@Param("id") int id);
	
	/**
	 * 根据id查找对象
	 * @param id
	 * @return
	 */
	List<TurntableRuleEntity> selectEntityById(@Param("id") int id);
	
	/**
	 * 查询时间范围包含某天的规则(每日)
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<TurntableRuleEntity> getRulesByDay(@Param("date") String date);
	
	/**
	 * 查询时间范围包含某时间段的规则(每周、每月)
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<TurntableRuleEntity> getRulesByPeriod(@Param("startDate") String startDate,@Param("endDate")String endDate,@Param("rechargeType")String rechargeType);
	
	/**
	 * 查询时间范围包含某时间段的规则(自定义时间)
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<TurntableRuleEntity> getRulesByTimePeriod(@Param("endDate")String endDate,@Param("rechargeType")String rechargeType);

	/**
	 * 查询单笔支付的规则
	 * @param date
	 * @return
	 */
	TurntableRuleEntity getRulesBySingle(@Param("date")Date date,@Param("appCode")String appCode);
	
}
