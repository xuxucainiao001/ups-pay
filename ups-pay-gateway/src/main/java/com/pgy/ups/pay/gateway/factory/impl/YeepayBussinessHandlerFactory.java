package com.pgy.ups.pay.gateway.factory.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pgy.ups.pay.gateway.factory.BussinessHandlerFactory;
import com.pgy.ups.pay.interfaces.pay.*;
import org.springframework.stereotype.Service;

@Service
public class YeepayBussinessHandlerFactory implements BussinessHandlerFactory {

   /* @Reference(group = "yeepayBindCard")
    private BindCardService bindCardService;

    @Reference(group = "yeepaySignature")
    private SignatureService signatureService;

    @Reference(group = "yeepayProtocolSignature")
    private  ProtocolSignatureService protocolSignatureService;

    @Reference(group = "yeepayProtocolBindCard")
    private  ProtocolBindCardService protocolBindCardService;*/

    @Reference(group = "yeepayPay")
    private  PayService  payService;

    @Reference(group = "yeepayCollect")
    private  CollectService collectService;

    @Reference(group = "yeepayProtocolCollect")
    private  ProtocolCollectService protocolCollectService;

    @Override
    public SignatureService getSignatureService() {
        return null;
    }

    @Override
    public BindCardService getBindCardService() {
        return null;
    }

    @Override
    public ProtocolBindCardService getProtocolBindCardService() {
        return null;
    }

    @Override
    public ProtocolSignatureService getProtocolSignatureService() {
        return null;
    }

    @Override
    public PayService getPayService() {
        return payService;
    }

    @Override
    public CollectService getCollectService() {
        return collectService;
    }

    @Override
    public ProtocolCollectService getProtocolCollectService() {
        return protocolCollectService;
    }
}
