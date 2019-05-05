package com.pgy.ups.pay.interfaces.pay;

import com.pgy.ups.pay.interfaces.model.UpsBindCardParamModel;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;

public interface BindCardService {

    UpsResultModel bindCard(UpsBindCardParamModel upsBindCardParamModel);
}
