package com.pgy.ups.pay.gateway.factory;

import com.pgy.ups.pay.interfaces.pay.*;

public interface BussinessHandlerFactory {

	/**
	 *
	 *
	 * @return 签约
	 * */
	  SignatureService getSignatureService();



	/**
	 * 绑卡
	 *
	 *
	 */
	 BindCardService getBindCardService();

	/**
	 * 协议绑卡
	 *
	 *
	 */
	 ProtocolBindCardService getProtocolBindCardService();


	/**
	 * 协议签约
	 */
	 ProtocolSignatureService getProtocolSignatureService();


	 PayService getPayService();


	CollectService getCollectService();



	ProtocolCollectService getProtocolCollectService();






}
