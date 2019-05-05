package com.pgy.ups.pay.interfaces.service.config;

import com.pgy.ups.pay.interfaces.entity.QuartzConfigEntity;

/**
  *  定时器任务配置
 * @author 墨凉
 *
 */
public interface UpsQuartzConfigService {
	
	/**
	  * 通过类名查询定时器任务
	 * @param className
	 * @return
	 */
	public QuartzConfigEntity queryQuartzConfigByClassName(String className);
    
	/**
	  *  停止任务
	 * @param className
	 * @param taskName
	 */
	public void stopQuartzTask(String className);
    
	/**
	  *  开始任务
	 * @param className
	 * @param taskName
	 */
	public void startQuartzTask(String className);

}
