package com.pgy.ups.pay.interfaces.enums;

import java.io.Serializable;

public enum  UpsResultEnum implements Serializable{

    SUCCESS("00","处理成功"),NO_PROPTOCAL("0201","用户没有签约！");

    private String code;

    private String message;

    UpsResultEnum(String code,String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

}
