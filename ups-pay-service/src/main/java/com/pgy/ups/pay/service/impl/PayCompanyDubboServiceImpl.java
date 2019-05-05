package com.pgy.ups.pay.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;
import com.pgy.ups.pay.interfaces.form.PayCompanyForm;
import com.pgy.ups.pay.interfaces.service.route.dubbo.PayCompanyService;
import com.pgy.ups.pay.service.dao.PayCompanyDubboDao;

@Service
public class PayCompanyDubboServiceImpl implements PayCompanyService{
	
	
	@Resource
	private PayCompanyDubboDao payCompanyDubboDao;

	@Override
	public List<PayCompanyEntity> queryAllPayChannels() {
		
		return payCompanyDubboDao.findAll();
	}

	@Override
	public PageInfo<PayCompanyEntity> queryPayChannelsForPage(PayCompanyForm form) {
		Page<PayCompanyEntity> page=payCompanyDubboDao.findByCondition(form.getCompanyName(),form.getCompanyCode(),form.getPageRequest());
		return new PageInfo<>(page);
	}

	@Override
	public boolean enableOrDisablePayCompany(Long id, boolean b) {
		Optional<PayCompanyEntity> optional=payCompanyDubboDao.findById(id);
		if(optional.isPresent()) {
			PayCompanyEntity entity=optional.get();
			entity.setActive(b);
			payCompanyDubboDao.save(entity);
			return true;
		}
		return false;		
		
	}
}
