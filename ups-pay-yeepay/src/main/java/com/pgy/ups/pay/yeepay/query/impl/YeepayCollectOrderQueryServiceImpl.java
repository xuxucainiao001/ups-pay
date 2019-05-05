package com.pgy.ups.pay.yeepay.query.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.annotation.PrintExecuteTime;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.commom.constants.OrderPushStatus;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.commom.constants.OrderType;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyConfigEntity;
import com.pgy.ups.pay.interfaces.query.OrderQueryService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;
import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;
import com.pgy.ups.pay.yeepay.utils.YeepayUtils;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.error.YopError;

/**
 * 易宝代扣轮询
 * 
 * @author 墨凉
 *
 */

@Service(group="yeepayCollect",timeout=60000,retries=0)
public class YeepayCollectOrderQueryServiceImpl implements OrderQueryService<String> {

	private Logger logger = LoggerFactory.getLogger(YeepayCollectOrderQueryServiceImpl.class);

	private static final String PAY_CHANNEL = "yeepay";

	private static final String PAY_SUCCESS = "PAY_SUCCESS";

	private static final String PAY_FAIL = "PAY_FAIL";

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private OrderPushService orderPushService;

	@Async
	@Override // 异步
	@PrintExecuteTime
	public void doMultiQuery() {

		List<OrderPushEntity> querylist = orderPushService.queryNeedQueryOrderList(PAY_CHANNEL, OrderType.COLLECT);
		Integer total = querylist.size();
		logger.info("易宝代扣交易查询任务开始！本次共查询{}笔", total);
		for (OrderPushEntity ope : querylist) {
			doSingleQuery(ope, false);
		}
		logger.info("易宝代扣交易查询任务结束！");
	}

	@Override
	public String doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException{
		// 查询配置
		UpsThirdpartyConfigEntity config = upsThirdpartyConfigService.queryThirdpartyConfig(PAY_CHANNEL,
				OrderType.COLLECT, ope.getMerchantCode());
		Map<String, Object> configMap = JSONObject.parseObject(config.getConfigDate(),
				new TypeReference<Map<String, Object>>() {
				});
		String merchantno = MapUtils.getString(configMap, "merchantno", "");
		String queryUrl = MapUtils.getString(configMap, "queryUrl", "");
		String terminalno = MapUtils.getString(configMap, "terminalno", "");
		String appKey = MapUtils.getString(configMap, "appKey", "");

		if (StringUtils.isAnyBlank(merchantno, terminalno, queryUrl, appKey)) {
			logger.error("易宝代扣交易查询配置信息包含空！{}", config);
			throw new BussinessException("易宝代扣交易查询配置信息包含空");
		}
		// 拼装报文
		Map<String, Object> params = new HashMap<>();
		params.put("requestno", ope.getOrderCode());

		YopRequest yoprequest = new YopRequest(appKey);

		Set<Entry<String, Object>> entry = params.entrySet();
		for (Entry<String, Object> s : entry) {
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		// 向YOP发请求
		YopResponse yopresponse = null;
		try {
			yopresponse = YeepayUtils.submit(queryUrl, yoprequest);
		} catch (IOException e) {
			logger.error("调用易宝代扣交易查询接口异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("调用易宝代扣交易查询接口异常！");
		}
		logger.info("宝代扣交易查询接口返回：{}", yopresponse);
		if (StringUtils.equalsAnyIgnoreCase(yopresponse.getState(), "SUCCESS")) {
			// 返回http状态码为200
			String resultStr = yopresponse.getStringResult();
			// 处理结果
			disposeResult(resultStr, ope, queryOnly);
			return yopresponse.toString();
		}
		logger.info("宝代扣交易查询失败！");
		// 记录失败状态并更新
		YopError errors = yopresponse.getError();
		if (Objects.nonNull(errors)) {
			String errorCode = errors.getCode();
			String errorMsg = errors.getMessage();
			ope.setChannelResultCode(errorCode);
			ope.setChannelResultMsg(errorMsg);
			orderPushService.updateOrderPush(ope);
		}
		return yopresponse.toString();
	}

	private String disposeResult(String resultStr, OrderPushEntity ope, boolean queryOnly) {

		Map<String, Object> result = JSONObject.parseObject(resultStr, new TypeReference<HashMap<String, Object>>() {
		});
		if (queryOnly) {
			return result.toString();
		}
		String status = MapUtils.getString(result, "status", "");
		String errorcode = MapUtils.getString(result, "errorcode", "");
		String errormsg = MapUtils.getString(result, "errormsg", "");
		// 查询订单
		UpsOrderEntity upsOrderEntity = upsOrderService.queryByOrderId(ope.getOrderId());
		// 更新订单和订单推送对象状态
		ope.setChannelResultCode(status + " " + errorcode);
		ope.setChannelResultMsg(errormsg);
		ope.setUpdateTime(new Date());
		upsOrderEntity.setResultCode(status + " " + errorcode);
		upsOrderEntity.setResultMessage(errormsg);
		upsOrderEntity.setUpdateTime(new Date());
		if (StringUtils.equalsIgnoreCase(PAY_SUCCESS, status)) {
			// 交易成功
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		} else if (StringUtils.equalsIgnoreCase(PAY_FAIL, status)) {
			// 交易失败
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		} else {
			// 处理中
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);

		}
		// 开启事务更新
		upsOrderService.updateOrderAndupdateOrderPush(upsOrderEntity, ope);
		return result.toString();
	}
}
