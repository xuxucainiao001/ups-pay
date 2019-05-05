package com.pgy.ups.pay.commom.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pgy.data.handler.domain.PgyData;
import com.pgy.data.handler.service.PgyDataHandlerService;
import com.pgy.data.handler.service.impl.PgyDataHandlerServiceImpl;
import com.pgy.ups.common.utils.ReflectUtils;
import com.pgy.ups.pay.commom.constants.OrderPushStatus;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.commom.constants.RequestAttributeName;
import com.pgy.ups.pay.commom.dao.UpsOrderDao;
import com.pgy.ups.pay.commom.utils.IdGenerateWorker;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;
import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.log.UpsLogService;
import com.pgy.ups.pay.interfaces.service.order.OrderPushService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;

/**
 * 订单业务逻辑接口实现
 * 
 * @author acer
 *
 */
@Service
public class UpsOrderServiceImpl implements UpsOrderService {

	@Resource
	private HttpServletRequest request;

	@Resource
	private UpsOrderDao upsOrderDao;

	@Resource
	private UpsLogService upsLogService;

	@Resource
	private OrderPushService orderPushService;

	/**
	 * 创建还款（代付 代扣）新订单
	 */
	@Override
	public UpsOrderEntity createUpsOrder(UpsParamModel upsParamModel) {
		UpsOrderEntity uspOrderEntity = new UpsOrderEntity();
		//反射获取金额
		Map<String, String> params = ReflectUtils.objectToMap(upsParamModel);		
		uspOrderEntity.setAmount(new BigDecimal(MapUtils.getString(params, "amount")));
		//生成Id
		Long id = IdGenerateWorker.getInstance().nextId();
		uspOrderEntity.setId(id);
		// 订购类型
		uspOrderEntity.setOrderType(upsParamModel.getOrderType());
		// 来源系统
		uspOrderEntity.setMerchantCode(upsParamModel.getMerchantCode());
		// 业务类型
		uspOrderEntity.setBusinessType(upsParamModel.getBusinessType());
		// 业务流水号
		uspOrderEntity.setBusinessFlowNum(upsParamModel.getBusinessFlowNum());
		// 用户编码
		uspOrderEntity.setUserNo(upsParamModel.getUserNo());
		/*四要素加密开始*/
		
		String realName=upsParamModel.getRealName();
		String identity=upsParamModel.getIdentity();
		String phoneNo=upsParamModel.getPhoneNo();
		String bankCard=upsParamModel.getBankCard();
		
		//加密工具service
		PgyDataHandlerService pgyDataHandlerService =new PgyDataHandlerServiceImpl();
		
		PgyData realNamePgyData=pgyDataHandlerService.chineseName(realName);
		PgyData identityPgyData=pgyDataHandlerService.idcard(identity);
		PgyData phoneNoPgyData=pgyDataHandlerService.fixedPhone(phoneNo);
		PgyData bankCardPgyData=pgyDataHandlerService.bankCard(bankCard);
		
		
		// 真实姓名
		uspOrderEntity.setRealName(realNamePgyData.getValMask());
		uspOrderEntity.setRealNameEncrypt(realNamePgyData.getValEncrypt());
		uspOrderEntity.setRealNameMd5(realNamePgyData.getValMd5());
		// 身份证号
		uspOrderEntity.setIdentity(identityPgyData.getValMask());
		uspOrderEntity.setIdentityEncrypt(identityPgyData.getValEncrypt());
		uspOrderEntity.setIdentityMd5(identityPgyData.getValMd5());
		// 手机号码
		uspOrderEntity.setPhoneNo(phoneNoPgyData.getValMask());
		uspOrderEntity.setPhoneNoEncrypt(phoneNoPgyData.getValEncrypt());
		uspOrderEntity.setPhoneNoMd5(phoneNoPgyData.getValMd5());
		// 银行卡号
		uspOrderEntity.setBankCard(bankCardPgyData.getValMask());
		uspOrderEntity.setBankEncrypt(bankCardPgyData.getValEncrypt());
		uspOrderEntity.setBankMd5(bankCardPgyData.getValMd5());
		/*四要素加密结束*/
		// 银行编码
		uspOrderEntity.setBankCode(upsParamModel.getBankCode());	
		// 支付渠道
		uspOrderEntity.setPayChannel(upsParamModel.getPayChannel());
		// 订单状态
		uspOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_NEW);
		// 回调地址
		uspOrderEntity.setNotifyUrl(upsParamModel.getNotifyUrl());
		// 创建时间
		uspOrderEntity.setCreateTime(new Date());
		// 更新时间
		uspOrderEntity.setUpdateTime(new Date());
		// 订单编码生成
		uspOrderEntity.setUpsOrderCode(getUpsOrderCode(uspOrderEntity));

