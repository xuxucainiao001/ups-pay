package com.pgy.ups.pay.yeepay.pay.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.pay.BindCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.commom.utils.SignatureBindBaseCore;
import com.pgy.ups.pay.commom.utils.UpsResultModelUtil;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
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
@Service(group="yeepayBindCard",timeout=60000,retries=0)
public class YeepayBindCardServiceImpl extends SignatureBindBaseCore implements BindCardService {

	@Resource
	private UpsThirdpartyConfigService upsThirdpartyConfigService;

	@Resource
	private UpsAuthSignService  upsAuthSignService;

	private Logger logger = LoggerFactory.getLogger(YeepayBindCardServiceImpl.class);

	@Override
	public UpsResultModel bindCard(UpsBindCardParamModel upsBindCardParamModel) throws BussinessException{
		UpsAuthSignEntity upsAuthSignEntity = checkBaofooSignatureBind(upsBindCardParamModel, SignTypeEnum.AUTH);
		String configStr = upsThirdpartyConfigService.queryThirdpartyConfig(upsBindCardParamModel.getPayChannel(),
				upsBindCardParamModel.getOrderType(), upsBindCardParamModel.getMerchantCode()).getConfigDate();
		Map<String,String> configMap=JSONObject.parseObject(configStr,new TypeReference<Map<String,String>>(){});
		//String upsOrderNo = getUpsOrderNo(upsBindCardParamModel);
		Map<String, Object> params = new HashMap<>();
		params.put("requestno","SQKK10020981510");
		params.put("validatecode",upsBindCardParamModel.getSmsCode());

		YopRequest yoprequest = new YopRequest(configMap.get("appKey"));
		Set<Entry<String, Object>> entry = params.entrySet();
		for(Entry<String, Object> s:entry){
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		YopResponse  yopresponse = null;
		logger.info("易宝鉴权绑卡传参{}",yoprequest);
		try {
			yopresponse = YopClient3.postRsa("/rest/v1.0/paperorder/auth/confirm", yoprequest);
		} catch (IOException e) {
           logger.error("易宝绑卡接口调用异常{}",e);
		}
		logger.info("易宝鉴权绑卡返回{}",yopresponse);
		if(yopresponse.isSuccess()){
			if("SUCCESS".equals(yopresponse.getState())){
				String  result =	yopresponse.getStringResult();
				logger.info("易宝鉴权绑卡解析返回{}",result);
				JSONObject jSONObject = JSONObject.parseObject(result);
				String status = jSONObject.getString("status");
				if("BIND_SUCCESS".equals(status)){
					if (null==upsAuthSignEntity) {
						upsAuthSignService.saveRecord(upsAuthSignEntity);
					}
				}
			}
		}
		logger.info("易宝返回{}",yopresponse);
		return UpsResultModelUtil.upsResultModelSuccess();
	}
}
