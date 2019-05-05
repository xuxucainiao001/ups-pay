package com.pgy.ups.pay.commom.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.pay.commom.dao.UpsLogDao;
import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.log.UpsLogService;

/**
 * USP日志系统逻辑接口实现
 * @author acer
 *
 */

@Service
public class UpsLogServiceImpl implements UpsLogService{
	
	@Resource
	private UpsLogDao upsLogDao;


	@Override
	public UpsLogEntity createUpsLog(UpsParamModel upsParamModel,String url) {
		
		UpsLogEntity upsLogEntity=new UpsLogEntity();
		upsLogEntity.setRequestUrl(url);
		upsLogEntity.setFromSystem(upsParamModel.getMerchantCode());
		upsLogEntity.setBusinessFlowNum(upsParamModel.getBusinessFlowNum());
		upsLogEntity.setBusinessParam(JSONObject.toJSONString(upsParamModel));
		upsLogEntity.setCreateTime(new Date());
		upsLogEntity.setUpdateTime(new Date());
		return upsLogDao.save(upsLogEntity);
	}

    /**
     * 异步更新日志
     */
	@Async
	@Override
	public void updateUpsLog(UpsLogEntity upsLogEntity) {
		upsLogEntity.setUpdateTime(new Date());
		upsLogDao.saveAndFlush(upsLogEntity);		
	}
}
