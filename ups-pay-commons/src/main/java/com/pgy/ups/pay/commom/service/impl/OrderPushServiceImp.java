package com.pgy.ups.pay.commom.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.common.utils.ReflectUtils;
import com.pgy.ups.common.utils.SpringUtils;
import com.pgy.ups.pay.commom.constants.OrderPushStatus;
import com.pgy.ups.pay.commom.dao.OrderPushDao;
import com.pgy.ups.pay.commom.utils.SecurityUtils;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.model.OrderPushModel;
import com.pgy.ups.pay.interfaces.service.config.MerchantConfigService;
import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;

/**
 * 订单推送业务
 * 
 * @author 墨凉
 *
 */
@Service
public class OrderPushServiceImp implements OrderPushService {

	private Logger logger = LoggerFactory.getLogger(OrderPushServiceImp.class);

	@Resource
	private OrderPushDao orderPushDao;

	@Resource
	private MerchantConfigService merchantConfigService;

	@Override
	public OrderPushEntity queryByOrderId(Long upsOrderId) {

		return orderPushDao.queryByOrderId(upsOrderId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public OrderPushEntity createOrderPush(OrderPushEntity orderPushEntity) {

		return orderPushDao.saveAndFlush(orderPushEntity);
	}

	@Override
	public List<OrderPushEntity> queryNeedQueryOrderList(String payChannel, String orderType) {
		// 仅仅查询72小时之前的所有订单
		Date beforeDays = DateUtils.addDay(new Date(), -3);
		List<OrderPushEntity> list = orderPushDao.queryByNeedOrderQueryList(payChannel, orderType, beforeDays);
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public OrderPushEntity updateOrderPush(OrderPushEntity ope) {
		return orderPushDao.saveAndFlush(ope);
	}

	/**
	 * 重置订查询送次数和时间
	 */
	@Override
	@Async
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void resetOrderQueryTimeAndCountAsyn(Long id) {

		orderPushDao.resetOrderQueryTimeAndCount(id);

	}

	@Override
	public List<OrderPushEntity> queryFinalStatusOrderPushList() {
		List<OrderPushEntity> list = orderPushDao.queryByNeedOrderPushList();
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
		// 推送次数超过10次的不再推送
		// return list.stream().filter(p -> p.getPushCount() <
		// 10).collect(Collectors.toList());
		return list;
	}

	@Override
	@Async
	public void pushOrder() {

		List<OrderPushEntity> list = this.queryFinalStatusOrderPushList();

		logger.info("订单推送业务端任务开始！本次推送订单数：{}条", list.size());
		// 遍历推送
		for (OrderPushEntity ope : list) {
			pushOrder(ope);
		}
		logger.info("订单推送业务端任务结束！");
	}

	@Override
	public void pushOrder(OrderPushEntity ope) {
		UpsOrderEntity oe = SpringUtils.getBean(UpsOrderService.class).queryByOrderId(ope.getOrderId());

		// 封装返回参数
		OrderPushModel orderPushModel = new OrderPushModel();
		orderPushModel.setMerchantName(ope.getMerchantCode());
		orderPushModel.setPayChannel(ope.getPayChannel());
		orderPushModel.setOrderType(ope.getOrderType());
		orderPushModel.setUpsOrderId(ope.getOrderId());
		orderPushModel.setOrderStatus(ope.getOrderStatus());
		orderPushModel.setChannelResultCode(ope.getChannelResultCode());
		orderPushModel.setChannelResultMsg(ope.getChannelResultMsg());
		orderPushModel.setBussinessFlowNum(oe.getBusinessFlowNum());
		orderPushModel.setSign(
				SecurityUtils.sign(orderPushModel, merchantConfigService.queryMerchantPrivateKey(ope.getMerchantCode())));
		boolean flag = false;
		try {
			String resultStr = OkHttpUtil.postForm(ope.getNotifyUrl(), ReflectUtils.objectToMap(orderPushModel));
			flag = parseResult(resultStr);
		} catch (IOException e) {
			logger.error("订单推送业务端掉调用HttpClient异常：{},推送内容：{}", e, orderPushModel);
			// 获取当前环境 非生产环境直接设为推送成功
			flag = false;
		}

		// 根据返回状态处理
		if (flag) {
			logger.info("推送订单成功：{}", orderPushModel);
			// 成功
			ope.setPushStatus(OrderPushStatus.PUSH_FINSH);
		} else {
			// 失败
			logger.info("推送订单失败：{}", orderPushModel);
			ope.setPushStatus(OrderPushStatus.PUSH_ING);
		}
		// 增加推送次数
		Integer pushCount = ope.getPushCount();
		ope.setPushCount(++pushCount);
		updateOrderPush(ope);

	}

	private boolean parseResult(String resultStr) {
		Map<String, Object> result = JSONObject.parseObject(resultStr, new TypeReference<Map<String, Object>>() {
		});
		return MapUtils.getBoolean(result, "flag", false);
	}

}
