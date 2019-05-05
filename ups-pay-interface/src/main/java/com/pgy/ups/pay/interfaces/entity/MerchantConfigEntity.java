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

import com.alibaba.fastjson.annotation.JSONField;
import com.pgy.ups.pay.interfaces.model.Model;

/**
 * 来源商户配置表
 * 
 * @author 墨凉
 *
 */
@Entity
@Table(name = "ups_t_merchant_config")
public class MerchantConfigEntity extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1205296307960629481L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "merchant_code")
	private String merchantCode;

	@Column(name = "merchant_name")
	private String merchantName;

	@Column(name = "description")
	private String description;

	@Column(name = "available", columnDefinition = "bit")
	private Boolean available;
    	
	@Column(name="merchant_public_key",columnDefinition="text")
	private String merchantPublicKey;
    	
	@Column(name = "ups_private_key",columnDefinition="text")
	private String upsPrivateKey;

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

	/*@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "ups_t_merchant_order_type", joinColumns = @JoinColumn(name = "merchant_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "order_type_id", referencedColumnName = "id"))
	private List<UpsOrderTypeEntity> orderTypeList;*/
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "merchantConfigEntity")
	@JSONField(serialize=false)  
	private List<MerchantOrderTypeEntity> merchantOrderTypeList;

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

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}


	public String getMerchantPublicKey() {
		return merchantPublicKey;
	}

	public void setMerchantPublicKey(String merchantPublicKey) {
		this.merchantPublicKey = merchantPublicKey;
	}

	public String getUpsPrivateKey() {
		return upsPrivateKey;
	}

	public void setUpsPrivateKey(String upsPrivateKey) {
		this.upsPrivateKey = upsPrivateKey;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MerchantOrderTypeEntity> getMerchantOrderTypeList() {
		return merchantOrderTypeList;
	}

	public void setMerchantOrderTypeList(List<MerchantOrderTypeEntity> merchantOrderTypeList) {
		this.merchantOrderTypeList = merchantOrderTypeList;
	}
	
	
	
}
