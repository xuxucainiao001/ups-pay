package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsBalanceTransferRecordDao;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceTransferRecordEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UpsBalanceTransferRecordServiceImpl implements UpsBalanceTransferRecordService {

   @Resource
   private UpsBalanceTransferRecordDao  upsBalanceTransferRecordDao;

    @Override
    public UpsBalanceTransferRecordEntity insertSelective(UpsBalanceTransferRecordEntity upsBalanceTransferRecordEntity) {
        return upsBalanceTransferRecordDao.saveAndFlush(upsBalanceTransferRecordEntity);
    }
}
