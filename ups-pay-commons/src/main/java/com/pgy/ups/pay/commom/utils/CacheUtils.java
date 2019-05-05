package com.pgy.ups.pay.commom.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.utils.RedisUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;

/**
 * UPS缓存模块
 * 
 * @author acer
 *
 */
@Component
public class CacheUtils {

	@Resource
	private RedisUtils redisUtils;

	/**
	 * 所有Cacheable接口
	 */
	@Resource
	private List<Cacheable<? extends Serializable>> list;

	/**
	 * 预加载所有Cacheable内容
	 */
	public void initCacheUtils() {
		for (Cacheable<? extends Serializable> c : list) {
			String keyname = c.getCacheKeyname();
			Map<String, ? extends Serializable> cache = c.getCacheableData();
			deleteCahce(keyname);
			setCacheByRediskeyname(keyname, cache);
			
		}
	}

	private void deleteCahce(String keyname) {
		redisUtils.delete(keyname);		
	}

	/**
	 * 获取缓存
	 * 
	 * @param keyName
	 * @return
	 */
	public <T> Map<String, T> getCacheByRediskeyname(String rediskeyName, Class<T> clazz) {
		Map<String, String> jsonMap = redisUtils.getMap(rediskeyName, String.class);
		Map<String, T> cache = new LinkedHashMap<>();
		for (Entry<String, String> entry : jsonMap.entrySet()) {
			cache.put(entry.getKey(), JSONObject.parseObject(entry.getValue(), clazz));
		}
		return cache;
	}

	/**
	 * 获取缓存
	 * 
	 * @param keyName
	 * @return
	 */
	public <T> T getCacheByRediskeynameAndKey(String rediskeyName, String key, Class<T> clazz) {
		Map<String, String> map = redisUtils.getMap(rediskeyName, String.class);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		String josnStr=MapUtils.getString(map, key);
		return JSON.parseObject(josnStr, clazz);
	}

	/**
	 * 更新缓存
	 * 
	 * @param keyName
	 * @param cache
	 * @return
	 */
	public <T> boolean setCacheByRediskeyname(String rediskeyName, Map<String, T> cache) {
		if (Objects.isNull(cache)) {
			return false;
		}
		Map<String,String> map=new LinkedHashMap<>();
		for(Entry<String,T> entry:cache.entrySet()) {
			map.put(entry.getKey(), JSON.toJSONStringWithDateFormat(entry.getValue(),JSON.DEFFAULT_DATE_FORMAT));
		}
		return redisUtils.hmset(rediskeyName, map);
	}

	/**
	 * 更新缓存
	 * 
	 * @param keyName
	 * @param cache
	 * @return
	 */
	public <T> boolean setCacheByRediskeynameAndKey(String rediskeyName, String key, T value) {
		if (Objects.isNull(value)) {
			return false;
		}
		Map<String, String> cache = redisUtils.getMap(rediskeyName, String.class);
		if (MapUtils.isEmpty(cache)) {
			Map<String, String> cacheMap = new LinkedHashMap<>();
			cacheMap.put(key, JSON.toJSONStringWithDateFormat(value,JSON.DEFFAULT_DATE_FORMAT));
		} else {
			cache.put(key, JSON.toJSONStringWithDateFormat(value,JSON.DEFFAULT_DATE_FORMAT));
		}
		return setCacheByRediskeyname(rediskeyName, cache);
	}

	/**
	 * 统一生成map的key
	 * 
	 * @param param
	 * @return
	 */
	public static String generateKey(String... param) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < param.length; i++) {
			sb.append(param[i]);
			if (i != param.length - 1) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

}
