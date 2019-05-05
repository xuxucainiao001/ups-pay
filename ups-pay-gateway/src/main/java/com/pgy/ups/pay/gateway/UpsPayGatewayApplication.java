package com.pgy.ups.pay.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com.pgy.ups.**" }, exclude = { DataSourceAutoConfiguration.class })
public class UpsPayGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(UpsPayGatewayApplication.class);

	public static void main(String[] args) {
		logger.info("begin to start-up [ups-pay-gateway]");
		SpringApplication.run(UpsPayGatewayApplication.class, args);
		logger.info("start-up [ups-pay-gateway] success !!!");
	}

}
