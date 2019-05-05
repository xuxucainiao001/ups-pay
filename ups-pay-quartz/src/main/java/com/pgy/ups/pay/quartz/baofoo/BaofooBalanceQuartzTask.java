package com.pgy.ups.pay.quartz.baofoo;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.service.balance.UpsBalaceConfigService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;


/*@Component
@DisallowConcurrentExecution*/
public class BaofooBalanceQuartzTask extends ConfigQuartzTask {

    @Resource
    UpsBalaceConfigService upsBalaceConfigService;

    @Override
    protected void runTask() {
        upsBalaceConfigService.balaceConfigeQuartzTask();
    }
}
