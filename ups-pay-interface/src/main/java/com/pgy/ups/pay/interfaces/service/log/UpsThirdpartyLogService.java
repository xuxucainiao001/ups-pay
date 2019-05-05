package com.pgy.ups.pay.interfaces.service.log;

import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyLogEntity;

/**
 * 
 * 第三方支付渠道请求交互日志
 * @author 墨凉
 *
 */
public interface UpsThirdpartyLogService {

	UpsThirdpartyLogEntity createThirdpartyLog(UpsThirdpartyLogEntity utl);

}
