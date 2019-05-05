package com.pgy.ups.pay.interfaces.model;

public class UpsSignatureParamModel extends UpsParamModel  {

    private static final long serialVersionUID = 2900816929919234401L;


    /**
     * 验签顺序
     */
    private static final String[] SIGN_RULE = new String[] { "merchantCode", "userNo", "realName", "bankCard",
            "identity", "bankCode", "phoneNo", "remark",  "businessType","businessFlowNum" };


    @Override
    public String[] getSignRule() {
        return SIGN_RULE;
    }
}
