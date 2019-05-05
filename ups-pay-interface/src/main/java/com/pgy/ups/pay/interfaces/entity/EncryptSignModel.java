package com.pgy.ups.pay.interfaces.entity;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EncryptSignModel extends EncryptModel {

    private static final long serialVersionUID = -361609968004083311L;

    @Column(name = "real_name")
    protected String realName;

    @Column(name = "bank_card")
    protected String bankCard;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "identity")
    private String identity;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }


    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
