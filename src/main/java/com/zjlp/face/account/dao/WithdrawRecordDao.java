package com.zjlp.face.account.dao;

import java.util.List;

import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.WithdrawRecordDto;

public interface WithdrawRecordDao {

	void addWithdrawRecord(WithdrawRecord withdrawRecord);
	
	void editRecordBalanceAf(String serialNo, Long balance);
	
	WithdrawRecord getRecordBySeriNum(String transferSerino);

	void editRecordStates(WithdrawRecord withdrawRecord);

	WithdrawRecord getWithdrawRecordById(Long id);

	Integer getCount(WithdrawRecordDto recordDto);

	List<WithdrawRecordDto> findPageList(WithdrawRecordDto recordDto);

	void editStatesBySerialNo(WithdrawRecord record);

}
