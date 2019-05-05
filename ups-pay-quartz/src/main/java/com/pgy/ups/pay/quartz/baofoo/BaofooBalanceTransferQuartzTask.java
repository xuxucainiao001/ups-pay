package com.pgy.ups.pay.quartz.baofoo;

import javax.annotation.Resource;

import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;


/*@Component
@DisallowConcurrentExecution*/
public class BaofooBalanceTransferQuartzTask extends ConfigQuartzTask {

    @Resource
    private UpsBalanceTransferService balanceTransferService;

    @Override
    protected void runTask() {
        balanceTransferService.balanceTransferQuartz();
    }
}
