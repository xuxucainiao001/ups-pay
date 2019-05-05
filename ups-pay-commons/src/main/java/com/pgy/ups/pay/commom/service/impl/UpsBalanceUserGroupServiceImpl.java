package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsBalanceUserGroupDao;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceUserGroupEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceUserGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UpsBalanceUserGroupServiceImpl implements UpsBalanceUserGroupService {

    @Resource
    private UpsBalanceUserGroupDao  upsBalanceUserGroupDao;

    @Override
    public List<UpsBalanceUserGroupEntity> getListByCodeAndTppMerNo(String code, String tppMerNo) {
       return upsBalanceUserGroupDao.findByGroupCodeAndTppMerNo(code,tppMerNo);
    }
}
