package com.pgy.ups.pay.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.service.cache.dubbo.UpsCacheService;

@Service
public class UpsCacheDubboServiceImpl implements UpsCacheService{
	
	@Resource
	private CacheUtils cacheUtils;

	@Override
	public void refreshCache() {
		cacheUtils.initCacheUtils();		
	}

}
