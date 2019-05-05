package com.pgy.ups.pay.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.form.UpsUserSignForm;
import com.pgy.ups.pay.interfaces.service.auth.dubbo.UpsUserSignService;
import com.pgy.ups.pay.service.dao.UpsUserSignDubboDao;

@Service
public class UpsUserSignDubboServiceImpl implements UpsUserSignService {

    @Resource
    private UpsUserSignDubboDao upsUserSignDubboDao;

    @Override
    public PageInfo<UpsAuthSignEntity> queryByForm(UpsUserSignForm upsBankForm) {
        return  new PageInfo<>(upsUserSignDubboDao.queryByForm(upsBankForm,upsBankForm.getPageRequest()));
    }
}
