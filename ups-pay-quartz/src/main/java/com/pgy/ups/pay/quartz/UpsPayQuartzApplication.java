package com.pgy.ups.pay.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com.pgy.ups.**" })
public class UpsPayQuartzApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(UpsPayQuartzApplication.class);

	public static void main(String[] args) {
		logger.info("begin to start-up [ups-pay-quartz]");
		SpringApplication.run(UpsPayQuartzApplication.class, args);
		logger.info("start-up [ups-pay-quartz] success !!!");
	}

	
}




