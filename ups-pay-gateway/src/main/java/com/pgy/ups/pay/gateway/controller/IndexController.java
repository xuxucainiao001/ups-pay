package com.pgy.ups.pay.gateway.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.pgy.ups.pay.gateway.factory.BussinessHandlerFactory;
import com.pgy.ups.pay.gateway.factory.ChannelHandlerFactory;
import com.pgy.ups.pay.gateway.factory.HandlerFactory;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.annotation.PrintExecuteTime;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.exception.ParamValidException;
import com.pgy.ups.common.utils.SpringUtils;
import com.pgy.ups.pay.commom.constants.OrderType;
import com.pgy.ups.pay.commom.constants.RequestAttributeName;
import com.pgy.ups.pay.commom.utils.SecurityUtils;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;
import com.pgy.ups.pay.commom.utils.ValidateUtils;
import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;
import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsSignDefaultConfigEntity;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyConfigEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;
import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsCollectParamModel;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.model.UpsPayParamModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;
import com.pgy.ups.pay.interfaces.model.UpsUnBindCardModel;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.auth.UpsSignDefaultConfigervice;
import com.pgy.ups.pay.interfaces.service.config.CollectChooseService;
import com.pgy.ups.pay.interfaces.service.config.MerchantConfigService;
import com.pgy.ups.pay.interfaces.service.config.MerchantOrderTypeService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;
import com.pgy.ups.pay.interfaces.service.log.UpsLogService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;
import com.pgy.ups.pay.interfaces.service.route.PayCompanyService;
import com.pgy.ups.pay.interfaces.service.route.RouteService;

/**
 * 支付网关
 *
 * @author 墨凉
 *
 */

@Controller
@RequestMapping("/index")
public class IndexController {

	private Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Resource
	private HttpServletRequest request;

	@Resource
	private UpsLogService upsLogService;

	@Resource
	private RouteService routeService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private MerchantConfigService merchantConfigService;

	@Resource
	private UpsSignDefaultConfigervice upsSignDefaultConfigervice;

	@Resource
	private CollectChooseService collectChooseService;

	@Resource
	private UpsAuthSignService upsAuthSignBaofooService;




     @Resource
	 private HandlerFactory handlerFactory;

	@Resource
	private UpsAuthSignService upsAuthSignService;

	@Resource
	private MerchantOrderTypeService merchantOrderTypeService;
	
	@Resource
	private PayCompanyService PayCompanyService;

	/**
	 * 代付接口
	 *
	 * @return
	 * @throws ParamValidException
	 */

	@ResponseBody
	@RequestMapping("/pay.do")
	@PrintExecuteTime
	public UpsResultModel upsPay(UpsPayParamModel upsPayParamModel) throws ParamValidException {
		logger.info("接收借款参数：{}", upsPayParamModel);
		// 验证并保存参数，记录日志
		recordAndVerifyParam(upsPayParamModel);
		// 验证该商户是否开通了代付功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsPayParamModel.getMerchantCode(), OrderType.PAY);
		upsPayParamModel.setOrderType(OrderType.PAY);

		return disposePayOrCollect(upsPayParamModel, mote);
	}

