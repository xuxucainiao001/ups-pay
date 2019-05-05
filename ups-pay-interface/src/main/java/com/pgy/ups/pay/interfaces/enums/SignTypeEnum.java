package com.pgy.ups.pay.interfaces.enums;

public enum SignTypeEnum {

    AUTH("auth", "签约"),PROTOCOL("protocol", "协议签约");


    private String code;

    private String name;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    SignTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

}
