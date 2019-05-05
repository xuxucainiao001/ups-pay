package com.pgy.ups.pay.interfaces.entity;


import javax.persistence.*;

@Entity
@Table(name="ups_t_balance_sms_record")
public class UpsBalanceSmsRecordEntity extends BaseEntity {


    /**
	 * 
	 */
	private static final long serialVersionUID = -8781228156872442548L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private    Long id;

    @Column(name = "send_account")
    private String sendAccount;



    @Column(name = "msg_context")
    private String msgContext;


    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "errorMsg")
    private String errorMsg;


    @Column(name = "tpp_mer_no")
    private String tppMerNo;



    @Column(name = "result_code")
    private String resultCode;




    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }

    public String getMsgContext() {
        return msgContext;
    }

    public void setMsgContext(String msgContext) {
        this.msgContext = msgContext;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }



}