	/**
	 * 代扣接口
	 *
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@RequestMapping("/collect.do")
	@PrintExecuteTime
	public UpsResultModel upsCollect(UpsCollectParamModel upsCollectParamModel) throws ParamValidException {
		logger.info("接收还款参数：{}", upsCollectParamModel);
		recordAndVerifyParam(upsCollectParamModel);
		// 判断走普通代扣或者协议代扣
		CollectChooseEntity cce = collectChooseService.queryCollectType(upsCollectParamModel.getMerchantCode());
		upsCollectParamModel.setOrderType(Objects.isNull(cce) ? OrderType.COLLECT
				: StringUtils.isBlank(cce.getCollectType()) ? OrderType.COLLECT : cce.getCollectType());
		// 验证该商户是否开通了代扣或协议代扣功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsCollectParamModel.getMerchantCode(), upsCollectParamModel.getOrderType());
		return disposePayOrCollect(upsCollectParamModel, mote);
	}

	/**
	 * 短信验证码的接口
	 *
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/auth/signature.do")
	public UpsResultModel upsSignature(UpsSignatureParamModel upsSignatureParamModel) throws ParamValidException {
		logger.info("认证签约参数：{}", upsSignatureParamModel);
		recordAndVerifyParam(upsSignatureParamModel);
		// 验证该商户是否开通了签约该功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsSignatureParamModel.getMerchantCode(), OrderType.SIGNATRUE);
		upsSignatureParamModel.setOrderType(OrderType.SIGNATRUE);
		// 返回并设置路由
		String payChannel = getPayChannel(upsSignatureParamModel, mote);
		upsSignatureParamModel.setPayChannel(payChannel);
		// 处理并返回结果
		UpsResultModel upsResultModel = handlerFactory.signature(upsSignatureParamModel);
		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 绑卡接口
	 *
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/auth/bindCard.do")
	public UpsResultModel upsBindCard(UpsBindCardParamModel upsBindCardParamModel) throws ParamValidException {
		logger.info("认证绑卡参数：{}", upsBindCardParamModel);
		recordAndVerifyParam(upsBindCardParamModel);
		// 验证该商户是否开通了绑卡该功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsBindCardParamModel.getMerchantCode(), OrderType.BINDCARD);
		upsBindCardParamModel.setOrderType(OrderType.BINDCARD);
		// 返回并设置路由
		String payChannel = getPayChannel(upsBindCardParamModel, mote);
		upsBindCardParamModel.setPayChannel(payChannel);
		// 处理并返回结果
		UpsResultModel upsResultModel = handlerFactory.bindCard(upsBindCardParamModel);
		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 协议签约
	 * 
	 * @param upsBindCardParamModel
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/protocol/signature.do")
	public UpsResultModel protocolSignature(UpsSignatureParamModel upsSignatureParamModel) throws ParamValidException {
		logger.info("协议签约参数：{}", upsSignatureParamModel);
		recordAndVerifyParam(upsSignatureParamModel);
		// 验证该商户是否开通了协议签约该功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsSignatureParamModel.getMerchantCode(), OrderType.PROTOCOL_SIGNATRUE);
		upsSignatureParamModel.setOrderType(OrderType.PROTOCOL_SIGNATRUE);

		// 返回并设置路由
		String payChannel = getPayChannel(upsSignatureParamModel, mote);
		upsSignatureParamModel.setPayChannel(payChannel);
		// 处理并返回结果
		UpsResultModel upsResultModel = handlerFactory.signature(upsSignatureParamModel);
		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 协议绑卡
	 * 
	 * @param upsBindCardParamModel
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/protocol/bindCard.do")
	public UpsResultModel protocolBindCard(UpsBindCardParamModel upsBindCardParamModel) throws ParamValidException {
		logger.info("协议绑卡参数：{}", upsBindCardParamModel);
		recordAndVerifyParam(upsBindCardParamModel);
		// 验证该商户是否开通了协议签约该功能
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsBindCardParamModel.getMerchantCode(), OrderType.PROTOCOL_BINDCARD);
		upsBindCardParamModel.setOrderType(OrderType.PROTOCOL_BINDCARD);
		// 返回并设置路由
		String payChannel = getPayChannel(upsBindCardParamModel, mote);
		upsBindCardParamModel.setPayChannel(payChannel);

		// 处理并返回结果
		UpsResultModel upsResultModel = handlerFactory.bindCard(upsBindCardParamModel);

		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 统一签约
	 * 
	 * @param upsBindCardParamModel
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/signature.do")
	public UpsResultModel signature(UpsSignatureParamModel upsBindCardParamModel) throws ParamValidException {
		logger.info("统一签约入参：{}", upsBindCardParamModel);
		recordAndVerifyParam(upsBindCardParamModel);
		UpsSignDefaultConfigEntity entity = upsSignDefaultConfigervice
				.queryUpsSignDefaultConfig(upsBindCardParamModel.getMerchantCode());
		String signType = entity == null ? SignTypeEnum.PROTOCOL.getCode() : entity.getSignType();

		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsBindCardParamModel.getMerchantCode(),OrderType.SignatureMap.get(signType));
		upsBindCardParamModel.setOrderType(OrderType.SignatureMap.get(signType));
		// 返回并设置路由
		String payChannel = getPayChannel(upsBindCardParamModel, mote);
		upsBindCardParamModel.setPayChannel(payChannel);
		UpsResultModel upsResultModel = handlerFactory.signature(upsBindCardParamModel);
		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 统一绑卡
	 * 
	 * @param upsBindCardParamModel
	 * @return
	 * @throws ParamValidException
	 */
	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/bindCard.do")
	public UpsResultModel bindCard(UpsBindCardParamModel upsBindCardParamModel) throws ParamValidException {
		logger.info("统一绑卡参数：{}", upsBindCardParamModel);
		recordAndVerifyParam(upsBindCardParamModel);
		UpsSignDefaultConfigEntity entity = upsSignDefaultConfigervice
				.queryUpsSignDefaultConfig(upsBindCardParamModel.getMerchantCode());
		String signType = entity == null ? SignTypeEnum.PROTOCOL.getCode() : entity.getSignType();
		MerchantOrderTypeEntity mote = merchantOrderTypeService
				.confirmMerchantOrderType(upsBindCardParamModel.getMerchantCode(),OrderType.BindCardMap.get(signType));

		upsBindCardParamModel.setOrderType(OrderType.BindCardMap.get(signType));
		// 返回并设置路由
		String payChannel = getPayChannel(upsBindCardParamModel, mote);
		upsBindCardParamModel.setPayChannel(payChannel);
		// 处理并返回结果
		UpsResultModel upsResultModel = handlerFactory.bindCard(upsBindCardParamModel);
		return recordAndReturnResult(upsResultModel);
	}

	@ResponseBody
	@PrintExecuteTime
	@RequestMapping("/unbindCard.do")
	public UpsResultModel unbindCard(UpsUnBindCardModel upsUnBindCardModel) {
		logger.info("统一解绑参数：{}", upsUnBindCardModel);
		upsAuthSignService.unbindCard(upsUnBindCardModel);
		return UpsResultModelUtil.upsResultModelSuccess();
	}

