package com.pgy.ups.pay.interfaces.entity;

import com.pgy.ups.pay.interfaces.model.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name="ups_t_balance_transfer_record")
public class UpsBalanceTransferRecordEntity extends Model {

    private static final long serialVersionUID = -7431618803465883848L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_delete",columnDefinition="BIT")
    private Boolean isDelete;

    @Column(name = "remakes")
    private String remakes;

    @Column(name = "status")
    private String status;

    @Column(name = "to_member_id")
    private String toMemberId;


    @Column(name = "pay_channel")
    private String payChannel;

    @Column(name = "transfer_money")
    private BigDecimal transferMoney;

    @Column(name = "error_reason")
    private  String errorReason;

    @Column(name = "trans_no")
    private  String transNo;

    @Column(name = "trans_orderid")
    private  String transOrderid;



    @Column(name = "rans_batchid")
    private  String ransBatchid;


    @Column(name = "return_code")
    private  String returnCode;

    @Column(name = "member_id")
    private  String memberId;

    @Column(name="update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    public String getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(String toMemberId) {
        this.toMemberId = toMemberId;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getRansBatchid() {
        return ransBatchid;
    }

    public void setRansBatchid(String ransBatchid) {
        this.ransBatchid = ransBatchid;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

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



    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getRemakes() {
        return remakes;
    }

    public void setRemakes(String remakes) {
        this.remakes = remakes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public BigDecimal getTransferMoney() {
        return transferMoney;
    }

    public String getTransOrderid() {
        return transOrderid;
    }

    public void setTransOrderid(String transOrderid) {
        this.transOrderid = transOrderid;
    }

    public void setTransferMoney(BigDecimal transferMoney) {
        this.transferMoney = transferMoney;
    }


}
