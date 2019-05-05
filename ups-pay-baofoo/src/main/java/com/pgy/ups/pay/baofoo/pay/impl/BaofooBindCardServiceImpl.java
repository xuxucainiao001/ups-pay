package com.pgy.ups.pay.baofoo.pay.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.BindCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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
import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.pay.BussinessHandler;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;

/**
 * 绑卡
 * 
 * @author 墨凉
 *
 */
@Service(group="baofooBindCard",timeout=60000,retries=0)
public class BaofooBindCardServiceImpl extends SignatureBindBaseCore  implements BindCardService {

	@Value("${ups.baofoo.rsa.path}")
	private String RSA_KEY_PATH;

	private Logger logger = LoggerFactory.getLogger(BaofooBindCardServiceImpl.class);

	@Resource
	private UpsAuthSignService upsAuthSignBaofooService;

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Override
	public UpsResultModel bindCard(UpsBindCardParamModel upsBindCardParamModel) throws BussinessException{
		logger.info("宝付认证绑卡参数{}",upsBindCardParamModel);
		String url = "https://public.baofoo.com/cutpayment/api/backTransRequest";
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsBindCardParamModel, SignTypeEnum.AUTH);
		if (upsAuthSignEntity == null) {
			throw new BussinessException("2001", "请先完成认证签约");
		}
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsBindCardParamModel.getPayChannel(),
				upsBindCardParamModel.getOrderType(), upsBindCardParamModel.getMerchantCode()).getConfigDate();
		Map<String, String> map = JSONObject.parseObject(configStr, new TypeReference<Map<String, String>>() {
		});
		String responseString = null, memberId = map.get("member_id"), upsOrdeNo = getUpsOrderNo(upsBindCardParamModel),
				upsUserId = getUpsUserId(upsBindCardParamModel);
		HashMap<String, Object> requestMap = requestMap("12", map);
		JSONObject dataContentJson = new JSONObject();
		dataContentJson.put("trans_id", upsAuthSignEntity.getTradeNo());
		dataContentJson.put("trans_serial_no", upsOrdeNo);
		dataContentJson.put("txn_sub_type", "12");
		dataContentJson.put("biz_type", "0000");
		dataContentJson.put("member_id", map.get("member_id"));
		dataContentJson.put("terminal_id", map.get("terminal_id"));
		dataContentJson.put("sms_code", upsBindCardParamModel.getSmsCode());
		dataContentJson.put("trade_date", DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		try {
			UpsUserSignLogEntity upsUserSignLogEntity = saveTppRequestParams(upsBindCardParamModel, upsUserId, memberId,
					upsOrdeNo, dataContentJson.toString());
			String base64str = BaofooSecurityUtil.Base64Encode(dataContentJson.toString());
			String dataContent = BaofooRsaCodingUtil.encryptByPriPfxFile(base64str, RSA_KEY_PATH + map.get("pfxPath"),
					map.get("pfxpwd"));
			requestMap.put("data_content", dataContent);// 加入请求密文
			logger.info("宝付绑卡请求{}", requestMap);
			responseString = OkHttpUtil.postForm(url, requestMap);
			logger.info("宝付绑卡请求返回返回{}", responseString);
			String cerFileString  = BaofooRsaCodingUtil.decryptByPubCerFile(responseString, RSA_KEY_PATH + map.get("cerpath"));
			logger.info("宝付绑卡CerFile返回{}", cerFileString);
			if (cerFileString == null) {
				saveTppResponseParams(upsUserSignLogEntity, responseString);
				throw new BussinessException("宝付配置文件出现问题");
			}
			responseString = BaofooSecurityUtil.Base64Decode(cerFileString);
			logger.info("宝付绑卡解码返回{}", responseString);
			saveTppResponseParams(upsUserSignLogEntity, responseString);
		} catch (IOException e) {
			logger.info("宝付绑卡异常", e);
			throw new BussinessException(e.getMessage());
		}
		JSONObject resultJson = JSONObject.parseObject(responseString);
		String respCode = resultJson.getString("resp_code");
		String respMsg = resultJson.getString("resp_msg");
		if (!"0000".equals(respCode)) {
			return  UpsResultModelUtil.upsResultModelSuccess("02",respMsg,respCode,respMsg);
		}
		upsAuthSignEntity.setStatus(10);
		upsAuthSignEntity.setSignDate(new Date());
		upsAuthSignEntity.setTppSignNo(resultJson.getString("bind_id"));
		upsAuthSignEntity.setTppOrderNo(upsOrdeNo);
		upsAuthSignEntity.setBusinessFlowNum(upsAuthSignEntity.getBusinessFlowNum());
		upsAuthSignEntity.setGmtYmd(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
		upsAuthSignBaofooService.updateRecord(upsAuthSignEntity);
		logger.info("宝付认证绑卡完成{}",upsBindCardParamModel.getRealName());
		return UpsResultModelUtil.upsResultModelSuccess();
	}

}
