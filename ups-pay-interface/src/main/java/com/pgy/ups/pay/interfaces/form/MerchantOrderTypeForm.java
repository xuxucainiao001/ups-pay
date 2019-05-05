package com.pgy.ups.pay.interfaces.form;

import java.util.Date;

public class MerchantOrderTypeForm extends BaseForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7012778222353400375L;
	
	private Long id;

    private  Long merchantId;

    private Long orderTypeId;
    
    private String defaultPayChannel; 
    
    private String routeStatus;

    private Date startTime;

    private Date endTime;
    
    private Date updateTime;
    
    private String updateUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;
	}
	
	public String getDefaultPayChannel() {
		return defaultPayChannel;
	}

	public void setDefaultPayChannel(String defaultPayChannel) {
		this.defaultPayChannel = defaultPayChannel;
	}

	public String getRouteStatus() {
		return routeStatus;
	}

	public void setRouteStatus(String routeStatus) {
		this.routeStatus = routeStatus;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
}
