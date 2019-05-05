package com.pgy.ups.pay.gateway.configuration;

import java.util.concurrent.Executor;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync(proxyTargetClass=true) // 开启对异步任务的支持
public class ThreadAsynConfiguration implements AsyncConfigurer {
	
	private Logger logger=LoggerFactory.getLogger(ThreadAsynConfiguration.class);
	
	@Bean
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor(); 
		// 设置核心线程数
		threadPool.setCorePoolSize(4);
		// 设置最大线程数
		threadPool.setMaxPoolSize(8); 
		// 线程池所使用的缓冲队列
		threadPool.setQueueCapacity(20000); 
		// 等待任务在关机时完成--表明等待所有线程执行完
		threadPool.setWaitForTasksToCompleteOnShutdown(true); 
		// 等待时间 （默认为0，此时立即停止），并等待xx秒后强制停止
		threadPool.setAwaitTerminationSeconds(120); 
		// 当对于核心线程数量时，多余线程5秒后回收
		threadPool.setKeepAliveSeconds(10);
		// 线程名称前缀s
		threadPool.setThreadNamePrefix("Ups-Pay线程池线程Async-");
	    // 主线程处理任务
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 初始化线程
		threadPool.initialize();
		
		logger.info("UPS线程池统一配置完成！");
		
		return threadPool;
	}
}
