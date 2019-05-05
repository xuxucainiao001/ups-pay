package com.pgy.ups.pay.quartz.controller;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.interfaces.entity.QuartzConfigEntity;
import com.pgy.ups.pay.interfaces.service.config.UpsQuartzConfigService;


@Controller
@RequestMapping("/scheduler")
public class SchedulerController {

	private Logger logger = LoggerFactory.getLogger(SchedulerController.class);

	@Resource(name = "quartzScheduler")
	private Scheduler scheduler;

	@Resource
	private UpsQuartzConfigService upsQuartzConfigService;

	/**
	 * 关闭定时器任务任务
	 * 
	 * @param taskGroupName
	 * @param taskName
	 */
	@ResponseBody
	@GetMapping("/stop/{className}")
	public String stopTask(@PathVariable("className") String className) { 
		if (StringUtils.isAnyBlank(className)) {
			logger.info("任务类名不能为空");
			throw new BussinessException("任务类名不能为空");
		}
		logger.info("关闭指定任务{}", className);
		upsQuartzConfigService.stopQuartzTask(className);
		return "success";
	}
	
	/**
	  *  开始指定任务
	 * @param className
	 * @return
	 */
	@ResponseBody
	@GetMapping("/start/{className}")
	public String startTask(@PathVariable("className") String className) {
		if (StringUtils.isAnyBlank(className)) {
			logger.info("任务类名不能为空");
			throw new BussinessException("任务类名不能为空");
		}
		logger.info("打开指定任务{}", className);
		upsQuartzConfigService.startQuartzTask(className);
		return "success";
	}

	/**
	  * 重置定时器任务
	 * 
	 * @param className
	 * @return
	 * @throws SchedulerException
	 */
	@ResponseBody
	@GetMapping("/reset/{className}")
	public String reSetTask(@PathVariable("className")String className) throws SchedulerException {
		QuartzConfigEntity qce = upsQuartzConfigService.queryQuartzConfigByClassName(className);
		if (Objects.isNull(qce)) {
			throw new BussinessException("没有找到该任务");
		}
		//获取当前任务
		JobKey jobKey=JobKey.jobKey("job-" + qce.getTaskName(), "job-group-" + qce.getTaskGroupName());
		JobDetail oldJobDetail=scheduler.getJobDetail(jobKey);
		Class<? extends Job > job=oldJobDetail.getJobClass();
		//删除任务
		scheduler.deleteJob(jobKey);
		// 创建新触发器
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger-" +qce.getTaskName(),
						"trigger-group-" + qce.getTaskGroupName())
				.withSchedule(CronScheduleBuilder.cronSchedule(qce.getCronExpress())).build();
		
		// 创建新任务
		JobDetail jobDetail = JobBuilder.newJob(job)
				.withIdentity("job-" + qce.getTaskName(), "job-group-" + qce.getTaskGroupName())
				.build();
		//添加并开始任务
		scheduler.scheduleJob(jobDetail, trigger);
		return "success";
	}

}