	/**
	 * 处理代付代扣
	 * 
	 * @param upm
	 * @return
	 */

	private UpsResultModel disposePayOrCollect(UpsParamModel upm, MerchantOrderTypeEntity mote) {
		// 路由逻辑
		String payChannel = getPayChannel(upm, mote);
		upm.setPayChannel(payChannel);
		// 协议代扣验证是否有先签约
		if (Objects.equals(upm.getOrderType(), OrderType.PROTOCOL_COLLECT)) {
			// 查询用户协议信息
			UpsAuthSignEntity uasbe = upsAuthSignBaofooService.queryProtocolSignBaofoo(upm.getMerchantCode(),
					upm.getPayChannel(), upm.getUserNo(), upm.getBankCard());
			if (Objects.isNull(uasbe) || StringUtils.isBlank(uasbe.getTppSignNo())) {
				logger.error("宝付协议代扣未查到用户签约信息！查询信息：{}", uasbe);
				return new UpsResultModel(UpsResultEnum.NO_PROPTOCAL);
			}
		}
		// 查询支付配置信息
		UpsThirdpartyConfigEntity upsThirdpartyConfigEntity = upsThirdpartyConfigService
				.queryThirdpartyConfig(upm.getPayChannel(), upm.getOrderType(), upm.getMerchantCode());
		Map<String, Object> configDateMap = JSONObject.parseObject(upsThirdpartyConfigEntity.getConfigDate(),
				new TypeReference<Map<String, Object>>() {
				});
		// 创建订单并保存
		UpsOrderEntity upsOrderEntity = upsOrderService.createUpsOrder(upm);
		// 将第三方渠道调用配置放入订单实体中
		upsOrderEntity.setUpsConfigDate(configDateMap);
		upsOrderEntity.setUpsParamModel(upm);
		// 处理并返回结果

		UpsResultModel upsResultModel;
		if (Objects.equals(upm.getOrderType(), OrderType.PAY)) {
			upsResultModel = handlerFactory.pay(upsOrderEntity);
		}else{
			 upsResultModel = handlerFactory.collect(upsOrderEntity);
		}
		return recordAndReturnResult(upsResultModel);
	}

	/**
	 * 获取路由
	 *
	 * @param upsParamModel
	 * @throws ParamValidException
	 */
	private String getPayChannel(UpsParamModel upm, MerchantOrderTypeEntity mote) {
		// 若商户端未指定路由，且商户支付路由策略为开启默认，则设为默认路由
		if (Objects.equals(mote.getRouteStatus(), MerchantOrderTypeService.OPEN_DEFAULT)) {
			String payChannel = mote.getDefaultPayChannel();
			if (StringUtils.isBlank(payChannel)) {
				throw new BussinessException("默认路由不能为空！");
			}
			PayCompanyService.confirmPayChanelisAvaliable(payChannel);
			return payChannel;
		} else {
			return routeService.obtainAvalibaleRoute(upm);
		}
		
	}

	/**
	 * 验证和记录参数
	 *
	 * @param upsParamModel
	 * @throws ParamValidException
	 */

	private void recordAndVerifyParam(UpsParamModel upsParamModel) throws ParamValidException {
		UpsLogEntity upsLogEntity = upsLogService.createUpsLog(upsParamModel, request.getRequestURI());
		// 保存当前请求日志对象
		request.setAttribute(RequestAttributeName.UPS_LOG_ENTITY, upsLogEntity);
		// 参数合法性验证
		ValidateUtils.validate(upsParamModel);
		// 查询商户配置公钥
		String publicKey = merchantConfigService.queryMerchantPublicKey(upsParamModel.getMerchantCode());
		// RSA验证签名
		SecurityUtils.signVerification(upsParamModel, publicKey);
		// 获取当前环境
		String profile = SpringUtils.getApplicationContext().getEnvironment().getActiveProfiles()[0];

		logger.info("spring当前运行环境：{}", profile);
		// 非生产环境限制金钱为0.01
		if (!StringUtils.equals(profile.trim(), "prod")) {
			if (upsParamModel instanceof UpsPayParamModel) {
				((UpsPayParamModel) upsParamModel).setAmount(new BigDecimal("0.01"));
			}
			if (upsParamModel instanceof UpsCollectParamModel) {
				((UpsCollectParamModel) upsParamModel).setAmount(new BigDecimal("0.01"));
			}
		}

	}

	/**
	 * 记录返回给业务的结果
	 *
	 * @param upsResultModel
	 */
	private UpsResultModel recordAndReturnResult(UpsResultModel upsResultModel) {
		UpsLogEntity upsLogEntity = (UpsLogEntity) request.getAttribute(RequestAttributeName.UPS_LOG_ENTITY);
		upsLogEntity.setReturnParam(JSONObject.toJSONString(upsResultModel));
		upsLogEntity.setCode(upsResultModel.getCode());
		upsLogEntity.setMessage(upsResultModel.getMessage());
		upsLogService.updateUpsLog(upsLogEntity);
		return upsResultModel;
	}
}
