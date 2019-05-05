package com.pgy.ups.pay.route.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.service.route.PayCompanyService;
import com.pgy.ups.pay.route.dao.PayCompanyDao;

@Service
public class PayCompanyServiceImpl implements PayCompanyService, Cacheable<PayCompanyEntity> {

	public static final String UPS_ROUTE_PAY_COMPANY_CACHE = "ups-route-pay-company-cache";

	@Resource
	private PayCompanyDao payCompanyDao;

	@Resource
	private CacheUtils cacheUtils;

	@Override
	public List<PayCompanyEntity> queryAllAvailablePayChannels() {
		// 先查询缓存
		Map<String, PayCompanyEntity> cacheMap = cacheUtils.getCacheByRediskeyname(UPS_ROUTE_PAY_COMPANY_CACHE,
				PayCompanyEntity.class);
		List<PayCompanyEntity> list = new ArrayList<>(cacheMap.values());
		if (CollectionUtils.isEmpty(list)) {
			// 重新加载缓存
			cacheMap = getCacheableData();
			cacheUtils.setCacheByRediskeyname(UPS_ROUTE_PAY_COMPANY_CACHE, cacheMap);
			list.addAll(cacheMap.values());
		}
		return list;
	}

	@Override
	public String getCacheKeyname() {

		return UPS_ROUTE_PAY_COMPANY_CACHE;
	}

	@Override
	public Map<String, PayCompanyEntity> getCacheableData() {
		List<PayCompanyEntity> list = payCompanyDao.queryAllAvailablePayChannels();
		Map<String, PayCompanyEntity> cacheMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (PayCompanyEntity pce : list) {
				cacheMap.put(pce.getCompanyCode(), pce);
			}
			return cacheMap;
		}
		return null;
	}
    
	/**
	 * 确认支付渠道是否可用
	 */
	@Override
	public void confirmPayChanelisAvaliable(String payChannel) {
		// 先查询缓存
		Map<String, PayCompanyEntity> cacheMap = cacheUtils.getCacheByRediskeyname(UPS_ROUTE_PAY_COMPANY_CACHE,
				PayCompanyEntity.class);
		if (!cacheMap.containsKey(payChannel)) {
			List<PayCompanyEntity> list = payCompanyDao.queryAllAvailablePayChannels();
			for (PayCompanyEntity pe : list) {
				if (pe.getCompanyCode().equals(payChannel)) {
					return;
				}
			}
			throw new BussinessException("支付渠道不存在或禁用！");
		}		
	}

}
