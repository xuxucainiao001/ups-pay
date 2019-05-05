package com.pgy.ups.pay.gateway.listener;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgy.ups.pay.commom.utils.CacheUtils;

/**
 * UPS 预先加载部分
 * 
 * @author 墨凉
 *
 */

@WebListener
public class UpsPayListener implements ServletContextListener {
	
	private Logger logger=LoggerFactory.getLogger(UpsPayListener.class);

	
	@Resource
	private CacheUtils cacheUtils;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("UPS网关预加载内容开始...");
		// 配置缓存
		cacheUtils.initCacheUtils();
		
		logger.info("UPS网关预加载内容结束！");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}

}
