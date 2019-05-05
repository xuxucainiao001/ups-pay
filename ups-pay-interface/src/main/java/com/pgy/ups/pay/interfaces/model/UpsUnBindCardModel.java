package com.pgy.ups.pay.interfaces.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpsUnBindCardModel extends Model {

    private static final long serialVersionUID = -5798223986075224745L;

    /* 用户编码 */
    @NotBlank(message = "用户编码不能为空")
    private String userNo;

    /* 真实姓名 */
    @Size(max = 50, min = 1, message = "真实姓名长度必须在{min}和{max}之间")
    private String realName;

    /* 银行卡号 */
    @NotBlank(message = "银行卡号不能为空")
    private String bankCard;

    /* 身份证号 */
    @Size(max = 18, min = 18, message = "身份证号必须为18位")
    private String identity;

    /* 银行编码 */
    @NotBlank(message = "银行编码不能为空")
    private String bankCode;

    /* 手机号码 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[1][0-9]{10}$", message = "手机号码必须是以1开头的11位数字")
    private String phoneNo;


    /* 美期 Meiqi 米融 Mirong 秒呗 Miaobei 多呗 Duobei 迅到 XunDao*/
    private String fromSystem;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

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

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFromSystem() {
        return fromSystem;
    }

    public void setFromSystem(String fromSystem) {
        this.fromSystem = fromSystem;
    }
}
