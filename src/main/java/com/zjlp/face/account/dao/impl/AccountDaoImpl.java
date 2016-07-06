package com.zjlp.face.account.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zjlp.face.account.dao.AccountDao;
import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.mapper.AccountMapper;

@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public Long addAccount(Account account) {
		sqlSession.getMapper(AccountMapper.class).insertSelective(account);
		return account.getId();
	}

	@Override
	public Account getAccountById(Long id) {
		return sqlSession.getMapper(AccountMapper.class).selectByPrimaryKey(id);
	}

	@Override
	public Account getAccountByRemoteId(Account account) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectAccountByRemoteId(account);
	}

	@Override
	public void editCellById(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateCellById(account);
	}

	@Override
	public void editCellByRemoteId(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateCellByRemoteId(account);
	}

	@Override
	public void editPaymentCodeById(Account account) {
		sqlSession.getMapper(AccountMapper.class)
				.updatePaymentCodeById(account);
	}

	@Override
	public void editPaymentCodeByRmoteId(Account account) {
		sqlSession.getMapper(AccountMapper.class).updatePaymentCodeByRmoteId(
				account);
	}

	@Override
	public void editWithdrawAmount(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateWithdrawAmount(account);
	}

	@Override
	public void editConsumeAmountById(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateConsumeAmountById(
				account);
	}

	@Override
	public void editAccount(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateByPrimaryKeySelective(account);
	}

	@Override
	public Account getByPrimaryKeyLock(Long id) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectByPrimaryKeyLock(id);
	}

	@Override
	public void editWithdrawCommission(Account account) {
		sqlSession.getMapper(AccountMapper.class).updateCommissionById(account);
	}

	@Override
	public Long getWithdrawAmountById(Long id) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectWithdrawAmountById(id);
	}

	@Override
	public Long getWithdrawAmountByRemoteId(Account account) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectWithdrawAmountByRemoteId(account);
	}

	@Override
	public Account getByRemoteIdLock(Account account) {
		return sqlSession.getMapper(AccountMapper.class).selectByRemoteIdLock(
				account);
	}

	// ------------------------------
	// 是支付服务所用接口 --------- 开始
	// ------------------------------


	@Override
	public void platfromSendAmount(Long id, Long price) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("price", price);
		sqlSession.getMapper(AccountMapper.class).platfromSendAmount(param);
	}

	@Override
	public void sumAccountAmount(Long id, Long price) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("price", price);
		sqlSession.getMapper(AccountMapper.class).sumAccountAmount(param);
	}

	@Override
	public Account getAccountByIdLock(Long id) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectByPrimaryKeyLock(id);
	}

	@Override
	public void sumAccountCommission(Long id, Long price) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("price", price);
		sqlSession.getMapper(AccountMapper.class).sumAccountCommission(param);
	}

	@Override
	public void minusAccountAmount(Long id, Long price) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("price", price);
		sqlSession.getMapper(AccountMapper.class).minusAccountAmount(param);
	}

	@Override
	public void sumConsumeAmount(Account account) {
		sqlSession.getMapper(AccountMapper.class).sumConsumeAmount(account);
	}

	@Override
	public void minusAccountCommission(Long id, Long price) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("price", price);
		sqlSession.getMapper(AccountMapper.class).minusAccountCommission(param);
	}

	@Override
	public Account getAccountByInvitationCode(String invitationCode) {
		return sqlSession.getMapper(AccountMapper.class)
				.selectByInvitationCode(invitationCode);
	}

	@Override
	public boolean checkPaymentCode(Account account) {
		Integer row = sqlSession.getMapper(AccountMapper.class)
				.selectByUserIdAndPaymentCode(account);
		return row == 1 ? true : false;
	}

	@Override
	public void increaseSlcoin(Account account) {
		sqlSession.getMapper(AccountMapper.class).increaseSlcoin(account);
	}

	@Override
	public void decreaseSlcoin(Account account) {
		sqlSession.getMapper(AccountMapper.class).decreaseSlcoin(account);
	}

	// ------------------------------
	// 支付服务所用接口  -----------  结束
	// ------------------------------

}
