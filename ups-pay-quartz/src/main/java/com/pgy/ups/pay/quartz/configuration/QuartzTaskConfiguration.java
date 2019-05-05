package com.pgy.ups.pay.quartz.configuration;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pgy.ups.pay.interfaces.quartz.QuartzTask;

/**
 * quartz 定时器任务管理类
 * 
 * @author 墨凉
 *
 */
@Configuration
public class QuartzTaskConfiguration {

	private Logger logger = LoggerFactory.getLogger(QuartzTaskConfiguration.class);

	/**
	 * 调度器工厂Bean
	 */
	@Bean
	public Scheduler schedulerFactory (Scheduler s,QuartzTask... quartzTasks) {

		try {
			//ClassPathResource classPathResource = new ClassPathResource("quartz.properties");
			//Properties p = new Properties();
			//p.load(classPathResource.getInputStream());
			//SchedulerFactory schedulerFactory = new StdSchedulerFactory(p);
			//Scheduler s = schedulerFactory.getScheduler();
			for (int i=0;i<quartzTasks.length ;i++) {
				// 创建触发器
				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity("trigger-" + quartzTasks[i].getTaskName(),
								"trigger-group-" + quartzTasks[i].getTaskGoupName())
						.withSchedule(CronScheduleBuilder.cronSchedule(quartzTasks[i].getCronExpress())).build();

				// 创建任务
				JobDetail jobDetail = JobBuilder.newJob(quartzTasks[i].getClass())
						.withIdentity("job-" + quartzTasks[i].getTaskName(), "job-group-" + quartzTasks[i].getTaskGoupName())
						.build();

				s.scheduleJob(jobDetail, trigger);

			}
			
			if (!s.isStarted()) {
				s.start();
			}
			return s;
		} catch (Exception e) {
			this.logger.error("加载quartz.properties或创建Scheduler失败", e);
			throw new Error(e);
		}

	}


}
