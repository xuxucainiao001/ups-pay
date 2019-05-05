package com.pgy.ups.pay.route.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pgy.ups.common.exception.ParamValidException;
import com.pgy.ups.common.utils.SpringUtils;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.route.RouteService;
import com.pgy.ups.pay.route.intercepter.impl.RouteChooseWorker;

@Service
public class RouteServiceImpl implements RouteService {

	private Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);


	/**
	 * 获取可用的路由信息
	 * 
	 * @return
	 * @throws ParamValidException
	 */
	@Override
	public String obtainAvalibaleRoute(UpsParamModel upsParamModel) {
		// 若已经有，则无需设置
		String payChannel = upsParamModel.getPayChannel();
		if (StringUtils.isNotBlank(payChannel)) {
			return payChannel;
		}

		RouteChooseWorker routeChooseWorker = SpringUtils.getBean(RouteChooseWorker.class);
		try {
			routeChooseWorker.initRouteChooseHandler(upsParamModel);
			routeChooseWorker.doWorker();
			return routeChooseWorker.getWorkerResult();
		} catch (Exception e) {
			logger.error("路由选择发生异常“{}", ExceptionUtils.getStackTrace(e));
			throw e;
		}

	}

}
