package com.pgy.ups.pay.baofoo.pay.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.ProtocolCollectService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.annotation.PrintExecuteTime;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooFormatUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSignatureUtil;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyLogEntity;
import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.log.UpsThirdpartyLogService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;

/**
 * 宝付协议支付处理器
 * 
 * @author 墨凉
 * 
 *
 */
@Service(group="baofooProtocolCollect",timeout=60000,retries=0)
public class BaofooProtocolCollectServiceImpl implements ProtocolCollectService {
	
	
	private Logger logger = LoggerFactory.getLogger(BaofooProtocolCollectServiceImpl.class);
	
	@Resource
	private UpsAuthSignService upsAuthSignBaofooService;
	

	/**
	 * 公钥私钥相对classpath的路径
	 */
	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;


	/**
	 * 协议代扣交易成功编码
	 */
	public static final String SUCCESS_CODE = "S";
	/**
	 * 协议代扣交易处理中编码
	 */
	public static final String ACCEPT_CODE = "I";

	/**
	 * 协议代扣交易处理中编码
	 */
	public static final String REJECT_CODE = "F";



	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyLogService upsThirdpartyLogService;


	@Resource(name = "threadPoolExecutor")
	private ThreadPoolExecutor threadPoolExecutor;


	@Override
	public UpsResultModel protocolColle(UpsOrderEntity orderEntity) throws BussinessException {

		// 查询用户协议信息
		UpsAuthSignEntity uasbe = upsAuthSignBaofooService.queryProtocolSignBaofoo(orderEntity.getMerchantCode(),
				orderEntity.getPayChannel(), orderEntity.getUserNo(), orderEntity.getUpsParamModel().getBankCard());
		if (Objects.isNull(uasbe) || StringUtils.isBlank(uasbe.getTppSignNo())) {
			logger.error("宝付协议代扣未查到用户签约信息！查询信息：{}", uasbe);
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			orderEntity.setRemark("宝付协议代扣未查到用户签约信息！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			throw new BussinessException("宝付协议代扣未查到用户签约信息！");
		}

		// 获取公共配置信息
		Map<String, Object> baofooConfig = orderEntity.getUpsConfigDate();
		String member_id = MapUtils.getString(baofooConfig, "member_id", "");
		String terminal_id = MapUtils.getString(baofooConfig, "terminal_id", "");
		String aes_key = MapUtils.getString(baofooConfig, "aes_key", "");
		String private_key = MapUtils.getString(baofooConfig, "private_key", "");
		String public_key = MapUtils.getString(baofooConfig, "public_key", "");
		String version = MapUtils.getString(baofooConfig, "version", "");
		String key_store_password = MapUtils.getString(baofooConfig, "key_store_password", "");
		String request_url = MapUtils.getString(baofooConfig, "request_url", "");

		if (StringUtils.isAnyBlank(member_id, terminal_id, aes_key, private_key, public_key, version,
				key_store_password, request_url)) {
			logger.error("读取宝付协议代扣配置信息错误:{}", baofooConfig);
			throw new BussinessException("读取宝付协议代扣配置信息错误！");
		}

		private_key = RSA_KEY_PATH + private_key;
		final String  publicKeyRsa = RSA_KEY_PATH + public_key;

		// 签约协议号加密
		String cipherProtocolNo = uasbe.getTppSignNo();
		try {
			cipherProtocolNo = BaofooSecurityUtil.AesEncryptProtocol(BaofooSecurityUtil.Base64Encode(cipherProtocolNo),
					aes_key);
		} catch (UnsupportedEncodingException e) {
			logger.error("宝付协议代扣签约协议号AES加密异常{}", ExceptionUtils.getStackTrace(e));
			logger.info("宝付协议代扣对称秘钥AES_KEY:{}", aes_key);
			logger.info("加密内容：{}", uasbe.getTppSignNo());
			throw new BussinessException("宝付协议代扣签约协议号AES加密异常！");
		}

		// 关键信息加密
		String cipher_dgtl_envlp;
		try {
			cipher_dgtl_envlp = BaofooRsaCodingUtil
					.encryptByPubCerFile(BaofooSecurityUtil.Base64Encode("01|" + aes_key), public_key);
		} catch (UnsupportedEncodingException e) {
			logger.error("宝付协议代扣对称秘钥加密发生异常{}", ExceptionUtils.getStackTrace(e));
			logger.info("宝付协议代扣对称秘钥AES_KEY:{}", aes_key);
			logger.info("宝付协议代扣公钥：PUBLIC_KEY:{}", public_key);
			logger.info("加密内容：{}", "01|" + aes_key);
			throw new BussinessException("宝付协议代扣对称秘钥加密发生异常！");
		}

		// 构建风控信息
		Map<String, String> riskItem = new HashMap<>();
		riskItem.put("goodsCategory", "02"); // 行业鼠疫互金消金
		riskItem.put("chPayIp", "127.0.0.1");

		// 构建post发送报文
		Map<String, String> postData = new TreeMap<>();
		postData.put("risk_item", JSONObject.toJSONString(riskItem));// 风控
		postData.put("send_time", DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		postData.put("msg_id", orderEntity.getId() + "");// 报文流水号
		postData.put("version", version);
		postData.put("terminal_id", terminal_id);
		postData.put("txn_type", "08");// 交易类型(参看：文档中《交易类型枚举》)
		postData.put("member_id", member_id);
		postData.put("trans_id", orderEntity.getUpsOrderCode());
		postData.put("dgtl_envlp", cipher_dgtl_envlp);
		postData.put("user_id", uasbe.getUpsUserId());// 用户在商户平台唯一ID (和绑卡时要一致)
		postData.put("protocol_no", cipherProtocolNo);// 签约协议号（密文）
		postData.put("txn_amt", orderEntity.getAmount().multiply(new BigDecimal("100")).setScale(0).toPlainString());// 交易金额
																														// [单位：分

		// 加签
		String signVStr = BaofooFormatUtil.coverMap2String(postData);
		String sign = "";
		try {
			String signature = BaofooSecurityUtil.sha1X16(signVStr, "UTF-8");
			sign = BaofooSignatureUtil.encryptByRSA(signature, private_key, key_store_password);
		} catch (Exception e) {
			logger.error("宝付协议代扣签名发生异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("宝付协议代扣签名发生异常！");
		} // 签名
		postData.put("signature", sign);// 签名域

		logger.info("宝付协议代扣报文：{}", postData);

		threadPoolExecutor.submit(() -> httpRemoteInvoke(request_url,postData,publicKeyRsa,orderEntity));
		
		return new UpsResultModel(UpsResultEnum.SUCCESS, orderEntity.getId());

	}
	
	


	


	
	
	@PrintExecuteTime
	@Async
	public void httpRemoteInvoke(String request_url,Map<String, String> postData,String public_key,UpsOrderEntity orderEntity) {
		String resutlStr = "";
		try {
			resutlStr = OkHttpUtil.postForm(request_url, postData);
		} catch (IOException e) {
			logger.error("调用宝付协议代扣接口异常:{}", ExceptionUtils.getStackTrace(e));
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("调用宝付协议代扣接口异常！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return ;
		}

		logger.info("宝付协议代扣返回信息：{}", resutlStr);
		// 创建与第三方交互日志
		UpsThirdpartyLogEntity utl = new UpsThirdpartyLogEntity();
		utl.setMerchantCode(orderEntity.getMerchantCode());
		utl.setOrderType(orderEntity.getOrderType());
		utl.setPayChannel(orderEntity.getPayChannel());
		utl.setUpsOrderCode(orderEntity.getUpsOrderCode());
		utl.setRequestUrl(request_url);
		utl.setRequestText(JSONObject.toJSONString(postData));
		utl.setReturnText(resutlStr);

		upsThirdpartyLogService.createThirdpartyLog(utl);
		// 处理返回结果
	     disposeResult(resutlStr, public_key, orderEntity);
	}

	/**
	 * 
	 * 
	 * @param resutlStr
	 * @param public_key
	 * @param orderEntity
	 * @param utl
	 * @return
	 */
	private void disposeResult(String resutlStr, String public_key, UpsOrderEntity orderEntity) {
		Map<String, String> returnData = null;
		// 返回报文string转map
		try {
			returnData = BaofooFormatUtil.getParm(resutlStr);
		} catch (Exception e) {
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("宝付协议代扣返回报文解析异常！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			logger.error("宝付协议代扣返回报文解析异常，报文{}", resutlStr);
			return ;
		}
		// 验签
		if (!returnData.containsKey("signature")) {
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("宝付协议代扣返回报文缺少signature参数！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			logger.error("宝付协议代扣返回报文缺少signature参数，报文{}", returnData);
			return ;
		}
		String signature = returnData.get("signature");
		// 需要删除签名字段
		returnData.remove("signature");
		boolean flag = false;
		try {
			String returnDataStr = BaofooFormatUtil.coverMap2String(returnData);
			String rSignature = BaofooSecurityUtil.sha1X16(returnDataStr, "UTF-8");
			flag = BaofooSignatureUtil.verifySignature(public_key, rSignature, signature);
		} catch (Exception e) {
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("宝付协议代扣验签发生异常！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			logger.error("宝付协议代扣验签发生异常！");
			return ;
		}

		// 验签失败：
		if (!flag) {
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("宝付协议代扣返回报文验签失败！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			logger.error("宝付协议代扣返回报文验签失败！");
			return ;
		}
		// 验签成功：
		logger.info("宝付协议代扣返回报文验签成功！");

		String resp_code = MapUtils.getString(returnData, "resp_code", "");
		String biz_resp_code = MapUtils.getString(returnData, "biz_resp_code", "");
		String biz_resp_msg = MapUtils.getString(returnData, "biz_resp_msg", "");
		orderEntity.setResultCode(biz_resp_code + " " + resp_code);
		orderEntity.setResultMessage(biz_resp_msg);

		if (resp_code.equals(ACCEPT_CODE)||resp_code.equals(SUCCESS_CODE)) {
			logger.info("协议代扣受理成功！");
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return ;
		}
		if (resp_code.equals(REJECT_CODE)) {
			logger.info("协议代扣受理失败！");
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return ;
		}
		logger.error("宝付协议代扣返回未知resp_code编码:{}", returnData);
		// 修改订单状态
		orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
		orderEntity.setRemark("宝付协议代扣返回未知resp_code编码！");
		upsOrderService.updateOrderAndCreateOrderPush(orderEntity);

	}
}
