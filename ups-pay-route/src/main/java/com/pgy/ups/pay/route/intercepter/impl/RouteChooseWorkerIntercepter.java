package com.pgy.ups.pay.route.intercepter.impl;

import java.util.List;

import org.springframework.core.Ordered;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.intercepter.Intercepter;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;

public abstract class RouteChooseWorkerIntercepter
		implements Intercepter<RouteChooseWorker>, Ordered, Comparable<RouteChooseWorkerIntercepter> {

	@Override
	public void doIntercepte(RouteChooseWorker routeChooseWorker) {
		routeDispose(routeChooseWorker.getUpsParamModel(), routeChooseWorker.getPayChannels());
		routeChooseWorker.doWorker();
	}

	/**
	 * 路由处理过程
	 * 
	 * @param upsParamModel 选择渠道需要用的参数，由调用者传入
	 * @param payChannels  当前所有可用的渠道
	 */
	public abstract void routeDispose(UpsParamModel upsParamModel, List<PayCompanyEntity> payChannels);

	/**
	 * 定义 拦截器顺序
	 */
	@Override
	public int compareTo(RouteChooseWorkerIntercepter r) {
		return r.getOrder() - this.getOrder();
	}
}
