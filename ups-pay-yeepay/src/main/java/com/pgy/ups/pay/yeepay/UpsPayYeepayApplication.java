package com.pgy.ups.pay.yeepay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com.pgy.ups.**" },exclude = { DataSourceAutoConfiguration.class })
public class UpsPayYeepayApplication {

	
	private static final Logger logger = LoggerFactory.getLogger(UpsPayYeepayApplication.class);

	public static void main(String[] args) {
		logger.info("begin to start-up [ups-pay-yeepay]");
		SpringApplication.run(UpsPayYeepayApplication.class, args);		
		logger.info("start-up [ups-pay-yeepay] success !!!");
	}

	
}




