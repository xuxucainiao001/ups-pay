package com.pgy.ups.pay.interfaces.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;



/**
 * UPS订单实体
 * @author 墨凉
 *
 */
@Entity
@Table(name="ups_t_order")
public class UpsOrderEntity extends EncryptModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8305499426384251725L;
	
	@Id
	private Long id;
	
	@Column(name="ups_order_code")
	private String upsOrderCode;
	
	@Column(name="merchant_code")
	private String merchantCode;
	
	@Column(name="order_type")
	private String orderType;
	
	@Column(name="business_type")
	private String businessType;
	
	@Column(name="business_flow_num")
	private String businessFlowNum;
	
	@Column(name="user_no")
	private String userNo;
	
	@Column(name="real_name")
	private String realName;
	
	@Column(name="identity")
	private String identity;
	
	@Column(name="phone_no")
	private String phoneNo;
	
	@Column(name="bank_code")
	private String bankCode;
	
	@Column(name="bank_card")
	private String bankCard;
	
	@Column(name="amount",columnDefinition="decimal")
	private BigDecimal amount;
	
	@Column(name="pay_channel")
	private String payChannel;
	
	@Column(name="order_status")
	private String orderStatus;
	
	@Column(name="notify_url")
	private String notifyUrl;
	
	@Column(name="result_code")
	private String resultCode;
	
	@Column(name="result_message")
	private String resultMessage;
	
	@Column(name="remark")
	private String remark;
	
	@Column(name="create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Column(name="update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	@Transient
	@JsonIgnore
	private Map<String,Object> upsConfigDate;
	
	@Transient
	@JsonIgnore
	private UpsParamModel upsParamModel;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
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

	public Map<String, Object> getUpsConfigDate() {
		return upsConfigDate;
	}

	public void setUpsConfigDate(Map<String, Object> upsConfigDate) {
		this.upsConfigDate = upsConfigDate;
	}

	public String getUpsOrderCode() {
		return upsOrderCode;
	}

	public void setUpsOrderCode(String upsOrderCode) {
		this.upsOrderCode = upsOrderCode;
	}

	public UpsParamModel getUpsParamModel() {
		return upsParamModel;
	}

	public void setUpsParamModel(UpsParamModel upsParamModel) {
		this.upsParamModel = upsParamModel;
	}
	

}
