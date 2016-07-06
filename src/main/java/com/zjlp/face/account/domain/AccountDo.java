package com.zjlp.face.account.domain;

import java.io.Serializable;
import java.util.List;

import com.zjlp.face.util.page.Aide;

/**
 * 数据库交互DO
 * @ClassName: AccountDo 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author lys
 * @date 2015年3月12日 下午4:01:02
 */
public class AccountDo implements Serializable {

	private static final long serialVersionUID = 477184411890902872L;
	//辅助
	private Aide aide;
	private Long userId;
	//钱包
	private Account account;
	//资金流转记录
	private AccountOperationRecord accountOperationRecord;
	//钱包id
	private Long accountId;
	//资金来源者操作类型
	private List<Integer> fromTypeList;
	//资金流转处者操作类型
	private List<Integer> toTypeList;
	// 查询状态List
	private List<Integer> status;
	//查询状态 1为收入， 2为支出， 3为提现
	private Integer type;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public List<Integer> getStatus() {
		return status;
	}
	public void setStatus(List<Integer> status) {
		this.status = status;
	}
	public Aide getAide() {
		return null == aide ? new Aide() : aide;
	}
	public void setAide(Aide aide) {
		this.aide = aide;
	}
	public Account getAccount() {
		return null == account ? new Account() : account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public AccountOperationRecord getAccountOperationRecord() {
		return null == accountOperationRecord ? new AccountOperationRecord()
				: accountOperationRecord;
	}
	public void setAccountOperationRecord(
			AccountOperationRecord accountOperationRecord) {
		this.accountOperationRecord = accountOperationRecord;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public List<Integer> getFromTypeList() {
		return fromTypeList;
	}
	public void setFromTypeList(List<Integer> fromTypeList) {
		this.fromTypeList = fromTypeList;
	}
	public List<Integer> getToTypeList() {
		return toTypeList;
	}
	public void setToTypeList(List<Integer> toTypeList) {
		this.toTypeList = toTypeList;
	}
	
}
