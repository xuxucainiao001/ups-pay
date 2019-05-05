package com.pgy.ups.pay.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.MerchantConfigEntity;
import com.pgy.ups.pay.interfaces.form.MerchantConfigForm;
import com.pgy.ups.pay.interfaces.service.config.dubbo.MerchantConfigService;
import com.pgy.ups.pay.service.dao.MerchantConfigDubboDao;

@Service
public class MerchantConfigDubboServiceImpl implements MerchantConfigService {

	private Logger logger = LoggerFactory.getLogger(MerchantConfigDubboServiceImpl.class);

	@Resource
	private MerchantConfigDubboDao merchantConfigDubboDao;

	@Override
	public PageInfo<MerchantConfigEntity> queryByForm(MerchantConfigForm form) {
		Page<MerchantConfigEntity> page = merchantConfigDubboDao.findByForm(form.getMerchantCode(),
				form.getMerchantName(), form.getDescription(), form.getPageRequest());
		return new PageInfo<>(page);
	}

	@Override
	public void enableMerchantConfig(Long id) {
		merchantConfigDubboDao.enableOrDisableMerchantConfig(id, true);

	}

	@Override
	public void disableMerchantConfig(Long id) {
		merchantConfigDubboDao.enableOrDisableMerchantConfig(id, false);

	}

	@Override
	public void deleteMerchantConfig(Long id) {
		merchantConfigDubboDao.deleteById(id);
	}

	@Override
	public MerchantConfigEntity queryMerchantConfig(Long id) {
		return merchantConfigDubboDao.findById(id).orElse(null);
	}

	@Override
	public boolean updateMerchantConfig(MerchantConfigForm form) {
		try {
			merchantConfigDubboDao.updateMerchantConfig(form);
			return true;
		} catch (Exception e) {
			logger.error("修改商户配置异常：{}", e);
			return false;
		}
	}

	@Override
	public MerchantConfigEntity createMerchantConfig(MerchantConfigForm form) {

		MerchantConfigEntity mce = merchantConfigDubboDao.queryByMerchantCode(form.getMerchantCode());
		if (Objects.nonNull(mce)) {
			logger.info("新增商户配置失败，商户编码不能重复");
			return null;
		}
		mce = merchantConfigDubboDao.queryByMerchantName(form.getMerchantName());
		if (Objects.nonNull(mce)) {
			logger.info("新增商户配置失败，商户名称不能重复");
			return null;
		}
		mce = new MerchantConfigEntity();
		mce.setCreateTime(new Date());
		mce.setCreateUser(form.getCreateUser());
		mce.setDescription(form.getDescription());
		mce.setMerchantCode(form.getMerchantCode());
		mce.setMerchantName(form.getMerchantName());
		mce.setMerchantPublicKey(form.getMerchantPublicKey());
		mce.setUpdateTime(new Date());
		mce.setUpdateUser(form.getUpdateUser());
		mce.setUpsPrivateKey(form.getUpsPrivateKey());
		mce.setAvailable(false);
		return merchantConfigDubboDao.saveAndFlush(mce);
	}

	@Override
	public List<MerchantConfigEntity> findAll() {
		return merchantConfigDubboDao.findAll();
	}

}
