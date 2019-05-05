package com.pgy.ups.pay.quartz.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync(proxyTargetClass=true) // 开启对异步任务的支持
public class ThreadAsynConfiguration implements AsyncConfigurer {
	
	@Bean
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor(); 
		// 设置核心线程数
		threadPool.setCorePoolSize(8);
		// 设置最大线程数
		threadPool.setMaxPoolSize(40); 
		// 线程池所使用的缓冲队列
		threadPool.setQueueCapacity(10000); 
		// 等待任务在关机时完成--表明等待所有线程执行完
		threadPool.setWaitForTasksToCompleteOnShutdown(true); 
		// 等待时间 （默认为0，此时立即停止），并等待xx秒后强制停止
		threadPool.setAwaitTerminationSeconds(60); 
		// 当对于核心线程数量时，多余线程5秒后回收
		threadPool.setKeepAliveSeconds(5);
		// 线程名称前缀s
		threadPool.setThreadNamePrefix("Ups-Pay-Quartz线程池线程Async-");
		// 初始化线程
		threadPool.initialize();
		return threadPool;
	}
}
