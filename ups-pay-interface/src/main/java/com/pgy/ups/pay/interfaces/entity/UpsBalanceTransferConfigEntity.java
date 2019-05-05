package com.pgy.ups.pay.interfaces.entity;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name="ups_t_balance_transfer_config")
public class UpsBalanceTransferConfigEntity    extends BaseEntity {

    private static final long serialVersionUID = 6828400846276804368L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_delete",columnDefinition="BIT")
    private Boolean isDelete;

    @Column(name = "remakes")
    private String remakes;

    @Column(name = "status",columnDefinition="BIT")
    private Boolean status;

    @Column(name = "receipt_member_id")
    private String receiptMemberId;

    @Column(name = "pay_member_id")
    private String payMemberId;

    @Column(name = "pay_channel")
    private String payChannel;

    @Column(name = "threshold_money")
    private BigDecimal thresholdMoney;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getReceiptMemberId() {
        return receiptMemberId;
    }

    public void setReceiptMemberId(String receiptMemberId) {
        this.receiptMemberId = receiptMemberId;
    }

    public String getPayMemberId() {
        return payMemberId;
    }

    public void setPayMemberId(String payMemberId) {
        this.payMemberId = payMemberId;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public BigDecimal getThresholdMoney() {
        return thresholdMoney;
    }

    public void setThresholdMoney(BigDecimal thresholdMoney) {
        this.thresholdMoney = thresholdMoney;
    }



}
