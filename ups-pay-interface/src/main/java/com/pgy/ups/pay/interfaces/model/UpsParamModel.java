package com.pgy.ups.pay.interfaces.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.pgy.ups.pay.interfaces.common.Signable;


public abstract class UpsParamModel extends Model implements Signable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7016931404301904100L;

	/* 美期 Meiqi 米融 Mirong 秒呗 Miaobei 多呗 Duobei 迅到 Xundao*/
	private String merchantCode;

	/* 用户编码 */
	@NotBlank(message = "用户编码不能为空")
	private String userNo;

	/* 真实姓名 */
	@Size(max = 50, min = 1, message = "真实姓名长度必须在{min}和{max}之间")
	private String realName;

	/* 银行卡号 */
	@NotBlank(message = "银行卡号不能为空")
	private String bankCard;

	/* 身份证号 */
	@Size(max = 18, min = 18, message = "身份证号必须为18位")
	private String identity;

	/* 银行编码 */
	@NotBlank(message = "银行编码不能为空")
	private String bankCode;

	/* 手机号码 */
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^[1][0-9]{10}$", message = "手机号码必须是以1开头的11位数字")
	private String phoneNo;

	/* 回调地址 */
	@NotBlank(message = "回调地址不能为空")
	//@Pattern(regexp = "^(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?$", message = "回调地址必须以http(s)开头")
	private String notifyUrl;
	
	/* 业务订单号 */
	@NotBlank(message = "业务流水号不能为空")
	private String businessFlowNum;
	
	/*签名*/
	@NotBlank(message = "签名不能为空")
	private String sign;
		
	/*备注*/
	private String remark;
	
	/*支付路由*/
	private String payChannel;
	
	/*业务类型*/
	private String businessType;
	
	/*订单类型*/
	private String orderType;
	
	

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBusinessFlowNum() {
		return businessFlowNum;
	}

	public void setBusinessFlowNum(String businessFlowNum) {
		this.businessFlowNum = businessFlowNum;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
