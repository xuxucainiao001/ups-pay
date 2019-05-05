package com.pgy.ups.pay.interfaces.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name="ups_t_user_sign_log")
public class UpsUserSignLogEntity extends  EncryptSignModel{


    /**
	 * 
	 */
	private static final long serialVersionUID = -4785484671515602490L;

	@Id
    private Long id;

    @Column(name="merchant_code")
    private String merchantCode;

    @Column(name="pay_channel")
    private String payChannel;

    @Column(name="user_no")
    private String userNo;

    @Column(name="ups_user_id")
    private String upsUserId;


    @Column(name="tpp_mer_no")
    private String  tppMerNo;

    @Column(name="`mer_request_params`")
    private String merRequestParams;

    @Column(name="mer_request_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date merRequestTime;


    @Column(name="mer_response_params")
    private String merResponseParams;


    @Column(name="mer_response_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date merResponseTime;


    @Column(name="tpp_request_params")
    private String tppRequestParams;


    @Column(name="tpp_request_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tppRequestTime;

    @Column(name="`tpp_response_params`")
    private String tppResponseParams;


    @Column(name="tpp_response_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tppResponseTime;


    @Column(name = "business_flow_num")
    private String businessFlowNum;



    @Column(name = "tpp_order_no")
    private String tppOrderNo;

    @Column(name = "order_type")
    private String orderType;



    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUpsUserId() {
        return upsUserId;
    }

    public void setUpsUserId(String upsUserId) {
        this.upsUserId = upsUserId;
    }



    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }



    public String getMerRequestParams() {
        return merRequestParams;
    }

    public void setMerRequestParams(String merRequestParams) {
        this.merRequestParams = merRequestParams;
    }

    public Date getMerRequestTime() {
        return merRequestTime;
    }

    public void setMerRequestTime(Date merRequestTime) {
        this.merRequestTime = merRequestTime;
    }

    public String getMerResponseParams() {
        return merResponseParams;
    }

    public void setMerResponseParams(String merResponseParams) {
        this.merResponseParams = merResponseParams;
    }

    public Date getMerResponseTime() {
        return merResponseTime;
    }

    public void setMerResponseTime(Date merResponseTime) {
        this.merResponseTime = merResponseTime;
    }

    public String getTppRequestParams() {
        return tppRequestParams;
    }

    public void setTppRequestParams(String tppRequestParams) {
        this.tppRequestParams = tppRequestParams;
    }

    public Date getTppRequestTime() {
        return tppRequestTime;
    }

    public void setTppRequestTime(Date tppRequestTime) {
        this.tppRequestTime = tppRequestTime;
    }

    public String getTppResponseParams() {
        return tppResponseParams;
    }

    public void setTppResponseParams(String tppResponseParams) {
        this.tppResponseParams = tppResponseParams;
    }

    public Date getTppResponseTime() {
        return tppResponseTime;
    }

    public void setTppResponseTime(Date tppResponseTime) {
        this.tppResponseTime = tppResponseTime;
    }



    public String getBusinessFlowNum() {
        return businessFlowNum;
    }

    public void setBusinessFlowNum(String businessFlowNum) {
        this.businessFlowNum = businessFlowNum;
    }

    public String getTppOrderNo() {
        return tppOrderNo;
    }

    public void setTppOrderNo(String tppOrderNo) {
        this.tppOrderNo = tppOrderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

}
