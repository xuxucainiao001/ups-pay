package com.pgy.ups.pay.quartz.yeepay;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pgy.ups.pay.interfaces.query.OrderQueryService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;

/**
 * 易宝轮询任务
 * 
 * @author 墨凉
 *
 */
@Component
@DisallowConcurrentExecution
public class YeepayQueryQuartzTask extends ConfigQuartzTask {

	@Reference(group = "yeepayPay")
	private OrderQueryService<String> yeepayQueryPay;
	
	@Reference(group = "yeepayCollect")
	private OrderQueryService<String> yeepayQueryCollect;

	@Override
	protected void runTask() {
		// 异步代付轮询		
		yeepayQueryPay.doMultiQuery();
		
		// 异步代扣轮询
		yeepayQueryCollect.doMultiQuery();
	}

}
