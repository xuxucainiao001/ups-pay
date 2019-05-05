/**
 * 
 */
package com.pgy.ups.pay.quartz.interfaces;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.IPUtils;
import com.pgy.ups.pay.interfaces.entity.QuartzConfigEntity;
import com.pgy.ups.pay.interfaces.quartz.QuartzTask;
import com.pgy.ups.pay.interfaces.service.config.UpsQuartzConfigService;

/**
 * 可配置的定时器任务抽象类 模板方法模式
 * 
 * @author 墨凉
 *
 */
public abstract class ConfigQuartzTask extends QuartzTask {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private UpsQuartzConfigService upsQuartzConfigService;

	private QuartzConfigEntity qce;

	@PostConstruct
	public void initConfigQuartzTask() {
		// 加载的时候使用
		qce = upsQuartzConfigService.queryQuartzConfigByClassName(this.getClass().getSimpleName());
		if (Objects.isNull(qce)) {
			throw new BussinessException("定时器任务配置信息不可为null");
		}
	}

	@Override
	public String getTaskName() {
		return qce.getTaskName();
	}

	@Override
	public String getTaskGoupName() {
		return qce.getTaskGroupName();
	}

	@Override
	public String getCronExpress() {
		return qce.getCronExpress();
	}

	@Override
	public String getRunIp() {
		return qce.getRunIp();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 任务调度时重新查询
		QuartzConfigEntity config = upsQuartzConfigService
				.queryQuartzConfigByClassName(this.getClass().getSimpleName());
		// 若任务未开启则不运行
		if (!config.getActive()) {
			return;
		}
		String runIp = config.getRunIp();
		String localIp = IPUtils.getLocalIp();
		String taskName = config.getTaskName();
		if (Objects.equals(localIp, runIp)) {
			runTask();
			logger.info("定时器执行成功,定时器任务{}指定运行机器IP:{},当前机器IP:{}", taskName,runIp,localIp);
			return;
		}
		logger.info("定时器不执行,定时器任务{}指定运行机器IP:{},当前机器IP:{}", taskName,runIp,localIp);
	}

	protected abstract void runTask();

}
