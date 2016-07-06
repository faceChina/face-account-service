package com.zjlp.face.account.service.factory;

import java.util.Date;

import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountOperationRecord;
import com.zjlp.face.account.util.ConstantsUtil;

public class AccountOperatorFactory {

	public static AccountOperationRecord create(String serialNumber, Account fromAccount,
			Account toAccount, Long amount, Integer businessType, String remark) {
		
		AccountOperationRecord oprationRecord = new AccountOperationRecord();
		oprationRecord.setFromAccountId(fromAccount.getId());
		oprationRecord.setToAccountId(toAccount.getId());
		oprationRecord.setOperationAmount(amount);
		oprationRecord.setFromAmountBefore(fromAccount.getWithdrawAmount());
		oprationRecord.setFromAmountAfter(fromAccount.getWithdrawAmount() - amount);
		oprationRecord.setToAmountBefore(toAccount.getWithdrawAmount());
		oprationRecord.setToAmountAfter(toAccount.getWithdrawAmount() + amount);
		oprationRecord.setOperationType(businessType);
		oprationRecord.setSerialNumber(serialNumber);
		oprationRecord.setRemark(remark);
		Date date = new Date();
		oprationRecord.setUpdateTime(date);
		oprationRecord.setCreateTime(date);
		return oprationRecord;
	}
	/**
	 * 平台与第三方支付记录
	 * @Description: 
	 * @return AccountOperationRecord
	 * @date: 2015-10-28上午9:43:59
	 * @author: wangzhang
	 */
	public static AccountOperationRecord createThirdPayRecrod(String serialNumber, Account fromAccount,Account pingtaiAccount,
			 Long amount, Integer businessType, String remark, Integer payWay) {
		AccountOperationRecord oprationRecord = new AccountOperationRecord();
		oprationRecord.setFromAccountId(fromAccount.getId());
		oprationRecord.setToAccountId(pingtaiAccount.getId());
		oprationRecord.setFromAmountBefore(fromAccount.getWithdrawAmount());
		oprationRecord.setFromAmountAfter(fromAccount.getWithdrawAmount());
		oprationRecord.setToAmountBefore(pingtaiAccount.getWithdrawAmount());
		oprationRecord.setToAmountAfter(pingtaiAccount.getWithdrawAmount() + amount);
		oprationRecord.setOperationAmount(amount);
		oprationRecord.setOperationType(businessType);
		oprationRecord.setSerialNumber(serialNumber);
		oprationRecord.setRemark(remark);
		oprationRecord.setPayWayFlag(payWay);
		Date date = new Date();
		oprationRecord.setUpdateTime(date);
		oprationRecord.setCreateTime(date);
		return oprationRecord;
	}
	public static AccountOperationRecord createThirdPayRecrod(String serialNumber, Account fromAccount,
			 Long amount, Integer businessType, String remark, Integer payWay) {
		AccountOperationRecord oprationRecord = new AccountOperationRecord();
		oprationRecord.setFromAccountId(fromAccount.getId());
		oprationRecord.setToAccountId(ConstantsUtil.ACCOUNT_PINGTAI);
		oprationRecord.setOperationAmount(amount);
		oprationRecord.setOperationType(businessType);
		oprationRecord.setSerialNumber(serialNumber);
		oprationRecord.setRemark(remark);
		oprationRecord.setPayWayFlag(payWay);
		Date date = new Date();
		oprationRecord.setUpdateTime(date);
		oprationRecord.setCreateTime(date);
		return oprationRecord;
	}
	public static AccountOperationRecord createFreezeRecrod(
			String serialNumber, Account fromAccount, Account toAccount, Long amount,
			Integer businessType, String remark) {
		
		AccountOperationRecord oprationRecord = new AccountOperationRecord();
		oprationRecord.setFromAccountId(fromAccount.getId());
		oprationRecord.setToAccountId(toAccount.getId());
		oprationRecord.setOperationAmount(amount);
		oprationRecord.setToAmountBefore(toAccount.getWithdrawAmount());
		oprationRecord.setToAmountAfter(toAccount.getWithdrawAmount() + amount);
		oprationRecord.setOperationType(businessType);
		oprationRecord.setSerialNumber(serialNumber);
		oprationRecord.setRemark(remark);
		
		Date date = new Date();
		oprationRecord.setUpdateTime(date);
		oprationRecord.setCreateTime(date);
		return oprationRecord;
	}
	
	
	public static AccountOperationRecord createRedenvelopeRefundRecord(Account oldToAccount, Account oldFromAccount, Long operateAmount, String serialNumber) {
		AccountOperationRecord oprationRecord = new AccountOperationRecord();
		oprationRecord.setFromAccountId(oldFromAccount.getId());
		oprationRecord.setToAccountId(oldToAccount.getId());
		oprationRecord.setOperationAmount(operateAmount);
		oprationRecord.setToAmountBefore(oldToAccount.getWithdrawAmount());
		oprationRecord.setToAmountAfter(oldToAccount.getWithdrawAmount() + operateAmount);
		oprationRecord.setFromAmountBefore(oldFromAccount.getWithdrawAmount());
		oprationRecord.setFromAmountAfter(oldFromAccount.getWithdrawAmount() - operateAmount);
		oprationRecord.setFromCommissionBefore(oldFromAccount.getWithdrawCommission());
		oprationRecord.setFromCommissionAfter(oldFromAccount.getWithdrawCommission());
		oprationRecord.setToCommissionBefore(oldToAccount.getWithdrawCommission());
		oprationRecord.setToCommissionAfter(oldToAccount.getWithdrawCommission());
		oprationRecord.setOperationType(AccountOperationRecord.TYPE_REDENVELOPE_REFUND);
		oprationRecord.setSerialNumber(serialNumber);
		oprationRecord.setRemark("红包退款");
		
		Date date = new Date();
		oprationRecord.setUpdateTime(date);
		oprationRecord.setCreateTime(date);
		return oprationRecord;
	}
}
