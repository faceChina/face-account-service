package com.zjlp.face.account.dto;

import java.io.Serializable;

public class BankInfo implements Serializable{
	private static final long serialVersionUID = 860625234697575153L;
	private String frontNo;
	private String bankName;
	private String bankCode;
	/** 0,借记卡;1,信用卡;2,准贷记卡;3,预付费卡*/
	private String type;
	public String getFrontNo() {
		return frontNo;
	}
	public void setFrontNo(String frontNo) {
		this.frontNo = frontNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "BankInfo [frontNo=" + frontNo + ", bankName=" + bankName + ", bankCode=" + bankCode + ", type=" + type + "]";
	}
	
}