		uspOrderEntity = upsOrderDao.saveAndFlush(uspOrderEntity);

		updateUspLog(uspOrderEntity);
		return uspOrderEntity;
	}

	/**
	 * 保存订单信息
	 * 
	 * @param uspOrderEntity
	 */
	private void updateUspLog(UpsOrderEntity uspOrderEntity) {
		// 从request中获取日志对象
		UpsLogEntity upsLogEntity = (UpsLogEntity) request.getAttribute(RequestAttributeName.UPS_LOG_ENTITY);
		// 设置订单id和修改时间
		upsLogEntity.setOrderId(uspOrderEntity.getId() + "");
		upsLogService.updateUpsLog(upsLogEntity);
	}

	/**
	 * 生成订单编码
	 * 
	 * @param uspOrderEntity
	 * @return
	 */
	private String getUpsOrderCode(UpsOrderEntity uspOrderEntity) {
		return uspOrderEntity.getPayChannel() + uspOrderEntity.getMerchantCode() + uspOrderEntity.getId();
	}

	@Override
	public UpsOrderEntity updateUpsOrder(UpsOrderEntity orderEntity) {
		orderEntity.setUpdateTime(new Date());
		return upsOrderDao.saveAndFlush(orderEntity);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public UpsOrderEntity updateOrderAndCreateOrderPush(UpsOrderEntity orderEntity) {
		orderEntity.setUpdateTime(new Date());
		// 构建订单推送实体
		OrderPushEntity orderPushEntity = new OrderPushEntity();
		orderPushEntity.setOrderId(orderEntity.getId());
		orderPushEntity.setPayChannel(orderEntity.getPayChannel());
		orderPushEntity.setOrderType(orderEntity.getOrderType());
		orderPushEntity.setOrderCode(orderEntity.getUpsOrderCode());
		orderPushEntity.setMerchantCode(orderEntity.getMerchantCode());
		orderPushEntity.setOrderStatus(orderEntity.getOrderStatus());
		
		orderPushEntity.setNotifyUrl(orderEntity.getNotifyUrl());
		orderPushEntity.setOrderCreateTime(orderEntity.getCreateTime());
		orderPushEntity.setRequery(false);
		// 若直接失敗。則無需查詢，直接推送
		if(OrderStatus.ORDER_STATUS_FAIL.equals(orderEntity.getOrderStatus())) {
			orderPushEntity.setPushStatus(OrderPushStatus.PUSH_READY);
			orderPushEntity.setChannelResultCode(orderEntity.getResultCode());
			orderPushEntity.setChannelResultMsg(orderEntity.getResultMessage());			
		}else {
			orderPushEntity.setPushStatus(OrderPushStatus.QUERY_READY);
			orderPushEntity.setChannelResultCode(StringUtils.EMPTY);
			orderPushEntity.setChannelResultMsg(StringUtils.EMPTY);
		}
		
		orderPushEntity.setPushCount(0);
		orderPushEntity.setQueryCount(0);
		orderPushEntity.setNextQueryTime(new Date());
		orderPushEntity.setCreateTime(new Date());
		orderPushEntity.setUpdateTime(new Date());
		orderPushEntity.setId(IdGenerateWorker.getInstance().nextId());
		orderPushService.createOrderPush(orderPushEntity);
		return upsOrderDao.saveAndFlush(orderEntity);
	}

	@Override
	public UpsOrderEntity queryByOrderId(Long orderId) {
		Optional<UpsOrderEntity> optional = upsOrderDao.findById(orderId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	@Async // 异步更新
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateOrderAndupdateOrderPush(UpsOrderEntity upsOrderEntity, OrderPushEntity ope) {
		// 查询次数增加1
		int queryCount = ope.getQueryCount();
		ope.setNextQueryTime(DateUtils.addMinutes(new Date(),1));
		if (queryCount > 2) {
			// 查询超过三次，下次查询时间每次增加5*queryCount分钟
			Date nextQueryTime = ope.getNextQueryTime();
			nextQueryTime = DateUtils.addMinutes(nextQueryTime, 5 * queryCount - 2);
			ope.setNextQueryTime(nextQueryTime);
		}
		ope.setRequery(false);
		ope.setQueryCount(++queryCount);
		upsOrderDao.saveAndFlush(upsOrderEntity);
		orderPushService.updateOrderPush(ope);
	}

	@Override
	public UpsOrderEntity queryUpsOrderEntity(UpsOrderEntity order) {
		Example<UpsOrderEntity> e = Example.of(order);
		Optional<UpsOrderEntity> o = upsOrderDao.findOne(e);
		return o.orElse(null);
	}

}
