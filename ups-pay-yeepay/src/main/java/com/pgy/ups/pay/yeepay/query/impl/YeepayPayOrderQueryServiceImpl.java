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
import org.springframework.util.CollectionUtils;

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
 * 宝付代付轮询
 * 
 * @author 墨凉
 *
 */
@Service(group="yeepayPay",timeout=60000,retries=0)
public class YeepayPayOrderQueryServiceImpl implements OrderQueryService<String> {

	private Logger logger = LoggerFactory.getLogger(YeepayPayOrderQueryServiceImpl.class);

	private static final String PAY_CHANNEL = "yeepay";

	@Resource
	private OrderPushService orderPushService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private UpsOrderService upsOrderService;

	@Async // 异步轮询
	@PrintExecuteTime
	public void doMultiQuery() {

		List<OrderPushEntity> querylist = orderPushService.queryNeedQueryOrderList(PAY_CHANNEL, OrderType.PAY);
		logger.info("易宝代付交易查询任务开始！本次共查询{}笔", querylist.size());
		for (OrderPushEntity ope : querylist) {
			doSingleQuery(ope, false);
		}
		logger.info("易宝代付交易查询任务结束！");
	}

	@Override
	public String doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException{
		// 查询配置
		UpsThirdpartyConfigEntity config = upsThirdpartyConfigService.queryThirdpartyConfig(PAY_CHANNEL, OrderType.PAY,
				ope.getMerchantCode());
		// 抽取配置信息
		Map<String, Object> configMap = JSONObject.parseObject(config.getConfigDate(),
				new TypeReference<HashMap<String, Object>>() {
				});
		String merchantno = MapUtils.getString(configMap, "merchantno", "");
		String groupNumber = MapUtils.getString(configMap, "groupNumber", "");
		String appKey = MapUtils.getString(configMap, "appKey", "");
		String queryUrl = MapUtils.getString(configMap, "queryUrl", "");

		if (StringUtils.isAnyBlank(merchantno, groupNumber, queryUrl, appKey)) {
			logger.error("易宝代付交易查询配置信息包含空！{}", config);
			throw new BussinessException("易宝代付交易查询配置信息包含空");
		}

		// 拼装报文
		Map<String, Object> params = new HashMap<>();
		params.put("batchNo", ope.getOrderId());

		YopRequest yoprequest = new YopRequest(appKey);

		Set<Entry<String, Object>> entry = params.entrySet();
		for (Entry<String, Object> s : entry) {
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		// 调用接口
		YopResponse yopresponse = null;
		try {
			yopresponse = YeepayUtils.submit(queryUrl, yoprequest);
		} catch (IOException e) {
			logger.error("调用易宝代付交易查询接口异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("调用易宝代付交易查询接口异常！");
		}
		logger.info("易宝代付交易查询接口返回：{}", yopresponse);
		if (queryOnly) {
			return yopresponse.toString();
		}
		if (Objects.equals(yopresponse.getState(), "SUCCESS")) {
			// 返回http状态码为200
			String resultStr = yopresponse.getStringResult();
			// 处理结果
			disposeResult(resultStr, ope);
		}
		logger.info("易宝代付交易查询失败！");
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

	/**
	 * 处理返回结果
	 * 
	 * @param 墨凉
	 */
	private void disposeResult(String resultStr, OrderPushEntity ope) {

		Map<String, Object> result = JSONObject.parseObject(resultStr, new TypeReference<HashMap<String, Object>>() {
		});
		String listStr = MapUtils.getString(result, "list", "");
		if (StringUtils.isEmpty(listStr)) {
			logger.info("易宝代付交易查询结果中list参数为空");
			return;
		}
		List<Map<String, Object>> list = JSONObject.parseObject(listStr,
				new TypeReference<List<Map<String, Object>>>() {
				});
		if (CollectionUtils.isEmpty(list)) {
			logger.info("易宝代付交易查询结果中list解析结果为空");
			return;
		}
		// 获取唯一一个元素
		Map<String, Object> map = list.get(0);
		// 系统返回码
		String errorCode = MapUtils.getString(result, "errorCode", "");
		// 打款状态
		String transferStatusCode = MapUtils.getString(map, "transferStatusCode", "");
		// 银行处理状态
		String bankTrxStatusCode = MapUtils.getString(map, "bankTrxStatusCode", "");
		// 消息
		String bankMsg = MapUtils.getString(map, "bankMsg", "");

		// 查询订单
		UpsOrderEntity upsOrderEntity = upsOrderService.queryByOrderId(ope.getOrderId());
		// 更新订单和订单推送对象状态
		ope.setChannelResultCode(errorCode + " " + transferStatusCode + " " + bankTrxStatusCode);
		ope.setChannelResultMsg(bankMsg);
		ope.setUpdateTime(new Date());
		upsOrderEntity.setResultCode(errorCode + " " + transferStatusCode + " " + bankTrxStatusCode);
		upsOrderEntity.setResultMessage(bankMsg);
		upsOrderEntity.setUpdateTime(new Date());
		// 失败状态
		if (!StringUtils.equalsIgnoreCase("BAC001", errorCode)) {
			// 交易失败
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		} else if (StringUtils.equalsIgnoreCase("BAC001", errorCode)) {
			// 交易失败
			if (StringUtils.equalsIgnoreCase(transferStatusCode, "0027")
					|| StringUtils.equalsIgnoreCase(transferStatusCode, "0028")
					|| (StringUtils.equalsIgnoreCase(transferStatusCode, "0026")
							&& StringUtils.equalsIgnoreCase(bankTrxStatusCode, "F"))) {
				upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
				ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
				ope.setPushCount(0);
				ope.setPushStatus(OrderPushStatus.PUSH_READY);

			} else if (StringUtils.equalsIgnoreCase(transferStatusCode, "0026")
					&& StringUtils.equalsIgnoreCase(bankTrxStatusCode, "S")) {
				// 交易成功
				upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
				ope.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
				ope.setPushCount(0);
				ope.setPushStatus(OrderPushStatus.PUSH_READY);
			} else {
				// 其他情况等待再次查询
				upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
				ope.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			}
			// 开启事务更新
			upsOrderService.updateOrderAndupdateOrderPush(upsOrderEntity, ope);
		}

	}
}
