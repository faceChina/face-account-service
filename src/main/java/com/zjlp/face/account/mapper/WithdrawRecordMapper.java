package com.zjlp.face.account.mapper;

import java.util.List;

import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.WithdrawRecordDto;

public interface WithdrawRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WithdrawRecord record);

    int insertSelective(WithdrawRecord record);

    WithdrawRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WithdrawRecord record);

    int updateByPrimaryKey(WithdrawRecord record);

    //--------------------------------------------
	WithdrawRecord selectBySeriNo(String transferSerino);

	void updateStateById(WithdrawRecord withdrawRecord);

	void editRecordBalanceAf(WithdrawRecord withdrawRecord);

	Integer selectCount(WithdrawRecordDto recordDto);

	List<WithdrawRecordDto> selectPageList(WithdrawRecordDto recordDto);

	void updateStatesBySerialNo(WithdrawRecord record);
}