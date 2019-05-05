package com.pgy.ups.pay.yeepay.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		threadPool.setCorePoolSize(8);
		// 设置最大线程数
		threadPool.setMaxPoolSize(40); 
		// 线程池所使用的缓冲队列
		threadPool.setQueueCapacity(10000); 
		// 等待任务在关机时完成--表明等待所有线程执行完
		threadPool.setWaitForTasksToCompleteOnShutdown(true); 
		// 等待时间 （默认为0，此时立即停止），并等待180秒后强制停止
		threadPool.setAwaitTerminationSeconds(180); 
		// 当对于核心线程数量时，多余线程10秒后回收
		threadPool.setKeepAliveSeconds(10);
		// 线程名称前缀s
		threadPool.setThreadNamePrefix("Ups-Pay-Yeepay线程池线程Async-");
	    // 主线程处理任务
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 初始化线程
		threadPool.initialize();
		
		logger.info("UPS-PAY-BAOFOO线程池统一配置完成！");
		
		return threadPool;
	}

	@Bean("threadPoolExecutor")
	public ThreadPoolExecutor threadPoolExcutor(){
		return new ThreadPoolExecutor(8,40,10, TimeUnit.SECONDS,new LinkedBlockingQueue<>(1000),new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy() );
	}
}
