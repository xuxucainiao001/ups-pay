package com.pgy.ups.pay.interfaces.service.route;

import com.pgy.ups.pay.interfaces.model.UpsParamModel;


/**
 * 
 * @author 路由逻辑接口
 *
 */
public interface RouteService {

	/**
	 * 获取可用的路由信息
	 * 
	 * @return
	 */
	String obtainAvalibaleRoute(UpsParamModel upsParamMode);

}
