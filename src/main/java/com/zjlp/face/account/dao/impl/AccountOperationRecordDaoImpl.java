package com.zjlp.face.account.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zjlp.face.account.dao.AccountOperationRecordDao;
import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountDo;
import com.zjlp.face.account.domain.AccountOperationRecord;
import com.zjlp.face.account.mapper.AccountOperationRecordMapper;

@Repository("accountOperationRecordDao")
public class AccountOperationRecordDaoImpl implements AccountOperationRecordDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public Long addOperationRecord(AccountOperationRecord operationRecord) {
		sqlSession.getMapper(AccountOperationRecordMapper.class).insertSelective(operationRecord);
		return operationRecord.getId();
	}

	@Override
	public List<AccountOperationRecord> findRecordListBySerialNo(
			String serialNo, Integer type) {
		AccountOperationRecord record = new AccountOperationRecord();
		record.setSerialNumber(serialNo);
		record.setOperationType(type);
		return sqlSession.getMapper(AccountOperationRecordMapper.class).selectRecordListBySerialNo(record);
	}

	@Override
	public void editOperationRecord(AccountOperationRecord record) {
		sqlSession.getMapper(AccountOperationRecordMapper.class).updateByPrimaryKeySelective(record);
	}

	@Override
	public List<AccountOperationRecord> findRecordPage(AccountDo accountDo) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).selectRecordPage(accountDo);
	}

	@Override
	public int getCount(AccountDo accountDo) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).selectCount(accountDo);
	}

	@Override
	public AccountOperationRecord getOperationRecordById(Long id) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).selectByPrimaryKey(id);
	}

	@Override
	public List<AccountOperationRecord> findUserOperationRecord(
			AccountDo accountDo) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).findUserOperationRecord(accountDo);
	}

	@Override
	public Integer getCountWithStatus(AccountDo accountDo) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).getCountWithStatus(accountDo);
	}

	@Override
	public void insertRedEnvelopeRefundRecord(AccountOperationRecord record) {
		sqlSession.getMapper(AccountOperationRecordMapper.class).insertSelective(record);
	}

	@Override
	public Long getMaxId() {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).getMaxId();
	}

	@Override
	public Long getUndealSumAmount(Long id, Integer type) {
		Account account=new Account();
		account.setId(id);
		account.setType(type);
		return sqlSession.getMapper(AccountOperationRecordMapper.class).getUndealSumAmount(account);
	}

	@Override
	public void updateUndealRecord(Long maxId) {
		sqlSession.getMapper(AccountOperationRecordMapper.class).updateUndealRecord(maxId);
	}

	@Override
	public Integer getOperationCounts(AccountOperationRecord record) {
		return sqlSession.getMapper(AccountOperationRecordMapper.class).selectCounts(record);
	}
	
}
