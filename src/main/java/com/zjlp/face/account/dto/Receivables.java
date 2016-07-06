package com.zjlp.face.account.dto;

import java.io.Serializable;

public class Receivables implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4780062885161350486L;

	/**付款金额*/
	private Long amount;
	/**手续费*/
	private Long fee;
	/**银行卡编号*/
	private Long bankCardId;
	/**银行卡号*/
	private String bankCard;
	/**银行名称*/
	private String bankName;
	/**付款微信OPENID*/
	private String openId;
	/**交易流水号*/
	private String transactionSerialNumber;
	/**支付渠道 1.连连支付2.捷蓝绑定支付3.支付宝支付6.拉卡拉支付*/
	private Integer channel;
	/**备注**/
	private String remark;
	
	public String getTransactionSerialNumber() {
		return transactionSerialNumber;
	}
	public void setTransactionSerialNumber(String transactionSerialNumber) {
		this.transactionSerialNumber = transactionSerialNumber;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getFee() {
		return fee;
	}
	public void setFee(Long fee) {
		this.fee = fee;
	}
	public Long getBankCardId() {
		return bankCardId;
	}
	public void setBankCardId(Long bankCardId) {
		this.bankCardId = bankCardId;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
