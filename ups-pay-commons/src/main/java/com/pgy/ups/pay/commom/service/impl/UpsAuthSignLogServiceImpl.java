package com.pgy.ups.pay.commom.service.impl;

import javax.annotation.Resource;

import com.pgy.ups.pay.commom.dao.UpsAuthSignLogDao;
import com.pgy.ups.pay.interfaces.entity.UpsUserSignLogEntity;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignLogService;
import org.springframework.stereotype.Service;


@Service
public class UpsAuthSignLogServiceImpl implements UpsAuthSignLogService {

    @Resource
    UpsAuthSignLogDao upsAuthSignLogDao;

    @Override
    public UpsUserSignLogEntity saveRecord(UpsUserSignLogEntity entity) {
        return upsAuthSignLogDao.saveAndFlush(entity);
    }

}
