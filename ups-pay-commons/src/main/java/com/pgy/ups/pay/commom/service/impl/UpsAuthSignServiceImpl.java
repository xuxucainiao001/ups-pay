package com.pgy.ups.pay.commom.service.impl;



import com.pgy.ups.common.annotation.PrintExecuteTime;

import com.pgy.data.handler.service.PgyDataHandlerService;
import com.pgy.data.handler.service.impl.PgyDataHandlerServiceImpl;

import com.pgy.ups.pay.commom.dao.UpsAuthSignDao;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsUnBindCardModel;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;

import java.util.Date;
import java.util.Optional;
import javax.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class UpsAuthSignServiceImpl implements UpsAuthSignService {

    @Resource
    UpsAuthSignDao upsAuthSignBaofooDao;


    @Override
    public UpsAuthSignEntity queryUpsAuthSignBaofoo(UpsAuthSignEntity upsAuthSignBaofooEntity) {
         upsAuthSignBaofooEntity.setDelete(false);
         Optional<UpsAuthSignEntity> upsAuthSignBaofoo =  upsAuthSignBaofooDao.findOne(Example.of(upsAuthSignBaofooEntity));
         return upsAuthSignBaofoo.isPresent() ? upsAuthSignBaofoo.get() : null;
    }

    @Override
    public UpsAuthSignEntity saveRecord(UpsAuthSignEntity upsAuthSignBaofooEntity) {
        upsAuthSignBaofooEntity.setCreateTime(new Date());
        upsAuthSignBaofooEntity.setUpdateTime(new Date());
        return upsAuthSignBaofooDao.saveAndFlush(upsAuthSignBaofooEntity);
    }

    @Override
    @PrintExecuteTime
    public UpsAuthSignEntity queryProtocolSignBaofoo(String merchantCode, String payChannel, String userNo, String cardNo) {
        UpsAuthSignEntity upsAuthSignBaofooEntity = new UpsAuthSignEntity();
        PgyDataHandlerService payDate =  new  PgyDataHandlerServiceImpl();
        upsAuthSignBaofooEntity.setMerchantCode(merchantCode);
        upsAuthSignBaofooEntity.setPayChannel(payChannel);
        upsAuthSignBaofooEntity.setUserNo(userNo);
        upsAuthSignBaofooEntity.setBankMd5(payDate.md5(cardNo));
        upsAuthSignBaofooEntity.setStatus(10);
        upsAuthSignBaofooEntity.setSignType(SignTypeEnum.PROTOCOL.getCode());
        upsAuthSignBaofooEntity.setDelete(false);
        return queryUpsAuthSignBaofoo(upsAuthSignBaofooEntity);
    }

    @Override
    public UpsAuthSignEntity updateRecord(UpsAuthSignEntity upsAuthSignBaofooEntity) {
        upsAuthSignBaofooEntity.setUpdateTime(new Date());
        return saveRecord(upsAuthSignBaofooEntity);
    }

    @Override
    public void unbindCard(UpsUnBindCardModel upsUnBindCardModel) {
        PgyDataHandlerService payDate =  new  PgyDataHandlerServiceImpl();
        upsAuthSignBaofooDao.unbindCard(upsUnBindCardModel.getFromSystem(),upsUnBindCardModel.getUserNo(),payDate.md5(upsUnBindCardModel.getBankCard()),payDate.md5(upsUnBindCardModel.getPhoneNo()),payDate.md5(upsUnBindCardModel.getIdentity()),payDate.md5(upsUnBindCardModel.getRealName()));
    }

}
