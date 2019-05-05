package com.pgy.ups.pay.interfaces.service.config;

import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyConfigEntity;

/**
 * 第三方支付渠道配置
 * @author 墨凉
 *
 */
public interface UpsThirdpartyConfigService {
    
    
   
	/**
	 * 查询UPS第三方支付渠道配置
	 * @param payChannel
	 * @param orderType
	 * @param fromSystem
	 * @return
	 */
	UpsThirdpartyConfigEntity queryThirdpartyConfig(String payChannel,String orderType,String fromSystem);


}
