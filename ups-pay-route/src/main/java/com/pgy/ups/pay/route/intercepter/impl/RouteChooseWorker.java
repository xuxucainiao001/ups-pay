package com.pgy.ups.pay.route.intercepter.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.pgy.ups.common.utils.SpringUtils;
import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.route.PayCompanyService;
import com.pgy.ups.pay.interfaces.worker.Worker;
import com.pgy.ups.pay.route.utils.RouteUtils;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RouteChooseWorker implements Worker<String> {

	private Logger logger = LoggerFactory.getLogger(RouteChooseWorker.class);

	@Resource
	private PayCompanyService payCompanyService;

	// 支付处理器拦截器列表
	private static Set<RouteChooseWorkerIntercepter> intercepters = new TreeSet<>();

	private Iterator<RouteChooseWorkerIntercepter> intercepterIterator;

	private String payChannel;

	// 前台传入参数
	private UpsParamModel upsParamModel;

	// 可用支付渠道
	private List<PayCompanyEntity> payChannels;

	public void initRouteChooseHandler(UpsParamModel upsParamModel) {
		this.upsParamModel = upsParamModel;
		payChannels = payCompanyService.queryAllAvailablePayChannels();
		if (CollectionUtils.isEmpty(RouteChooseWorker.intercepters)) {
			RouteChooseWorker.intercepters.addAll(SpringUtils.getBeans(RouteChooseWorkerIntercepter.class));
		}
		intercepterIterator = RouteChooseWorker.intercepters.iterator();
	}

	@Override
	public void doWorker() {
		while (intercepterIterator.hasNext()) {
			// 拦截处理，把不符合条件的支付渠道从payCannels集合中剔除
			intercepterIterator.next().doIntercepte(this);
			// 若已经没有合适的支付渠道，直接返回默认支付渠道
			if (StringUtils.isEmpty(payChannel) && CollectionUtils.isEmpty(payChannels)) {
				logger.info("已经没有符合条件的支付渠道！");
				payChannel = RouteUtils.getDefaultRouteResult(upsParamModel.getMerchantCode());
			}
		}
	}

	@Override
	public String getWorkerResult() {
		if (StringUtils.isEmpty(payChannel)) {
			return RouteUtils.getPerfectRouteResult(payChannels);
		}
		return payChannel;
	}

	protected UpsParamModel getUpsParamModel() {
		return upsParamModel;
	}

	protected List<PayCompanyEntity> getPayChannels() {
		return payChannels;
	}

}
