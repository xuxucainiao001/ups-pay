package com.pgy.ups.pay.baofoo.pay.impl;

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
import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.pay.ProtocolBindCardService;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


@Service(group="baofooProtocolBindCard",timeout=60000,retries=0)
public class BaofooProtocolBindCardServiceImpl extends SignatureBindBaseCore implements ProtocolBindCardService {

	private Logger logger = LoggerFactory.getLogger(BaofooProtocolBindCardServiceImpl.class);

	@Resource
	private UpsAuthSignService upsAuthSignBaofooService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;



	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	@Override
	public UpsResultModel protocolBindCard(UpsBindCardParamModel upsBindCardParamModel) throws BussinessException {
		logger.info("宝付协议绑卡参数{}",upsBindCardParamModel);
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsBindCardParamModel, SignTypeEnum.PROTOCOL);
		if (upsAuthSignEntity == null) {
			throw new BussinessException("2002", "请先完成协议签约");
		}
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsBindCardParamModel.getPayChannel(),
				upsBindCardParamModel.getOrderType(), upsBindCardParamModel.getMerchantCode()).getConfigDate();
		Map<String, String> configMap = JSONObject.parseObject(configStr, new TypeReference<Map<String, String>>() {
		});
		String responseString, memberId = configMap.get("member_id"), upsOrdeNo = getUpsOrderNo(upsBindCardParamModel),
				upsUserId = getUpsUserId(upsBindCardParamModel);
		String url = "https://public.baofoo.com/cutpayment/protocol/backTransRequest";
		String dgtl_envlp = "01|" + configMap.get("aeskey");// 使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
		logger.info("【协议签约】密码dgtl_envlp：" + dgtl_envlp);
		String uniqueCode = "";
		try {
			dgtl_envlp = BaofooRsaCodingUtil.encryptByPubCerFile(BaofooSecurityUtil.Base64Encode(dgtl_envlp),
					RSA_KEY_PATH + configMap.get("cerpath"));// 公钥加密
			uniqueCode = upsAuthSignEntity.getTradeNo() + "|" + upsBindCardParamModel.getSmsCode();// 预签约唯一码(预绑卡返回的值)[格式：预签约唯一码|短信验证码]
			logger.info("【协议签约】预签约唯一码：" + uniqueCode);
			uniqueCode = BaofooSecurityUtil.AesEncryptProtocol(BaofooSecurityUtil.Base64Encode(uniqueCode),
					configMap.get("aeskey"));// 先BASE64后进行AES加密
			logger.info("【协议签约】AES结果:" + uniqueCode);
		} catch (UnsupportedEncodingException e) {
			logger.error("【协议签约】异常", e);
			throw new BussinessException("协议签约AES错误");
		}
		Map<String, String> dateArry = new TreeMap<String, String>();
		dateArry.put("send_time", DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		dateArry.put("msg_id", upsOrdeNo);// 报文流水号
		dateArry.put("version", "4.0.0.0");
		dateArry.put("txn_type", "02");// 交易类型
		dateArry.put("member_id", configMap.get("member_id"));
		dateArry.put("terminal_id", configMap.get("terminal_id"));
		dateArry.put("dgtl_envlp", dgtl_envlp);
		dateArry.put("unique_code", uniqueCode);// 预签约唯一码

		String SignVStr = BaofooFormatUtil.coverMap2String(dateArry);
		logger.info("【协议签约】SHA-1摘要字串：" + SignVStr);
		String signature = null, sign = null;

		try {
			signature = BaofooSecurityUtil.sha1X16(SignVStr, "UTF-8");
			logger.info("【协议签约】SHA-1摘要结果：" + signature);
			sign = BaofooSignatureUtil.encryptByRSA(signature, RSA_KEY_PATH + configMap.get("pfxPath"),
					configMap.get("pfxpwd"));
		} catch (Exception e) {
			throw new BussinessException("协议签约sha错误");
		}
		logger.info("【协议签约】RSA签名结果：" + sign);
		dateArry.put("signature", sign);// 签名域
		try {
			logger.info("【协议签约】请求请求:" + dateArry);
			UpsUserSignLogEntity upsUserSignLogEntity = saveTppRequestParams(upsBindCardParamModel, upsUserId, memberId,
					upsOrdeNo, dateArry.toString());
			responseString = OkHttpUtil.postForm(url, dateArry);
			saveTppResponseParams(upsUserSignLogEntity, responseString);
			logger.info("协议签约返回{}", responseString);
		} catch (IOException e) {
			logger.error("协议签约异常", e);
			throw new BussinessException("协议签约请求第三方错误");
		}
		HashMap<String, String> result = new HashMap<>();
		try {
			Map<String, String> map = BaofooFormatUtil.getParm(responseString);
			String resp_code = map.get("resp_code");
			String biz_resp_code = map.get("biz_resp_code");
			String biz_resp_msg = map.get("biz_resp_msg");
			if (!"S".equalsIgnoreCase(resp_code) && !"0000".equals(biz_resp_code)) {
				return  UpsResultModelUtil.upsResultModelSuccess("02",biz_resp_msg,biz_resp_code,biz_resp_msg);
			}
			String rdgtlEnvlp = BaofooSecurityUtil.Base64Decode(BaofooRsaCodingUtil.decryptByPriPfxFile(
					map.get("dgtl_envlp"), RSA_KEY_PATH + configMap.get("pfxPath"), configMap.get("pfxpwd")));
			logger.info("【协议签约】返回数字信封：" + rdgtlEnvlp);
			String rAesKey = BaofooFormatUtil.getAesKey(rdgtlEnvlp);// 获取返回的AESkey
			logger.info("【协议签约】返回的AESkey:" + rAesKey);
			String protocolNo = BaofooSecurityUtil.Base64Decode((BaofooSecurityUtil.AesDecryptProtocol(map.get("protocol_no"), rAesKey)));
			logger.info("【协议签约】唯一码:" + protocolNo);
			result.put("bankCode", map.get("bank_code"));
			result.put("bankName", map.get("bank_name"));
			upsAuthSignEntity.setStatus(10);
			upsAuthSignEntity.setSignDate(new Date());
			upsAuthSignEntity.setTppSignNo(protocolNo);
			upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
			upsAuthSignEntity.setBankCode(map.get("bank_code"));
			upsAuthSignEntity.setBusinessFlowNum(upsAuthSignEntity.getBusinessFlowNum());
			upsAuthSignEntity.setGmtYmd(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
			upsAuthSignBaofooService.updateRecord(upsAuthSignEntity);
		} catch (Exception e) {
			logger.error("【协议签约预】出现错误", e);
			throw new BussinessException(e.getMessage());
		}
		logger.info("宝付协议绑卡完成{}",upsBindCardParamModel.getRealName());
		return UpsResultModelUtil.upsResultModelSuccess(result);
	}

}
