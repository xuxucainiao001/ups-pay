package com.pgy.ups.pay.baofoo.model;

import com.pgy.ups.pay.interfaces.model.Model;

/**
 * 
 * @author 宝付代扣个人信息报文
 *
 */
public class BaoFooCollectPrivateModel extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9134947426155893073L;
	
	private String txn_sub_type; //交易子类
	private String biz_type;// 接入类型
	private String terminal_id;// 终端号
	private String member_id;// 商户号
	private String pay_code;// 银行编码

	private String acc_no;// 卡号
	private String id_card_type="01";// 身份证类型
	private String id_card;// 身份证号
	private String id_holder;//持卡人姓名 
	private String mobile;// 银行卡绑定手机号
	private String trans_id;// 商户订单号
	private String txn_amt;// 交易金额(分)
	private String trade_date;//订单日期    yyyyMMddhhmmss
	private String trans_serial_no;//  商户流水号 8-20 位字母和数字，每次请求都不可重复

	public String getTxn_sub_type() {
		return txn_sub_type;
	}
	public void setTxn_sub_type(String txn_sub_type) {
		this.txn_sub_type = txn_sub_type;
	}
	public String getBiz_type() {
		return biz_type;
	}
	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getPay_code() {
		return pay_code;
	}
	public void setPay_code(String pay_code) {
		this.pay_code = pay_code;
	}
	public String getAcc_no() {
		return acc_no;
	}
	public void setAcc_no(String acc_no) {
		this.acc_no = acc_no;
	}
	public String getId_card_type() {
		return id_card_type;
	}
	public void setId_card_type(String id_card_type) {
		this.id_card_type = id_card_type;
	}
	public String getId_card() {
		return id_card;
	}
	public void setId_card(String id_card) {
		this.id_card = id_card;
	}
	public String getId_holder() {
		return id_holder;
	}
	public void setId_holder(String id_holder) {
		this.id_holder = id_holder;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTrans_id() {
		return trans_id;
	}
	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}
	public String getTxn_amt() {
		return txn_amt;
	}
	public void setTxn_amt(String txn_amt) {
		this.txn_amt = txn_amt;
	}
	public String getTrade_date() {
		return trade_date;
	}
	public void setTrade_date(String trade_date) {
		this.trade_date = trade_date;
	}
	public String getTrans_serial_no() {
		return trans_serial_no;
	}
	public void setTrans_serial_no(String trans_serial_no) {
		this.trans_serial_no = trans_serial_no;
	}


}
