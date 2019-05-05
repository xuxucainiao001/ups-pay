package com.pgy.ups.pay.service.impl;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;
import com.pgy.ups.pay.interfaces.form.CollectChooseForm;
import com.pgy.ups.pay.interfaces.service.config.dubbo.CollectChooseService;
import com.pgy.ups.pay.service.dao.CollectChooseDubboDao;

@Service
public class CollectChooseDubboServiceImpl implements CollectChooseService {
	
	private Logger logger=LoggerFactory.getLogger(CollectChooseDubboServiceImpl.class);

	@Resource
	private CollectChooseDubboDao collectChooseDubboDao;

	@Override
	public PageInfo<CollectChooseEntity> queryByForm(CollectChooseForm form) {
		Page<CollectChooseEntity> page = collectChooseDubboDao.queryByForm(form, form.getPageRequest());
		return new PageInfo<>(page);
	}

	@Override
	public void deleteCollectChooseById(Long id) {
		collectChooseDubboDao.deleteById(id);
	}

	@Override
	public boolean updateCollectChoose(CollectChooseForm form) {
		Optional<CollectChooseEntity> optional = collectChooseDubboDao.findById(form.getId());
		if (optional.isPresent()) {
			CollectChooseEntity c = optional.get();
			c.setCollectType(form.getCollectType());
			c.setUpdateTime(new Date());
			c.setUpdateUser(form.getUpdateUser());
			collectChooseDubboDao.save(c);
			return true;
		}
		return false;

	}

	@Override
	public CollectChooseEntity createCollectChoose(CollectChooseForm form) {
		CollectChooseEntity cce = new CollectChooseEntity();
		cce.setMerchantCode(form.getMerchantCode());
		Example<CollectChooseEntity> example = Example.of(cce);
		Optional<CollectChooseEntity> option = collectChooseDubboDao.findOne(example);
		if (option.isPresent()) {
			logger.warn("该商户已经配置代扣类型！不可重复创建！{}",form.getMerchantCode());
			return null;
		}
		cce.setActive(true);
		cce.setCollectType(form.getCollectType());
		cce.setCreateUser(form.getCreateUser());
		cce.setUpdateUser(form.getCreateUser());

		cce.setCreateTime(new Date());
		cce.setUpdateTime(new Date());
		return collectChooseDubboDao.save(cce);
	}

}
