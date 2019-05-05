package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsSignDefaultConfigDao;
import com.pgy.ups.pay.interfaces.entity.UpsSignDefaultConfigEntity;
import com.pgy.ups.pay.interfaces.service.auth.UpsSignDefaultConfigervice;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class UpsSignDefaultConfigerviceImpl  implements UpsSignDefaultConfigervice {

    @Resource
    private UpsSignDefaultConfigDao upsSignDefaultConfigDao;

    @Override
    public UpsSignDefaultConfigEntity queryUpsSignDefaultConfig(String fromSystem) {
        UpsSignDefaultConfigEntity upsSignDefaultConfigEntity = new UpsSignDefaultConfigEntity();
        upsSignDefaultConfigEntity.setMerchantCode(fromSystem);
        return selectEntity(upsSignDefaultConfigEntity);
    }


    public UpsSignDefaultConfigEntity  selectEntity (UpsSignDefaultConfigEntity upsSignDefaultConfigEntity) {
        Optional<UpsSignDefaultConfigEntity> optional = upsSignDefaultConfigDao.findOne(Example.of(upsSignDefaultConfigEntity));
        return   optional.isPresent() ? optional.get() : null;
    }


}
