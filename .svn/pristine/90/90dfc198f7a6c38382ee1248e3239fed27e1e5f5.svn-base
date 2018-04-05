package com.qbao.sdk.server.metadata.dao.mapper.pay;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qbao.sdk.server.metadata.dao.IBaseMapperDao;
import com.qbao.sdk.server.metadata.entity.pay.PayTransEntity;
import com.qbao.sdk.server.metadata.entity.pay.QueryParamEntity;
import com.qbao.sdk.server.metadata.entity.pay.TurntablePayTransEntity;
import com.qbao.sdk.server.metadata.entity.statistic.SdkDaySumEntity;


@Repository
public interface PayTransMapper extends IBaseMapperDao<PayTransEntity, Long> {
	
	/**
	 * 查询单个计费点日流水
	 * @param suffix
	 * @param appCode
	 * @param chargePointCode
	 * @return
	 */
	SdkDaySumEntity queryDaySumTrans(@Param("suffix") String suffix, @Param("transDate") String transDate, 
			@Param("appCode") String appCode, @Param("chargePointCode") String chargePointCode );
	
	/**
	 * 新增支付请求对象
	 * @param grantEntity
	 */
	Integer addPayTransRequest(@Param("entity")PayTransEntity payTransEntity,@Param("suffix") String suffix);
	
	/**
	 * 根据商户编码和商户交易流水号得到支付交易对象
	 * @param merchantCode 商户编码
	 * @param merchantTransCode 商户交易流水号
	 * @return
	 */
	PayTransEntity getPayTransReqByMertCodeTransCode(@Param("appCode")String appCode
			,@Param("bizCode")String bizCode,@Param("suffix") String suffix);
	
	
	/**
	 * 根据支付ID得到支付交易对象
	 * @param payTransId 支付ID
	 * @return
	 */
	PayTransEntity getPayTransReqById(@Param("payTransId")Long payTransId,@Param("suffix") String suffix);
	
	/**
	 * 更新支付结果
	 * @param payTransEntity
	 */
	void updatePayTransByPayTransId(@Param("entity")PayTransEntity payTransEntity,@Param("suffix") String suffix);
	
	/**
	 * 支付明细查询
	 * @param queryParamEntity
	 * @return
	 */
	PayTransEntity getPayRequest(@Param("entity")QueryParamEntity queryParamEntity,@Param("suffix") String suffix);
	
	/**
	 * 根据appCode和时间范围查询支付成功的流水信息，并根据userId和appCode分组
	 * @param appCode
	 * @param startDate
	 * @param endDate
	 * @param suffix
	 * @return
	 */
	List<TurntablePayTransEntity> getTurntablePayRequest(@Param("appCode")String appCode, @Param("startDate")String startDate,@Param("endDate")String endDate,@Param("suffix")String suffix);

}
