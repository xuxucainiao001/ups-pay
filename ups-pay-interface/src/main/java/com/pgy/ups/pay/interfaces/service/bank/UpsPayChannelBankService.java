package com.pgy.ups.pay.interfaces.service.bank;

import com.pgy.ups.pay.interfaces.entity.UpsPayChannelBankEntity;

public interface UpsPayChannelBankService {

    String  queryChannelBankCode(String payChannle,String bussinessBankCode);
    
    String  queryChannelBankName(String payChannle,String bussinessBankCode);
    
    UpsPayChannelBankEntity queryUpsPayChannelBank(String payChannle, String bussinessBankCode);

}
