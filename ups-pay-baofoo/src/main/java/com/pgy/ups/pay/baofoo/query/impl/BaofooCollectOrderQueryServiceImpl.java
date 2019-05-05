package com.pgy.ups.pay.baofoo.query.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.pgy.ups.common.utils.DateUtils;
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
 * 宝付代扣轮询
 * 
 * @author 墨凉
 *
 */

@Service(group = "baofooCollect", timeout = 60000, retries = 3)
public class BaofooCollectOrderQueryServiceImpl implements OrderQueryService<String> {

	private Logger logger = LoggerFactory.getLogger(BaofooCollectOrderQueryServiceImpl.class);

	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	private static final String PAY_CHANNEL = "baofoo";

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private OrderPushService orderPushService;

	@Async
	@Override // 异步
	public void doMultiQuery() {

		List<OrderPushEntity> querylist = orderPushService.queryNeedQueryOrderList(PAY_CHANNEL, OrderType.COLLECT);
		Integer total = querylist.size();
		logger.info("宝付代扣交易查询任务开始！本次共查询{}笔", total);

		for (OrderPushEntity ope : querylist) {
			doSingleQuery(ope, false);
		}
		logger.info("宝付代扣交易查询任务结束！");

	}

	@Override
	public String doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException {
		// 查询配置
		UpsThirdpartyConfigEntity config = upsThirdpartyConfigService.queryThirdpartyConfig(PAY_CHANNEL,
				OrderType.COLLECT, ope.getMerchantCode());
		Map<String, Object> configMap = JSONObject.parseObject(config.getConfigDate(),
				new TypeReference<Map<String, Object>>() {
				});
		String member_id = MapUtils.getString(configMap, "member_id", "");
		String terminal_id = MapUtils.getString(configMap, "terminal_id", "");
		String biz_type = MapUtils.getString(configMap, "biz_type", "");
		String txn_type = MapUtils.getString(configMap, "txn_type", "");
		String data_type = MapUtils.getString(configMap, "data_type", "");
		String private_key = MapUtils.getString(configMap, "private_key", "");
		String public_key = MapUtils.getString(configMap, "public_key", "");
		String version = MapUtils.getString(configMap, "version", "");
		String key_store_password = MapUtils.getString(configMap, "key_store_password", "");
		String query_url = MapUtils.getString(configMap, "query_url", "");
		if (StringUtils.isAnyBlank(member_id, terminal_id, biz_type, txn_type, data_type, private_key, public_key,
				version, key_store_password, query_url)) {
			logger.error("宝付代扣交易查询读取配置信息错误:{}", configMap);
			throw new BussinessException("宝付代扣交易查询读取配置信息错误");
		}
		private_key = RSA_KEY_PATH + private_key;
		public_key = RSA_KEY_PATH + public_key;
		// 报文加密
		Map<String, String> data_content = new LinkedHashMap<>();
		data_content.put("txn_sub_type", "31"); // 代付为13 查询为31
		data_content.put("biz_type", biz_type);
		data_content.put("terminal_id", terminal_id);
		data_content.put("member_id", member_id);
		data_content.put("trans_serial_no", ope.getOrderId() + "");
		data_content.put("orig_trans_id", ope.getOrderCode());
		data_content.put("orig_trade_date", DateUtils.dateToString(ope.getOrderCreateTime(), "yyyyMMddHHmmss"));
		// 个人信息转json
		String contentJson = JSONObject.toJSONString(data_content);
		String contentJsonCiphertext = null;
		try {
			contentJsonCiphertext = BaofooSecurityUtil.Base64Encode(contentJson);
			// 再私钥加密
			contentJsonCiphertext = BaofooRsaCodingUtil.encryptByPriPfxFile(new String(contentJsonCiphertext),
					private_key, key_store_password);
		} catch (UnsupportedEncodingException e) {
			logger.error("宝付代扣参数base64加密异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("宝付代扣参数base64加密异常！");
		}

		// 拼装发送报文
		Map<String, String> postData = new LinkedHashMap<>();
		// 版本号
		postData.put("version", version);
		// 终端号
		postData.put("terminal_id", terminal_id);
		// 交易类型
		postData.put("txn_type", txn_type);
		// 交易子类
		postData.put("txn_sub_type", "31"); // 代付为13 查询为31
		// 商户号
		postData.put("member_id", member_id);
		// 数据类型
		postData.put("data_type", data_type);
		// 加密数据
		postData.put("data_content", contentJsonCiphertext);
		// HttpClient调用
		String resutlStr = "";
		try {
			resutlStr = OkHttpUtil.postForm(query_url, postData);
		} catch (IOException e) {
			logger.error("调用宝付代扣交易查询接口异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("调用宝付代扣交易查询接口异常！");
		}
		return disposeResult(resutlStr, public_key, ope, queryOnly);
	}

	private String disposeResult(String resutlStr, String public_key, OrderPushEntity ope, boolean queryOnly) {
		// 第一步报文AES解密
		String deciphertext = BaofooRsaCodingUtil.decryptByPubCerFile(resutlStr, public_key);
		if (StringUtils.isEmpty(deciphertext)) {
			logger.error("AES解密宝付代扣查询返回报文发生常");
			throw new BussinessException("AES解密宝付代扣查询返回报文发生异常！");
		}
		// 第二步base64解密
		try {
			deciphertext = BaofooSecurityUtil.Base64Decode(deciphertext);
		} catch (IOException e) {
			logger.error("base64解码宝付代扣查询密文出错：{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("base64解码宝付代扣查询密文出错！");
		}
		// 保存第三方支付渠道交互日志
		logger.info("宝付代扣交易查询返回明文：{}", deciphertext);

		Map<String, String> result = parseReturnMessage(deciphertext);
		String order_stat = MapUtils.getString(result, "order_stat", "");
		String resp_code = MapUtils.getString(result, "resp_code", "");
		String resp_msg = MapUtils.getString(result, "resp_msg", "");
		if (queryOnly) {
			return deciphertext;
		}
		// 查询订单
		UpsOrderEntity upsOrderEntity = upsOrderService.queryByOrderId(ope.getOrderId());
		// 更新订单和订单推送对象状态
		ope.setChannelResultCode(resp_code + " " + order_stat);
		ope.setUpdateTime(new Date());
		upsOrderEntity.setResultCode(resp_code + " " + order_stat);
		upsOrderEntity.setUpdateTime(new Date());
		// 返回交易成功
		if (StringUtils.equals("S", order_stat)) {
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
			ope.setChannelResultMsg("交易成功");
			upsOrderEntity.setResultMessage("交易成功");
		}

		else if (StringUtils.equals("F", order_stat) || StringUtils.equals("FF", order_stat)) {
			// F 和 FF 均视为失败
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
			ope.setChannelResultMsg("交易失败  " + resp_msg);
			upsOrderEntity.setResultMessage("交易失败  " + resp_msg);
			if (StringUtils.equals("FF", order_stat)) {
				ope.setChannelResultMsg("交易失败，订单不存在  " + resp_msg);
				upsOrderEntity.setResultMessage("交易失败，订单不存在  " + resp_msg);
			}
		} else {
			// 交易处理中 I 或者 空
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			ope.setChannelResultMsg("处理中 " + resp_msg);
			upsOrderEntity.setResultMessage("处理中 " + resp_msg);
		}
		// 开启事务更新
		upsOrderService.updateOrderAndupdateOrderPush(upsOrderEntity, ope);
		return deciphertext;
	}

	/**
	 * 解析返回的明文
	 * 
	 * @param returnMessage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> parseReturnMessage(String returnMessage) {
		return JSONObject.parseObject(returnMessage, Map.class);
	}
}
