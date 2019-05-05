package com.pgy.ups.pay.interfaces.quartz;

import org.quartz.Job;

public abstract class QuartzTask implements Job {

	public abstract String getTaskName();

	public abstract String getTaskGoupName();

	public abstract String getCronExpress();
	
	public abstract String getRunIp();

}
