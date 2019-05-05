package com.pgy.ups.pay.gateway.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.form.UpsOrderForm;
import com.pgy.ups.pay.interfaces.service.order.dubbo.UpsOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pgy.ups.common.utils.HttpClientUtils;
import com.pgy.ups.common.utils.ParamUtils;
import com.pgy.ups.common.utils.ReflectUtils;
import com.pgy.ups.pay.commom.utils.SecurityUtils;
import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsCollectParamModel;
import com.pgy.ups.pay.interfaces.model.UpsPayParamModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;

/**
 * 支付网关
 *
 * @author 墨凉
 *
 */
@Controller
@RequestMapping("/index")
public class TestController {

	@Value("${rsa.privateKey}")
	private String privateKey;
	
	
	private Logger logger=LoggerFactory.getLogger(TestController.class);


	@Resource
	private UpsOrderService upsOrderService;


	/**
	 * 測試接口
	 *
	 * @throws InterruptedException
	 */
	@ResponseBody
	@RequestMapping("/testPay.do")
	public Object testPay(UpsPayParamModel model) throws InterruptedException {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));		
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/pay.do", map, 100000);
	}

	@ResponseBody
	@RequestMapping("/testCollect.do")
	public Object testCollect(UpsCollectParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);		
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/collect.do", map, 100000);

	}

	@ResponseBody
	@RequestMapping("/testSignature.do")
	public String testSignature(UpsSignatureParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		//model.setBusinessFlowNum("sa123j123jw232218eee88");
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/auth/signature.do", map, 100000);
	}

	@ResponseBody
	@RequestMapping("/testBindCard.do")
	public String bindCard(UpsBindCardParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/auth/bindCard.do", map, 100000);
	}

	@ResponseBody
	@RequestMapping("/testProtocolSignature.do")
	public String protocolSignature(UpsSignatureParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://trc.letto8.cn/ups-pay/index/protocol/signature.do", map, 100000);
	}

	@ResponseBody
	@RequestMapping("/testProtocolBindCard.do")
	public String protocolBindCard(UpsBindCardParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/protocol/bindCard.do", map, 100000);

	}

	@ResponseBody
	@RequestMapping("/testUnityBindCard.do")
	public String unityBindCard(UpsBindCardParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/bindCard.do", map, 100000);

	}

	@ResponseBody
	@RequestMapping("/testUnitySignature.do")
	public String unitySignature(UpsBindCardParamModel model) {
		model.setBusinessFlowNum(UUID.randomUUID().toString());
		model.setSign(SecurityUtils.sign(model, privateKey));
		Map<String, String> map = ReflectUtils.objectToMap(model);
		return HttpClientUtils.postRequest("http://127.0.0.1:9080/ups-pay/index/signature.do", map, 100000);

	}
	
	/**
	 * 推送回调测试接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/testReturn.do")
	public Map<String,Object> testCallback(HttpServletRequest request){
		
		logger.info("推送参数：{}",ParamUtils.getParamterMap(request));
		
		Map<String,Object> resultMap=new LinkedHashMap<>();
		
		resultMap.put("flag", true);
		
		return resultMap;
		
	}

	/**
	 * 推送回调测试接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/testReturn1.do")
	public PageInfo<UpsOrderEntity> testCallback1(UpsOrderForm form){
	  return  	upsOrderService.queryByForm(form);
	}

}
