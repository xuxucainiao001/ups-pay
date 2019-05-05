package com.pgy.ups.pay.interfaces.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;

/**
 * Ups返回结果
 * @author 墨凉
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpsResultModel extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2687923683243786130L;
	
	/**
	 * 返回编码
	 * @See UpsResultCode.java
	 */
	private String code;

	/**
	 * 返回信息
	 */
	private String message;

	private Object result;
	
	private String sign;
	
	public UpsResultModel() {}
	
	public UpsResultModel(String code,String message) {
		this.code=code;
		this.message=message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public UpsResultModel(UpsResultEnum upsResultEnum) {
		this.code=upsResultEnum.getCode();
		this.message=upsResultEnum.getMessage();
	}

	public  UpsResultModel(UpsResultEnum upsResultEnum,Object result) {
		this(upsResultEnum);
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public  UpsResultModel(String code, String message, Object result) {
		this(code,message);
		this.result = result;
	}


}
