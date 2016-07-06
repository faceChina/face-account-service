package com.zjlp.face.account.dao;

import com.zjlp.face.account.domain.Account;

public interface AccountDao {

	Long addAccount(Account account);

	Account getAccountById(Long id);

	Account getAccountByRemoteId(Account account);

	void editCellById(Account account);

	void editCellByRemoteId(Account account);

	void editPaymentCodeById(Account account);

	void editPaymentCodeByRmoteId(Account account);

	void editWithdrawAmount(Account newAccount);

	void editConsumeAmountById(Account newAccount);

	void editAccount(Account account);

	Account getByPrimaryKeyLock(Long id);

	void editWithdrawCommission(Account newAccount);

	Long getWithdrawAmountById(Long accountId);

	Long getWithdrawAmountByRemoteId(Account account);

	Account getByRemoteIdLock(Account account);
	
	

	// ------------------------------
	// 以下是支付服务所用接口
	// ------------------------------
	
	

	void platfromSendAmount(Long valueOf, Long totalPrice);

	void sumAccountAmount(Long id, Long totalPrice);

	Account getAccountByIdLock(Long valueOf);

	void sumAccountCommission(Long id, Long price);

	void minusAccountAmount(Long id, Long price);

	void sumConsumeAmount(Account newAccount);

	void minusAccountCommission(Long id, Long price);

	Account getAccountByInvitationCode(String onInvitationCode);

	boolean checkPaymentCode(Account checkUser);

	void increaseSlcoin(Account account);
	
	void decreaseSlcoin(Account account);

	
}
