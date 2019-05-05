package com.pgy.ups.pay.baofoo.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(DubboProperties.class)
@DubboComponentScan("com.pgy.ups.pay.baofoo.**")
public class DubboConfiguration {

	@Bean
	public ApplicationConfig applicationConfig(DubboProperties dubboProperties) {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(dubboProperties.getName());
		applicationConfig.setLogger(dubboProperties.getLogger());
		// 关闭远程控制端口服务，防止2222端口占用异常
		applicationConfig.setQosEnable(false);
		return applicationConfig;
	}

	@Bean
	public RegistryConfig registryConfig(DubboProperties dubboProperties) {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(dubboProperties.getAddress());
		registryConfig.setClient(dubboProperties.getZkClient());
		registryConfig.setPort(-1);
		return registryConfig;
	}

	@Bean
	public ProtocolConfig protocolConfig(DubboProperties dubboProperties) {
		ProtocolConfig registryConfig = new ProtocolConfig();
		registryConfig.setPort(-1);
		return registryConfig;
	}

}
