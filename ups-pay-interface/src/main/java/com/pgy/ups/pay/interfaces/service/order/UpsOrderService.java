package com.pgy.ups.pay.interfaces.service.order;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.model.*;

/**
 * Ups订单业务逻辑
 * @author 墨凉
 *
 */
public interface UpsOrderService {
    
	//创建订单
	UpsOrderEntity createUpsOrder(UpsParamModel upsParamModel);
   
	//更新订单
	UpsOrderEntity updateUpsOrder(UpsOrderEntity orderEntity);
	
	//更新订单并创建附加订单信息
	UpsOrderEntity updateOrderAndCreateOrderPush(UpsOrderEntity orderEntity);
    
	//根据id查询
	UpsOrderEntity queryByOrderId(Long orderId);
    
	//更新订单和订单推送信息
	void updateOrderAndupdateOrderPush(UpsOrderEntity upsOrderEntity, OrderPushEntity ope);
    
	//查询订单信息
	UpsOrderEntity queryUpsOrderEntity(UpsOrderEntity order);

	

}
