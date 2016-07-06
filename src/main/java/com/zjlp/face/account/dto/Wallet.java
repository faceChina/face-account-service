package com.zjlp.face.account.dto;

import java.io.Serializable;
/**
 * 钱包支付对象
 * @ClassName: WalletPayDto 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年7月21日 下午8:39:30
 */
public class Wallet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3342782390160647810L;
	/**用户编号*/
	private String userId;
	/**支付密码*/
	private String paymendCode;
	/**店铺编号*/
	private String shopNo;
	/**支付价格*/
	private Long totalPrice;
	/**交易流水号*/
	private String transactionSerialNumber;
	/**交易备注信息**/
	private String remark;
	/** 操作类型,对应ConstantsUtil.ACCOUNT_OPERATION_* */
	private Integer operationType;

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Long totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getTransactionSerialNumber() {
		return transactionSerialNumber;
	}

	public void setTransactionSerialNumber(String transactionSerialNumber) {
		this.transactionSerialNumber = transactionSerialNumber;
	}

	public String getPaymendCode() {
		return paymendCode;
	}

	public void setPaymendCode(String paymendCode) {
		this.paymendCode = paymendCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
