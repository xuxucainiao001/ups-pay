package com.pgy.ups.pay.interfaces.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.pgy.ups.pay.interfaces.model.Model;

@MappedSuperclass
public abstract  class EncryptModel extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1582723423865483874L;
	
	
	@Column(name="real_name_encrypt")
	protected String realNameEncrypt; 
	
	@Column(name="real_name_md5")
	protected String realNameMd5;
	
	@Column(name="identity_encrypt")
	protected String identityEncrypt;
	
	@Column(name="identity_md5")
	protected String identityMd5;
	
	@Column(name="phone_no_encrypt")
	protected String phoneNoEncrypt;
	
	@Column(name="phone_no_md5")
	protected String phoneNoMd5;
	
	@Column(name="bank_encrypt")
	protected String bankEncrypt;
	
	@Column(name="bank_md5")
	protected String bankMd5;

	public String getRealNameEncrypt() {
		return realNameEncrypt;
	}

	public void setRealNameEncrypt(String realNameEncrypt) {
		this.realNameEncrypt = realNameEncrypt;
	}

	public String getRealNameMd5() {
		return realNameMd5;
	}

	public void setRealNameMd5(String realNameMd5) {
		this.realNameMd5 = realNameMd5;
	}

	public String getIdentityEncrypt() {
		return identityEncrypt;
	}

	public void setIdentityEncrypt(String identityEncrypt) {
		this.identityEncrypt = identityEncrypt;
	}

	public String getIdentityMd5() {
		return identityMd5;
	}

	public void setIdentityMd5(String identityMd5) {
		this.identityMd5 = identityMd5;
	}


	public String getPhoneNoEncrypt() {
		return phoneNoEncrypt;
	}

	public void setPhoneNoEncrypt(String phoneNoEncrypt) {
		this.phoneNoEncrypt = phoneNoEncrypt;
	}

	public String getPhoneNoMd5() {
		return phoneNoMd5;
	}

	public void setPhoneNoMd5(String phoneNoMd5) {
		this.phoneNoMd5 = phoneNoMd5;
	}

	public String getBankEncrypt() {
		return bankEncrypt;
	}

	public void setBankEncrypt(String bankEncrypt) {
		this.bankEncrypt = bankEncrypt;
	}

	public String getBankMd5() {
		return bankMd5;
	}

	public void setBankMd5(String bankMd5) {
		this.bankMd5 = bankMd5;
	}
	
}
