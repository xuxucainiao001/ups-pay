package com.pgy.ups.pay.gateway.factory;


import com.pgy.ups.pay.commom.constants.OrderType;
import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class HandlerFactory {

    @Resource
    private  ChannelHandlerFactory channelHandlerFactory;


    public UpsResultModel signature(UpsSignatureParamModel model){
        BussinessHandlerFactory factory = getBussinessHandlerFactory(model.getPayChannel());
        if(OrderType.SIGNATRUE.equals(model.getOrderType()) ){
          return   factory.getSignatureService().signature(model);
        }else if(OrderType.PROTOCOL_SIGNATRUE.equals(model.getOrderType()) ){
            return  factory.getProtocolSignatureService().protocolSignature(model);
        }
        return  null;
    }

    public UpsResultModel bindCard(UpsBindCardParamModel model){
        BussinessHandlerFactory factory = getBussinessHandlerFactory(model.getPayChannel());
        if(OrderType.BINDCARD.equals(model.getOrderType()) ){
             return   factory.getBindCardService().bindCard(model);
        }else if(OrderType.PROTOCOL_BINDCARD.equals(model.getOrderType())){
            return  factory.getProtocolBindCardService().protocolBindCard(model);
        }
        return  null;
    }


    public UpsResultModel pay(UpsOrderEntity upsOrderEntity){
        BussinessHandlerFactory factory = getBussinessHandlerFactory(upsOrderEntity.getPayChannel());
          return   factory.getPayService().pay(upsOrderEntity);
    }



    public UpsResultModel collect(UpsOrderEntity upsOrderEntity){
        BussinessHandlerFactory factory = getBussinessHandlerFactory(upsOrderEntity.getPayChannel());
        if(OrderType.COLLECT.equals(upsOrderEntity.getOrderType()) ){
            return   factory.getCollectService().collet(upsOrderEntity);
        }else if(OrderType.PROTOCOL_COLLECT.equals(upsOrderEntity.getOrderType()) ){
            return  factory.getProtocolCollectService().protocolColle(upsOrderEntity);
        }
            return  null;
    }

    private BussinessHandlerFactory getBussinessHandlerFactory(String payChannle){
        return  channelHandlerFactory.getChannel(payChannle);
    }

}
