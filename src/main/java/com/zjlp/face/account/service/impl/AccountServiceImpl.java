package com.zjlp.face.account.service.impl;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zjlp.face.account.dao.AccountDao;
import com.zjlp.face.account.dao.AccountOperationRecordDao;
import com.zjlp.face.account.dao.WithdrawRecordDao;
import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountDo;
import com.zjlp.face.account.domain.AccountOperationRecord;
import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.exception.AccountException;
import com.zjlp.face.account.service.AccountService;
import com.zjlp.face.account.service.factory.AccountOperatorFactory;
import com.zjlp.face.account.util.ConstantsUtil;
import com.zjlp.face.util.calcu.CalculateUtils;
import com.zjlp.face.util.constants.ConstantsMethod;
import com.zjlp.face.util.date.DateStyle;
import com.zjlp.face.util.date.DateUtil;
import com.zjlp.face.util.exception.AssertUtil;
import com.zjlp.face.util.file.PropertiesUtil;
import com.zjlp.face.util.page.Aide;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private Logger accountLog = Logger.getLogger(getClass());
	private Logger withdrawLog = Logger.getLogger("withdrawInfoLog");
	
	private static final String JZ_ACCOUNT_ID = "JZ_ACCOUNT_ID";
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountOperationRecordDao accountOperationRecordDao;
	@Autowired
	private WithdrawRecordDao withdrawRecordDao;

	@Override
	public synchronized Long addAccount(String shopNo, String phone,
			String invitationCode, String email, Long margin)
			throws AccountException {
		try {
			AssertUtil.hasLength(shopNo, "Param [shopNo] can not be null.");
			AssertUtil.hasLength(invitationCode, "Param [invitationCode] can not be null.");
			AssertUtil.hasLength(phone, "Param [phone] can not be null.");
			Long id = this.addAccount(shopNo, Account.REMOTE_TYPE_SHOP, phone,
					invitationCode, email, margin);
			return id;
		} catch (Exception e) {
			accountLog.error("[addAccount] Add account faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public Long addAccount(Long userId, String phone, String email, Long margin)
			throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			AssertUtil.hasLength(phone, "Param [phone] can not be null.");
			Long id = this.addAccount(String.valueOf(userId),
					Account.REMOTE_TYPE_USER, phone, null, email, margin);
			return id;
		} catch (Exception e) {
			accountLog.error("[addAccount] Add account faild.", e);
			throw new AccountException(e);
		}
	}
	
	/**
	 * 添加钱包
	 * 
	 * @Title: addAccount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键id
	 * @param remoteType
	 *            外键类型
	 * @param phone
	 *            手机号码
	 * @param invitationCode
	 *            邀请码
	 * @return
	 * @throws AccountException
	 * @date 2015年3月17日 下午1:30:45
	 * @author lys
	 */
	private Long addAccount(String remoteId, Integer remoteType, String phone,
			String invitationCode, String email, Long margin) throws AccountException {
		try {
			AssertUtil.hasLength(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			AssertUtil.hasLength(phone, "Param [phone] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[addAccount] Add account begain, Param[").append("remoteId=")
					.append(remoteId).append(", remoteType=").append(remoteType).append(", phone=")
					.append(phone).append(",invitationCode").append(invitationCode).append(", email")
					.append(email).append(",margin").append(margin).append("]"));
			//过滤重复
			Account account = new Account(remoteId, remoteType);
			Account repeat = accountDao.getAccountByRemoteId(account);
			AssertUtil.isNull(repeat, "Account[remoteType={}, remoteId={}] is already exists.", remoteType, remoteId);
			// 钱包初始化
			Date date = new Date();
			account.setInvitationCode(null);
			account.setType(Account.REMOTE_TYPE_USER.equals(remoteType) 
					? Account.TYPE_USER : Account.TYPE_SHOP);
			account.setWithdrawAmount(0L);
			account.setWithdrawCommission(0L);
			account.setConsumeAmount(0L);
			account.setMargin(margin);
			account.setFreezeAmount(0L);
			account.setTosendAmount(0L);
			account.setInvitationCode(invitationCode);
			account.setEmail(email);
			// 用户初始支付密码为空
			account.setPaymentCode(null);
			account.setSystemCode(null);
			account.setName(null);
			account.setCell(phone);
			account.setTelephone(null);
			account.setUpdateTime(date);
			account.setCreateTime(date);
			Long id = accountDao.addAccount(account);
			accountLog.info(sb.delete(0, sb.length()).append("[addAccount] Add account successful. [id=")
					.append(id).append("]"));
			return id;
		} catch (Exception e) {
			accountLog.error("[addAccount] Add account faild.", e);
			throw new AccountException(e);
		}
	}
	
	
	/**
	 * 添加钱包
	 * 
	 * @Title: addAccount
	 * @Description: (用于微信免登陆注册)
	 * @param remoteId
	 *            外键id
	 * @param remoteType
	 *            外键类型
	 * @return
	 * @throws AccountException
	 * @date 2015年9月2日 下午17:30:20
	 * @author talo
	 */
	public synchronized Long addAccount(String remoteId, Integer remoteType) throws AccountException {
		try {
			AssertUtil.hasLength(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[addAccount] Add account begain, Param[").append("remoteId=")
					.append(remoteId).append(", remoteType=").append(remoteType).append("]"));
			//过滤重复
			Account account = new Account(remoteId, remoteType);
			Account repeat = accountDao.getAccountByRemoteId(account);
			AssertUtil.isNull(repeat, "Account[remoteType={}, remoteId={}] is already exists.", remoteType, remoteId);
			// 钱包初始化
			Date date = new Date();
			account.setInvitationCode(null);
			account.setType(Account.REMOTE_TYPE_USER.equals(remoteType) 
					? Account.TYPE_USER : Account.TYPE_SHOP);
			account.setWithdrawAmount(0L);
			account.setWithdrawCommission(0L);
			account.setConsumeAmount(0L);
			account.setMargin(0L);
			account.setFreezeAmount(0L);
			account.setTosendAmount(0L);
			account.setInvitationCode(null);
			account.setEmail(null);
			// 用户初始支付密码为空
			account.setPaymentCode(null);
			account.setSystemCode(null);
			account.setName(null);
			account.setCell(null);
			account.setTelephone(null);
			account.setUpdateTime(date);
			account.setCreateTime(date);
			Long id = accountDao.addAccount(account);
			accountLog.info(sb.delete(0, sb.length()).append("[addAccount] Add account successful. [id=")
					.append(id).append("]"));
			return id;
		} catch (Exception e) {
			accountLog.error("[addAccount] Add account faild.", e);
			throw new AccountException(e);
		}
	}


	@Override
	public Account getAccountById(Long id) throws AccountException {
		try {
			AssertUtil.notNull(id, "Param [id] can not be null.");
			Account account = accountDao.getAccountById(id);
			return account;
		} catch (Exception e) {
			accountLog.error("[getAccountById] Get account faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public Account getAccountByUserId(Long userId) throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			Account selector = new Account(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			Account account = accountDao.getAccountByRemoteId(selector);
			return account;
		} catch (Exception e) {
			accountLog.error("[getAccountByUserId] Get account faild.", e);
			throw new AccountException(e);
		}

	}

	@Override
	public Account getAccountByRemoteId(String remoteId, Integer remoteType)
			throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			Account selector = new Account(remoteId, remoteType);
			Account account = accountDao.getAccountByRemoteId(selector);
			return account;
		} catch (Exception e) {
			accountLog.error("[getAccountByRemoteId] Get account faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean editCellById(Long id, String cell)
			throws AccountException {
		try {
			AssertUtil.notNull(id, "Param [id] can not be null.");
			AssertUtil.hasLength(cell, "Param [cell] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[editCellById] Edit account's phone begin, Param [id=")
					.append(id).append(", cell=").append(cell).append("]"));
			Account account = new Account();
			account.setId(id);
			account.setCell(cell);
			account.setUpdateTime(new Date());
			accountDao.editCellById(account);
			accountLog.info("[editCellById] Edit account's phone successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[editCellById] Edit account's phone faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean editCellByUserId(Long userId, String cell)
			throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			AssertUtil.hasLength(cell, "Param [cell] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[editCellByUserId] Edit account's phone begin, Param [userId=")
					.append(userId).append(", cell=").append(cell).append("]"));
			Account account = new Account(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			account.setCell(cell);
			account.setUpdateTime(new Date());
			accountDao.editCellByRemoteId(account);
			accountLog.info("[editCellByUserId] Edit account's phone successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[editCellByUserId] Edit account's phone faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean editPaymentCodeById(Long id, String paymentCode)
			throws AccountException {
		try {
			AssertUtil.notNull(id, "Param [id] can not be null.");
			AssertUtil.hasLength(paymentCode, "Param [paymentCode] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[editPaymentCodeById] Edit account's paymentcode begin, Param [id=")
					.append(id).append(", paymentCode=").append(paymentCode).append("]"));
			Account account = new Account();
			account.setId(id);
			account.setPaymentCode(paymentCode);
			account.setUpdateTime(new Date());
			accountDao.editPaymentCodeById(account);
			accountLog.info("[editPaymentCodeById] Edit account's paymentcode successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[editPaymentCodeById] Edit account's paymentcode faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean editPaymentCodeByUserId(Long userId, String paymentCode)
			throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			AssertUtil.hasLength(paymentCode, "Param [paymentCode] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[editPaymentCodeByUserId] Edit account's paymentcode begin, Param [userId=")
					.append(userId).append(", paymentCode=").append(paymentCode).append("]"));
			Account account = new Account(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			account.setPaymentCode(paymentCode);
			account.setUpdateTime(new Date());
			accountDao.editPaymentCodeByRmoteId(account);
			accountLog.info("[editPaymentCodeByUserId] Edit account's paymentcode successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[editPaymentCodeByUserId] Edit account's paymentcode faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public Long getWithdrawAmountById(Long id) throws AccountException {
		try {
			AssertUtil.notNull(id, "Param [id] can not be null.");
			Account account = accountDao.getAccountById(id);
			AssertUtil.notNull(account, "[getWithdrawAmountById] Can not find account with id={}", id);
			return account.getWithdrawAmount();
		} catch (Exception e) {
			accountLog.error("[getWithdrawAmountById] Get account's withdraw amout faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public Long getWithdrawAmountByUserId(Long userId) throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			Account selector = new Account(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			Account account = accountDao.getAccountByRemoteId(selector);
			AssertUtil.notNull(account, "[getWithdrawAmountByUserId] Can not find account with userId={}", userId);
			return account.getWithdrawAmount();
		} catch (Exception e) {
			accountLog.error("[getWithdrawAmountByUserId] Get account's withdraw amout faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean sumWithdrawAmount(String remoteId, Integer remoteType, Long amount)
			throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumWithdrawAmount] Param[remoteId=")
					.append(remoteId).append(", remoteType").append(remoteType).append(", amount=").append(amount).append("]"));
			Account account = this.getValidAccountByRemoteId(remoteId, remoteType);
			this.sumWithdrawAmount(account.getId(), amount);
			return true;
		} catch (Exception e) {
			accountLog.error("[sumWithdrawAmount] Edit account's withdraw amout faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public boolean sumWithdrawAmount(Long accountId, Long amount)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param [accountId] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumWithdrawAmount] Param[accountId=")
					.append(accountId).append(", amount=").append(amount).append("]"));
			/*Account account = this.getValidByPrimaryKeyLock(accountId);
			AssertUtil.notNull(account, "Can not find account with id={}", accountId);
			accountLog.info(sb.delete(0, sb.length()).append("[sumWithdrawAmount] Old withdraw amount=")
					.append(account.getWithdrawAmount()));
			Long newAmount = CalculateUtils.getSum(account.getWithdrawAmount(), amount);
			AssertUtil.isTrue(newAmount >= 0, "Old withdrawAmount:{} + new withdrawAmount:{} < 0", account.getWithdrawAmount(), amount);*/
			//Account newAccount = new Account(account.getId(), newAmount, null, null, new Date());
			//accountDao.editWithdrawAmount(newAccount);
			accountDao.sumAccountAmount(accountId, amount);
			if(amount<0){
				Account account=accountDao.getAccountById(accountId);
				AssertUtil.isTrue(account.getWithdrawAmount()>=0, "账户余额不足", "账户余额不足");
			}
			return true;
		} catch (Exception e) {
			accountLog.error("[sumWithdrawAmount] Edit account's withdraw amout faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean extractAmount(Long userId, String shopNo, Long amount, String serialNo,String remark)
			throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param [userId] can not be null.");
			AssertUtil.hasLength(shopNo, "Param [shopNo] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			AssertUtil.notNull(serialNo, "Param [serialNo] can not be null.");
			//账户获取
			Account shopAccount = this.getValidAccountByRemoteId(shopNo, Account.REMOTE_TYPE_SHOP);
			AssertUtil.isTrue(shopAccount.getWithdrawAmount() >= amount, 
					"shop's amount={} less than extract amount={}. shopNo={}",
					shopAccount.getWithdrawAmount(), amount, shopNo);
			Account userAccount = this.getValidAccountByRemoteId(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			//转出
			this.sumWithdrawAmount(shopAccount.getId(), - amount);
			this.sumWithdrawAmount(userAccount.getId(), amount);
			//操作记录生成
			this.addExtractAmountOparetion(serialNo, shopAccount.getId(), userAccount.getId(), 
					amount, shopAccount.getWithdrawAmount(), userAccount.getWithdrawAmount(),remark);
			return true;
		} catch (Exception e) {
			accountLog.error("[editWithdrawAmount] Add account's consume amout faild.", e);
			throw new AccountException(e);
		}
	}
	
	/**
	 * 转出操作记录生成
	 * 
	 * @Title: addExtractAmountOparetion
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param serialNo
	 *            流水号
	 * @param fromId
	 *            来源
	 * @param toId
	 *            去处
	 * @param operatAmount
	 *            操作金额
	 * @param shopAmount
	 *            店铺原始金额
	 * @param userAmount
	 *            用户原始金额
	 * @date 2015年3月11日 下午9:08:35
	 * @author lys
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void addExtractAmountOparetion(String serialNo, Long fromId, Long toId, 
			Long operatAmount, Long shopAmount, Long userAmount,String remark) {
		AssertUtil.notNull(serialNo, "Param [serialNo] can not be null.");
		AssertUtil.notNull(fromId, "Param [fromId] can not be null.");
		AssertUtil.notNull(toId, "Param [toId] can not be null.");
		AssertUtil.notNull(operatAmount, "Param [operatAmount] can not be null.");
		AssertUtil.notNull(shopAmount, "Param [shopAmount] can not be null.");
		AssertUtil.notNull(userAmount, "Param [userAmount] can not be null.");
		Date date = new Date();
		AccountOperationRecord operationRecord = new AccountOperationRecord();
		operationRecord.setSerialNumber(serialNo);
		operationRecord.setFromAccountId(fromId);
		operationRecord.setToAccountId(toId);
		operationRecord.setOperationAmount(operatAmount);
		operationRecord.setFromAmountBefore(shopAmount);
		operationRecord.setToAmountBefore(userAmount);
		operationRecord.setFromAmountAfter(shopAmount - operatAmount);
		operationRecord.setToAmountAfter(userAmount + operatAmount);
		operationRecord.setOperationType(AccountOperationRecord.TYPE_BALANCE_EXTRACT);  //一键余额提取
		operationRecord.setRemark(remark);
		operationRecord.setCreateTime(date);
		operationRecord.setUpdateTime(date);
		accountOperationRecordDao.addOperationRecord(operationRecord);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized boolean extractCommission(String remoteId, Integer remoteType,
			Long amount, String serialNo) throws AccountException {
		try {
			AssertUtil.hasLength(remoteId, "Param[remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param[remoteType] can not be null.");
			AssertUtil.notNull(amount, "Param[amount] can not be null.");
			AssertUtil.hasLength(serialNo, "Param[serialNo] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[extractCommission] Extract commission begin, Param[remoteId=")
					.append(remoteId).append(", remoteType=").append(remoteType).append(", amount=")
					.append(amount).append("serialNo=").append(serialNo));
			//验证金额
			Account account = this.getValidByRemoteIdLock(remoteId, remoteType);
			AssertUtil.isTrue(account.getWithdrawCommission() >= amount, 
					"Account [id={}]'s commission must greate than {} for extract.",
					account.getId(), amount);
			//指定佣金转入余额
			this.sumWithdrawCommission(account.getId(), -amount);
			this.sumWithdrawAmount(account.getId(), amount);
			accountLog.info("[extractCommission] Extract commission successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[extractCommission] has error.", e);
			throw new AccountException(e);
		}
	}

	@Override
	public Integer getLastWithdrawCount(Long userId) throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param[userId] can not be null.");
			Account account = this.getValidAccountByRemoteId(String.valueOf(userId), Account.REMOTE_TYPE_USER);
			String countStr = (String) PropertiesUtil.getContextProperty("withdraw_count");
			AssertUtil.isTrue(StringUtils.isNumeric(countStr), "Error property[key=withdraw_count, value="+countStr+"]");
			Integer maxCount = Integer.valueOf(countStr);
			String today = DateUtil.DateToString(new Date(), DateStyle.YYYYMMDD);
			//确认最后一次提现日期是否是当天
			if (today.equals(account.getWithdrawDate())) {
				//是,返回提现次数
				return maxCount - (null == account.getWithdrawCount() ? 0 : account.getWithdrawCount());
			} else {
				//否,返回提现次数
				return maxCount;
			}
		} catch (Exception e) {
			accountLog.error("[getLastWithdrawCount] has error.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addLastWithdrawCount(Long userId) throws AccountException {
		try {
			AssertUtil.notNull(userId, "Param[userId] can not be null.");
			Account account = this.getValidAccountByRemoteId(String.valueOf(userId), Account.REMOTE_TYPE_USER);
		    String today = DateUtil.DateToString(new Date(), DateStyle.YYYYMMDD);
		    //确认最后一次提现日期是否是当天
			if (today.equals(account.getWithdrawDate())) {
				account.setWithdrawCount(account.getWithdrawCount() + 1);
			} else {
				account.setWithdrawDate(today);
				account.setWithdrawCount(1);
			}
			accountDao.editAccount(account);
		} catch (Exception e) {
			accountLog.error("[addLastWithdrawCount] has error.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized boolean transferredFreeze(Long accountId, Long freezePrice, String serialNo)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notNull(freezePrice, "Param[freezePrice] can not be null.");
			AssertUtil.notNull(serialNo, "Param[serialNo] can not be null.");
			StringBuilder sb = new StringBuilder();
			withdrawLog.info(sb.append("[transferredFreeze] begin, Param[accountId=").append(accountId)
					.append(", freezePrice=").append(freezePrice).append(", serialNo=") .append(serialNo));
			//操作过滤
			List<AccountOperationRecord> freezeList = this.checkRepeatOperat(accountId, serialNo, 
					AccountOperationRecord.TYPE_WITHDRAW_TRANSFER);
			//验证金额
			Account account = this.getValidAccountById(accountId);
			withdrawLog.info(sb.delete(0, sb.length()).append("Amount before freeze=")
					.append(account.getWithdrawAmount()).append("， and freezeAmount=")
					.append(account.getFreezeAmount()));
			AssertUtil.isTrue(freezePrice <= account.getFreezeAmount(), "Less freeze amount to transfer : {} > {}", 
					freezePrice, account.getFreezeAmount());
			//金额转出
			Date date = new Date();
			Account newAccount = this.getWithdrawEditRecord(accountId, account.getWithdrawAmount(), 
					account.getFreezeAmount() - freezePrice, date);
			accountDao.editAccount(newAccount);
			//资金流转记录
			this.addUnfrrezeAccountOperationRecord(account, freezePrice, serialNo, AccountOperationRecord.TYPE_WITHDRAW_TRANSFER);
			this.editWithRecordStates(freezeList.get(0).getId(), ConstantsUtil.WD_STATE_SUCC, date);
			withdrawLog.info("[transferredFreeze] successful.");
			return true;
		} catch (Exception e) {
			withdrawLog.error("[transferredFreeze] has error.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized boolean returnFreeze(Long accountId, Long freezePrice, String serialNo)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notNull(freezePrice, "Param[freezePrice] can not be null.");
			AssertUtil.notNull(serialNo, "Param[serialNo] can not be null.");
			StringBuilder sb = new StringBuilder();
			withdrawLog.info(sb.append("[returnFreeze] begin, Param[accountId=").append(accountId)
					.append(", freezePrice=").append(freezePrice).append(", serialNo=") .append(serialNo));
			//操作过滤
			List<AccountOperationRecord> freezeList = this.checkRepeatOperat(accountId, serialNo, 
					AccountOperationRecord.TYPE_WITHDRAW_RETURN);
			//验证金额
			Account account = this.getValidAccountById(accountId);
			withdrawLog.info(sb.delete(0, sb.length()).append("Amount before freeze=")
					.append(account.getWithdrawAmount()).append("， and freezeAmount=")
					.append(account.getFreezeAmount()));
			AssertUtil.isTrue(freezePrice <= account.getFreezeAmount(), "Less amount to return : {} > {}", 
					freezePrice, account.getFreezeAmount());
			//金额转出
			Date date = new Date();
			Account newAccount = this.getWithdrawEditRecord(accountId, account.getWithdrawAmount() + freezePrice, 
					account.getFreezeAmount() - freezePrice, date);
			accountDao.editAccount(newAccount);
			//资金流转记录
			this.addUnfrrezeAccountOperationRecord(account, freezePrice, serialNo, AccountOperationRecord.TYPE_WITHDRAW_RETURN);
			//状态修改
			this.editWithRecordStates(freezeList.get(0).getId(), ConstantsUtil.WD_STATE_FAIL, date);
			withdrawLog.info("[returnFreeze] successful.");
			return true;
		} catch (Exception e) {
			withdrawLog.error("[returnFreeze] has error.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized boolean freeze(WithdrawReq withdrawReq,Long accountId, Long freezePrice, String serialNo)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notNull(freezePrice, "Param[freezePrice] can not be null.");
			AssertUtil.notNull(serialNo, "Param[serialNo] can not be null.");
			StringBuilder sb = new StringBuilder();
			withdrawLog.info(sb.append("[freeze] begin, Param[accountId=").append(accountId)
					.append(", freezePrice=").append(freezePrice).append(", serialNo=") .append(serialNo));
			//操作过滤
			this.checkRepeatOperat(accountId, serialNo, AccountOperationRecord.TYPE_WITHDRAW_AMOUNT);
			//验证金额
			Account account = this.getValidAccountById(accountId);
			withdrawLog.info(sb.delete(0, sb.length()).append("Amount before freeze=")
					.append(account.getWithdrawAmount()).append("， and freezeAmount=")
					.append(account.getFreezeAmount()));
			AssertUtil.isTrue(freezePrice <= account.getWithdrawAmount(), "Less amount to freeze : {} > {}", 
					account.getWithdrawAmount(), freezePrice);
			//冻结金额
			Account newAccount = getWithdrawEditRecord(accountId, account.getWithdrawAmount() - freezePrice, 
					account.getFreezeAmount() + freezePrice, new Date());
			accountDao.editAccount(newAccount);
			//操作记录
			this.addFrrezeAccountOperationRecord(withdrawReq,account, freezePrice, serialNo);
			withdrawLog.info("[freeze] successful.");
			return true;
		} catch (Exception e) {
			withdrawLog.error("[freeze] has error.", e);
			throw new AccountException(e);
		}
	}
	
	/**
	 * 提现操作过滤
	 * 
	 * @Title: checkRepeatOperat
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param serialNo
	 *            流水号
	 * @param operationType
	 *            操作类型
	 * @return
	 * @date 2015年3月11日 下午7:21:46
	 * @author lys
	 */
	private List<AccountOperationRecord> checkRepeatOperat(Long accountId, String serialNo, Integer operationType) {
		List<AccountOperationRecord> freezeList = null;
		//1.是否有冻结记录
		if (AccountOperationRecord.TYPE_WITHDRAW_RETURN.equals(operationType)
				|| AccountOperationRecord.TYPE_WITHDRAW_TRANSFER.equals(operationType)) {
			freezeList = accountOperationRecordDao
					.findRecordListBySerialNo(serialNo, AccountOperationRecord.TYPE_WITHDRAW_AMOUNT);
			AssertUtil.notEmpty(freezeList, "No freeze operation, accountId={}, serialNo={}", 
					accountId, serialNo);
		}
		//操作过滤
		List<AccountOperationRecord> repeatRecords = accountOperationRecordDao
				.findRecordListBySerialNo(serialNo, operationType);
		AssertUtil.isEmpty(repeatRecords, "Repeat returnFreeze operation, accountId={}, serialNo={}", 
				accountId, serialNo);
		return freezeList;
	}
	
	/**
	 * 提现更新钱包实体
	 * 
	 * @Title: getWithdrawEditRecord
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param withdrawAmount
	 *            可用金额
	 * @param freezeAmount
	 *            冻结金额
	 * @param updateTime
	 *            更新时间
	 * @return
	 * @date 2015年3月11日 下午7:06:07
	 * @author lys
	 */
	private Account getWithdrawEditRecord(Long accountId, Long withdrawAmount,
			Long freezeAmount, Date updateTime) {
		Account account = new Account();
		account.setId(accountId);
		account.setWithdrawAmount(withdrawAmount);
		account.setFreezeAmount(freezeAmount);
		account.setUpdateTime(updateTime);
		return account;
	}
	
	

	@Override
	public Integer getCount(Long accountId, List<Integer> fromTypeList, 
			List<Integer> toTypeList, Aide aide) throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			//数据组织
			aide.calculationTime();
			AccountDo accountDo = new AccountDo();
			accountDo.setAccountId(accountId);
			accountDo.setFromTypeList(fromTypeList);
			accountDo.setToTypeList(toTypeList);
			accountDo.setAide(aide);
			int count = accountOperationRecordDao.getCount(accountDo);
			return count;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	

	@Override
	public Integer getCountWithStatus(Long accountId,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide,
			List<Integer> status, Integer type) throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			aide.calculationTime();
			AccountDo accountDo = new AccountDo();
			accountDo.setAccountId(accountId);
			accountDo.setFromTypeList(fromTypeList);
			accountDo.setToTypeList(toTypeList);
			accountDo.setAide(aide);
			accountDo.setStatus(status);
			accountDo.setType(type);
			return accountOperationRecordDao.getCountWithStatus(accountDo);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	public List<AccountOperationRecord> findOperationRecordPage(
			Long accountId, List<Integer> fromTypeList,
			List<Integer> toTypeList, Aide aide) throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			//数据组织
			aide.calculationTime();
			AccountDo accountDo = new AccountDo();
			accountDo.setAccountId(accountId);
			accountDo.setFromTypeList(fromTypeList);
			accountDo.setToTypeList(toTypeList);
			accountDo.setAide(aide);
			return accountOperationRecordDao.findRecordPage(accountDo);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	

	@Override
	public List<AccountOperationRecord> findUserOperationRecord(Long accountId,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide,
			List<Integer> status, Integer type) throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param[accountId] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			aide.calculationTime();
			AccountDo accountDo = new AccountDo();
			accountDo.setAccountId(accountId);
			accountDo.setFromTypeList(fromTypeList);
			accountDo.setToTypeList(toTypeList);
			accountDo.setAide(aide);
			accountDo.setStatus(status);
			accountDo.setType(type);
			return accountOperationRecordDao.findUserOperationRecord(accountDo);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	public Integer getCount(String remoteId, Integer remoteType,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide)
			throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param[remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param[remoteType] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			//数据组织
			Account account = this.getValidAccountByRemoteId(remoteId, remoteType);
			Integer count = this.getCount(account.getId(), fromTypeList, toTypeList, aide);
			return count;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	public List<AccountOperationRecord> findOperationRecordPage(
			String remoteId, Integer remoteType, List<Integer> fromTypeList,
			List<Integer> toTypeList, Aide aide) throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param[remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param[remoteType] can not be null.");
			AssertUtil.notEmpty(fromTypeList, "Param[fromTypeList] can not be null.");
			AssertUtil.notEmpty(toTypeList, "Param[toTypeList] can not be null.");
			AssertUtil.notNull(aide, "Param[aide] can not be null.");
			//对应钱包查询
			Account account = this.getValidAccountByRemoteId(remoteId, remoteType);
			//钱包记录查询
			List<AccountOperationRecord> list = this.findOperationRecordPage(account.getId(),
					fromTypeList, toTypeList, aide);
			return list;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private void addUnfrrezeAccountOperationRecord(Account bfAccount, Long operationPrice,
			String serialNo, Integer operationType) throws AccountException {
		try {
			Long accountId = bfAccount.getId();
			Account afAccount = this.getAccountById(accountId);
			AssertUtil.notNull(afAccount, "Can not find account by Id"+accountId);
			//添加资金流转记录
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setFromAccountId(accountId);
			aor.setSerialNumber(serialNo);
			aor.setOperationAmount(operationPrice);
			aor.setFromAmountBefore(bfAccount.getWithdrawAmount());
			aor.setFromAmountAfter(afAccount.getWithdrawAmount());
			aor.setOperationType(operationType);
			if (AccountOperationRecord.TYPE_WITHDRAW_RETURN.equals(operationType)) {
				aor.setToAccountId(accountId);
				aor.setWithdrawalStates(ConstantsUtil.WD_STATE_FAIL);
			} else if (AccountOperationRecord.TYPE_WITHDRAW_TRANSFER.equals(operationType)) {
				aor.setToAccountId(accountId);
				aor.setWithdrawalStates(ConstantsUtil.WD_STATE_SUCC);
			}
			Date date = new Date();
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			aor.setPayWayFlag(ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_WALLET);
			WithdrawRecord record = withdrawRecordDao.getRecordBySeriNum(serialNo);
			StringBuilder sb = new StringBuilder();
			String banckCard = record.getReciveBankCard();
			banckCard = ConstantsMethod.replaceToHide(banckCard, 3, 3);
			sb.append("转入").append(record.getReciveBankName()).append(banckCard);
			aor.setRemark(sb.toString());
			accountOperationRecordDao.addOperationRecord(aor);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private void addFrrezeAccountOperationRecord(WithdrawReq withdrawReq,Account bfAccount, Long operationPrice, String serialNo) 
			throws AccountException {
		
		try {
			Long accountId = bfAccount.getId();
			Account afAccount = this.getAccountById(accountId);
			//添加资金流转记录
			Date date = new Date();
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setWithdrawalStates(ConstantsUtil.WD_STATE_DEAL);
			aor.setSerialNumber(serialNo);
			aor.setFromAccountId(accountId);
			aor.setToAccountId(accountId);
			aor.setOperationAmount(operationPrice);
			aor.setFromAmountBefore(bfAccount.getWithdrawAmount());
			aor.setFromAmountAfter(afAccount.getWithdrawAmount());
//			aor.setBankCard(withdrawReq.getReciveAccountNo());
//			aor.setBankName(withdrawReq.getBankName());
			aor.setOperationType(AccountOperationRecord.TYPE_WITHDRAW_AMOUNT);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			aor.setPayWayFlag(ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_WALLET);
			withdrawRecordDao.getRecordBySeriNum(serialNo);
			StringBuilder sb = new StringBuilder();
			String banckCard = withdrawReq.getReciveAccountNo();
			banckCard = ConstantsMethod.replaceToHide(banckCard, 3, 3);
			sb.append("转入").append(withdrawReq.getBankName()).append(banckCard);
			aor.setRemark(sb.toString());
			accountOperationRecordDao.addOperationRecord(aor);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	/**
	 * 操作记录状态更新
	 * @Title: editWithRecordStates 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param id
	 * @param states
	 * @param date
	 * @date 2015年3月11日 下午6:51:38  
	 * @author lys
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void editWithRecordStates(Long id, Integer states, Date date) {
		AccountOperationRecord record = new AccountOperationRecord();
		record.setId(id);
		record.setWithdrawalStates(states);
		record.setUpdateTime(date);
		accountOperationRecordDao.editOperationRecord(record);
	}
	
	@Override
	public Account getValidAccountById(Long accountId) throws AccountException {
		try {
			Account account = accountDao.getAccountById(accountId);
			AssertUtil.notNull(account, "Account by id={} can not be null.", accountId);
			return account;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	public Account getValidAccountByRemoteId(String remoteId, Integer remoteType) throws AccountException {
		try {
			AssertUtil.hasLength(remoteId, "Param[remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param[remoteType] can not be null.");
			Account selector = new Account(remoteId, remoteType);
			Account account = accountDao.getAccountByRemoteId(selector);
			AssertUtil.notNull(account, "Account[remoteType={}, remoteId={}] can not be null.", 
					remoteType, remoteId);
			return account;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	public synchronized boolean sumWithdrawCommission(Long accountId, Long amount)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param [accountId] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumConsumeAmountById] begin, Param[accountId")
					.append(accountId).append(", amount=").append(amount).append("]"));
			if (0 == amount) {
				return true;
			}
			Account account = this.getValidByPrimaryKeyLock(accountId);
			accountLog.info(sb.delete(0, sb.length()).append("[sumConsumeAmountById]")
					.append("old consume amout = ").append(account.getWithdrawCommission()));
		    Long newAmount = CalculateUtils.getSum(account.getWithdrawCommission(), amount);
		    AssertUtil.isTrue(newAmount >= 0, "old amount:{} + new amount:{} < 0.", 
		    		account.getWithdrawCommission(), amount);
		    Account newAccount = new Account(account.getId(), null, newAmount, null, new Date());
		    accountDao.editWithdrawCommission(newAccount);
		    accountLog.info("[sumConsumeAmountById] successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[sumWithdrawCommission] Add account's commission faild.", e);
			throw new AccountException(e);
		}
	}
	
	@Override
	public synchronized boolean sumWithdrawCommission(String remoteId, Integer remoteType,
			Long amount) throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumConsumeAmountByRemoteId] begin, Param[remoteId=")
					.append(remoteId).append(", remoteType=").append(remoteType)
					.append(", amount=").append(amount).append("]"));
			if (0 == amount) {
				return true;
			}
			Account selector = new Account(remoteId, remoteType);
			Account account = accountDao.getAccountByRemoteId(selector);
			AssertUtil.notNull(account, "Can not find account with remoteType={}, remoteId={}", remoteType, remoteId);
			accountLog.info(sb.delete(0, sb.length()).append("[sumConsumeAmountByRemoteId]")
					.append("old commission amout = ").append(account.getWithdrawCommission()));
			this.sumWithdrawCommission(account.getId(), amount);
			accountLog.info("[sumConsumeAmountByRemoteId] successful.");
			return true;
		} catch (Exception e) {
			accountLog.error("[sumWithdrawCommission] Add account's commission faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional
	public synchronized boolean sumConsumeAmount(String remoteId, Integer remoteType, Long amount)
			throws AccountException {
		try {
			AssertUtil.notNull(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumConsumeAmountByRemoteId] begin, Param[remoteId=")
					.append(remoteId).append(", remoteType=").append(remoteType)
					.append(", amount=").append(amount).append("]"));
			if (0 == amount) {
				return true;
			}
			Account selector = new Account(remoteId, remoteType);
			Account account = accountDao.getAccountByRemoteId(selector);
			AssertUtil.notNull(account, "Can not find account with remoteType={}, remoteId={}", remoteType, remoteId);
			accountLog.info(sb.delete(0, sb.length()).append("[editWithdrawAmount]")
					.append("old consume amout = ").append(account.getConsumeAmount()));
		    this.sumConsumeAmount(account.getId(), amount);
			return true;
		} catch (Exception e) {
			accountLog.error("[sumConsumeAmountByRemoteId] Add account's consume amout faild.", e);
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional
	public synchronized boolean sumConsumeAmount(Long accountId, Long amount)
			throws AccountException {
		try {
			AssertUtil.notNull(accountId, "Param [accountId] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			StringBuilder sb = new StringBuilder();
			accountLog.info(sb.append("[sumConsumeAmountById] begin, Param[accountId=")
					.append(accountId).append(", amount=").append(amount).append("]"));
			if (0 == amount) {
				return true;
			}
			Account account = this.getValidByPrimaryKeyLock(accountId);
			accountLog.info(sb.delete(0, sb.length()).append("[sumConsumeAmountById]")
					.append("old consume amout = ").append(account.getConsumeAmount()));
		    Long newAmount = CalculateUtils.getSum(account.getConsumeAmount(), amount);
		    AssertUtil.isTrue(newAmount >= 0, "old amount:{} + new amount:{} < 0.", 
		    		account.getConsumeAmount(), amount);
		    Account newAccount = new Account();
		    newAccount.setId(account.getId());
		    newAccount.setConsumeAmount(newAmount);
		    newAccount.setUpdateTime(new Date());
		    accountDao.editConsumeAmountById(newAccount);
			return true;
		} catch (Exception e) {
			accountLog.error("[sumConsumeAmountById] Add account's consume amout faild.", e);
			throw new AccountException(e);
		}
	}
	
	private Account getValidByPrimaryKeyLock(Long accountId) {
		Account account = accountDao.getByPrimaryKeyLock(accountId);
		AssertUtil.notNull(account, "Can not find account with accountId={}", accountId);
		return account;
	}
	
	private Account getValidByRemoteIdLock(String remoteId, Integer remoteType) {
		Account account = accountDao.getByRemoteIdLock(new Account(remoteId, remoteType));
		AssertUtil.notNull(account, "Can not find account with remoteType={}, remoteId={}", 
				remoteType, remoteId);
		return account;
	}

	@Override
	public Long getWithdrawAmount(Long accountId) {
		try {
			AssertUtil.notNull(accountId, "Param [accountId] can not be null.");
			Long amount = accountDao.getWithdrawAmountById(accountId);
			return amount;
		} catch (RuntimeException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public Long getWithdrawAmount(String remoteId, Integer remoteType) {
		try {
			AssertUtil.notNull(remoteId, "Param [remoteId] can not be null.");
			AssertUtil.notNull(remoteType, "Param [remoteType] can not be null.");
			Long amount = accountDao.getWithdrawAmountByRemoteId(new Account(remoteId, remoteType));
			return amount;
		} catch (RuntimeException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public AccountOperationRecord getOperationRecordById(Long id)
			throws AccountException {
		try {
			AssertUtil.notNull(id, "Param [id] can not be null.");
			return accountOperationRecordDao.getOperationRecordById(id);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional
	public boolean transaferAccountAsync(String serialNumber, Long fromUserId,
			Long toUserId, Long amount, Integer businessType, String businessId, String remark) {
		try {
			//参数验证
			AssertUtil.notNull(toUserId, "Param [toUserId] can not be null.");
			
			Account fromAccount = this.getValidAccountById(ConstantsUtil.ACCOUNT_PINGTAI);
			Account toAccount = this.getValidAccountByRemoteId(String.valueOf(toUserId),
					Account.REMOTE_TYPE_USER);
			
			transaferAccount(serialNumber, toAccount, fromAccount,
					amount, businessType,  businessId, remark);
			return true;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	@Override
	@Transactional
	public boolean transaferAccountPlatformToUser(String serialNumber, Long fromUserId, Long toUserId,
			Long amount, Integer businessType, String businessId, String remark) {
		try {
			accountLog.info("账户流转红包ID:"+businessId+",fromUserId:"+fromUserId+",serialNumber:"+serialNumber);
			//参数验证
			AssertUtil.notNull(toUserId, "Param [toUserId] can not be null.");
			accountLog.info("transaferAccountPlatformToUser调用开始...");
			Account fromAccount = this.getValidAccountById(ConstantsUtil.ACCOUNT_PINGTAI);
			Account toAccount = this.getValidAccountByRemoteId(String.valueOf(toUserId),
					Account.REMOTE_TYPE_USER);
			
			transaferAccount(serialNumber, toAccount, fromAccount,
					amount, businessType,  businessId ,remark);
			return true;
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	private void transaferAccount(String serialNumber, Account toAccount,
			Account fromAccount, Long amount, Integer businessType,
			 String businessId,String remark) {

		try {
			AssertUtil.notNull(serialNumber, "Param [serialNumber] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			AssertUtil.isTrue(amount > 0, "Param [amount] must be bigger than 0.");
			AssertUtil.notNull(businessType, "Param [businessType] can not be null.");
			AssertUtil.notNull(fromAccount, "Result fromAccount is not exists.");
			AssertUtil.notNull(toAccount, "Result toAccount is not exists.");
			
			//过滤操作
			this.checkRepeatOperat(fromAccount.getId(), toAccount.getId(), serialNumber, businessType);
			
			//平台账号不进行累加操作
			if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(fromAccount.getId())){
				this.sumWithdrawAmount(fromAccount.getId(), amount * -1);
			}
			if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(toAccount.getId())){
				this.sumWithdrawAmount(toAccount.getId(), amount);
			}
			
			//账户操作明细
			AccountOperationRecord operationRecord = AccountOperatorFactory.create(serialNumber, 
					fromAccount, toAccount, amount, businessType, remark);
			
			if(ConstantsUtil.ACCOUNT_PINGTAI.equals(fromAccount.getId()) || ConstantsUtil.ACCOUNT_PINGTAI.equals(toAccount.getId())){
				operationRecord.setDeal(DEAL_WAIT);
			}else{
				operationRecord.setDeal(DEAL_NO_REQUIRE);
			}
			accountOperationRecordDao.addOperationRecord(operationRecord);
			
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	/**
	 * 用于第三方支付到 公司账户类操作，仅用于平台账户变动
	 * 平台账户增加，个人账户不变
	 * @Description: 描述
	 * @param payWay TODO
	 * @return void
	 * @date: 2015-10-28上午9:39:56
	 * @author: wangzhang
	 */
	private void transaferToPlatformOnly(String serialNumber,
			Account fromAccount, Long amount, Integer businessType,String remark, Integer payWay) {
		try {
			AssertUtil.notNull(serialNumber, "Param [serialNumber] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			AssertUtil.isTrue(amount > 0, "Param [amount] must be bigger than 0.");
			AssertUtil.notNull(businessType, "Param [businessType] can not be null.");
			AssertUtil.notNull(fromAccount, "Result fromAccount is not exists.");
			
			//过滤操作
			this.checkRepeatOperat(fromAccount.getId(), ConstantsUtil.ACCOUNT_PINGTAI, serialNumber, businessType);
			Account pingtaiAccount = this.getValidAccountById(ConstantsUtil.ACCOUNT_PINGTAI);
			//账户操作明细
			AccountOperationRecord operationRecord = AccountOperatorFactory.createThirdPayRecrod(serialNumber, fromAccount, pingtaiAccount, amount, businessType, remark, payWay);
			operationRecord.setDeal(DEAL_WAIT);
			accountOperationRecordDao.addOperationRecord(operationRecord);
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}

	@Override
	@Transactional
	public void refund(Long id, Long money, String serialNumber) throws AccountException {
		try {
			
			AssertUtil.notNull(id, "Param [id] can not be null.");
			AssertUtil.notNull(money, "Param [money] can not be null.");
			AssertUtil.hasLength(serialNumber, "Param [serialNumber] can not be null.");
			String param = PropertiesUtil.getContexrtParam(JZ_ACCOUNT_ID);
			Long fromAccountId = Long.valueOf(param);
			this.checkRepeatOperat(fromAccountId, id, serialNumber, AccountOperationRecord.TYPE_REDENVELOPE_REFUND);
			
			accountLog.info("[红包退款业务]到账账户ID："+id+"，操作金额: "+money+"分");
			//到账账户
			Account toAccount = this.getValidAccountById(id);
			//出账账户
			Account fromAccount = this.getValidAccountById(fromAccountId);
			
			accountDao.sumAccountAmount(id, money);
			AccountOperationRecord record = AccountOperatorFactory.createRedenvelopeRefundRecord(toAccount, fromAccount, money, serialNumber);
			record.setDeal(DEAL_WAIT);
			accountOperationRecordDao.addOperationRecord(record);
			
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	public final static Integer DEAL_NO_REQUIRE=-1;
	public final static Integer DEAL_WAIT=0;
	
	@Transactional
	public boolean transaferAccountFromFreeze(String serialNumber, Account fromAccount, Account toAccount,
			Long amount, Integer businessType, Long businessId, String remark) {
		
		try {
			AssertUtil.notNull(serialNumber, "Param [serialNumber] can not be null.");
			AssertUtil.notNull(amount, "Param [amount] can not be null.");
			AssertUtil.isTrue(amount > 0, "Param [amount] must be bigger than 0.");
			AssertUtil.notNull(businessType, "Param [businessType] can not be null.");
			AssertUtil.notNull(fromAccount, "Result fromAccount is not exists.");
			AssertUtil.notNull(toAccount, "Result toAccount is not exists.");
			
			//过滤操作
			this.checkRepeatOperat(fromAccount.getId(), toAccount.getId(), serialNumber, businessType);
			
			//平台账号不进行累加操作
			if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(fromAccount.getId())){
				this.sumWithdrawAmount(fromAccount.getId(), amount * -1);
			}
			if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(toAccount.getId())){
				this.sumWithdrawAmount(toAccount.getId(), amount);
			}
			
			//账户操作明细
			AccountOperationRecord operationRecord = AccountOperatorFactory.createFreezeRecrod(serialNumber, 
					fromAccount, toAccount, amount, businessType, remark);
			
			if(ConstantsUtil.ACCOUNT_PINGTAI.equals(fromAccount.getId()) || ConstantsUtil.ACCOUNT_PINGTAI.equals(toAccount.getId())){
				operationRecord.setDeal(DEAL_WAIT);
			}else{
				operationRecord.setDeal(DEAL_NO_REQUIRE);
			}
			accountOperationRecordDao.addOperationRecord(operationRecord);
			
			return true;
			
		} catch (Exception e) {
			throw new AccountException(e);
		}
	}
	
	private void checkRepeatOperat(Long fromAccountId, Long toAccountId,
			String serialNumber, Integer operationType) {
		
		AccountOperationRecord record = new AccountOperationRecord();
		record.setFromAccountId(fromAccountId);
		record.setToAccountId(toAccountId);
		record.setSerialNumber(serialNumber);
		record.setOperationType(operationType);
		Integer count = accountOperationRecordDao.getOperationCounts(record);
		
		AssertUtil.isTrue(null == count || count.equals(0) , "AccountOperationRecord[fromAccountId={}, toAccounId={}, serialNumber={}, operationType={}] is already exists."
				, fromAccountId, toAccountId, serialNumber, operationType);
	}


	@Override
	public void increaseSlcoin(Long userId, Integer slcoinAmount) {
		Account account=new Account();
		account.setRemoteId(userId.toString());
		account.setRemoteType(1);
		account.setSlcoinAmount(slcoinAmount);
		accountDao.increaseSlcoin(account);
	}

	@Override
	public void decreaseSlcoin(Long userId, Integer slcoinAmount) {
		Account account=new Account();
		account.setRemoteId(userId.toString());
		account.setRemoteType(1);
		account.setSlcoinAmount(slcoinAmount);
		accountDao.decreaseSlcoin(account);
	}
	
	private final static Integer TYPE_IN=1;
	private final static Integer TYPE_OUT=2;
	
	@Override
	@Transactional
	public Long operationRecordTask() {
		Long maxId=accountOperationRecordDao.getMaxId();
		/** 收入*/
		Long inAmount=accountOperationRecordDao.getUndealSumAmount(maxId, TYPE_IN);
		/** 支出*/
		Long outAmount=accountOperationRecordDao.getUndealSumAmount(maxId, TYPE_OUT);
		if(inAmount==null)inAmount=0l;
		if(outAmount==null)outAmount=0l;
		/** 变动金额*/
		Long amount=inAmount-outAmount;
		this.sumWithdrawAmount(ConstantsUtil.ACCOUNT_PINGTAI, amount);
		accountOperationRecordDao.updateUndealRecord(maxId);
		return amount;
	}

	@Override
	public boolean transaferThirdPayToPlatform(String serialNumber,
			Long fromUserId,Long amount, Integer businessType,String remark, Integer payWay) {
		try {
			//参数验证
			AssertUtil.notNull(fromUserId, "Param [fromUserId] can not be null.");
			accountLog.info("transaferThirdPayToPlatform调用开始...");
			//--第三方支付 平台加钱 个人账户 不变
			Account fromAccount = this.getValidAccountByRemoteId(String.valueOf(fromUserId),
					Account.REMOTE_TYPE_USER);
			transaferToPlatformOnly(serialNumber, fromAccount, amount, businessType,remark, payWay);
			return true;
		} catch (Exception e) {
			accountLog.error("[transaferThirdPayToPlatform] transaferToPlatform faild.", e);
			throw new AccountException(e);
		}
	}
}
