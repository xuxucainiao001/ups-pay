package com.pgy.ups.pay.commom.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.commom.dao.UpsThirdpartyConfigDao;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyConfigEntity;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;

/**
 * UPS第三方支付渠道配置逻辑接口实现
 * 
 * @author 墨凉
 *
 */
@Service
public class UpsThirdpartyConfigServiceImpl
		implements UpsThirdpartyConfigService, Cacheable<UpsThirdpartyConfigEntity> {

	@Resource
	private CacheUtils cacheUtils;

	public static final String UPS_THIRDPARTY_CONFIG_CACHE = "ups-thirdparty-config-cache";

	private static final Logger logger = LoggerFactory.getLogger(UpsThirdpartyConfigServiceImpl.class);

	@Resource
	private UpsThirdpartyConfigDao upsThirdpartyConfigDao;

	@Override
	public UpsThirdpartyConfigEntity queryThirdpartyConfig(String payChannel, String orderType, String merchantCode) {
		// 先查询缓存
		String key = CacheUtils.generateKey(payChannel, orderType, merchantCode);
		UpsThirdpartyConfigEntity config = cacheUtils.getCacheByRediskeynameAndKey(UPS_THIRDPARTY_CONFIG_CACHE, key,
				UpsThirdpartyConfigEntity.class);
		if (Objects.isNull(config)) {
			// 若缓存不存在则查询数据库，并更新缓存
			config = upsThirdpartyConfigDao.queryByMerchantCodeAndPayChannelAndOrderType(merchantCode, payChannel,
					orderType);
			cacheUtils.setCacheByRediskeynameAndKey(UPS_THIRDPARTY_CONFIG_CACHE, key, config);
		}

		if (Objects.nonNull(config)) {
			return config;
		}
		logger.error("没有查询到第三方支付渠道配置信息,订单信息：{}，{}，{}", merchantCode, payChannel, orderType);
		throw new BussinessException("没有查询到第三方支付渠道配置信息！");
	}

	@Override
	public String getCacheKeyname() {
		return UPS_THIRDPARTY_CONFIG_CACHE;
	}

	@Override
	public Map<String, UpsThirdpartyConfigEntity> getCacheableData() {
		List<UpsThirdpartyConfigEntity> list = upsThirdpartyConfigDao.findAll();
		Map<String, UpsThirdpartyConfigEntity> cache = new LinkedHashMap<>();
		for (UpsThirdpartyConfigEntity e : list) {
			cache.put(CacheUtils.generateKey(e.getPayChannel(), e.getOrderType(), e.getMerchantCode()), e);
		}
		return cache;
	}

}
