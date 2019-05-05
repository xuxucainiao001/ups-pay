package com.pgy.ups.pay.baofoo.pay.impl;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.ProtocolSignatureService;
import com.pgy.ups.pay.interfaces.pay.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooFormatUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSignatureUtil;
import com.pgy.ups.pay.commom.utils.SignatureBindBaseCore;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsUserSignLogEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;

@Service(group="baofooProtocolSignature",timeout=60000,retries=0)
public class BaofooProtocolSignatureServiceImpl extends SignatureBindBaseCore
		implements ProtocolSignatureService {

	@Resource
	private UpsAuthSignService upsAuthSignBaofooService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	private Logger logger = LoggerFactory.getLogger(BaofooProtocolSignatureServiceImpl.class);

	@Override
	public UpsResultModel protocolSignature (UpsSignatureParamModel upsSignatureParamModel) throws BussinessException{
		logger.info("宝付协议签约参数{}",upsSignatureParamModel);
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsSignatureParamModel, SignTypeEnum.PROTOCOL);
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsSignatureParamModel.getPayChannel(),
				upsSignatureParamModel.getOrderType(), upsSignatureParamModel.getMerchantCode()).getConfigDate();
		Map<String, String> configMap = JSONObject.parseObject(configStr, new TypeReference<Map<String, String>>() {
		});
		String responseString, memberId = configMap.get("member_id"), upsOrdeNo = getUpsOrderNo(upsSignatureParamModel),
				upsUserId = getUpsUserId(upsSignatureParamModel);
		String url = "https://public.baofoo.com/cutpayment/protocol/backTransRequest";
		String dgtl_envlp = "01|" + configMap.get("aeskey");// 使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
		String cardinfo = "";
		try {
			dgtl_envlp = BaofooRsaCodingUtil.encryptByPubCerFile(BaofooSecurityUtil.Base64Encode(dgtl_envlp),
					RSA_KEY_PATH + configMap.get("cerpath"));// 公钥加密
			// 账户信息[银行卡号|持卡人姓名|证件号|手机号|银行卡安全码|银行卡有效期]
			cardinfo = upsSignatureParamModel.getBankCard() + "|" + upsSignatureParamModel.getRealName() + "|"
					+ upsSignatureParamModel.getIdentity() + "|" + upsSignatureParamModel.getPhoneNo() + "||";
			logger.info("【协议签约预】卡信息：" + cardinfo + ",长度：" + cardinfo.length());
			cardinfo = BaofooSecurityUtil.AesEncryptProtocol(BaofooSecurityUtil.Base64Encode(cardinfo),
					configMap.get("aeskey"));// 先BASE64后进行AES加密
		} catch (Exception e) {
			logger.error("协议签约预】出现异常", e);
			throw new BussinessException("【协议签约预】卡信息异常");
		}
		Map<String, String> dateArry = new TreeMap<>();
		dateArry.put("send_time", DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		dateArry.put("msg_id", upsOrdeNo);// 报文流水号
		dateArry.put("version", "4.0.0.0");
		dateArry.put("txn_type", "01");// 交易类型
		dateArry.put("member_id", configMap.get("member_id"));
		dateArry.put("terminal_id", configMap.get("terminal_id"));
		dateArry.put("dgtl_envlp", dgtl_envlp);
		dateArry.put("user_id", upsUserId);// 用户在商户平台唯一ID
		dateArry.put("card_type", "101");// 卡类型 101 借记卡，102 信用卡
		dateArry.put("id_card_type", "01");// 证件类型
		dateArry.put("acc_info", cardinfo);

		String signVStr = BaofooFormatUtil.coverMap2String(dateArry);
		String signature = null;// 签名
		try {
			signature = BaofooSecurityUtil.sha1X16(signVStr, "UTF-8");
			String sign = BaofooSignatureUtil.encryptByRSA(signature, RSA_KEY_PATH + configMap.get("pfxPath"),
					configMap.get("pfxpwd"));
			dateArry.put("signature", sign);// 签名域
		} catch (Exception e) {
			logger.error("协议签约预】签名异常", e);
			throw new BussinessException("协议签约预】签名异常");
		}
		try {
			UpsUserSignLogEntity upsUserSignLogEntity = saveTppRequestParams(upsSignatureParamModel, upsUserId,
					memberId, upsOrdeNo, dateArry.toString());
			logger.info("预协议签约请求{}", dateArry);
			responseString = OkHttpUtil.postForm(url, dateArry);
			logger.info("预协议签约返回{}", responseString);
			saveTppResponseParams(upsUserSignLogEntity, responseString);
		} catch (IOException e) {
			logger.error("预协议签约异常", e);
			throw new BussinessException("预协议签约第三方请求异常");
		}

		try {
			Map<String, String> map = BaofooFormatUtil.getParm(responseString);
			String resp_code = map.get("resp_code");
			String biz_resp_code = map.get("biz_resp_code");
			String biz_resp_msg = map.get("biz_resp_msg");
			if (!"S".equalsIgnoreCase(resp_code) && !"0000".equals(biz_resp_code)){
				return  UpsResultModelUtil.upsResultModelSuccess("02",biz_resp_msg,biz_resp_code,biz_resp_msg);
			}
			String rdgtlEnvlp = BaofooSecurityUtil.Base64Decode(BaofooRsaCodingUtil.decryptByPriPfxFile(
					map.get("dgtl_envlp"), RSA_KEY_PATH + configMap.get("pfxPath"), configMap.get("pfxpwd")));
			logger.info("【协议签约预】返回数字信封{}", rdgtlEnvlp);
			String rAesKey = BaofooFormatUtil.getAesKey(rdgtlEnvlp);// 获取返回的AESkey
			logger.info("【协议签约预】返回的AESkey:{}", rAesKey);
			String uniqueCode = BaofooSecurityUtil
					.Base64Decode((BaofooSecurityUtil.AesDecryptProtocol(map.get("unique_code"), rAesKey)));
			logger.info("【协议签约预】唯一码:{}", uniqueCode);
			if (upsAuthSignEntity == null) {
				upsAuthSignEntity = creatBaofooSignatureBind(upsSignatureParamModel);
				upsAuthSignEntity.setStatus(11);
				upsAuthSignEntity.setCertType("01");
				upsAuthSignEntity.setSignType(SignTypeEnum.PROTOCOL.getCode());
				upsAuthSignEntity.setTppMerNo(memberId);
				upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
				upsAuthSignEntity.setTradeNo(uniqueCode);
				upsAuthSignEntity.setUpsUserId(upsUserId);
				upsAuthSignBaofooService.saveRecord(upsAuthSignEntity);
			} else {
				upsAuthSignEntity.setStatus(11);
				upsAuthSignEntity.setBusinessFlowNum(upsSignatureParamModel.getBusinessFlowNum());
				upsAuthSignEntity.setTradeNo(uniqueCode);
				upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
				upsAuthSignBaofooService.updateRecord(upsAuthSignEntity);
			}
		} catch (Exception e) {
			logger.error("【协议签约预】出现错误", e);
			throw new BussinessException(e.getMessage());
		}
		logger.info("宝付协议签约完成{}",upsSignatureParamModel.getRealName());
		return UpsResultModelUtil.upsResultModelSuccess();
	}


}
