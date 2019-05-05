package com.pgy.ups.pay.interfaces.service.route.dubbo;

import java.util.List;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.form.PayCompanyForm;

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
	List<PayCompanyEntity> queryAllPayChannels();

	PageInfo<PayCompanyEntity> queryPayChannelsForPage(PayCompanyForm form);

	boolean enableOrDisablePayCompany(Long id, boolean b);



}
