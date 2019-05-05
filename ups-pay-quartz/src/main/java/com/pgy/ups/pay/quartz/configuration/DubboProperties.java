package com.pgy.ups.pay.quartz.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix=DubboProperties.DUBBO_PREFIX)
public class DubboProperties {
	
	public static final String DUBBO_PREFIX = "dubbo";
    
	private String name;
	
	private String logger;
	
	private String address;
	
	private String zkClient;
	
	private Integer timeout;

	

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

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
