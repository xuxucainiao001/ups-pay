package com.pgy.ups.pay.interfaces.entity;

import javax.persistence.*;


@Entity
@Table(name="ups_t_tpp_mer_config")
public class UpsTppMerConfigEntity extends BaseEntity{

    private static final long serialVersionUID = 4141020562644458029L;


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="pay_channel")
    private String payChannel;

    @Column(name="order_type")
    private String orderType;

    @Lob
    @Column(name="config_data",columnDefinition="text")
    private String configDate;


    @Column(name="tpp_mer_no")
    private String tppMerNo;


    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getConfigDate() {
        return configDate;
    }

    public void setConfigDate(String configDate) {
        this.configDate = configDate;
    }

    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }



}
