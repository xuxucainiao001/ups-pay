package com.pgy.ups.pay.interfaces.cache;

import java.io.Serializable;
import java.util.Map;

public interface Cacheable<T extends Serializable> {
	 
	/**
	 * 
	 * @return redis 缓存key
	 */
	 String getCacheKeyname();
	 
	 /**
	  * 获取缓存数据
	  * @return
	  */
	 
	 Map<String, T> getCacheableData();

}
