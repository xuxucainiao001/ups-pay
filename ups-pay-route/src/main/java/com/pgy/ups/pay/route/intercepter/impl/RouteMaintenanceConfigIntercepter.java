package com.pgy.ups.pay.route.intercepter.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.entity.RouteMaintenanceEntity;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.route.RouteMaintenanceService;

/**
 * 
 * @author 通过银行维护列表筛选支付渠道
 *
 */
@Component
public class RouteMaintenanceConfigIntercepter extends RouteChooseWorkerIntercepter {
    
	@Resource
	private RouteMaintenanceService routeMaintenanceService;

	@Override
	public void routeDispose(UpsParamModel upsParamModel, List<PayCompanyEntity> payChannels) {
		// 查询所有的维护配置列表
		List<RouteMaintenanceEntity> maintenance = routeMaintenanceService.queryAllRouteMaintenance();
		if(CollectionUtils.isEmpty(maintenance)) {
			return;
		}
		// 调用方传入的银行编码
		String bankCode = upsParamModel.getBankCode();
		// 调用方传入的订单类型
		String orderType = upsParamModel.getOrderType();
		// 系统来源 美期 秒呗或 米融 多呗等
		String fromSystem = upsParamModel.getMerchantCode();

		for (RouteMaintenanceEntity routeMaintenanceEntity : maintenance) {
			// 渠道公司编码相同，系统来源相同，银行也相同，支付场景相同，且当前时间在维护时间范围内，则说明在维护中, 从payChannels剔除
			Iterator<PayCompanyEntity> it = payChannels.iterator();
			while (it.hasNext()) {
				PayCompanyEntity payCompany = it.next();
				if (routeMaintenanceEntity.getPayChannel().equalsIgnoreCase(payCompany.getCompanyCode())
						&& bankCode.equalsIgnoreCase(routeMaintenanceEntity.getBankCode())
						&& fromSystem.equalsIgnoreCase(routeMaintenanceEntity.getMerchantCode())
						&& orderType.equalsIgnoreCase(routeMaintenanceEntity.getOrderType()))
					it.remove();
			}
		}
	}

	@Override
	public int getOrder() {

		return 0;
	}


}
