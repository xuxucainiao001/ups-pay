package com.pgy.ups.pay.baofoo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(exposeProxy=true,proxyTargetClass=true)
public class AspectJConfiguration {

}
