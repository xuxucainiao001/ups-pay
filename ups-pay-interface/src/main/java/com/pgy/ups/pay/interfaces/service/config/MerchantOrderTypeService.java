package com.pgy.ups.pay.interfaces.service.config;

import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;

public interface MerchantOrderTypeService {
	
	public static final String OPEN_DEFAULT="0";
	
	public static final String OPEN_ROUTE="1";

	public MerchantOrderTypeEntity confirmMerchantOrderType(String fromSystem, String orderType);

}
