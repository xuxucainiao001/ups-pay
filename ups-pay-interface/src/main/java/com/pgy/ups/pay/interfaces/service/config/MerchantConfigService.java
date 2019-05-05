package com.pgy.ups.pay.interfaces.service.config;

import java.util.List;

import com.pgy.ups.pay.interfaces.entity.MerchantConfigEntity;

/**
 * 
 * @author 来源商户业务层
 *
 */
public interface MerchantConfigService {
    
	/**
	 * 查询来源商户公钥信息
	 * @param fromSystem
	 * @return
	 */
	String queryMerchantPublicKey(String fromSystem);
	
	/**
	 * 查询来源商户私钥信息
	 * @param fromSystem
	 * @return
	 */
	String queryMerchantPrivateKey(String fromSystem);
	
	/**
	 * 查询来源商户信息
	 */
	
	MerchantConfigEntity findMerchant(String fromSystem);
    
	/**
	 * 查询所有商户信息
	 * @return
	 */
	List<MerchantConfigEntity> queryAvaliableMerchantList();

}
