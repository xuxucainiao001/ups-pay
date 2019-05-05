package com.pgy.ups.pay.interfaces.entity;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="ups_t_balance")
public class UpsBalanceEntity  extends BaseEntity{


    private static final long serialVersionUID = -705624003924700876L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected   Long id;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "pay_channel")
    private String payChannel;



    @Column(name = "tpp_mer_no")
    private String tppMerNo;

    @Column(name = "status",columnDefinition="tinyint")
    private Integer status;



    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
