package com.pgy.ups.pay.gateway.controller;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.pgy.ups.common.exception.ParamValidException;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.commom.constants.UpsResultCode;
import com.pgy.ups.pay.commom.factory.impl.QueryServiceFactory;
import com.pgy.ups.pay.commom.utils.SecurityUtils;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsSignDefaultConfigEntity;
import com.pgy.ups.pay.interfaces.model.UpsOrderModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.auth.UpsSignDefaultConfigervice;
import com.pgy.ups.pay.interfaces.service.config.MerchantConfigService;
import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;

/**
 * 支付网关
 *
 * @author 墨凉
 *
 */

@Controller
@RequestMapping("/query")
public class QueryController {

	private Logger logger = LoggerFactory.getLogger(QueryController.class);

	@Resource
	private OrderPushService orderPushService;

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsAuthSignService upsAuthSignService;

	@Resource
	private UpsSignDefaultConfigervice upsSignDefaultConfigervice;

	@Resource
	private MerchantConfigService merchantConfigService;

	@Resource
	private QueryServiceFactory queryServiceFactory;

	/**
	 * 查询UPS订单信息
	 * 
	 * @param merchant
	 * @param upsOrderId
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@RequestMapping("/order.action")
	public UpsResultModel queryOrderEntity(String merchant, String upsOrderId, String businessFlowNum)
			throws ParamValidException {
		if (StringUtils.isBlank(merchant)) {
			throw new ParamValidException("系统来源的商户编码不能为空！");
		}
		if (StringUtils.isAllBlank(upsOrderId, businessFlowNum)) {
			throw new ParamValidException("UPS订单ID和业务流水号不能都为空！");
		}
		if (StringUtils.isNotBlank(upsOrderId) && !StringUtils.isNumeric(upsOrderId)) {
			throw new ParamValidException("UPS订单ID必须为数字！");
		}
		UpsOrderEntity order = new UpsOrderEntity();
		order.setMerchantCode(merchant);
		if (StringUtils.isNotBlank(upsOrderId)) {
			order.setId(Long.parseLong(upsOrderId));
		}
		if (StringUtils.isNotBlank(businessFlowNum)) {
			order.setBusinessFlowNum(businessFlowNum);
		}
		order = upsOrderService.queryUpsOrderEntity(order);
		if (Objects.isNull(order)) {
			return new UpsResultModel(UpsResultCode.BUSINESS_ERROR, "没有查询到订单信息");
		}
		// 若订单不为最终态，重置订单查询状态和时间
		if (!StringUtils.equalsIgnoreCase(order.getOrderStatus(), OrderStatus.ORDER_STATUS_SUCCESS)
				&& !StringUtils.equalsIgnoreCase(order.getOrderStatus(), OrderStatus.ORDER_STATUS_FAIL)) {
			// 异步修改订单查询时间和次数
			orderPushService.resetOrderQueryTimeAndCountAsyn(order.getId());
		}
		UpsOrderModel upsOrderModel = new UpsOrderModel();
		upsOrderModel.setId(order.getId());
		upsOrderModel.setAmount(order.getAmount());
		upsOrderModel.setBankCard(order.getBankCard());
		upsOrderModel.setBankCode(order.getBankCode());
		upsOrderModel.setBusinessFlowNum(order.getBusinessFlowNum());
		upsOrderModel.setBusinessType(order.getBusinessType());
		upsOrderModel.setCreateTime(order.getCreateTime());
		upsOrderModel.setFromSystem(order.getMerchantCode());
		upsOrderModel.setIdentity(order.getIdentity());
		upsOrderModel.setNotifyUrl(order.getNotifyUrl());
		upsOrderModel.setOrderStatus(order.getOrderStatus());
		upsOrderModel.setOrderType(order.getOrderType());
		upsOrderModel.setPayChannel(order.getPayChannel());
		upsOrderModel.setPhoneNo(order.getPhoneNo());
		upsOrderModel.setRealName(order.getRealName());
		upsOrderModel.setRemark(order.getRemark());
		upsOrderModel.setResultCode(order.getResultCode());
		upsOrderModel.setResultMessage(order.getResultMessage());
		upsOrderModel.setUpdateTime(order.getUpdateTime());
		upsOrderModel.setUpsOrderCode(order.getUpsOrderCode());
		upsOrderModel.setUserNo(order.getUserNo());
		UpsResultModel resultModel = new UpsResultModel(UpsResultCode.SUCCESS, "查询成功");
		resultModel.setResult(upsOrderModel);
		resultModel.setSign(SecurityUtils.sign(upsOrderModel,
				merchantConfigService.queryMerchantPrivateKey(upsOrderModel.getFromSystem())));
		return resultModel;
	}

	@ResponseBody
	@RequestMapping("/userSignStatus.action")
	public UpsResultModel queryUserSignStatus(UpsAuthSignEntity upsAuthSignBaofooEntity) {
		logger.info("查询用户是否需要签约传参:{}", upsAuthSignBaofooEntity);
		if (StringUtils.isEmpty(upsAuthSignBaofooEntity.getSignType())) {
			UpsSignDefaultConfigEntity entity = upsSignDefaultConfigervice
					.queryUpsSignDefaultConfig(upsAuthSignBaofooEntity.getMerchantCode());
			String type = entity == null ? "protocol" : entity.getSignType();
			upsAuthSignBaofooEntity.setSignType(type);
		}
		UpsAuthSignEntity upsAuthSignEntity = upsAuthSignService.queryUpsAuthSignBaofoo(upsAuthSignBaofooEntity);
		Boolean flag = false;
		if (upsAuthSignEntity == null || upsAuthSignEntity.getStatus() != 10) {
			flag = true;
		}
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("signStatus", flag);
		return UpsResultModelUtil.upsResultModelSuccess(map);
	}

	/**
	 * 查询订单在第三方支付渠道的状态
	 * 
	 * @param upsOrderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/query.action")
	public String queryThirdpartyOrderStatus(Long upsOrderId) {
		OrderPushEntity ope = orderPushService.queryByOrderId(upsOrderId);
		if (Objects.isNull(ope)) {
			return "没有该订单号！";
		}
		return queryServiceFactory.getInstance(ope).doSingleQuery(ope, true);
	}

	/**
	 * 重置查询第三方订单状态
	 * 
	 * @param upsOrderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/requery.action")
	public OrderPushEntity requeryOrder(Long upsOrderId) {
		OrderPushEntity ope = orderPushService.queryByOrderId(upsOrderId);
		ope.setRequery(true);
		orderPushService.updateOrderPush(ope);
		return ope;
	}

}
