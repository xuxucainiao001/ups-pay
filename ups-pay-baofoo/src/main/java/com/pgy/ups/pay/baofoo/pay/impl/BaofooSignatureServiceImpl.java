package com.pgy.ups.pay.baofoo.pay.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.commom.utils.SignatureBindBaseCore;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsUserSignLogEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.pay.SignatureService;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 签约
 * 
 * @author 墨凉
 *
 */
@Service(group="baofooSignature",timeout=60000,retries=0)
public class BaofooSignatureServiceImpl extends SignatureBindBaseCore
		implements SignatureService {

	private Logger logger = LoggerFactory.getLogger(BaofooSignatureServiceImpl.class);

	@Resource
	private UpsAuthSignService upsAuthSignService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	@Override
	public UpsResultModel signature(UpsSignatureParamModel upsSignatureParamModel)  throws  BussinessException{
		logger.info("宝付认证签约参数{}",upsSignatureParamModel);
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsSignatureParamModel, SignTypeEnum.AUTH);
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsSignatureParamModel.getPayChannel(),
				upsSignatureParamModel.getOrderType(), upsSignatureParamModel.getMerchantCode()).getConfigDate();
		Map<String, String> map = JSONObject.parseObject(configStr, new TypeReference<Map<String, String>>() {
		});
		String url = "https://public.baofoo.com/cutpayment/api/backTransRequest";
		String responseString, memberId = map.get("member_id"), upsOrdeNo = getUpsOrderNo(upsSignatureParamModel),
				upsUserId = getUpsUserId(upsSignatureParamModel);
		HashMap<String, Object> requestMap = requestMap("11", map);
		JSONObject dataContentJson = new JSONObject();
		dataContentJson.put("trans_id", upsOrdeNo);//
		dataContentJson.put("trans_serial_no", upsOrdeNo);
		dataContentJson.put("id_card_type", "01");// 证件类型固定01（身份证）
		dataContentJson.put("txn_sub_type", "11");
		dataContentJson.put("biz_type", "0000");
		dataContentJson.put("member_id", memberId);
		dataContentJson.put("terminal_id", map.get("terminal_id"));
		dataContentJson.put("acc_no", upsSignatureParamModel.getBankCard());// 银行卡
		dataContentJson.put("id_card", upsSignatureParamModel.getIdentity());
		dataContentJson.put("id_holder", upsSignatureParamModel.getRealName());// 姓
		dataContentJson.put("mobile", upsSignatureParamModel.getPhoneNo());
		dataContentJson.put("pay_code", upsSignatureParamModel.getBankCode());
		String formatDate = DateUtils.getCurrentDate("yyyyMMddHHmmss");
		dataContentJson.put("trade_date", formatDate);
		// 保存日志信息
		UpsUserSignLogEntity upsUserSignLogEntity = saveTppRequestParams(upsSignatureParamModel, upsUserId, memberId,
				upsOrdeNo, dataContentJson.toString());
		try {
			String base64str = BaofooSecurityUtil.Base64Encode(dataContentJson.toString());
			String dataContent = BaofooRsaCodingUtil.encryptByPriPfxFile(base64str, RSA_KEY_PATH + map.get("pfxPath"),
					map.get("pfxpwd"));
			requestMap.put("data_content", dataContent);// 加入请求密文
			logger.info("宝付预约绑卡请求{}", requestMap);
			responseString = OkHttpUtil.postForm(url, requestMap);
			logger.info("宝付预约绑卡返回{}", responseString);
			String cerFileString   = BaofooRsaCodingUtil.decryptByPubCerFile(responseString, RSA_KEY_PATH + map.get("cerpath"));
			if (cerFileString == null) {
				saveTppResponseParams(upsUserSignLogEntity, responseString);
				throw new BussinessException("读取宝付签约配置信息错误");
			}
			responseString = BaofooSecurityUtil.Base64Decode(cerFileString);
			logger.info("宝付预约绑卡解码返回{}", responseString);
			saveTppResponseParams(upsUserSignLogEntity, responseString);
		} catch (IOException e) {
			logger.error("宝付预约绑卡失败", e);
			throw new BussinessException("宝付预约绑卡失败");
		}
		logger.info("宝付预约绑卡解析数据{}", responseString);
		JSONObject resultJson = JSONObject.parseObject(responseString);
		String respCode = resultJson.getString("resp_code");
		String respMsg = resultJson.getString("resp_msg");
		if (!"0000".equals(respCode)) {
			return  UpsResultModelUtil.upsResultModelSuccess("02",respMsg,respCode,respMsg);
		}
		if (upsAuthSignEntity == null) {
			upsAuthSignEntity = creatBaofooSignatureBind(upsSignatureParamModel);
			upsAuthSignEntity.setStatus(11);
			upsAuthSignEntity.setCertType("01");
			upsAuthSignEntity.setSignType(SignTypeEnum.AUTH.getCode());
			upsAuthSignEntity.setTppMerNo(memberId);
			upsAuthSignEntity.setTradeNo(upsOrdeNo);
			upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
			upsAuthSignEntity.setUpsUserId(upsUserId);
			upsAuthSignService.saveRecord(upsAuthSignEntity);
		} else {
			upsAuthSignEntity.setStatus(11);
			upsAuthSignEntity.setBusinessFlowNum(upsSignatureParamModel.getBusinessFlowNum());
			upsAuthSignEntity.setTradeNo(upsOrdeNo);
			upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
			upsAuthSignService.saveRecord(upsAuthSignEntity);
		}
		logger.info("宝付认证签约完成{}",upsSignatureParamModel.getRealName());
		return UpsResultModelUtil.upsResultModelSuccess();
	}

}
