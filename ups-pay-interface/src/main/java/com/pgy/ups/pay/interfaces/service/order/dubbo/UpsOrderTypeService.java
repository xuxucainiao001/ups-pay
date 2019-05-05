package com.pgy.ups.pay.interfaces.service.order.dubbo;

import java.util.List;

import com.pgy.ups.pay.interfaces.entity.UpsOrderTypeEntity;

/**
 * 订单类型
 * @author acer
 *
 */
public interface UpsOrderTypeService {
	
	List<UpsOrderTypeEntity> getAllOrderType();

	UpsOrderTypeEntity queryOrderTypeById(Long orderTypeId);


}
