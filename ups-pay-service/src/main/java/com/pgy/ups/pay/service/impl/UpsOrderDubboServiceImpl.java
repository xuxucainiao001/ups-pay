package com.pgy.ups.pay.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.form.UpsOrderForm;
import com.pgy.ups.pay.interfaces.service.order.dubbo.UpsOrderService;
import com.pgy.ups.pay.service.dao.UpsOrderDubboDao;

import javax.annotation.Resource;


@Service
public class UpsOrderDubboServiceImpl implements UpsOrderService {

    @Resource
    private UpsOrderDubboDao upsOrderDubboDao;

    @Override
    public PageInfo<UpsOrderEntity> queryByForm(UpsOrderForm form) {
        return  new PageInfo<>(upsOrderDubboDao.getPage(form,form.getPageRequest()));
    }
}
