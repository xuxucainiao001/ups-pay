package com.pgy.ups.pay.interfaces.worker;

public interface Worker<T> {
	
	void doWorker();
	
	T getWorkerResult();

}
