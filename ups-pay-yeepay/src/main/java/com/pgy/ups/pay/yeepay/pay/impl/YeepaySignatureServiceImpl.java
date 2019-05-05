package com.pgy.ups.pay.yeepay.pay.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.DateUtils;
import com.pgy.ups.pay.commom.utils.SignatureBindBaseCore;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import com.pgy.ups.pay.interfaces.service.config.UpsThirdpartyConfigService;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;


/**
  * 易宝代付 借款
 * 
 * @author 墨凉
 *
 */
@Service(group="yeepaySignature",timeout=60000,retries=0)
public class YeepaySignatureServiceImpl extends SignatureBindBaseCore implements SignatureService {

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private UpsAuthSignService  upsAuthSignService;

	private Logger logger = LoggerFactory.getLogger(YeepaySignatureServiceImpl.class);

	@Override
	public UpsResultModel signature(UpsSignatureParamModel upsSignatureParamModel) throws BussinessException{
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsSignatureParamModel, SignTypeEnum.AUTH);
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsSignatureParamModel.getPayChannel(),
				upsSignatureParamModel.getOrderType(), upsSignatureParamModel.getMerchantCode()).getConfigDate();
		Map<String,String> configMap=JSONObject.parseObject(configStr,new TypeReference<Map<String,String>>(){});
		String upsOrderNo = getUpsOrderNo(upsSignatureParamModel);
		Map<String, Object> params = new HashMap<>();
		params.put("merchantno","SQKK10020981510");
		params.put("requestno",upsOrderNo);
		params.put("identityid", getUpsUserId(upsSignatureParamModel));
		params.put("identitytype", "ID_CARD");
		params.put("cardno",upsSignatureParamModel.getBankCard());
		params.put("idcardno",upsSignatureParamModel.getIdentity());
		params.put("idcardtype", "ID");
		params.put("username",upsSignatureParamModel.getRealName());
		params.put("phone",upsSignatureParamModel.getPhoneNo());
		params.put("issms", true);
		params.put("avaliabletime",15);
		params.put("requesttime", DateUtils.getCurrentDate(DateUtils.DATE_TIME_FORMAT));
		params.put("authtype", "COMMON_FOUR");

		YopRequest yoprequest = new YopRequest(configMap.get("appKey"));
		Set<Entry<String, Object>> entry = params.entrySet();
		for(Entry<String, Object> s:entry){
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		YopResponse  yopresponse = null;
		logger.info("易宝鉴权绑卡传参{}",yoprequest);
		try {
			yopresponse = YopClient3.postRsa("/rest/v1.0/paperorder/unified/auth/request", yoprequest);
		} catch (IOException e) {
			  logger.error("易宝签约接口调用异常{}",e);
		}
		logger.info("易宝鉴权绑卡返回{}",yopresponse.toString());
		if(yopresponse.isSuccess()){
			if("SUCCESS".equals(yopresponse.getState())){
			 String  result =	yopresponse.getStringResult();
			 logger.info("易宝鉴权绑卡解析返回{}",result);
			 JSONObject jSONObject = JSONObject.parseObject(result);
			 String status = jSONObject.getString("status");
			 if("BIND_SUCCESS".equals(status)){
				 if (upsAuthSignEntity == null) {
					 upsAuthSignEntity = new UpsAuthSignEntity();
					 upsAuthSignEntity.setPayChannel(upsSignatureParamModel.getPayChannel());
					 upsAuthSignEntity.setStatus(10);
					 upsAuthSignEntity.setCertType("01");
					 upsAuthSignEntity.setUserNo(upsSignatureParamModel.getUserNo());
					 upsAuthSignEntity.setBankCard(upsSignatureParamModel.getBankCard());
					 upsAuthSignEntity.setRealName(upsSignatureParamModel.getRealName());
					 upsAuthSignEntity.setCreateTime(new Date());
					 upsAuthSignEntity.setIdentity(upsSignatureParamModel.getIdentity());
					 upsAuthSignEntity.setPhoneNo(upsSignatureParamModel.getPhoneNo());
					 upsAuthSignEntity.setSignType(SignTypeEnum.AUTH.getCode());
					 upsAuthSignEntity.setTppMerNo(configMap.get("appKey"));
					 upsAuthSignEntity.setBankCode(upsSignatureParamModel.getBankCode());
					 upsAuthSignEntity.setMerchantCode(upsSignatureParamModel.getMerchantCode());
					 upsAuthSignEntity.setTradeNo(upsOrderNo);
					 upsAuthSignEntity.setBusinessFlowNum(upsSignatureParamModel.getBusinessFlowNum());
					 upsAuthSignEntity.setTppOrderNo(jSONObject.getString("yborderid"));
					 upsAuthSignEntity.setUpsUserId(getUpsUserId(upsSignatureParamModel));
					 upsAuthSignService.saveRecord(upsAuthSignEntity);
				 }
			 }
			}
		}
		logger.info("易宝返回{}",yopresponse);
		return UpsResultModelUtil.upsResultModelSuccess();
	}
}
