package com.pgy.ups.pay.interfaces.form;

import com.pgy.data.handler.service.PgyDataHandlerService;
import com.pgy.data.handler.service.impl.PgyDataHandlerServiceImpl;
import org.apache.commons.lang3.StringUtils;

public class UpsOrderForm  extends BaseForm{

    private static final long serialVersionUID = 8837735964328170783L;
    
    private Long id;

    private  String bankCard;

    private String orderType;
    
    private String upsOrderCode;

    private String merchantCode;
    
    private String orderStatus;

    private String userNo;

    private  String payChannel;

    private String bankMd5;

    private  String businessFlowNum;

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        if(StringUtils.isNoneBlank(bankCard)){
            PgyDataHandlerService pgyDataHandlerService = new PgyDataHandlerServiceImpl();
            this.bankMd5 = pgyDataHandlerService.md5(bankCard);
        }
        this.bankCard = bankCard;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getBankMd5() {
        return bankMd5;
    }

    public void setBankMd5(String bankMd5) {
        this.bankMd5 = bankMd5;
    }


    public String getBusinessFlowNum() {
        return businessFlowNum;
    }

    public void setBusinessFlowNum(String businessFlowNum) {
        this.businessFlowNum = businessFlowNum;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

	public String getUpsOrderCode() {
		return upsOrderCode;
	}

	public void setUpsOrderCode(String upsOrderCode) {
		this.upsOrderCode = upsOrderCode;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}  

}
