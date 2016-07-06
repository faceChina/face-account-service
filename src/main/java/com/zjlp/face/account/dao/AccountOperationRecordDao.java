package com.zjlp.face.account.dao;

import java.util.List;

import com.zjlp.face.account.domain.AccountDo;
import com.zjlp.face.account.domain.AccountOperationRecord;

public interface AccountOperationRecordDao {

	Long addOperationRecord(AccountOperationRecord operationRecord);

	List<AccountOperationRecord> findRecordListBySerialNo(String serialNo, Integer type);

	void editOperationRecord(AccountOperationRecord record);

	List<AccountOperationRecord> findRecordPage(AccountDo accountDo);
	
	int getCount(AccountDo accountDo);

	AccountOperationRecord getOperationRecordById(Long id);

	List<AccountOperationRecord> findUserOperationRecord(AccountDo accountDo);

	Integer getCountWithStatus(AccountDo accountDo);

	void insertRedEnvelopeRefundRecord(AccountOperationRecord record);
	
	/**
	 * @Description: 获取账号操作记录最大ID
	 * @ClassName: AccountService.getAccountOperationRecordMaxId
	 * @return
	 * @date: 2015年10月24日 下午3:00:50
	 * @author: zyl
	 */
	Long getMaxId();
	
	/**
	 * @Description: 定时任务未处理金额 
	 * @ClassName: AccountService.getUndealSumAmount
	 * @param id
	 * @param type 1,入账;2,出账
	 * @return
	 * @date: 2015年10月24日 下午2:57:14
	 * @author: zyl
	 */
	Long getUndealSumAmount(Long id, Integer type);
	
	/**
	 * @Description: 修改定时任务标志为已处理
	 * @ClassName: AccountOperationRecordDao.updateUndealRecord
	 * @param maxId
	 * @date: 2015年10月24日 下午4:49:34
	 * @author: zyl
	 */
	void updateUndealRecord(Long maxId);

	Integer getOperationCounts(AccountOperationRecord record);

}
