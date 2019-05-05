package com.pgy.ups.pay.commom.factory.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.pgy.ups.common.utils.SpringUtils;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.factory.BusinessFactory;
import com.pgy.ups.pay.interfaces.query.OrderQueryService;

@Component
public class QueryServiceFactory implements BusinessFactory<OrderQueryService<String>, OrderPushEntity>{
	
	private Logger logger=LoggerFactory.getLogger(QueryServiceFactory.class);

	@Override
	public OrderQueryService<String> getInstance(OrderPushEntity ope){
		String payChannel=ope.getPayChannel();
		String orderType=ope.getOrderType();
		Class<?> interf=null;
		try {
			interf = Class.forName(OrderQueryService.class.getName());
		} catch (ClassNotFoundException e) {
			logger.error("反射获取业务处理接口失败！BussinessHandler",e);
			return null;
		}
		//dubbo硬编码获取服务
        ReferenceConfig<OrderQueryService<String>> rc=new ReferenceConfig<>();
        rc.setInterface(interf);
        rc.setGroup(payChannel + orderType);
        rc.setApplication(SpringUtils.getBean(ApplicationConfig.class));
        rc.setRegistry(SpringUtils.getBean(RegistryConfig.class));
        rc.setConsumer(SpringUtils.getBean(ConsumerConfig.class));
        OrderQueryService<String> s= rc.get();
        return s;
	}

}
