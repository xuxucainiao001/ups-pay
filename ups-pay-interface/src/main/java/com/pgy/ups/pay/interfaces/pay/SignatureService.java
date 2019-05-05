package com.pgy.ups.pay.interfaces.pay;


import com.pgy.ups.pay.interfaces.model.UpsResultModel;
import com.pgy.ups.pay.interfaces.model.UpsSignatureParamModel;

public interface SignatureService {

    UpsResultModel signature(UpsSignatureParamModel upsSignatureParamModel);
}
