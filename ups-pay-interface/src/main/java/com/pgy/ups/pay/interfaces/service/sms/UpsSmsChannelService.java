package com.pgy.ups.pay.interfaces.service.sms;

import com.pgy.ups.pay.interfaces.entity.SmsSendResponse;

public interface UpsSmsChannelService {

    SmsSendResponse sendMessage(String phone, String context);
}
