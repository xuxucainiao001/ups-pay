package com.pgy.ups.pay.baofoo.query.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
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

/**
 * 宝付代付轮询
 * 
 * @author 墨凉
 *
 */
@Service(group="baofooPay",timeout=60000,retries=3)
public class BaofooPayOrderQueryServiceImpl implements OrderQueryService<String> {

	private Logger logger = LoggerFactory.getLogger(BaofooPayOrderQueryServiceImpl.class);

	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	private static final String PAY_CHANNEL = "baofoo";

	@Resource
	private OrderPushService orderPushService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private UpsOrderService upsOrderService;

	@Async // 异步轮询
	public void doMultiQuery() {

		List<OrderPushEntity> querylist = orderPushService.queryNeedQueryOrderList(PAY_CHANNEL, OrderType.PAY);
		Integer total = querylist.size();
		logger.info("宝付代付交易查询任务开始！本次共查询{}笔", total);
		for (OrderPushEntity ope : querylist) {
			// 查询配置
			doSingleQuery(ope, false);
		}
		logger.info("宝付代付交易查询任务结束！");
		// 查询所有
	}

	@Override
	public String doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException{
		UpsThirdpartyConfigEntity config = upsThirdpartyConfigService.queryThirdpartyConfig(PAY_CHANNEL, OrderType.PAY,
				ope.getMerchantCode());
		// 抽取配置信息
		Map<String, Object> configMap = JSONObject.parseObject(config.getConfigDate(),new TypeReference<Map<String, Object>>() {});
		String memberId = MapUtils.getString(configMap, "member_id", "");
		String terminalId = MapUtils.getString(configMap, "terminal_id", "");
		String dataType = MapUtils.getString(configMap, "data_type", "");
		String version = MapUtils.getString(configMap, "version", "");
		String privateKey = MapUtils.getString(configMap, "private_key", "");
		String publicKey = MapUtils.getString(configMap, "public_key", "");
		String keyStorePassword = MapUtils.getString(configMap, "key_store_password", "");
		String queryUrl = MapUtils.getString(configMap, "query_url", "");

		if (StringUtils.isAnyBlank(memberId, terminalId, dataType, version, privateKey, publicKey, queryUrl)) {
			logger.error("宝付代付交易查询读取配置信息异常:{}", configMap);
			throw new BussinessException("宝付代付交易查询读取配置信息异常");
		}
		privateKey = RSA_KEY_PATH + privateKey;
		publicKey = RSA_KEY_PATH + publicKey;

		// 拼装报文
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("trans_no", ope.getOrderCode());

		Map<String, Object> trans_reqData = new LinkedHashMap<>();
		trans_reqData.put("trans_reqData", data);

		List<Map<String, Object>> trans_reqDatas_list = new ArrayList<>();
		trans_reqDatas_list.add(trans_reqData);

		Map<String, Object> trans_reqDatas = new LinkedHashMap<>();
		trans_reqDatas.put("trans_reqDatas", trans_reqDatas_list);

		Map<String, Object> trans_content = new LinkedHashMap<>();
		trans_content.put("trans_content", trans_reqDatas);

		String trans_content_json = JSONObject.toJSONString(trans_content);
		// logger.info("代付查询报文：{}",trans_content_json);
		// 加密
		String cipher_text = "";
		try {
			cipher_text = new String(BaofooSecurityUtil.Base64Encode(trans_content_json));
			cipher_text = BaofooRsaCodingUtil.encryptByPriPfxFile(cipher_text, privateKey, keyStorePassword);
		} catch (UnsupportedEncodingException e) {
			logger.error("宝付代付交易查询加密异常！");
			throw new BussinessException("宝付代付交易查询加密异常！");
		}

		// 发送报文
		// 拼装发送报文
		Map<String, String> postData = new HashMap<>();
		// 商户号
		postData.put("member_id", memberId);
		// 终端号
		postData.put("terminal_id", terminalId);
		// 报文类型
		postData.put("data_type", dataType);
		// 加密内容
		postData.put("data_content", cipher_text);
		// 版本号
		postData.put("version", version);

		// HttpClient调用
		String resutlStr = "";
		try {
			resutlStr = OkHttpUtil.postForm(queryUrl, postData);
		} catch (Exception e) {
			logger.error("调用宝付代付交易查询接口异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("调用宝付代付交易查询接口异常！");
		}

		return disposeResult(resutlStr, publicKey, ope, queryOnly);
	}

	/**
	 * 处理返回结果
	 * 
	 * @param resutlStr
	 * @param ope
	 */
	private String disposeResult(String resutlStr, String publicKey, OrderPushEntity ope, boolean queryOnly) {
		// 直接返回明文时
		if (resutlStr.contains("trans_content")) {
			Map<String, String> result = parseReturnMessage(resutlStr);
			String return_code = MapUtils.getString(result, "return_code");
			String return_msg = MapUtils.getString(result, "return_msg");
			logger.error("宝付代付查询异常！{}", resutlStr);
			ope.setChannelResultCode(return_code);
			ope.setChannelResultMsg(return_msg);
			orderPushService.updateOrderPush(ope);
			// 无需更新订单
			return resutlStr;
		}
		// 解密
		String decipher_text = "";
		try {
			decipher_text = BaofooRsaCodingUtil.decryptByPubCerFile(resutlStr, publicKey);
			decipher_text = BaofooSecurityUtil.Base64Decode(decipher_text);
		} catch (Exception e) {
			logger.error("宝付代付查询结果解密异常！{}", e);
			throw new BussinessException("宝付代付查询结果解密异常！");
		}
		if (queryOnly) {
			return decipher_text;
		}
		logger.info("宝付查询交易返回报文：{}", decipher_text);
		// 解析明文
		Map<String, String> result = parseReturnMessage(decipher_text);
		// 0：转账中； 1：转账成功； -1：转账失败； 2：转账退款
		String state = MapUtils.getString(result, "state", "");
		String trans_remark = MapUtils.getString(result, "trans_remark", "");
		String return_code = MapUtils.getString(result, "return_code", "");
		String return_msg = MapUtils.getString(result, "return_msg", "");
		// 查询订单
		UpsOrderEntity upsOrderEntity = upsOrderService.queryByOrderId(ope.getOrderId());
		// 更新订单和订单推送对象状态
		ope.setChannelResultCode(return_code + " " + state);
		ope.setChannelResultMsg(return_msg + " " + trans_remark);
		ope.setUpdateTime(new Date());
		upsOrderEntity.setResultCode(return_code + " " + state);
		upsOrderEntity.setResultMessage(return_msg + " " + trans_remark);
		upsOrderEntity.setUpdateTime(new Date());
		// 交易成功！
		if (StringUtils.equals(state, "1")) {
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		} else if (StringUtils.equals(state, "0")||StringUtils.equals(state, "")) {
			// 处理中
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
		} else {
			// 交易失败
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		}
		// 开启事务更新
		upsOrderService.updateOrderAndupdateOrderPush(upsOrderEntity, ope);
		return decipher_text;
	}

	/**
	 * 解析宝付返回报文
	 * 
	 * @param returnMessage
	 * @return
	 */
	private static Map<String, String> parseReturnMessage(String deciphertext) {
		// 获取 trans_content
		Map<String, String> trans_content = JSONObject.parseObject(deciphertext, new TypeReference<Map<String, String>>() {});

		Map<String, String> trans_reqDatas = JSONObject.parseObject(MapUtils.getString(trans_content, "trans_content"),
				new TypeReference<Map<String, String>>() {
				});

		List<Map<String, String>> trans_reqDatas_list = JSONObject.parseObject(
				MapUtils.getString(trans_reqDatas, "trans_reqDatas"), new TypeReference<List<Map<String, String>>>() {
				});
		Map<String, String> results = new HashMap<>();
		if (CollectionUtils.isNotEmpty(trans_reqDatas_list)) {
			Map<String, String> trans_reqData = trans_reqDatas_list.get(0);
			Map<String, String> result = JSONObject.parseObject(MapUtils.getString(trans_reqData, "trans_reqData"),
					new TypeReference<Map<String, String>>() {
					});
			results.putAll(result);
		}

		Map<String, String> trans_head = JSONObject.parseObject(MapUtils.getString(trans_content, "trans_content"),
				new TypeReference<Map<String, String>>() {
				});
		Map<String, String> trans_head_map = JSONObject.parseObject(MapUtils.getString(trans_head, "trans_head"),
				new TypeReference<Map<String, String>>() {
				});
		results.putAll(trans_head_map);
		return results;

	}


}
