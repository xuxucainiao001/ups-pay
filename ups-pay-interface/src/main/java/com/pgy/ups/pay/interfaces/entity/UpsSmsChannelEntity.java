package com.pgy.ups.pay.interfaces.entity;


import com.pgy.ups.pay.interfaces.model.Model;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="ups_t_sms_channel")
public class UpsSmsChannelEntity extends Model {

    private static final long serialVersionUID = 1027044394025844202L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private    Long id;

    @Column(name = "sms_userful_code")
    private String smsUserfulCode;

    @Column(name = "sms_userful_name")
    private String smsUserfulName;

    @Column(name = "sms_channel_code")
    private  String  smsChannelCode;


    @Column(name = "sms_channel_name")
    private  String smsChannelName;


    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "account")
    private  String account;

    @Column(name = "password")
    private  String password;


    @Column(name = "sort")
    private Integer sort;

    @Column(name = "is_userd",columnDefinition="BIT")
    private Boolean isUserd;


    @Column(name = "from_system",columnDefinition="BIT")
    private Boolean fromSystem;

    @Column(name="update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;



    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSmsUserfulCode() {
        return smsUserfulCode;
    }

    public void setSmsUserfulCode(String smsUserfulCode) {
        this.smsUserfulCode = smsUserfulCode;
    }

    public String getSmsUserfulName() {
        return smsUserfulName;
    }

    public void setSmsUserfulName(String smsUserfulName) {
        this.smsUserfulName = smsUserfulName;
    }

    public String getSmsChannelCode() {
        return smsChannelCode;
    }

    public void setSmsChannelCode(String smsChannelCode) {
        this.smsChannelCode = smsChannelCode;
    }

    public String getSmsChannelName() {
        return smsChannelName;
    }

    public void setSmsChannelName(String smsChannelName) {
        this.smsChannelName = smsChannelName;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getUserd() {
        return isUserd;
    }

    public void setUserd(Boolean userd) {
        isUserd = userd;
    }

    public Boolean getFromSystem() {
        return fromSystem;
    }

    public void setFromSystem(Boolean fromSystem) {
        this.fromSystem = fromSystem;
    }


}
