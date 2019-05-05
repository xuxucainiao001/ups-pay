package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pgy.ups.pay.interfaces.model.Model;



@Entity
@Table(name="ups_t_log")
public class UpsLogEntity extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3034307866806481451L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="from_system")
	private String fromSystem;
	
	@Column(name="request_url")
	private String requestUrl;
	
	@Column(name="business_flow_num")
	private String businessFlowNum;
	
	@Column(name="business_param",columnDefinition="text")
	private String businessParam;
	
	@Column(name="return_param",columnDefinition="text")
	private String returnParam;
	
	@Column(name="code",columnDefinition="char")
	private String code;
	
	@Column(name="message")
	private String message;
	
	@Column(name="order_id")
	private String orderId;
	
	@Column(name="create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Column(name="update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

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

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getBusinessFlowNum() {
		return businessFlowNum;
	}

	public void setBusinessFlowNum(String businessFlowNum) {
		this.businessFlowNum = businessFlowNum;
	}

	public String getBusinessParam() {
		return businessParam;
	}

	public void setBusinessParam(String businessParam) {
		this.businessParam = businessParam;
	}

	public String getReturnParam() {
		return returnParam;
	}

	public void setReturnParam(String returnParam) {
		this.returnParam = returnParam;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

}
