package com.pgy.ups.pay.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsBankEntity;
import com.pgy.ups.pay.interfaces.form.UpsBankForm;
import com.pgy.ups.pay.interfaces.service.config.dubbo.BankConfigService;
import com.pgy.ups.pay.service.dao.BankConfigDubboDao;


/**
 * Hello world!
 *
 */
@Service
public class BankConfigDubboServiceImpl implements BankConfigService {

	@Resource
	private BankConfigDubboDao bankConfigDubboDao;

	@Override
	public PageInfo<UpsBankEntity> queryByForm(UpsBankForm upsBankForm) {

		Page<UpsBankEntity> page = bankConfigDubboDao.findByForm(upsBankForm.getBankCode(), upsBankForm.getBankName(),
				upsBankForm.getPageRequest());
		return new PageInfo<>(page);
	}

	@Override
	public void deleteBankConfigById(Long id) {
		bankConfigDubboDao.deleteById(id);
	}

	@Override
	public UpsBankEntity saveBankConfig(UpsBankForm form) {
		UpsBankEntity entity = new UpsBankEntity();
		entity.setBankCode(form.getBankCode());
		entity.setBankName(form.getBankName());
		entity.setCreateTime(new Date());
		entity.setUpdateTime(new Date());
		entity.setCreateUser(form.getCreateUser());
		entity.setUpdateUser(form.getUpdateUser());
		return bankConfigDubboDao.save(entity);
	}

}