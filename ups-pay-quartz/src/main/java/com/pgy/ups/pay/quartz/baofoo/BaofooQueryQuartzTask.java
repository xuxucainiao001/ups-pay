package com.pgy.ups.pay.quartz.baofoo;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pgy.ups.pay.interfaces.query.OrderQueryService;
import com.pgy.ups.pay.quartz.interfaces.ConfigQuartzTask;

/**
 * 宝付轮询任务
 * 
 * @author 墨凉
 *
 */
@Component
@DisallowConcurrentExecution
public class BaofooQueryQuartzTask extends ConfigQuartzTask {

	@Reference(group = "baofooPay")
	private OrderQueryService<String> baofooQueryPay;

	@Reference(group = "baofooCollect")
	private OrderQueryService<String> baofooQueryCollect;

	@Reference(group = "baofooProtocolCollect")
	private OrderQueryService<String> baofooQueryProtocolCollect;

	@Override
	protected void runTask() {
		// 异步代付轮询
		baofooQueryPay.doMultiQuery();

		// 异步代扣轮询
		baofooQueryCollect.doMultiQuery();

		// 异步协议代扣轮询
		baofooQueryProtocolCollect.doMultiQuery();

	}

}
