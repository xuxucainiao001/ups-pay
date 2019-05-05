package com.pgy.ups.pay.gateway.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;

@Configuration
@EnableConfigurationProperties(DubboProperties.class)
@DubboComponentScan(basePackages= {"com.pgy.ups.pay.service.**"})
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
	public ConsumerConfig consumerConfig(DubboProperties dubboProperties) {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setTimeout(dubboProperties.getTimeout());
		consumerConfig.setRetries(new Integer("0"));
		// 设置消费方启动 dubbo服务方未启动造成的null指针异常
		consumerConfig.setCheck(true);
		return consumerConfig;
	}

	@Bean
	public RegistryConfig registryConfig(DubboProperties dubboProperties) {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(dubboProperties.getAddress());
		registryConfig.setClient(dubboProperties.getZkClient());
		return registryConfig;
	}

	@Bean
	public ProtocolConfig protocolConfig(DubboProperties dubboProperties) {
		ProtocolConfig registryConfig = new ProtocolConfig();
		registryConfig.setPort(-1);
		return registryConfig;
	}
	
}
