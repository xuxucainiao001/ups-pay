package com.pgy.ups.pay.interfaces.service.route;

import java.util.List;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;

/**
 * 
 * @author 支付公司业务层
 *
 */
public interface PayCompanyService {
    
	/**
	 * 查询所有可用的支付公司（支付渠道）
	 * @return
	 */
	List<PayCompanyEntity> queryAllAvailablePayChannels();

	void confirmPayChanelisAvaliable(String payChannel) ;


}
