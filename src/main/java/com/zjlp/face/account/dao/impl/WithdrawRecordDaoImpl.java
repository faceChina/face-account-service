package com.zjlp.face.account.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zjlp.face.account.dao.WithdrawRecordDao;
import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.WithdrawRecordDto;
import com.zjlp.face.account.mapper.WithdrawRecordMapper;

@Repository("withdrawDao")
public class WithdrawRecordDaoImpl implements WithdrawRecordDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void addWithdrawRecord(WithdrawRecord withdrawRecord) {
		sqlSession.getMapper(WithdrawRecordMapper.class).insertSelective(withdrawRecord);
	}

	@Override
	public WithdrawRecord getRecordBySeriNum(String transferSerino) {
		return sqlSession.getMapper(WithdrawRecordMapper.class).selectBySeriNo(transferSerino);
	}

	@Override
	public void editRecordStates(WithdrawRecord withdrawRecord) {
		sqlSession.getMapper(WithdrawRecordMapper.class).updateStateById(withdrawRecord);
	}

	@Override
	public void editRecordBalanceAf(String serialNo, Long balance) {
		WithdrawRecord withdrawRecord = new WithdrawRecord();
		withdrawRecord.setSeriNumber(serialNo);
		withdrawRecord.setAmountAfter(balance);
		withdrawRecord.setUpdateTime(new Date());
		sqlSession.getMapper(WithdrawRecordMapper.class).editRecordBalanceAf(withdrawRecord);
	}
	
	
	

	@Override
	public WithdrawRecord getWithdrawRecordById(Long id) {
		return sqlSession.getMapper(WithdrawRecordMapper.class).selectByPrimaryKey(id);
	}

	@Override
	public Integer getCount(WithdrawRecordDto recordDto) {
		return sqlSession.getMapper(WithdrawRecordMapper.class).selectCount(recordDto);
	}

	@Override
	public List<WithdrawRecordDto> findPageList(WithdrawRecordDto recordDto) {
		return sqlSession.getMapper(WithdrawRecordMapper.class).selectPageList(recordDto);
	}

	@Override
	public void editStatesBySerialNo(WithdrawRecord record) {
		sqlSession.getMapper(WithdrawRecordMapper.class).updateStatesBySerialNo(record);
	}

}
