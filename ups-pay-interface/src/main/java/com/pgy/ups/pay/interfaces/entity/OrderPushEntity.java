package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pgy.ups.pay.interfaces.model.Model;

/**
  *  订单推送实体
 * @author 墨凉
 *
 */
@Entity
@Table(name="ups_t_order_push")
public class OrderPushEntity extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9061750972046017248L;
	
	@Id
	private Long id;
	
	@Column(name="pay_channel")
	private String payChannel;
	
	@Column(name="order_id",columnDefinition="BIGINT")
	private Long orderId;
	
	@Column(name="order_code")
	private String orderCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="order_create_time")
	private Date orderCreateTime;
	
	@Column(name="merchant_code")
	private String merchantCode;
	
	@Column(name="order_type")
	private String orderType;
	
	@Column(name="order_status")
	private String orderStatus;
	
	@Column(name="channel_result_code")
	private String channelResultCode;
	
	@Column(name="channel_result_msg")
	private String channelResultMsg;
	
	@Column(name="notify_url")
	private String notifyUrl;
	
	@Column(name="push_status",columnDefinition="char")
	private String pushStatus;
	
	@Column(name="push_count",columnDefinition="int")
	private Integer pushCount;
	
	@Column(name="query_count",columnDefinition="int")
	private Integer queryCount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="next_query_time")
	private Date nextQueryTime;
	
	@Column(name="create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Column(name="update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	@Column(name="requery",columnDefinition="BIT")
	private Boolean requery;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getChannelResultCode() {
		return channelResultCode;
	}

	public void setChannelResultCode(String channelResultCode) {
		this.channelResultCode = channelResultCode;
	}

	public String getChannelResultMsg() {
		return channelResultMsg;
	}

	public void setChannelResultMsg(String channelResultMsg) {
		this.channelResultMsg = channelResultMsg;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getPushStatus() {
		return pushStatus;
	}

	public void setPushStatus(String pushStatus) {
		this.pushStatus = pushStatus;
	}

	public Integer getPushCount() {
		return pushCount;
	}

	public void setPushCount(Integer pushCount) {
		this.pushCount = pushCount;
	}


	public Date getNextQueryTime() {
		return nextQueryTime;
	}

	public void setNextQueryTime(Date nextQueryTime) {
		this.nextQueryTime = nextQueryTime;
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

	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public Integer getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(Integer queryCount) {
		this.queryCount = queryCount;
	}

	public Boolean getRequery() {
		return requery;
	}

	public void setRequery(Boolean requery) {
		this.requery = requery;
	}
	
	
    
}
