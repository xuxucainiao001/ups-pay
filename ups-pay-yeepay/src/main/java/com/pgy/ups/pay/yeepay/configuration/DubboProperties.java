package com.pgy.ups.pay.yeepay.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix=DubboProperties.DUBBO_PREFIX)
public class DubboProperties {
	
	public static final String DUBBO_PREFIX = "dubbo";
    
	private String name;
	
	private String logger;
	
	private String address;
	
	private String zkClient;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZkClient() {
		return zkClient;
	}

	public void setZkClient(String zkClient) {
		this.zkClient = zkClient;
	}
	
}
