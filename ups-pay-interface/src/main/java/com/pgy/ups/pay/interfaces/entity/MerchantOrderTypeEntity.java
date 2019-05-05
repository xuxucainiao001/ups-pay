package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.alibaba.fastjson.annotation.JSONField;
import com.pgy.ups.pay.interfaces.model.Model;

@Entity
@Table(name="ups_t_merchant_order_type")
public  class MerchantOrderTypeEntity extends Model {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6386165233011450661L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="merchant_id")
    @JSONField(serialize=false)
    private  MerchantConfigEntity merchantConfigEntity;

    @ManyToOne
    @JoinColumn(name="order_type_id")
    private UpsOrderTypeEntity upsOrderTypeEntity;
    
    @Column(name="default_pay_channel")
    private String defaultPayChannel; 
    
    @Column(name="route_status",columnDefinition="char")
    private String routeStatus;


    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    
    @Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
    
    @Column(name = "update_user")
    private String updateUser;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public MerchantConfigEntity getMerchantConfigEntity() {
		return merchantConfigEntity;
	}

	public void setMerchantConfigEntity(MerchantConfigEntity merchantConfigEntity) {
		this.merchantConfigEntity = merchantConfigEntity;
	}

	public UpsOrderTypeEntity getUpsOrderTypeEntity() {
		return upsOrderTypeEntity;
	}

	public void setUpsOrderTypeEntity(UpsOrderTypeEntity upsOrderTypeEntity) {
		this.upsOrderTypeEntity = upsOrderTypeEntity;
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
