package com.pgy.ups.pay.gateway.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pgy.ups.pay.commom.utils.CacheUtils;

/**
 * 缓存
 *
 * @author 墨凉
 *
 */

@Controller
@RequestMapping("/cache")
public class CacheController {

	private Logger logger = LoggerFactory.getLogger(CacheController.class);

	@Resource
	private CacheUtils cacheUtils;

	@ResponseBody
	@RequestMapping("/refresh")
	public String refreshCache() {
		try {
			cacheUtils.initCacheUtils();
			return "ok!";
		} catch (Exception e) {
			logger.error("刷新缓存发生异常“{}", e);
		}
		return "fail!";
	}

}
