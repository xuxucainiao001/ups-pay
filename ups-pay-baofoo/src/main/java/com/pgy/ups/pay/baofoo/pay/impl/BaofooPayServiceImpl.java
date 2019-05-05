package com.pgy.ups.pay.baofoo.pay.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.PayService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.annotation.PrintExecuteTime;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.model.BaoFooPayPrivateModel;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.commom.constants.OrderStatus;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyLogEntity;
import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.service.bank.UpsPayChannelBankService;
import com.pgy.ups.pay.interfaces.service.log.UpsThirdpartyLogService;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderService;

/**
 * 代付 借款
 * 
 * @author 墨凉
 *
 */
@Service(group = "baofooPay",timeout=60000,retries=0)
public class BaofooPayServiceImpl implements PayService {



	/**
	 * 公钥私钥相对classpath的路径
	 */
	@Value("${ups.baofoo.rsa.path}")
	private  String RSA_KEY_PATH;

	private Logger logger = LoggerFactory.getLogger(BaofooPayServiceImpl.class);

	@Resource
	private UpsPayChannelBankService upsPayChannelBankService;


	@Resource(name = "threadPoolExecutor")
	private ThreadPoolExecutor threadPoolExecutor;


	/* 宝付代付 支付 交易成功编码 */
	private static final String[] PAY_SUCCESS_CODES = { "200" };

	/* 宝付代付 支付 受理编码 */
	private static final String[] PAY_ACCEPT_CODES = { "0000", "0300", "0999", "0401" };

	/* 宝付代付 支付 拒绝编码 */
	private static final String[] PAY_REJECT_CODES = { "0001", "0002", "0003", "0004", "0201", "0202", "0203", "0204",
			"0205", "0206", "0207", "0208", "0301", "0501", "0601" };



	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyLogService upsThirdpartyLogService;



