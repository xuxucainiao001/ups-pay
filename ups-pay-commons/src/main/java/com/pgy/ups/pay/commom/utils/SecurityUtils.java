package com.pgy.ups.pay.commom.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgy.ups.common.exception.ParamValidException;
import com.pgy.ups.common.utils.RSAUtils;
import com.pgy.ups.common.utils.ReflectUtils;
import com.pgy.ups.pay.interfaces.common.Signable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SecurityUtils {

	private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);


	/**
	 * 验签方法
	 * 
	 * @param upsParamModel
	 * @return
	 * @throws ParamValidException 
	 */
	public static void signVerification(Signable signable, String publicKey) throws ParamValidException {
		// 获取验签规则
		String[] sigeRule = signable.getSignRule();
		// 把model对象转为 key=value&key=value字符串形式
		String urlParam = ReflectUtils.objectToUrlParams(signable, sigeRule);
		String sign = signable.getSign();
		if(StringUtils.isBlank(sign)) {
			throw new ParamValidException("传入的签名不能为空！");
		}
		logger.info("公钥：{}", publicKey);
		logger.info("签名：{}", sign);
		logger.info("需要验签的url参数：{}", urlParam);		
		if(RSAUtils.verfySign(publicKey, urlParam, sign)) {
			logger.info("验签通过！");
			return ;
		}
		throw new ParamValidException("验签没有通过！");
	}

	/**
   	  *  签名
	 * 
	 * @param upsParamModel
	 * @param publicKey
	 */
	public static String sign(Signable signable, String privateKey) {
		// 获取验签规则
		String[] sigeRule = signable.getSignRule();
		// 把model对象转为 key=value&key=value字符串形式
		String urlParam = ReflectUtils.objectToUrlParams(signable, sigeRule);
		return RSAUtils.Sign(privateKey, urlParam);
	}

	public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (str == null) {
			return null;
		}
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(str.getBytes("UTF-8"));
		byte[] digest = md5.digest();
		StringBuffer hexString = new StringBuffer();
		String strTemp;
		for (int i = 0; i < digest.length; i++) {
			strTemp = Integer.toHexString((digest[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
			hexString.append(strTemp);
		}
		return hexString.toString();
	}

}
