package com.pgy.ups.pay.interfaces.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="ups_t_balance_config")
public class UpsBalanceConfigEntity  extends BaseEntity {

    private static final long serialVersionUID = 5591437914915639954L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected   Long id;

    @Column(name = "from_system")
    private String fromSystem;



    @Column(name = "pay_channel")
    private String payChannel;

    @Column(name = "tpp_mer_no")
    private String tppMerNo;



    public String getFromSystem() {
        return fromSystem;
    }

    public void setFromSystem(String fromSystem) {
        this.fromSystem = fromSystem;
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





}
