package com.qbao.sdk.server.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.qbao.sdk.server.metadata.dao.mapper.StoreAwardConfMapper;
import com.qbao.sdk.server.metadata.entity.StoreAwardConfEntity;
import com.qbao.sdk.server.service.StoreAwardConfService;
import com.qbao.sdk.server.utils.StoreAwardConfSortUtils;


/**
 * 游戏活动奖励规则
 * @author xuefeihu
 *
 */
@Service("storeAwardConfService")
public class StoreAwardConfServiceImpl implements StoreAwardConfService {
	
	@Resource
	private StoreAwardConfMapper storeAwardConfMapper;

	@Override
	public int save(StoreAwardConfEntity entity) {
		if (null != entity && null != entity.getStoreConfId()) {
			synchronized (storeAwardConfMapper) {
				List<StoreAwardConfEntity> list = storeAwardConfMapper.queryConfByStoreConfId(entity.getStoreConfId(),
						entity.getType());
				if (!CollectionUtils.isEmpty(list)) {// 编辑
					entity.setId(list.get(0).getId());
					return storeAwardConfMapper.updateById(entity);
				} else {// 新增
					return storeAwardConfMapper.insert(entity);
				}
			}
		}
		return 0;
	}

	@Override
	public int publish(String storeConfId, int isPublish, int type) {
		return storeAwardConfMapper.publish(storeConfId, isPublish, type);
	}

	@Override
	public List<StoreAwardConfEntity> queryConf(String time, String appCode) {
		List<StoreAwardConfEntity> list = storeAwardConfMapper.queryConf(time.substring(0, 8), "000059", appCode);
		if(!CollectionUtils.isEmpty(list)) {
			StoreAwardConfSortUtils.sortArray(list);
//			list.get(0).setReGrant(1);
			return list;
		}
		return null;
	}

	@Override
	public int deleteByStoreConfId(String storeConfId, Integer type) {
		return storeAwardConfMapper.deleteByStoreConfId(storeConfId, type);
	}

}
