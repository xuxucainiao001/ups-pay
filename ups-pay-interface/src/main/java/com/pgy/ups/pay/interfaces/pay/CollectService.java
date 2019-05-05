package com.pgy.ups.pay.interfaces.pay;

import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;

public interface CollectService {

    UpsResultModel collet(UpsOrderEntity orderEntity);
    
}
