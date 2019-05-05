package com.pgy.ups.pay.interfaces.entity;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="ups_t_balance_sms_config")
public class UpsBalanceSmsConfigEntity  extends BaseEntity{

    private static final long serialVersionUID = 7933673232062979146L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private    Long id;

    @Column(name = "threshold_money")
    private BigDecimal thresholdMoney;

    @Column(name = "number")
    private Integer number;

    @Column(name = "status",columnDefinition="tinyint")
    private Integer status;

    @Column(name = "tpp_mer_no")
    private String tppMerNo;

    @Column(name = "msg_threshold_context")
    private String msgThresholdContext;

    @Column(name = "msg_default_context")
    private String msgDefaultContext;


    public BigDecimal getThresholdMoney() {
        return thresholdMoney;
    }

    public void setThresholdMoney(BigDecimal thresholdMoney) {
        this.thresholdMoney = thresholdMoney;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }

    public String getMsgThresholdContext() {
        return msgThresholdContext;
    }

    public void setMsgThresholdContext(String msgThresholdContext) {
        this.msgThresholdContext = msgThresholdContext;
    }

    public String getMsgDefaultContext() {
        return msgDefaultContext;
    }

    public void setMsgDefaultContext(String msgDefaultContext) {
        this.msgDefaultContext = msgDefaultContext;
    }


}
