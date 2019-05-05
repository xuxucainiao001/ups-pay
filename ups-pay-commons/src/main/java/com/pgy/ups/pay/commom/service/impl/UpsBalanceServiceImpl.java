package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsBalanceDao;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class UpsBalanceServiceImpl implements UpsBalanceService {


    @Resource
    private UpsBalanceDao  upsBalanceDao;

    @Override
    public void saveEntity(UpsBalanceEntity upsBalanceEntity) {
        upsBalanceEntity.setCreateTime(new Date());
        upsBalanceEntity.setUpdateTime(new Date());
        upsBalanceDao.save(upsBalanceEntity);
    }

    @Override
    public void updateEntity(UpsBalanceEntity upsBalanceEntity) {
        upsBalanceEntity.setUpdateTime(new Date());
        upsBalanceDao.save(upsBalanceEntity);
    }

    @Override
    public UpsBalanceEntity getBalanceByMemberId(String memberId) {
        UpsBalanceEntity entity = new UpsBalanceEntity();
        entity.setTppMerNo(memberId);
       return upsBalanceDao.findOne(Example.of(entity)).isPresent() ? upsBalanceDao.findOne(Example.of(entity)).get() : null;
    }


}
