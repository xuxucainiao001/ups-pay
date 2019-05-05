package com.pgy.ups.pay.interfaces.service.balance;

import com.pgy.ups.pay.interfaces.entity.UpsBalanceUserGroupEntity;

import java.util.List;

public interface UpsBalanceUserGroupService {

     List<UpsBalanceUserGroupEntity>  getListByCodeAndTppMerNo(String code, String tppMerNo);


}
