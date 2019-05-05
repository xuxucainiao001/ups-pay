package com.pgy.ups.pay.commom.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pgy.ups.pay.commom.dao.UpsPayChannelBankDao;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.UpsBankEntity;
import com.pgy.ups.pay.interfaces.entity.UpsPayChannelBankEntity;
import com.pgy.ups.pay.interfaces.service.bank.UpsPayChannelBankService;

@Service
public class UpsPayChannelBankServiceImpl implements UpsPayChannelBankService, Cacheable<UpsPayChannelBankEntity> {

	/**
	 * redis 缓存key
	 */
	public static final String UPS_BANK_CACHE = "ups-bank-cache";

	@Resource
	private CacheUtils cacheUtils;

	@Resource
	private UpsPayChannelBankDao upsPayChannelBankDao;

	@Override
	public String queryChannelBankCode(String payChannle, String bussinessBankCode) {
		UpsPayChannelBankEntity upsPayChannelBankEntity = queryUpsPayChannelBank(payChannle, bussinessBankCode);
		return upsPayChannelBankEntity == null ? null
				: upsPayChannelBankEntity.getPayChannelBankCode();
	}

	@Override
	public String queryChannelBankName(String payChannle, String bussinessBankCode) {
		UpsPayChannelBankEntity upsPayChannelBankEntity = queryUpsPayChannelBank(payChannle, bussinessBankCode);
		return upsPayChannelBankEntity == null ? null
				: upsPayChannelBankEntity.getPayChannelBankName();
	}

	@Override
	public UpsPayChannelBankEntity queryUpsPayChannelBank(String payChannle, String bussinessBankCode) {
		// 先读取缓存
		String key = CacheUtils.generateKey(payChannle, bussinessBankCode);
		UpsPayChannelBankEntity upcb = cacheUtils.getCacheByRediskeynameAndKey(UPS_BANK_CACHE, key,
				UpsPayChannelBankEntity.class);
		if (Objects.isNull(upcb)) {
			// 缓存没有，查询数据库，并更新缓存
			upcb = upsPayChannelBankDao.queryUpsPayChannelBank(payChannle, bussinessBankCode);
			cacheUtils.setCacheByRediskeynameAndKey(UPS_BANK_CACHE, key, upcb);
		}
		return upcb;
	}

	/**
	 * redis缓存key
	 * 
	 * @return
	 */
	@Override
	public String getCacheKeyname() {
		return UPS_BANK_CACHE;
	}

	/**
	 * 预加载缓存
	 * 
	 * @return
	 */
	@Override
	public Map<String, UpsPayChannelBankEntity> getCacheableData() {
		List<UpsPayChannelBankEntity> list = upsPayChannelBankDao.findAll();
		Map<String, UpsPayChannelBankEntity> cacheMap = new LinkedHashMap<>();
		for (UpsPayChannelBankEntity upcb : list) {
			UpsBankEntity ube = upcb.getUpsBankEntity();
			cacheMap.put(CacheUtils.generateKey(upcb.getPayChannel(), ube.getBankCode()), upcb);
		}
		return cacheMap;
	}

}
