package com.pgy.ups.pay.interfaces.factory;

/**
 * 
 * @author 墨凉
 * 
 * 通过R类型参数返回T
 *
 * @param <T>
 * @param <R>
 */
public interface BusinessFactory<T,R> {
	
	T getInstance(R r);

}
