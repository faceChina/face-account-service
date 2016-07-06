package com.zjlp.face.account.dto;

import java.io.Serializable;

/**
 * 提现请求数据
 * 
 * @ClassName: WithdrawReq
 * @Description: (这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午9:19:35
 */
public class WithdrawReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2906225533767792663L;
	// 提现钱包id
	private Long accountId;
	// 用户
	private Long userId;
	// 商铺
	private String shopNo;
	// 用户名
	private String userName;
	// 银行名称
	private String bankName;
	// 银行编号
	private String bankCode;
	// 省份
	private String province;
	// 市区
	private String city;
	// 支付账号
	private String payAccountNo;
	// 收款账号
	private String reciveAccountNo;
	// 交易流水
	private String transferSerino;
	// 转账金额
	private Long transferAmount;
	// 服务类型
	private Integer serviceType;
	// 币种
	private String currencyType;
	// -------------活期查询---------------
	// 要查询的账号
	private String queryAccountNo;
	// 起始日期 yyyyMMdd
	private String startDate;
	// 终止日期 yyyyMMdd
	private String endDate;
	// 最小金额 >= 0.00
	private String minAmount;
	// 最大金额 >=最小金额and <= 9999999999.99
	private String maxAmount;
	// 显示页号
	private Integer showPage;
	// 每页显示条数
	private Integer countOfPage;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTransferSerino() {
		return transferSerino;
	}

	public void setTransferSerino(String transferSerino) {
		this.transferSerino = transferSerino;
	}

	public Long getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(Long transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getPayAccountNo() {
		return payAccountNo;
	}

	public void setPayAccountNo(String payAccountNo) {
		this.payAccountNo = payAccountNo;
	}

	public String getReciveAccountNo() {
		return reciveAccountNo;
	}

	public void setReciveAccountNo(String reciveAccountNo) {
		this.reciveAccountNo = reciveAccountNo;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getQueryAccountNo() {
		return queryAccountNo;
	}

	public void setQueryAccountNo(String queryAccountNo) {
		this.queryAccountNo = queryAccountNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}

	public String getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Integer getShowPage() {
		return showPage;
	}

	public void setShowPage(Integer showPage) {
		this.showPage = showPage;
	}

	public Integer getCountOfPage() {
		return countOfPage;
	}

	public void setCountOfPage(Integer countOfPage) {
		this.countOfPage = countOfPage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("提现钱包id").append(accountId).append(", 用户").append(userId)
				.append(", 商铺").append(shopNo).append(", 用户名").append(userName)
				.append(", 银行名称").append(bankName).append(", 银行编号")
				.append(bankCode).append(", 省份").append(province).append(", 市区")
				.append(city).append(", 支付账号").append(payAccountNo)
				.append(", 收款账号").append(reciveAccountNo).append(", 交易流水")
				.append(transferSerino).append(", 转账金额").append(transferAmount)
				.append(", 服务类型").append(serviceType).append(", 币种")
				.append(currencyType).append(", 要查询的账号").append(queryAccountNo);
		return sb.toString();
	}

}
