package com.pgy.ups.pay.quartz.ups;

import javax.annotation.Resource;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;

/**
 * 订单状态推送业务方
 * 
 * @author 墨凉
 *
 */


@Component
@DisallowConcurrentExecution
public class PushOrderStatusTask extends ConfigQuartzTask{
	
	@Resource
	private OrderPushService orderPushService;

	@Override
	protected void runTask() {
		
		orderPushService.pushOrder();
		
	}

}
