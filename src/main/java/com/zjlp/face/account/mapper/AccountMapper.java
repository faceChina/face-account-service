package com.zjlp.face.account.mapper;

import java.util.Map;

import com.zjlp.face.account.domain.Account;

public interface AccountMapper {

	int deleteByPrimaryKey(Long id);

	int insert(Account record);

	int insertSelective(Account record);

	Account selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Account record);

	int updateByPrimaryKey(Account record);

	Account selectAccountByRemoteId(Account account);

	void updateCellById(Account account);

	void updateCellByRemoteId(Account account);

	void updatePaymentCodeById(Account account);

	void updatePaymentCodeByRmoteId(Account account);

	void updateWithdrawAmount(Account account);

	void updateConsumeAmountById(Account account);

	void updateCommissionById(Account account);

	Long selectWithdrawAmountById(Long id);

	Long selectWithdrawAmountByRemoteId(Account account);

	Account selectByRemoteIdLock(Account account);

	// ------------------------------
	// 以下是支付服务所用接口
	// ------------------------------

	void platfromSendAmount(Map<String, Object> param);

	void sumAccountAmount(Map<String, Object> param);
	
	Account selectByPrimaryKeyLock(Long id);

	void sumAccountCommission(Map<String, Object> param);

	void minusAccountAmount(Map<String, Object> param);

	void sumConsumeAmount(Account account);

	void minusAccountCommission(Map<String, Object> param);

	Account selectByInvitationCode(String invitationCode);

	Integer selectByUserIdAndPaymentCode(Account account);

	void increaseSlcoin(Account account);

	void decreaseSlcoin(Account account);
}