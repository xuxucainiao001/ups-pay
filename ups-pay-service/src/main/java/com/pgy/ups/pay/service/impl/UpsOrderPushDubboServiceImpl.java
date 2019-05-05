package com.pgy.ups.pay.service.impl;

import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.pay.commom.factory.impl.QueryServiceFactory;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.interfaces.service.order.dubbo.UpsOrderPushService;
import com.pgy.ups.pay.service.dao.UpsOrderPushDubboDao;

@Service
public class UpsOrderPushDubboServiceImpl implements UpsOrderPushService{
	
	
	@Resource
	private QueryServiceFactory queryServiceFactory;
	
	
	@Resource
	private UpsOrderPushDubboDao upsOrderPushDubboDao;
	
	@Resource
	private OrderPushService orderPushService;
	

	@Override
	public OrderPushEntity queryByOrderId(Long id) {		
		return upsOrderPushDubboDao.queryByOrderId(id);
	}


	@Override
	public void updateOrderPush(OrderPushEntity ope) {
		upsOrderPushDubboDao.saveAndFlush(ope);		
	}


	@Override
	public String queryThirdpartResult(Long id) {
		OrderPushEntity ope=queryByOrderId(id);
		if(Objects.isNull(ope)) {
			return "该订单推送信息不存在！";
		}
		return queryServiceFactory.getInstance(ope).doSingleQuery(ope, true);
	}


	@Override
	public void pushOrder(OrderPushEntity ope) {
		orderPushService.pushOrder(ope);
		
	}

}
