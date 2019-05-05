package com.pgy.ups.pay.interfaces.service.log;

import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
/**
 * ups支付日志
 * @author 墨凉
 *
 */
public interface UpsLogService {

	UpsLogEntity createUpsLog(UpsParamModel upsParamModel,String url);

	void updateUpsLog(UpsLogEntity upsLogEntity);
	
}
