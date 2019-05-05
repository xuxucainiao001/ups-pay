package com.pgy.ups.pay.service.impl;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.MerchantConfigEntity;
import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;
import com.pgy.ups.pay.interfaces.entity.UpsOrderTypeEntity;
import com.pgy.ups.pay.interfaces.form.MerchantOrderTypeForm;
import com.pgy.ups.pay.interfaces.service.config.dubbo.MerchantConfigService;
import com.pgy.ups.pay.interfaces.service.config.dubbo.MerchantOrderTypeService;
import com.pgy.ups.pay.interfaces.service.order.dubbo.UpsOrderTypeService;
import com.pgy.ups.pay.service.dao.MerchantOrderTypeDubboDao;

@Service
public class MerchantOrderTypeDubboServiceImpl implements MerchantOrderTypeService {

	private Logger logger = LoggerFactory.getLogger(MerchantOrderTypeDubboServiceImpl.class);

	@Resource
	private MerchantOrderTypeDubboDao merchantOrderTypeDubboDao;

	@Resource
	private UpsOrderTypeService upsOrderTypeService;

	@Resource
	private MerchantConfigService merchantConfigService;

	@Override
	public boolean createMerchantOrderType(MerchantOrderTypeForm form) {
		MerchantOrderTypeEntity mote = new MerchantOrderTypeEntity();
		MerchantConfigEntity merchantConfigEntity = merchantConfigService.queryMerchantConfig(form.getMerchantId());
		UpsOrderTypeEntity upsOrderTypeEntity = upsOrderTypeService.queryOrderTypeById(form.getOrderTypeId());
		mote.setMerchantConfigEntity(merchantConfigEntity);
		mote.setUpsOrderTypeEntity(upsOrderTypeEntity);
		if (merchantOrderTypeDubboDao.findOne(Example.of(mote)).isPresent()) {
			logger.info("保存失败，该商户已经有该支付产品：{}", mote);
			return false;
		}
		mote.setUpdateTime(new Date());
		mote.setRouteStatus(MerchantOrderTypeService.OPEN_DEFAULT);
		mote.setUpdateUser(form.getUpdateUser());
		mote.setEndTime(form.getEndTime());
		mote.setStartTime(form.getStartTime());
		merchantOrderTypeDubboDao.saveAndFlush(mote);
		return true;
	}

	@Override
	public void deleteMerchantOrderType(Long id) {
		merchantOrderTypeDubboDao.deleteMerchantById(id);
	}

	@Override
	public boolean updateMerchantOrderType(MerchantOrderTypeForm form) {
		MerchantOrderTypeEntity mote = merchantOrderTypeDubboDao.findById(form.getId()).orElse(null);
		if (Objects.isNull(mote)) {
			return false;
		}
		mote.setStartTime(form.getStartTime());
		mote.setEndTime(form.getEndTime());
		merchantOrderTypeDubboDao.saveAndFlush(mote);
		return true;
	}

	@Override
	public PageInfo<MerchantOrderTypeEntity> queryMerchantOrderTypeForPage(MerchantOrderTypeForm form) {
		MerchantOrderTypeEntity mote = new MerchantOrderTypeEntity();
		if(form.getMerchantId()!=null) {
			mote.setMerchantConfigEntity(merchantConfigService.queryMerchantConfig(form.getMerchantId()));
		}
		if(form.getOrderTypeId()!=null) {
			mote.setUpsOrderTypeEntity(upsOrderTypeService.queryOrderTypeById(form.getOrderTypeId()));
		}		
		mote.setRouteStatus(form.getRouteStatus());
		mote.setDefaultPayChannel(form.getDefaultPayChannel());
		Page<MerchantOrderTypeEntity> page = merchantOrderTypeDubboDao.findAll(Example.of(mote), form.getPageRequest());
		return new PageInfo<>(page);
	}

	@Override
	public boolean openDefaultOrRoute(Long id, String openDefault,String updateUser) {
		MerchantOrderTypeEntity entity=merchantOrderTypeDubboDao.findById(id).orElse(null);
		if(Objects.isNull(entity)) {
			return false;
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUser(updateUser);
		entity.setRouteStatus(openDefault);
		merchantOrderTypeDubboDao.save(entity);	
		return true;
	}

	@Override
	public boolean updateMerchantRouteConfig(MerchantOrderTypeForm form) {
		MerchantOrderTypeEntity entity=merchantOrderTypeDubboDao.findById(form.getId()).orElse(null);
		if(Objects.isNull(entity)) {
			return false;
		}
		entity.setUpdateUser(form.getUpdateUser());
		entity.setDefaultPayChannel(form.getDefaultPayChannel());
		entity.setUpdateTime(new Date());
		merchantOrderTypeDubboDao.save(entity);	
		return true;
	}

}
