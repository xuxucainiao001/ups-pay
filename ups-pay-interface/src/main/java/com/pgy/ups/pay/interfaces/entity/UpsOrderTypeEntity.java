package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pgy.ups.pay.interfaces.model.Model;

@Entity
@Table(name="ups_t_order_type")
public class UpsOrderTypeEntity extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3587829708118316456L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "order_type")
	private String orderType;
	
	@Column(name = "order_type_name")
	private String orderTypeName;
	
	@Column(name = "order_business_type")
	private String orderBusinessType;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

	@Column(name = "create_user")
	private String createUser;

	@Column(name = "update_user")
	private String updateUser;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "upsOrderTypeEntity")
	private List<MerchantOrderTypeEntity> merchantOrderTypeList;
	

	/*@ManyToMany(mappedBy="orderTypeList",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private List<MerchantConfigEntity> merchantConfigList;*/
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public String getOrderBusinessType() {
		return orderBusinessType;
	}

	public void setOrderBusinessType(String orderBusinessType) {
		this.orderBusinessType = orderBusinessType;
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

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public List<MerchantOrderTypeEntity> getMerchantOrderTypeList() {
		return merchantOrderTypeList;
	}

	public void setMerchantOrderTypeList(List<MerchantOrderTypeEntity> merchantOrderTypeList) {
		this.merchantOrderTypeList = merchantOrderTypeList;
	}

	
}
