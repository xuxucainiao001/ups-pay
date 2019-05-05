package com.pgy.ups.pay.commom.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.commom.dao.UpsSmsChannelDao;
import com.pgy.ups.pay.interfaces.entity.SmsSendResponse;
import com.pgy.ups.pay.interfaces.entity.UpsSmsChannelEntity;
import com.pgy.ups.pay.interfaces.service.sms.UpsSmsChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class UpsSmsChannelServiceImpl implements UpsSmsChannelService {



    private static final Logger logger = LoggerFactory.getLogger(UpsSmsChannelServiceImpl.class);

    @Resource
    private UpsSmsChannelDao upsSmsChannelDao;


    @Override
    public SmsSendResponse sendMessage(String phone, String context) {
        //查询创蓝短信通道

        UpsSmsChannelEntity smsChannelDo = upsSmsChannelDao.findBySmsUserfulCode("NORMAL");
        if (smsChannelDo == null) {

        }
        //发送短信
        JSONObject requestJson = new JSONObject();
        requestJson.put("account",smsChannelDo.getAccount());
        requestJson.put("password",smsChannelDo.getPassword());
        requestJson.put("msg",context);
        requestJson.put("phone",phone);
        requestJson.put("report","true");
        logger.info("发送短信传参:{}",requestJson);
        String response = null;
        try {
            response = OkHttpUtil.post(smsChannelDo.getRequestUrl(), requestJson.toString());
        } catch (IOException e) {

        }
        logger.info("发送短信返回参数:{}",response);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
        return smsSingleResponse;
    }
}
