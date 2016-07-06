package com.zjlp.face.account.dto;

import java.io.Serializable;

public class DivideCommission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2711864907223438282L;
	/**商家编号*/
	private String shopNo;
	/**推荐人邀请码*/
	private String invitationCode;
	/**上家邀请码*/
	private String onInvitationCode;
	
	/**商家收入*/
	private Long shopIncome;
	/**推荐人佣金*/
	private Long refereeDivide;
	/**上家佣金*/
	private Long onRefereeDivide;
	/**商品发布者  1 商户发布  2 平台发布*/
	private Integer isPlatformPublicsh;
	
	/** 交易流水号 */
	private String transactionSerialNumber;
	
	public Integer getIsPlatformPublicsh() {
		return isPlatformPublicsh;
	}
	public void setIsPlatformPublicsh(Integer isPlatformPublicsh) {
		this.isPlatformPublicsh = isPlatformPublicsh;
	}
	public String getShopNo() {
		return shopNo;
	}
	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}
	public String getInvitationCode() {
		return invitationCode;
	}
	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}
	public String getOnInvitationCode() {
		return onInvitationCode;
	}
	public void setOnInvitationCode(String onInvitationCode) {
		this.onInvitationCode = onInvitationCode;
	}
	public Long getShopIncome() {
		return shopIncome;
	}
	public void setShopIncome(Long shopIncome) {
		this.shopIncome = shopIncome;
	}
	public Long getRefereeDivide() {
		return refereeDivide;
	}
	public void setRefereeDivide(Long refereeDivide) {
		this.refereeDivide = refereeDivide;
	}
	public Long getOnRefereeDivide() {
		return onRefereeDivide;
	}
	public void setOnRefereeDivide(Long onRefereeDivide) {
		this.onRefereeDivide = onRefereeDivide;
	}
	public String getTransactionSerialNumber() {
		return transactionSerialNumber;
	}
	public void setTransactionSerialNumber(String transactionSerialNumber) {
		this.transactionSerialNumber = transactionSerialNumber;
	}
	
}
