package com.pgy.ups.pay.interfaces.service.config.dubbo;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;
import com.pgy.ups.pay.interfaces.form.MerchantOrderTypeForm;

public interface MerchantOrderTypeService {
	
	public static final String OPEN_DEFAULT="0";
	
	public static final String OPEN_ROUTE="1";

	boolean createMerchantOrderType(MerchantOrderTypeForm form);

	void deleteMerchantOrderType(Long id);

	boolean updateMerchantOrderType(MerchantOrderTypeForm form);

	PageInfo<MerchantOrderTypeEntity> queryMerchantOrderTypeForPage(MerchantOrderTypeForm form);

	boolean openDefaultOrRoute(Long id, String openDefault,String updateUser);

	boolean updateMerchantRouteConfig(MerchantOrderTypeForm form);

}
