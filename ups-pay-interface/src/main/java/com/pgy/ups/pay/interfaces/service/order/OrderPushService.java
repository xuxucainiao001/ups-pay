package com.pgy.ups.pay.interfaces.service.order;

import java.util.List;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;

/**
 * 
 * @author 订单状态推送业务
 *
 */
public interface OrderPushService {
    
	OrderPushEntity createOrderPush(OrderPushEntity orderPushEntity);
    
	/**
	  * 查询需要轮询的订单
	 * @param payChannel
	 * @return
	 */
	List<OrderPushEntity> queryNeedQueryOrderList(String payChannel,String orderType);
    
	/**
	  *   更新订单推送状态
	 * @param OrderPushEntity
	 * @return
	 */
	OrderPushEntity updateOrderPush(OrderPushEntity ope);
    
	/**
	 * 查询最终状态订单列表
	 * @return
	 */
	List<OrderPushEntity> queryFinalStatusOrderPushList();
    
	/**
	 * 推送订单至业务端
	 */
	void pushOrder();
	
	/**
	 * 推送单个订单至业务端
	 */
	void pushOrder(OrderPushEntity ope);
    
	/**
	 * 重置订查询送次数和时间
	 */
	void resetOrderQueryTimeAndCountAsyn(Long id);
   
	/**
	  * 通过订单id查询
	 * @param upsOrderId
	 * @return
	 */
	OrderPushEntity queryByOrderId(Long upsOrderId);
    

}
