package com.pgy.ups.pay.commom.service.impl;


import com.pgy.ups.pay.commom.dao.UpsBalanceSmsRecordDao;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceSmsRecordEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceSmsRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UpsBalanceSmsRecordServiceImpl implements UpsBalanceSmsRecordService {


    @Resource
    private UpsBalanceSmsRecordDao upsBalanceSmsRecordDao;

    @Override
    public void saveEntity(UpsBalanceSmsRecordEntity upsBalanceSmsRecordEntity) {
        upsBalanceSmsRecordDao.save(upsBalanceSmsRecordEntity);
    }
}
