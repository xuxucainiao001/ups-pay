package com.pgy.ups.pay.yeepay.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pgy.ups.**") 
@EntityScan("com.pgy.ups.pay.interfaces.entity.**")
public class JpaConfiguration {

}
