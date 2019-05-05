package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * 签约表
 */

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="ups_t_user_sign")
public class UpsAuthSignEntity extends EncryptSignModel {

    private static final long serialVersionUID = -1243727760631039581L;

    @Id
    private Long id;

    @Column(name = "pay_channel")
    private String payChannel;

    @Column(name = "sign_type")
    private String signType;

    @Column(name = "merchant_code")
    private String merchantCode;



    @Column(name = "cert_type")
    private String certType;



    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "user_no")
    private String userNo;

    @Column(name = "tpp_mer_no")
    private String tppMerNo;




    @Column(name = "status",columnDefinition="tinyint")
    private Integer status;


    @Column(name = "is_delete",columnDefinition="BIT")
    private Boolean isDelete;

    @Column(name = "trade_no")
    private String tradeNo;



    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;


    @Column(name = "business_flow_num")
    private String businessFlowNum;

    @Column(name = "ups_user_id")
    private String upsUserId;




    @Column(name = "sign_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signDate;



    @Column(name = "tpp_sign_no")
    private String tppSignNo;


    @Column(name = "tpp_order_no")
    private String tppOrderNo;



    @Column(name = "business_type")
    private String businessType;


    @Column(name = "gmt_ymd")
    private  String gmtYmd;


    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }



    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }



    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
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

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getBusinessFlowNum() {
        return businessFlowNum;
    }

    public void setBusinessFlowNum(String businessFlowNum) {
        this.businessFlowNum = businessFlowNum;
    }

    public String getUpsUserId() {
        return upsUserId;
    }

    public void setUpsUserId(String upsUserId) {
        this.upsUserId = upsUserId;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getTppSignNo() {
        return tppSignNo;
    }

    public void setTppSignNo(String tppSignNo) {
        this.tppSignNo = tppSignNo;
    }

    public String getTppOrderNo() {
        return tppOrderNo;
    }

    public void setTppOrderNo(String tppOrderNo) {
        this.tppOrderNo = tppOrderNo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGmtYmd() {
        return gmtYmd;
    }

    public void setGmtYmd(String gmtYmd) {
        this.gmtYmd = gmtYmd;
    }


}
