package com.pgy.ups.pay.yeepay.pay.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.CollectService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.annotation.PrintExecuteTime;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyLogEntity;
import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.service.log.UpsThirdpartyLogService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;
import com.pgy.ups.pay.yeepay.utils.YeepayUtils;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.error.YopError;

/**
 * 易宝代收 还款
 * 
 * @author 墨凉
 *
 */
@Service(group = "yeepayCollect", timeout = 60000, retries = 0)
public class YeepayCollectServiceImpl implements CollectService {

	@Resource(name = "threadPoolExecutor")
	private ThreadPoolExecutor threadPoolExecutor;

	private static final String PAY_FAIL = "PAY_FAIL";

	private static final String PROCESSING = "PROCESSING";

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyLogService upsThirdpartyLogService;

	private Logger logger = LoggerFactory.getLogger(YeepayCollectServiceImpl.class);
	


	@Override
	public UpsResultModel collet(UpsOrderEntity upsOrderEntity) throws BussinessException{
		Map<String, Object> config = upsOrderEntity.getUpsConfigDate();
		String merchantno = MapUtils.getString(config, "merchantno", "");
		String requestUrl = MapUtils.getString(config, "requestUrl", "");
		String terminalno = MapUtils.getString(config, "terminalno", "");
		String appKey = MapUtils.getString(config, "appKey", "");

		if (StringUtils.isAnyBlank(merchantno, terminalno, requestUrl, appKey)) {
			logger.error("易宝代扣配置信息包含空！{}", config);
			throw new BussinessException("易宝代扣配置信息包含空");
		}

		Map<String, Object> params = new HashMap<>();

		params.put("requestno", upsOrderEntity.getUpsOrderCode());
		params.put("identityid", upsOrderEntity.getUserNo());
		params.put("identitytype", "ID_CARD");
		params.put("idcardtype", "ID");
		params.put("amount", upsOrderEntity.getAmount().setScale(2));
		params.put("terminalno", terminalno);
		params.put("authtype", "COMMON_FOUR");
		params.put("requesttime", DateUtils.dateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
		params.put("issms", "false");
		// 四要素必须从UpsParamModel中获得，因为orderEntity中的四要素是被加密过的
		params.put("cardno", upsOrderEntity.getUpsParamModel().getBankCard());
		params.put("idcardno", upsOrderEntity.getUpsParamModel().getIdentity());
		params.put("username", upsOrderEntity.getUpsParamModel().getRealName());
		params.put("phone", upsOrderEntity.getUpsParamModel().getPhoneNo());

		YopRequest yoprequest = new YopRequest(appKey);

		Set<Entry<String, Object>> entry = params.entrySet();
		for (Entry<String, Object> s : entry) {
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		String requestText = JSONObject.toJSONString(params);
		logger.info("易宝代扣发送明文:{}" , requestText);
		// 向YOP发请求
		threadPoolExecutor.submit(() -> httpRemoteInvoke(requestUrl, yoprequest, upsOrderEntity, requestText));
		return new UpsResultModel(UpsResultEnum.SUCCESS, upsOrderEntity.getId());
	}







    
	@PrintExecuteTime
	@Async
	public void httpRemoteInvoke(String requestUrl, YopRequest yoprequest, UpsOrderEntity upsOrderEntity,
			String requestText) {

		YopResponse yopresponse = null;
		try {
			yopresponse = YeepayUtils.submit(requestUrl, yoprequest);
		} catch (IOException e) {
			logger.error("调用易宝代扣接口异常:{}", ExceptionUtils.getStackTrace(e));
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			upsOrderEntity.setRemark("调用易宝代扣接口异常！");
			upsOrderService.updateOrderAndCreateOrderPush(upsOrderEntity);
			return;
		}
		String returnStr = yopresponse.toString();
		logger.info("易宝代扣返回结果:{}", returnStr);
		// 创建与第三方交互日志
		UpsThirdpartyLogEntity utl = new UpsThirdpartyLogEntity();
		utl.setMerchantCode(upsOrderEntity.getMerchantCode());
		utl.setOrderType(upsOrderEntity.getOrderType());
		utl.setPayChannel(upsOrderEntity.getPayChannel());
		utl.setUpsOrderCode(upsOrderEntity.getUpsOrderCode());
		utl.setRequestUrl(requestUrl);
		utl.setRequestText(requestText);
		utl.setReturnText(returnStr);
		upsThirdpartyLogService.createThirdpartyLog(utl);
		// 判断状态码200-300
		if (Objects.equals(yopresponse.getState(), "SUCCESS")) {
			// 返回http状态码为200
			String resultStr = yopresponse.getStringResult();
			// 处理结果
			disposeResult(resultStr, upsOrderEntity);
			return ;
		}
		// 返回http状态码大于500
		logger.info("易宝代扣接口接收数据失败！状态码：FAILURE");
		YopError error = yopresponse.getError();
		if (Objects.nonNull(error)) {
			String errorCode = error.getCode();
			String errorMsg = error.getMessage();
			upsOrderEntity.setResultCode(errorCode);
			upsOrderEntity.setResultMessage(errorMsg);
		}
		upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
		upsOrderEntity.setRemark("易宝代扣接口接收数据失败！状态码：FAILURE");
		upsOrderService.updateOrderAndCreateOrderPush(upsOrderEntity);
	}

	private void disposeResult(String resultStr, UpsOrderEntity orderEntity) {
		Map<String, Object> reseult = JSONObject.parseObject(resultStr, new TypeReference<Map<String, Object>>() {
		});
		String status = MapUtils.getString(reseult, "status", "");
		orderEntity.setResultCode(status);
		// 返回受理失败
		if (StringUtils.equalsIgnoreCase(status, PAY_FAIL)) {
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			orderEntity.setResultMessage("支付失败");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return;
		}

		if (StringUtils.equalsIgnoreCase(status, PROCESSING)) {
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			orderEntity.setResultMessage("支付处理中");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return;
		}
		// 返回异常
		orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
		orderEntity.setResultMessage("支付发生异常");
		upsOrderService.updateOrderAndCreateOrderPush(orderEntity);

	}

}
