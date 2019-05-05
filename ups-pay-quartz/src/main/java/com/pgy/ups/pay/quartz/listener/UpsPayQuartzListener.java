package com.pgy.ups.pay.quartz.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Value;

/**
 * UPS-Quartz 预先加载部分
 * @author 墨凉
 *
 */

@WebListener
public class UpsPayQuartzListener implements ServletContextListener{ 
	
	@Value("${yop.sdk.config.dir}")
	private String yeepayFileDir;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
	
		System.setProperty("yop.sdk.config.dir", yeepayFileDir);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing		
	}
	
}
