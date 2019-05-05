package com.pgy.ups.pay.commom.constants;

import java.util.HashMap;
import java.util.Map;

import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;

public final class OrderType {

	public static final Map<String,String> BindCardMap = new HashMap<>();

	public static final Map<String,String> SignatureMap = new HashMap<>();

	static {
		SignatureMap.put(SignTypeEnum.AUTH.getCode(),"Signature");
		SignatureMap.put(SignTypeEnum.PROTOCOL.getCode(),"ProtocolSignature");
		BindCardMap.put(SignTypeEnum.AUTH.getCode(),"BindCard");
		BindCardMap.put(SignTypeEnum.PROTOCOL.getCode(),"ProtocolBindCard");
	}


	//代付
	public static final String PAY="Pay";
	
	//代扣
	public static final String COLLECT="Collect";
		
	//签约
	public static final String SIGNATRUE="Signature";
	
	//绑卡
	public static final String BINDCARD="BindCard";
	
	//转账
	public static final String TRANSFER="Transfer";
	
	//协议支付
	public static final String PROTOCOL_COLLECT="ProtocolCollect";

	//协议签约
	public static final String PROTOCOL_SIGNATRUE="ProtocolSignature";

	//协议绑卡
	public static final String PROTOCOL_BINDCARD="ProtocolBindCard";


}
