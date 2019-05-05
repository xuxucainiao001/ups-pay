package com.pgy.ups.pay.quartz.baofoo;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceSmsWarningService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;


/*@Component
@DisallowConcurrentExecution*/
public class BaofooBalanceSmsWarningQuartzTask extends ConfigQuartzTask{

    @Resource
    UpsBalanceSmsWarningService upsBalanceSmsWarningService;


    @Override
    protected void runTask() {
        upsBalanceSmsWarningService.balanceSmsWarning();
    }
}
