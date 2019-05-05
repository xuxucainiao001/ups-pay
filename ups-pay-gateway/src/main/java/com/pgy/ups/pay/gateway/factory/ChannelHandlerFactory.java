package com.pgy.ups.pay.gateway.factory;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ChannelHandlerFactory {

    @Resource
    private BussinessHandlerFactory  baofooBussinessHandlerFactory;


    @Resource
     private BussinessHandlerFactory  yeepayBussinessHandlerFactory;

    public BussinessHandlerFactory getChannel(String channel){
        if("baofoo".equals(channel)){
            return baofooBussinessHandlerFactory;
        }else if("yeepay".equals(channel)){
            return yeepayBussinessHandlerFactory;
        }
        return  null;
    }

}
