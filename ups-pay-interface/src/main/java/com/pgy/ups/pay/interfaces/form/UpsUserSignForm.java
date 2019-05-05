package com.pgy.ups.pay.interfaces.form;

import com.pgy.data.handler.service.PgyDataHandlerService;
import com.pgy.data.handler.service.impl.PgyDataHandlerServiceImpl;
import org.apache.commons.lang3.StringUtils;

public class UpsUserSignForm  extends  BaseForm{


    private static final long serialVersionUID = 4881942736004919925L;

    private  String bankCard;

    private Integer status;
        
    private String signType;

    private String merchantCode;

    private String userNo;
    
    private String payChannel;


    private String realName;

    private String phoneNo;

    private  String identity;
    
    private  String tradeNo;

    private  String realNameMd5;

    private  String phoneNoMd5;

    private  String identityMd5;

    private  String businessFlowNum;

    public String getBankMd5() {
        return bankMd5;
    }

    public String getBusinessFlowNum() {
        return businessFlowNum;
    }

    public void setBusinessFlowNum(String businessFlowNum) {
        this.businessFlowNum = businessFlowNum;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public void setRealName(String realName) {
        this.realNameMd5 = getMd5(realName);
        this.realName = realName;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
        this.phoneNoMd5 = getMd5(phoneNo);
    }

    public void setIdentity(String identity) {
        this.identity = identity;
        this.identityMd5 = getMd5(identity);

    }

    public void setRealNameMd5(String realNameMd5) {
        this.realNameMd5 = realNameMd5;
    }

    public void setPhoneNoMd5(String phoneNoMd5) {
        this.phoneNoMd5 = phoneNoMd5;
    }

    public void setIdentityMd5(String identityMd5) {
        this.identityMd5 = identityMd5;
    }

    public void setBankMd5(String bankMd5) {
        this.bankMd5 = bankMd5;
    }

    private  String bankMd5;

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getBankCard() {
        return bankCard;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public void setBankCard(String bankCard) {
        this.bankMd5 = getMd5(bankCard);
        this.bankCard = bankCard;
    }

    public String getUserNo() {
        return userNo;
    }

    public String getRealName() {
        return realName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getIdentity() {
        return identity;
    }

    public String getRealNameMd5() {
        return realNameMd5;
    }

    public String getPhoneNoMd5() {
        return phoneNoMd5;
    }

    public String getIdentityMd5() {
        return identityMd5;
    }

    private  String  getMd5(String str){
        if(StringUtils.isNoneBlank(str)){
            PgyDataHandlerService pgyDataHandlerService = new PgyDataHandlerServiceImpl();
            return pgyDataHandlerService.md5(str);
        }
        return  null;
    }

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
  
}
