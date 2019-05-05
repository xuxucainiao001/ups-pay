package com.pgy.ups.pay.gateway.factory.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pgy.ups.pay.gateway.factory.BussinessHandlerFactory;
import com.pgy.ups.pay.interfaces.pay.*;
import org.springframework.stereotype.Service;

@Service
public class BaofooBussinessHandlerFactory implements BussinessHandlerFactory {

    @Reference(group = "baofooBindCard")
    private BindCardService bindCardService;

    @Reference(group = "baofooSignature")
    private SignatureService signatureService;

    @Reference(group = "baofooProtocolSignature")
    private  ProtocolSignatureService protocolSignatureService;

    @Reference(group = "baofooProtocolBindCard")
    private  ProtocolBindCardService protocolBindCardService;

    @Reference(group = "baofooPay")
    private  PayService  payService;

    @Reference(group = "baofooCollect")
    private  CollectService collectService;

    @Reference(group = "baofooProtocolCollect")
    private  ProtocolCollectService protocolCollectService;


    @Override
    public SignatureService getSignatureService() {
        return signatureService;
    }

    @Override
    public BindCardService getBindCardService() {
        return bindCardService;
    }

    @Override
    public ProtocolBindCardService getProtocolBindCardService() {
        return protocolBindCardService;
    }

    @Override
    public ProtocolSignatureService getProtocolSignatureService() {
        return protocolSignatureService;
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
