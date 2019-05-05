package com.pgy.ups.pay.commom.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.pgy.ups.pay.commom.dao.CollectChooseDao;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;
import com.pgy.ups.pay.interfaces.service.config.CollectChooseService;

@Service
public class CollectChooseServiceImpl implements CollectChooseService, Cacheable<CollectChooseEntity> {

	public static final String UPS_COLLECT_CHOOSE_CACHE = "ups-collect-choose-cache";

	@Resource
	private CollectChooseDao collectChooseDao;
	
	@Resource
	private CacheUtils cacheUtils;

	@Override
	public CollectChooseEntity queryCollectType(String merchantCode) {
		String key=CacheUtils.generateKey(merchantCode);
		CollectChooseEntity cce=cacheUtils.getCacheByRediskeynameAndKey(UPS_COLLECT_CHOOSE_CACHE, key, CollectChooseEntity.class);
		return cce;
	}

	@Override
	public String getCacheKeyname() {
		return UPS_COLLECT_CHOOSE_CACHE;
	}
	
	

	@Override
	public Map<String, CollectChooseEntity> getCacheableData() {
		Map<String, CollectChooseEntity> cacheMap = new HashMap<>();
		List<CollectChooseEntity> list = getActiveCollectChooseEntityList();
		for (CollectChooseEntity cce : list) {
			cacheMap.put(CacheUtils.generateKey(cce.getMerchantCode()), cce);
		}
		return cacheMap;
	}

	private List<CollectChooseEntity> getActiveCollectChooseEntityList() {
		CollectChooseEntity queryEntity = new CollectChooseEntity();
		queryEntity.setActive(true);
		return collectChooseDao.findAll(Example.of(queryEntity));
	}

}
