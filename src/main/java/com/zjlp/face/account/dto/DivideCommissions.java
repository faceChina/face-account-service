package com.zjlp.face.account.dto;

import java.io.Serializable;

/**
 * 交易完成分钱对象
 * @ClassName: DivideCommissions 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author phb
 * @date 2015年1月20日 下午2:45:18
 */
public class DivideCommissions implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7085539296932962044L;

	/**
	 * 帐号
	 */
	private Long accountId;
	
	/**
	 * 交易流水号
	 */
	private String transactionSerialNumber;
	/**
	 * 11.商家收益
	 * 13.佣金收益
	 * 18.代理收益
	 * 19.全民推广收益
	 */
	private Integer type;
	
	/**
	 * 收益金额
	 */
	private Long money;
	/**
	 * 备注
	 */
	private String remark;
	
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getTransactionSerialNumber() {
		return transactionSerialNumber;
	}

	public void setTransactionSerialNumber(String transactionSerialNumber) {
		this.transactionSerialNumber = transactionSerialNumber;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
