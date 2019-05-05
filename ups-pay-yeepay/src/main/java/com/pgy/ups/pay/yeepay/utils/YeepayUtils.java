package com.pgy.ups.pay.yeepay.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;

public class YeepayUtils {

	private static Logger logger = LoggerFactory.getLogger(YeepayUtils.class);

	public static YopResponse submit(String url, YopRequest yr) throws IOException {
		Long startTime = System.currentTimeMillis();
		YopResponse response = YopClient3.postRsa(url, yr);
		logger.info("易宝调用接口时间:{}", System.currentTimeMillis() - startTime);
		return response;
	}

}
