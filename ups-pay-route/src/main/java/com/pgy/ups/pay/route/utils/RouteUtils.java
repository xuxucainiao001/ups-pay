package com.pgy.ups.pay.route.utils;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.service.config.MerchantConfigService;

@Component
public class RouteUtils {

	//private static Logger logger = LoggerFactory.getLogger(RouteUtils.class);

	public static final String BAO_FOO = "baofoo";

	public static final String YEE_PAY = "yeepay";

	@Resource
	private MerchantConfigService merchantConfigService;

	/**
	 * 获取默认的路由结果
	 * 
	 * @return
	 */
	public static String getDefaultRouteResult(String fromSystem) {
		return BAO_FOO;
	}

	/**
	 * 根据渠道列表选择最好的支付渠道 
	 * 
	 * @param payCannels
	 * @return
	 */
	public static String getPerfectRouteResult(List<PayCompanyEntity> payChannels) {
		//获取商户默认渠道		
		return payChannels.get(0).getCompanyCode();
	}

}
