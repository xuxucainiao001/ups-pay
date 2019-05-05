package com.pgy.ups.pay.gateway.controller;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.exception.ParamValidException;
import com.pgy.ups.pay.commom.constants.RequestAttributeName;
import com.pgy.ups.pay.commom.constants.UpsResultCode;
import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.service.log.UpsLogService;

/**
 * 全局异常拦截器
 * 
 * @author 墨凉
 *
 */
@ControllerAdvice
public class ExceptionHandlerController {

	private final static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Resource
	private UpsLogService upsLogService;

	@ResponseBody
	@ExceptionHandler(Exception.class)
	public UpsResultModel handleException(Exception e, HttpServletRequest request) {

		String errorCode = "";
		String errorMessage = "";

		if (e instanceof ParamValidException) {
			errorMessage = e.getMessage();
			errorCode = UpsResultCode.PARAMS_ERROR;
			logger.error("参数验证异常！{}", errorMessage);
		} else if (e instanceof BussinessException) {
			errorMessage = e.getMessage();
			errorCode = ((BussinessException) e).getCode() == null ? UpsResultCode.BUSINESS_ERROR : ((BussinessException) e).getCode();
			logger.error("业务操作异常！{}", errorMessage);
		} else {
			errorMessage="系统发生异常！";
			errorCode=UpsResultCode.SYSTEM_ERROR;
			logger.error("系统异常！{}", ExceptionUtils.getStackTrace(e));
		} 
		//创建返回结果
		UpsResultModel upsResultModel = new UpsResultModel(errorCode, errorMessage);	
		//日志对象不为空时，更新日志
		UpsLogEntity upsLogEntity = (UpsLogEntity) request.getAttribute(RequestAttributeName.UPS_LOG_ENTITY);
		if(Objects.nonNull(upsLogEntity)) {
			upsLogEntity.setReturnParam(JSONObject.toJSONString(upsResultModel));
			upsLogEntity.setCode(UpsResultCode.BUSINESS_ERROR);			
			upsLogEntity.setMessage(errorMessage);
			upsLogService.updateUpsLog(upsLogEntity);
		}		
		return upsResultModel;
	}
}
