package com.pgy.ups.pay.commom.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.commom.dao.MerchantOrderTypeDao;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;
import com.pgy.ups.pay.interfaces.service.config.MerchantOrderTypeService;

@Service
public class MerchantOrderTypeServiceImpl implements MerchantOrderTypeService, Cacheable<MerchantOrderTypeEntity> {

	public static final String UPS_MERCHANT_ORDER_TYPE_CACHE = "ups-merchant-order-type-cache";

	@Resource
	private MerchantOrderTypeDao merchantOrderTypeDao;

	@Resource
	private CacheUtils cacheUtils;

	// 查询商户支付产品对象
	@Override
	public MerchantOrderTypeEntity confirmMerchantOrderType(String merchanCode, String orderType) {
		String key=CacheUtils.generateKey(merchanCode, orderType);
		MerchantOrderTypeEntity mot = cacheUtils.getCacheByRediskeynameAndKey(UPS_MERCHANT_ORDER_TYPE_CACHE,
				key, MerchantOrderTypeEntity.class);
		if(Objects.nonNull(mot)) {
			Date today=new Date();
			if(today.before(mot.getEndTime())&&today.after(mot.getStartTime())) {
				return mot;
			}else {
				throw new BussinessException("该商户有效日期已过期！");
			}
		}
		Optional<MerchantOrderTypeEntity> optional = merchantOrderTypeDao.confirmMerchantOrderType(merchanCode,
				orderType, new Date());
		if (optional.isPresent()) {
			mot= optional.get();
			cacheUtils.setCacheByRediskeynameAndKey(UPS_MERCHANT_ORDER_TYPE_CACHE, key, mot);
			return mot;			
		}
		throw new BussinessException("该商户不支持该支付产品，或有效日期已过期！");

	}

	@Override
	public String getCacheKeyname() {
		return UPS_MERCHANT_ORDER_TYPE_CACHE;
	}

	@Override
	public Map<String, MerchantOrderTypeEntity> getCacheableData() {
		List<MerchantOrderTypeEntity> list = merchantOrderTypeDao.findAll();
		Map<String, MerchantOrderTypeEntity> cacheMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (MerchantOrderTypeEntity mot : list) {
				cacheMap.put(CacheUtils.generateKey(mot.getMerchantConfigEntity().getMerchantCode(),
						mot.getUpsOrderTypeEntity().getOrderType()), mot);
			}
			return cacheMap;
		}
		return null;
	}

}
