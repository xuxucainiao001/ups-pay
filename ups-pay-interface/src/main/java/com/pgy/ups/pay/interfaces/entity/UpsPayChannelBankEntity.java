package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pgy.ups.pay.interfaces.model.Model;


@Entity
@Table(name="ups_t_pay_channel_bank")
public class UpsPayChannelBankEntity extends Model {


    private static final long serialVersionUID = -3805598917000755292L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "pay_channel")
    private String payChannel;

    @Column(name = "pay_channel_bank_code")
    private String payChannelBankCode;

    @Column(name = "pay_channel_bank_name")
    private String payChannelBankName;

    @Column(name="create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name="update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_user")
    private String createUser;


    @Column(name = "update_user")
    private String updateUser;
    
    @ManyToOne
    @JoinColumn(name="ups_bank_id")
    private UpsBankEntity upsBankEntity;
    

    public String getPayChannel() {
        return payChannel;
    }

    public UpsBankEntity getUpsBankEntity() {
		return upsBankEntity;
	}

	public void setUpsBankEntity(UpsBankEntity upsBankEntity) {
		this.upsBankEntity = upsBankEntity;
	}

	public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getPayChannelBankCode() {
        return payChannelBankCode;
    }

    public void setPayChannelBankCode(String payChannelBankCode) {
        this.payChannelBankCode = payChannelBankCode;
    }

    public String getPayChannelBankName() {
        return payChannelBankName;
    }

    public void setPayChannelBankName(String payChannelBankName) {
        this.payChannelBankName = payChannelBankName;
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
}
