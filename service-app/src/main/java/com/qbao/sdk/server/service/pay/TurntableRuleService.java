package com.qbao.sdk.server.service.pay;

import com.qbao.sdk.server.bo.pay.TurnableRuleRequest;
import com.qbao.sdk.server.bo.pay.TurnableRuleResponse;

/**
 * 应用市场大转盘规则服务接口
 * @author qianbao
 *
 */
public interface TurntableRuleService {
	/**
	 * 删除规则
	 * @param id 规则id
	 */
	
	TurnableRuleResponse delTurntableRule(int id,String requestIp);
	/**
	 * 添加或更新规则信息 
	 * @param turnableRuleRequest
	 */
	TurnableRuleResponse addOrUpdateRule(TurnableRuleRequest turnableRuleRequest);

}
