package com.pgy.ups.pay.interfaces.service.auth;

import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.model.UpsUnBindCardModel;

public interface UpsAuthSignService {

    UpsAuthSignEntity queryUpsAuthSignBaofoo(UpsAuthSignEntity upsAuthSignBaofooEntity);


    UpsAuthSignEntity saveRecord(UpsAuthSignEntity upsAuthSignBaofooEntity);

    UpsAuthSignEntity queryProtocolSignBaofoo(String fromSystem, String payChannel, String userNo, String cardNo);

    UpsAuthSignEntity  updateRecord (UpsAuthSignEntity upsAuthSignBaofooEntity);

    void  unbindCard(UpsUnBindCardModel upsUnBindCardModel);
}
