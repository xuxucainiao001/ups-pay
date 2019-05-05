package com.pgy.ups.pay.commom.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pgy.ups.pay.commom.dao.UpsThirdpartyLogDao;
import com.pgy.ups.pay.interfaces.entity.UpsThirdpartyLogEntity;
import com.pgy.ups.pay.interfaces.service.log.UpsThirdpartyLogService;

/**
 * 
 * 第三方支付渠道请求交互日志实现
 * @author 墨凉
 *
 */
@Service
public class UpsThirdpartyLogServiceImpl implements  UpsThirdpartyLogService{
	
	@Resource
	private UpsThirdpartyLogDao upsThirdpartyLogDao;
     
	/**
	 * 异步创建日志
	 */
	@Async
	@Override
	public UpsThirdpartyLogEntity createThirdpartyLog(UpsThirdpartyLogEntity utl) {
		utl.setCreateTime(new Date());
		utl.setUpdateTime(new Date());
		return upsThirdpartyLogDao.saveAndFlush(utl);
	}

}
