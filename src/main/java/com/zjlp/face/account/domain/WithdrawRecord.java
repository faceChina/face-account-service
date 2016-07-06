package com.zjlp.face.account.domain;

import java.io.Serializable;
import java.util.Date;

public class WithdrawRecord implements Serializable {
	public static final Integer REMOTE_TYPE_USER = 1;
	private static final long serialVersionUID = 4660368980772036603L;
	// /主键
	private Long id;
	// /外部ID 标识所属的唯一ID
	private String remoteId;
	// 外部ID类型：1.用户 2店铺
	private Integer remoteType;
	// 交易流水号
	private String seriNumber;
	// 银行交易流水
	private String bankSeriNumber;
	// 电子回单号
	private String elecBkNo;
	// 提现金额(单位：分)
	private Long withdrawPrice;
	// 交易状态 0 成功 -1 失败 5 交易处理中
	private Integer status;
	// 付款银行卡号
	private String payBankCard;
	// 收款银行卡号
	private String reciveBankCard;
	// 收款银行卡户名
	private String userName;
	// 收款方开户行名称
	private String reciveBankName;
	// 收款方所在省
	private String province;
	// 收款方所在市
	private String city;
	// 提现币种
	private String currencyType;
	// 提现服务类型 1 同行提款 2 跨行提款
	private Integer serviceType;
	// 转账类型 02 人行大额
	private String withdrawType;
	// 转账类别 0 普通 1 加急
	private Integer emergencyDegree;
	// 用途
	private String purpose;
	// 收款行号
	private String bankCode;
	// 提现后，账户金额（单位：分）
	private Long amountAfter;
	// 清帐时间
	private String settleDate;
	// 说明信息
	private String withdrawInfo;
	// 创建时间
	private Date createTime;
	// 更新时间
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId == null ? null : remoteId.trim();
	}

	public Integer getRemoteType() {
		return remoteType;
	}

	public void setRemoteType(Integer remoteType) {
		this.remoteType = remoteType;
	}

	public String getSeriNumber() {
		return seriNumber;
	}

	public void setSeriNumber(String seriNumber) {
		this.seriNumber = seriNumber == null ? null : seriNumber.trim();
	}

	public String getBankSeriNumber() {
		return bankSeriNumber;
	}

	public void setBankSeriNumber(String bankSeriNumber) {
		this.bankSeriNumber = bankSeriNumber == null ? null : bankSeriNumber
				.trim();
	}

	public String getElecBkNo() {
		return elecBkNo;
	}

	public void setElecBkNo(String elecBkNo) {
		this.elecBkNo = elecBkNo == null ? null : elecBkNo.trim();
	}

	public Long getWithdrawPrice() {
		return withdrawPrice;
	}

	public void setWithdrawPrice(Long withdrawPrice) {
		this.withdrawPrice = withdrawPrice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPayBankCard() {
		return payBankCard;
	}

	public void setPayBankCard(String payBankCard) {
		this.payBankCard = payBankCard == null ? null : payBankCard.trim();
	}

	public String getReciveBankCard() {
		return reciveBankCard;
	}

	public void setReciveBankCard(String reciveBankCard) {
		this.reciveBankCard = reciveBankCard == null ? null : reciveBankCard
				.trim();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getReciveBankName() {
		return reciveBankName;
	}

	public void setReciveBankName(String reciveBankName) {
		this.reciveBankName = reciveBankName == null ? null : reciveBankName
				.trim();
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province == null ? null : province.trim();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType == null ? null : currencyType.trim();
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType == null ? null : withdrawType.trim();
	}

	public Integer getEmergencyDegree() {
		return emergencyDegree;
	}

	public void setEmergencyDegree(Integer emergencyDegree) {
		this.emergencyDegree = emergencyDegree;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose == null ? null : purpose.trim();
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode == null ? null : bankCode.trim();
	}

	public Long getAmountAfter() {
		return amountAfter;
	}

	public void setAmountAfter(Long amountAfter) {
		this.amountAfter = amountAfter;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate == null ? null : settleDate.trim();
	}

	public String getWithdrawInfo() {
		return withdrawInfo;
	}

	public void setWithdrawInfo(String withdrawInfo) {
		this.withdrawInfo = withdrawInfo == null ? null : withdrawInfo.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}