	@Override
	public  UpsResultModel pay(UpsOrderEntity orderEntity) throws BussinessException{
		// 获取公共配置信息
		Map<String, Object> baofooConfig = orderEntity.getUpsConfigDate();
		String memberId = MapUtils.getString(baofooConfig, "member_id", "");
		String terminalId = MapUtils.getString(baofooConfig, "terminal_id", "");
		String privateKey = MapUtils.getString(baofooConfig, "private_key", "");
		final String publicKey = MapUtils.getString(baofooConfig, "public_key", "");
		String keyStorePassword = MapUtils.getString(baofooConfig, "key_store_password", "");
		String requestUrl = MapUtils.getString(baofooConfig, "request_url", "");
		if (StringUtils.isAnyBlank(memberId, terminalId, privateKey, publicKey, requestUrl)) {
			logger.error("读取宝付代付配置信息错误:{}", baofooConfig);
			throw new BussinessException("读取宝付代付配置信息错误");
		}
		privateKey = RSA_KEY_PATH + privateKey;
		final  String publicKeyRas = RSA_KEY_PATH + publicKey;
		// 拼装个人信息
		BaoFooPayPrivateModel baoFooPayModel = new BaoFooPayPrivateModel();
		baoFooPayModel.setTrans_no(orderEntity.getUpsOrderCode()); // 给第三方支付渠道订单编码，不给订单Id
		baoFooPayModel.setTrans_money(orderEntity.getAmount().toString());
		// 四要素必须从UpsParamModel中获取，因为orderEntiy中的为加密信息
		baoFooPayModel.setTo_acc_name(orderEntity.getUpsParamModel().getRealName());
		baoFooPayModel.setTo_acc_no(orderEntity.getUpsParamModel().getBankCard());
		baoFooPayModel.setTrans_card_id(orderEntity.getUpsParamModel().getIdentity());
		baoFooPayModel.setTrans_mobile(orderEntity.getUpsParamModel().getPhoneNo());
		String channelBankName = upsPayChannelBankService.queryChannelBankName(orderEntity.getPayChannel(),
				orderEntity.getBankCode());
		if (Objects.isNull(channelBankName)) {
			logger.error("没有查询到宝付该银行信息！业务银行编码：{}", orderEntity.getBankCode());
			throw new BussinessException("没有查询到宝付该银行信息！");
		}
		baoFooPayModel.setTo_bank_name(channelBankName.trim());
		baoFooPayModel.setTrans_summary(orderEntity.getRemark());

		Map<String, Object> trans_reqData = new LinkedHashMap<>();
		trans_reqData.put("trans_reqData", baoFooPayModel);

		List<Map<String, Object>> trans_reqDatas_list = new ArrayList<>();
		trans_reqDatas_list.add(trans_reqData);

		Map<String, List<Map<String, Object>>> trans_reqDatas = new LinkedHashMap<>();
		trans_reqDatas.put("trans_reqDatas", trans_reqDatas_list);

		Map<String, Object> trans_content = new LinkedHashMap<>();
		trans_content.put("trans_content", trans_reqDatas);

		// 个人信息转换为json并加密
		String transContentJson = JSONObject.toJSONString(trans_content);
		logger.info("宝付代付加密前用户信息：{}", transContentJson);
		// 先base64加密
		String transContentJsonCiphertext = null;
		try {
			transContentJsonCiphertext = BaofooSecurityUtil.Base64Encode(transContentJson);
		} catch (UnsupportedEncodingException e) {
			logger.error("宝付代付参数base64加密异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("宝付代付参数base64加密异常！");
		}
		// 再私钥加密
		transContentJsonCiphertext = BaofooRsaCodingUtil.encryptByPriPfxFile(transContentJsonCiphertext, privateKey,
				keyStorePassword);
		logger.info("宝付代付加密后用户信息：{}", transContentJsonCiphertext);

		// 拼装发送报文
		Map<String, String> postData = new HashMap<>();
		// 商户号
		postData.put("member_id", memberId);
		// 终端号
		postData.put("terminal_id", terminalId);
		// 报文类型
		postData.put("data_type", "json");
		// 加密内容
		postData.put("data_content", transContentJsonCiphertext);
		// 版本号
		postData.put("version", "4.0.0");
        
		//异步调用HTTP接口
		/*((BaofooPayServiceImpl) AopContext.currentProxy()).httpRemoteInvoke(requestUrl, postData, publicKey, orderEntity,
				transContentJson);*/
		threadPoolExecutor.submit(() -> httpRemoteInvoke(requestUrl, postData, publicKeyRas, orderEntity,
				transContentJson));
		return new UpsResultModel(UpsResultEnum.SUCCESS, orderEntity.getId());

	}




	






	@PrintExecuteTime
	public void httpRemoteInvoke(String requestUrl, Map<String, String> postData, String publicKey,
			UpsOrderEntity orderEntity, String transContentJson) {
		// HttpClient调用
		String resutlStr = "";
		try {
			resutlStr = OkHttpUtil.postForm(requestUrl, postData);
		} catch (IOException e) {
			logger.error("调用宝付代付接口异常:{}", ExceptionUtils.getStackTrace(e));
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("调用宝付代付接口异常！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
		}
		logger.info("宝付代付返回报文：{}", resutlStr);

		// 创建与第三方交互日志
		UpsThirdpartyLogEntity utl = new UpsThirdpartyLogEntity();
		utl.setMerchantCode(orderEntity.getMerchantCode());
		utl.setOrderType(orderEntity.getOrderType());
		utl.setPayChannel(orderEntity.getPayChannel());
		utl.setUpsOrderCode(orderEntity.getUpsOrderCode());
		utl.setRequestUrl(requestUrl);
		postData.put("data_content", transContentJson);
		utl.setRequestText(JSONObject.toJSONString(postData));
		// 处理返回结果
		disposeResult(resutlStr, publicKey, orderEntity, utl);
	}

	/**
	 * 处理字符串结果
	 * 
	 * @param resutlStr
	 * @return
	 */

	private void disposeResult(String resutlStr, String publicKey, UpsOrderEntity orderEntity,
			UpsThirdpartyLogEntity utl) {
		// 如果返回明文 说明宝未已经收到，但受理失败
		if (resutlStr.contains("trans_content")) {
			Map<String, String> trans_head_map = parseReturnMessage(resutlStr);
			// 获取返回结果
			String return_code = MapUtils.getString(trans_head_map, "return_code");
			String return_msg = MapUtils.getString(trans_head_map, "return_msg");
			// 修改订单状态
			orderEntity.setResultCode(return_code);
			orderEntity.setResultMessage(return_msg);
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			upsOrderService.updateUpsOrder(orderEntity);
			// 保存第三方支付渠道交互日志
			utl.setReturnText(resutlStr);
			upsThirdpartyLogService.createThirdpartyLog(utl);
			// 返回结果
			return;
		}
		// 当返回密文时
		// 第一步：公钥解密
		String deciphertext = BaofooRsaCodingUtil.decryptByPubCerFile(resutlStr, publicKey);
		if (StringUtils.isEmpty(deciphertext)) {
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("RSA解密宝付代付返回报文发生异常！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			// 保存第三方支付渠道交互日志
			utl.setReturnText(resutlStr);
			upsThirdpartyLogService.createThirdpartyLog(utl);
			// 返回结果
			return;

		}
		// 第二步base64解密
		try {
			deciphertext = BaofooSecurityUtil.Base64Decode(deciphertext);
		} catch (IOException e) {
			logger.error("base64解码宝付代付加密报文出错：{}", ExceptionUtils.getStackTrace(e));
			// 修改订单状态
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
			orderEntity.setRemark("base64解码宝付代付加密报文出错！");
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			// 保存第三方支付渠道交互日志
			utl.setReturnText(StringUtils.EMPTY);
			upsThirdpartyLogService.createThirdpartyLog(utl);
			return;
		}
		// 保存第三方支付渠道交互日志
		logger.info("宝付代付返回明文：{}", deciphertext);
		utl.setReturnText(deciphertext);
		upsThirdpartyLogService.createThirdpartyLog(utl);
		// 解析报文转换为Map
		Map<String, String> trans_head_map = parseReturnMessage(deciphertext);
		String return_code = MapUtils.getString(trans_head_map, "return_code");
		String return_msg = MapUtils.getString(trans_head_map, "return_msg");
		orderEntity.setResultCode(return_code);
		orderEntity.setResultMessage(return_msg);
		// 返回受理成功
		if (ArrayUtils.contains(PAY_ACCEPT_CODES, return_code)||ArrayUtils.contains(PAY_SUCCESS_CODES, return_code)) {
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return;
		}
		// 返回拒绝受理
		if (ArrayUtils.contains(PAY_REJECT_CODES, return_code)) {
			orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
			return;
		}
		// 返回其他未知编码
		orderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_ERROR);
		orderEntity.setRemark("宝付代付返回未知编码！");
		upsOrderService.updateOrderAndCreateOrderPush(orderEntity);
	}

	/**
	 * 解析宝付返回报文
	 * 
	 * @param returnMessage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> parseReturnMessage(String deciphertext) {
		// 获取 trans_content
		Map<String, String> transContent = JSONObject.parseObject(deciphertext, Map.class);
		// 获取 trans_head

		Map<String, String> transHead = JSONObject.parseObject(MapUtils.getString(transContent, "trans_content"),
				Map.class);
		// 获取 trans_head中的key和value
		return JSONObject.parseObject(MapUtils.getString(transHead, "trans_head"), Map.class);

	}
	
}


