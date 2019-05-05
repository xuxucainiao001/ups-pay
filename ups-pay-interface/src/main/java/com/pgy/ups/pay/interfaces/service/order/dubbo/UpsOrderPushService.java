package com.pgy.ups.pay.interfaces.service.order.dubbo;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;

/**
 * 
 * @author 订单状态推送业务
 *
 */
public interface UpsOrderPushService {

	OrderPushEntity queryByOrderId(Long id);

	void updateOrderPush(OrderPushEntity ope);

	String queryThirdpartResult(Long id);

	void pushOrder(OrderPushEntity ope);
    
}
