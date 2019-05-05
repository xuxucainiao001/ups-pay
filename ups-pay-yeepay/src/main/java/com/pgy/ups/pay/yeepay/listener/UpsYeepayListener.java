package com.pgy.ups.pay.yeepay.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * UPS Yeepay预先加载部分
 * 
 * @author 墨凉
 *
 */

@WebListener
public class UpsYeepayListener implements ServletContextListener {
	
	private Logger logger=LoggerFactory.getLogger(UpsYeepayListener.class);

	@Value("${yop.sdk.config.dir}")
	private String yeepayFileDir;


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("UPS-PAY-YEEPAY网关预加载内容开始...");
		System.setProperty("yop.sdk.config.dir", yeepayFileDir);
		logger.info("UPS-PAY-YEEPAY网关预加载内容结束！");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}

}
