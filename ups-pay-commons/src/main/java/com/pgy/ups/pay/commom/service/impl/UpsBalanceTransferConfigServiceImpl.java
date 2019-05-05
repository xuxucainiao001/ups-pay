package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsBalanceTransferConfigDao;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceTransferConfigEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UpsBalanceTransferConfigServiceImpl implements UpsBalanceTransferConfigService {



    @Resource
    private UpsBalanceTransferConfigDao  upsBalanceTransferConfigDao;

    @Override
    public List<UpsBalanceTransferConfigEntity> getList() {
       return  upsBalanceTransferConfigDao.findAll();
    }
}
