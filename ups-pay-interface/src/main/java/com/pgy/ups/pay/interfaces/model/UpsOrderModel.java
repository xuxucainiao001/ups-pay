package com.pgy.ups.pay.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pgy.ups.pay.interfaces.common.Signable;

/**
 * UPS订单实体
 * 
 * @author 墨凉
 *
 */

public class UpsOrderModel extends Model implements Signable {

	private static final String[] SIGN_RULE = new String[] { "id", "upsOrderCode", "merchantCode", "orderType",
			"businessType","businessFlowNum", "userNo", "realName", "identity", "phoneNo", "bankCode", "bankCard", "amount", "payChannel",
			"orderStatus" };

	/**
	 * 
	 */
	private static final long serialVersionUID = 8305499426384251725L;

	private Long id;

	private String upsOrderCode;

	private String fromSystem;

	private String orderType;

	private String businessType;

	private String businessFlowNum;

	private String userNo;

	private String realName;

	private String identity;

	private String phoneNo;

	private String bankCode;

	private String bankCard;

	private BigDecimal amount;

	private String payChannel;

	private String orderStatus;

	private String notifyUrl;

	private String resultCode;

	private String resultMessage;

	private String remark;

	private Date createTime;

	private Date updateTime;

	private String sign;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromSystem() {
		return fromSystem;
	}

	public void setFromSystem(String fromSystem) {
		this.fromSystem = fromSystem;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessFlowNum() {
		return businessFlowNum;
	}

	public void setBusinessFlowNum(String businessFlowNum) {
		this.businessFlowNum = businessFlowNum;
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

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpsOrderCode() {
		return upsOrderCode;
	}

	public void setUpsOrderCode(String upsOrderCode) {
		this.upsOrderCode = upsOrderCode;
	}
    
	@JsonIgnore
	@Override
	public String[] getSignRule() {
		return SIGN_RULE;
	}

	@Override
	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
