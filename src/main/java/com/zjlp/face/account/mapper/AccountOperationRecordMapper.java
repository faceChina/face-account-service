package com.zjlp.face.account.mapper;

import java.util.List;

import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountDo;
import com.zjlp.face.account.domain.AccountOperationRecord;

public interface AccountOperationRecordMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(AccountOperationRecord record);

    int insertSelective(AccountOperationRecord record);

    AccountOperationRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountOperationRecord record);

    int updateByPrimaryKey(AccountOperationRecord record);

	List<AccountOperationRecord> selectRecordListBySerialNo(
			AccountOperationRecord record);

	List<AccountOperationRecord> selectRecordPage(AccountDo accountDo);

	int selectCount(AccountDo accountDo);

	List<AccountOperationRecord> findUserOperationRecord(AccountDo accountDo);

	Integer getCountWithStatus(AccountDo accountDo);

	Long getUndealSumAmount(Account account);

	Long getMaxId();

	void updateUndealRecord(Long maxId);

	Integer selectCounts(AccountOperationRecord record);
}