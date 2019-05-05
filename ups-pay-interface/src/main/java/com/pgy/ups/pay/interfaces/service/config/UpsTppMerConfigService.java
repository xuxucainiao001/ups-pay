package com.pgy.ups.pay.interfaces.service.config;


import com.pgy.ups.pay.interfaces.entity.UpsTppMerConfigEntity;

import java.util.Map;

public interface UpsTppMerConfigService  {

    UpsTppMerConfigEntity queryUpsTppMer(String payChannel, String upsTppMer, String orderType);

    Map<String,String> queryUpsTppMerConfig(String payChannel, String upsTppMer, String orderType);
}
