package com.pgy.ups.pay.commom.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pgy.ups.pay.commom.dao.UpsOrderTypeDao;
import com.pgy.ups.pay.interfaces.entity.UpsOrderTypeEntity;
import com.pgy.ups.pay.interfaces.service.order.UpsOrderTypeService;

@Service
public class UpsOrderTypeServiceImpl implements UpsOrderTypeService{
	
	@Resource
	private UpsOrderTypeDao  upsOrderTypeDao;

	@Override
	public List<UpsOrderTypeEntity> getAllOrderType() {
		return upsOrderTypeDao.findAll();
	}

}
