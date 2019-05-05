package com.pgy.ups.pay.baofoo.query.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.pgy.ups.pay.baofoo.utils.BaofooFormatUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSignatureUtil;
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
 * 宝付协议代扣轮询
 * 
 * @author 墨凉
 *
 */

@Service(group="baofooProtocolCollect",timeout=60000,retries=3)
public class BaofooProtocolCollectOrderQueryServiceImpl implements OrderQueryService<String> {

	private Logger logger = LoggerFactory.getLogger(BaofooProtocolCollectOrderQueryServiceImpl.class);

	/**
	 * 公钥私钥相对classpath的路径
	 */
	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	private static final String PAY_CHANNEL = "baofoo";

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
	public static final String REJECT_CODE_1 = "F";
	
	/**
	 * 协议代扣交易处理中编码
	 */
	public static final String REJECT_CODE_2 = "FF";

	@Resource
	private UpsOrderService upsOrderService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private OrderPushService orderPushService;

	@Async
	@Override
	public void doMultiQuery() {

		List<OrderPushEntity> querylist = orderPushService.queryNeedQueryOrderList(PAY_CHANNEL,
				OrderType.PROTOCOL_COLLECT);
		Integer total = querylist.size();
		logger.info("宝付协议代扣交易查询任务开始！本次共查询{}笔", total);
		for (OrderPushEntity ope : querylist) {
			doSingleQuery(ope, false);
		}
		logger.info("宝付协议代扣交易查询任务结束！");
	}

	@Override
	public String doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException{
		// 查询配置
		UpsThirdpartyConfigEntity config = upsThirdpartyConfigService.queryThirdpartyConfig(PAY_CHANNEL,
				OrderType.PROTOCOL_COLLECT, ope.getMerchantCode());

		Map<String, Object> configMap = JSONObject.parseObject(config.getConfigDate(),
				new TypeReference<Map<String, Object>>() {
				});
		String member_id = MapUtils.getString(configMap, "member_id", "");
		String terminal_id = MapUtils.getString(configMap, "terminal_id", "");
		String aes_key = MapUtils.getString(configMap, "aes_key", "");
		String private_key = MapUtils.getString(configMap, "private_key", "");
		String public_key = MapUtils.getString(configMap, "public_key", "");
		String version = MapUtils.getString(configMap, "version", "");
		String key_store_password = MapUtils.getString(configMap, "key_store_password", "");
		String query_url = MapUtils.getString(configMap, "query_url", "");

		if (StringUtils.isAnyBlank(member_id, terminal_id, aes_key, private_key, public_key, version,
				key_store_password, query_url)) {
			logger.error("宝付协议代扣交易查询读取配置信息错误:{}", configMap);
			throw new BussinessException("宝付协议代扣交易查询读取配置信息错误");
		}
		private_key = RSA_KEY_PATH + private_key;
		public_key = RSA_KEY_PATH + public_key;

		Map<String, String> postData = new TreeMap<>();
		postData.put("send_time", DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		postData.put("msg_id", ope.getOrderId() + "");// 报文流水号
		postData.put("version", version);
		postData.put("terminal_id", terminal_id);
		postData.put("txn_type", "07");// 交易类型
		postData.put("member_id", member_id);
		postData.put("orig_trans_id", ope.getOrderCode());// 交易时的trans_id
		postData.put("orig_trade_date", DateUtils.dateToString(ope.getOrderCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		// 签名
		String signVStr = BaofooFormatUtil.coverMap2String(postData);
		String sign = "";
		try {
			String signature = BaofooSecurityUtil.sha1X16(signVStr, "UTF-8");
			sign = BaofooSignatureUtil.encryptByRSA(signature, private_key, key_store_password);
		} catch (Exception e) {
			logger.error("宝付协议代扣交易查询签名发生异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("宝付协议代扣交易查询签名发生异常！");
		}
		postData.put("signature", sign);// 签名域
		logger.info("宝付协议代扣交易查询报文：{}", JSONObject.toJSONString(postData));
		String resutlStr = "";
		try {
			resutlStr = OkHttpUtil.postForm(query_url, postData);
		} catch (IOException e) {
			logger.error("调用宝付协议代扣交易查询接口异常:{}", ExceptionUtils.getStackTrace(e));
			throw new BussinessException("调用宝付协议代扣交易查询接口异常！");
		}
		if (!queryOnly) {
			disposeResult(resutlStr, public_key, ope);
		}
		return resutlStr;

	}

	private void disposeResult(String resutlStr, String public_key, OrderPushEntity ope) {
		Map<String, String> returnData = null;
		// 返回报文string转map
		try {
			returnData = BaofooFormatUtil.getParm(resutlStr);
		} catch (Exception e) {
			logger.error("宝付协议代扣交易查询返回报文解析异常，报文{}", resutlStr);
			return;
		}
		logger.info("宝付协议代扣交易查询返回报文：{}", resutlStr);
		// 验签
		if (!returnData.containsKey("signature")) {
			logger.error("宝付协议代扣交易查询返回报文缺少signature参数，报文{}", returnData);
			return;
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
			logger.error("宝付协议代扣交易查询结果验签发生异常！");
			throw new BussinessException("宝付协议代扣交易查询结果验签发生异常！");
		}

		// 验签失败：
		if (!flag) {
			logger.error("宝付协议代扣交易查询结果验签失败！");
			return;
		}
		// 验签成功：
		String resp_code = MapUtils.getString(returnData, "resp_code", "");
		String biz_resp_code = MapUtils.getString(returnData, "biz_resp_code", "");
		String biz_resp_msg = MapUtils.getString(returnData, "biz_resp_msg", "");
		// 查询订单
		UpsOrderEntity upsOrderEntity = upsOrderService.queryByOrderId(ope.getOrderId());
		// 更新订单和订单推送对象状态
		ope.setChannelResultCode(biz_resp_code + " " + resp_code);
		ope.setChannelResultMsg(biz_resp_msg);
		ope.setUpdateTime(new Date());
		upsOrderEntity.setResultCode(biz_resp_code + " " + resp_code);
		upsOrderEntity.setResultMessage(biz_resp_msg);
		upsOrderEntity.setUpdateTime(new Date());
		// 根据 resp_code判断结果
		if (resp_code.equals(SUCCESS_CODE)) {
			// 协议代扣交易成功！
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_SUCCESS);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		} else if (resp_code.equals(ACCEPT_CODE)||resp_code.equals("")) {
			// 协议代扣受理成功！resp_code为空情况下默认处理中
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_DISPOSE);
		} else if (resp_code.equals(REJECT_CODE_1)||resp_code.equals(REJECT_CODE_2)) {
			// 交易失败
			upsOrderEntity.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setOrderStatus(OrderStatus.ORDER_STATUS_FAIL);
			ope.setPushCount(0);
			ope.setPushStatus(OrderPushStatus.PUSH_READY);
		}
		// 开启事务更新
		upsOrderService.updateOrderAndupdateOrderPush(upsOrderEntity, ope);

	}
}
