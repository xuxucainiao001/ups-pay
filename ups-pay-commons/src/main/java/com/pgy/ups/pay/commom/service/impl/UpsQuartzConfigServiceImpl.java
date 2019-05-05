package com.pgy.ups.pay.commom.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgy.ups.pay.commom.dao.UpsQuartzConfigDao;
import com.pgy.ups.pay.interfaces.entity.QuartzConfigEntity;
import com.pgy.ups.pay.interfaces.service.config.UpsQuartzConfigService;

@Service
public class UpsQuartzConfigServiceImpl implements UpsQuartzConfigService{
	
	@Resource
	private UpsQuartzConfigDao upsQuartzConfigDao;

	@Override
	public QuartzConfigEntity queryQuartzConfigByClassName(String className) {		
		return upsQuartzConfigDao.queryByClassName(className);
	}

	@Override
	@Transactional
	public void stopQuartzTask(String className) {		
		upsQuartzConfigDao.setQuartzConfigActive(className,false);		
	}

	@Override
	@Transactional
	public void startQuartzTask(String className) {		
		upsQuartzConfigDao.setQuartzConfigActive(className,true);		
	}

}
  