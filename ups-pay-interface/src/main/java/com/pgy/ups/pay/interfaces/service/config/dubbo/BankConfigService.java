package com.pgy.ups.pay.interfaces.service.config.dubbo;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsBankEntity;
import com.pgy.ups.pay.interfaces.form.UpsBankForm;

/**
 * 银行配置接口
 * @author 墨凉
 *
 */
public interface BankConfigService {
	
     PageInfo<UpsBankEntity> queryByForm(UpsBankForm upsBankForm);
     
     void deleteBankConfigById(Long id);

     UpsBankEntity saveBankConfig(UpsBankForm form);
     
     

}
