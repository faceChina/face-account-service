package com.zjlp.face.account.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.zjlp.face.account.dao.AccountDao;
import com.zjlp.face.account.dao.AccountOperationRecordDao;
import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountOperationRecord;
import com.zjlp.face.account.dto.BankCardInfo;
import com.zjlp.face.account.dto.BankInfo;
import com.zjlp.face.account.dto.DivideCommission;
import com.zjlp.face.account.dto.DivideCommissions;
import com.zjlp.face.account.dto.Receivables;
import com.zjlp.face.account.dto.Wallet;
import com.zjlp.face.account.dto.WapPayReq;
import com.zjlp.face.account.dto.WapPayRsp;
import com.zjlp.face.account.exception.AmountException;
import com.zjlp.face.account.exception.PaymentException;
import com.zjlp.face.account.service.PaymentService;
import com.zjlp.face.account.util.ConstantsUtil;
import com.zjlp.face.account.util.GenerateCode;
import com.zjlp.face.account.util.HttpClientUtils;
import com.zjlp.face.account.util.KabinUtil;
import com.zjlp.face.account.util.enums.ApplyBindBankError;
import com.zjlp.face.util.calcu.CalculateUtils;
import com.zjlp.face.util.compare.SortParamUtil;
import com.zjlp.face.util.date.DateUtil;
import com.zjlp.face.util.encryption.DigestUtils;
import com.zjlp.face.util.encryption.rsa.RSAUtil;
import com.zjlp.face.util.file.PropertiesUtil;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
	private Logger _infoLogger = Logger.getLogger("paymentInfoLog");
	
	private Logger _errorLogger  = Logger.getLogger("paymentErrorLog");
	
	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private AccountOperationRecordDao accountOperationRecordDao;
	
	@Override
	public String paymentProducer(WapPayReq wapPayReq) throws PaymentException {
		try {
			_infoLogger.info(new StringBuffer("【支付生产】请求参数").append(JSONObject.fromObject(wapPayReq).toString()).toString());
			//基本参数校验
			_checkReqParam(wapPayReq);
			Map<String,String> params = _createReqParam(wapPayReq);
			String plainText = SortParamUtil.sortParamForSign(params);
			_infoLogger.info(new StringBuffer("参数加密前：").append(plainText).toString());
			String sign = this._encryptByParams(plainText);
			_infoLogger.info(new StringBuffer("参数加密后：").append(sign).toString());
			if (StringUtils.isBlank(sign)) {
				throw new PaymentException("参数加密失败，系统无法完成支付!");
			}
			params.put("sign", sign);
			JSONObject jsonObject = JSONObject.fromObject(params);
			_infoLogger.info(new StringBuffer("【支付生产】返回参数").append(jsonObject.toString()).toString());
			return jsonObject.toString();
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(wapPayReq).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	public WapPayRsp checkPaymentSign(WapPayRsp wapPayRsp) throws PaymentException {
		try {
			_infoLogger.info(new StringBuffer("【签名验证】请求参数").append(JSONObject.fromObject(wapPayRsp).toString()).toString());
			//基本参数校验
			_checkRspParam(wapPayRsp);
			String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
			if(StringUtils.isBlank(signType)){
				throw new PaymentException("未获取到配置的加密方式");
			}
			_infoLogger.info(new StringBuffer("加密方式：").append(signType));
			if(!wapPayRsp.getSign_type().equals(signType)){
				throw new PaymentException("加密类型不正确，系统无法完成支付消费!signType："+signType);
			}
			if (!_comparableToParams(wapPayRsp.getSign(), wapPayRsp)) {
				throw new PaymentException("验证加密失败，系统无法完成支付消费!sign :"+wapPayRsp.getSign());
			}
			return wapPayRsp;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(wapPayRsp).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	public WapPayRsp getCardBin(String cardNo) throws PaymentException {
		Map<String, String> param = new HashMap<String, String>();
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		String cardBinUrl = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_CARD_BIN_URL);
		param.put("card_no", cardNo);
		param.put("oid_partner", oidPartner);
		param.put("sign_type", signType);
		String plainText = SortParamUtil.sortParamForSign(param);
		String sign = this._encryptByParams(plainText);
		if (StringUtils.isBlank(sign)) {
			throw new PaymentException("参数加密失败，系统无法完成支付!");
		}
		param.put("sign", sign);
		JSONObject object = JSONObject.fromObject(param);
		HttpClientUtils client = HttpClientUtils.getInstances();
		
		try {
			String rspData = client.doSSLPost(cardBinUrl, "utf-8", object.toString());
			JSONObject jsonObject = JSONObject.fromObject(rspData);
			if(null == jsonObject){
				throw new PaymentException("参数转换失败，无法查询卡BIN信息");
			}
			WapPayRsp wapPayRspVo = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			if(null == wapPayRspVo){
				throw new PaymentException("参数转换失败，无法查询卡BIN信息");
			}
			return wapPayRspVo;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":{\"cardNo\":\"").append(cardNo).append("\"}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getTransactionSerialNumber(String userId,Integer busiPartner) throws PaymentException{
		_infoLogger.info(new StringBuffer("获取交易流水号参数{\"userId\":\"").append(userId).append("\",\"busiPartner\":\"").append(busiPartner).append("\"}").toString());
		String number = null;
		switch (busiPartner) {
			case 1:
				number = GenerateCode.getSN(userId);
				break;
			case 2:
				number = GenerateCode.getPhoneSN(userId);
				break;
			case 3:
				number = GenerateCode.getLotterySN(userId);
				break;
			case 4:
				number = GenerateCode.getReserveSN(userId);
				break;
			case 5:
				number = GenerateCode.getRechargeSN(userId);
				break;
			case 6:
				number = GenerateCode.getFeeSN(userId);
				break;
			default:
				break;
		}
		_infoLogger.info(new StringBuffer("获取流水号：").append(number));
		return number;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException","AmountException" })
	public boolean paymentByWallet(Wallet wallet) throws PaymentException,AmountException {
		try {
			_infoLogger.info("钱包支付开始");
			//校验基本参数
			_checkWalletPayParam(wallet);
			
			//校验支付密码
			Account checkUser = new Account();
			checkUser.setRemoteId(wallet.getUserId());
			checkUser.setRemoteType(Account.REMOTE_TYPE_USER);
			checkUser.setPaymentCode(DigestUtils.jzShaEncrypt(wallet.getPaymendCode()));
			boolean chk = accountDao.checkPaymentCode(checkUser);
			if(!chk){
				throw new PaymentException("支付密码不正确");
			}
			Account account = new Account(wallet.getUserId(), Account.REMOTE_TYPE_USER);
			Account userAccount = accountDao.getAccountByRemoteId(account);
			//锁定用户账号
			_infoLogger.info(new StringBuffer("锁定账户USERID:").append(wallet.getUserId()));
			userAccount = accountDao.getAccountByIdLock(userAccount.getId());
			_infoLogger.info(new StringBuffer("账户被锁USERID:").append(wallet.getUserId()));
			
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			_infoLogger.info(new StringBuffer("jzAccountId:").append(jzAccountId).toString());
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号,无法使用钱包支付");
				throw new PaymentException("无法从配置中获取到平台账户编号,无法使用钱包支付");
			}
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			Integer operationType=wallet.getOperationType()==null?ConstantsUtil.ACCOUNT_OPERATION_WALLET_PAY:wallet.getOperationType();
			_transferAccountToAmount(userAccount,jzAccount,wallet.getTotalPrice(),wallet.getTransactionSerialNumber(),
					operationType,ConstantsUtil.TRANSFER_TO_AMOUNT,ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_WALLET,wallet.getRemark());
			return true;
		} catch (AmountException ae){
			String msg = _getErrorInfo(ae.getMessage(),"钱包余额不足",wallet);
			_errorLogger.error(msg);
			throw new AmountException(msg);
		} catch (Exception e) {
			String msg = _getErrorInfo(e.getMessage(),e.getMessage(),wallet);
			_errorLogger.error(msg);
			throw new PaymentException(msg);
		} 
	}
	
	@Deprecated
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException","AmountException" })
	public boolean divideCommission(DivideCommission divideCommission)
			throws PaymentException,AmountException {
		try {
			_infoLogger.info(new StringBuffer("佣金分成参数").append(JSONObject.fromObject(divideCommission).toString()).toString());
			
			//校验参数
			_checkDivideCommissionParam(divideCommission);
			/**商户利益*/
			//交易流水号
			String serialNumber = divideCommission.getTransactionSerialNumber();
			if (StringUtils.isBlank(serialNumber)) {
				_infoLogger.info("无法获取此交易的流水号，无法进行佣金分成");
				throw new PaymentException("无法获取此交易的流水号，无法进行佣金分成");
			}
			//查询平台账户
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号,无法使用钱包支付");
				throw new PaymentException("无法从配置中获取到平台账户编号,无法使用钱包支付");
			}
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			
			//查询商户账户
			Account shopAccount = accountDao.getAccountByRemoteId(new Account(divideCommission.getShopNo(), Account.REMOTE_TYPE_SHOP));
			//转账到商户余额中
			_transferAccountToAmount(jzAccount,shopAccount,divideCommission.getShopIncome(),serialNumber,
					ConstantsUtil.ACCOUNT_OPERATION_SHOP_INCOME,ConstantsUtil.TRANSFER_TO_AMOUNT);
			
			//验证是否是推广入驻商品
			if(StringUtils.isBlank(divideCommission.getInvitationCode())){
				//普通商品，分成操作完成
				_infoLogger.info("当前为普通商品，分成操作完成");
				return true;
			}
			/**推荐人佣金*/
			//查询平台账户
			jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			//查询推荐者账户
			Account inviAccount = accountDao.getAccountByInvitationCode(divideCommission.getInvitationCode());
			//转账到推荐人佣金中
			_transferAccountToAmount(jzAccount,inviAccount,divideCommission.getRefereeDivide(),serialNumber,
					ConstantsUtil.ACCOUNT_OPERATION_DIVIDE,ConstantsUtil.TRANSFER_TO_COMMISSION);
			
			if(StringUtils.isBlank(divideCommission.getOnInvitationCode())){
				//没有上家，分成操作完成
				_infoLogger.info("当前为推荐商品，无上家，分成操作完成");
				return true;
			}
			/**上家佣金*/
			//查询平台账户
			jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			//查询上家账户
			Account oninviAccount = accountDao.getAccountByInvitationCode(divideCommission.getOnInvitationCode());
			//转账到上家佣金中
			_transferAccountToAmount(jzAccount,oninviAccount,divideCommission.getOnRefereeDivide(),serialNumber,
					ConstantsUtil.ACCOUNT_OPERATION_DIVIDE,ConstantsUtil.TRANSFER_TO_COMMISSION);
			_infoLogger.info("当前为推荐商品，有上家，分成操作完成");
			return true;
		} catch (AmountException ae){
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(ae)
					.append("\",\"err_msg\":\"平台账户余额不足\",\"err_data\":").append(JSONObject.fromObject(divideCommission).toString()).append("}").toString());
			throw new PaymentException(ae);
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(divideCommission).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException","AmountException" })
	public boolean divideCommission(List<DivideCommissions> divideCommissions)
			throws PaymentException,AmountException {
		_infoLogger.info(new StringBuffer("交易完成后金额到账参数：").append(JSONArray.fromObject(divideCommissions)).toString());
		try {
			//验证参数
			_checkDivideCommissionParam(divideCommissions);
			//查询平台账户
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号");
				throw new PaymentException("无法从配置中获取到平台账户编号");
			}
			for (DivideCommissions dc : divideCommissions) {
				Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
				Account shopAccount = accountDao.getAccountById(dc.getAccountId());
				Integer toCommission = null;
				//商户收益
				if(ConstantsUtil.ACCOUNT_OPERATION_SHOP_INCOME.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_AMOUNT;
				} else
				//佣金收益
				if(ConstantsUtil.ACCOUNT_OPERATION_DIVIDE.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_COMMISSION;
				} else
				//分销收益
				if(ConstantsUtil.ACCOUNT_OPERATION_AGENCY.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_COMMISSION;
				} else
				//全民推广收益
				if(ConstantsUtil.ACCOUNT_OPERATION_NATIONAL_PROMOTION.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_COMMISSION;
				} else
				if(ConstantsUtil.ACCOUNT_OPERATION_EMPLOYEE.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_COMMISSION;
				}else if(ConstantsUtil.ACCOUNT_OPERATION_BF.intValue() == dc.getType().intValue()){
					toCommission = ConstantsUtil.TRANSFER_TO_COMMISSION;
				}else {
					throw new PaymentException("TYPE参数错误");
				}
				_transferAccountToAmount(jzAccount,shopAccount,dc.getMoney(),dc.getTransactionSerialNumber(),dc.getType(),toCommission,null,dc.getRemark());
			}
			return true;
		} catch (AmountException ae){
			_errorLogger.error(_getArrayErrorInfo(ae.getMessage(),"平台账户余额不足",divideCommissions));
			throw new PaymentException(ae);
		} catch (Exception e) {
			_errorLogger.error(_getArrayErrorInfo(e.getMessage(),e.getMessage(),divideCommissions));
			e.printStackTrace();
			throw new PaymentException(e.getMessage(),e);
		}
	}

	@Deprecated
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException","AmountException" })
	public boolean withdrawCommission(Wallet wallet)
			throws PaymentException {
		try {
			_infoLogger.info(new StringBuffer("佣金提现开始,参数为：").append(JSONObject.fromObject(wallet).toString()).toString());
			if(null == wallet){
				_infoLogger.info("参数为空，无法提现");
				throw new PaymentException("参数为空，无法提现");
			}
			if(StringUtils.isBlank(wallet.getShopNo())){
				_infoLogger.info("店铺编号为空,无法提现");
				throw new PaymentException("用户编号为空,无法提现");
			}
			Account account = accountDao.getAccountByRemoteId(new Account(wallet.getShopNo(), Account.REMOTE_TYPE_SHOP));
			
			//锁定账户
			_infoLogger.info(new StringBuffer("锁定账户SHOPNO:").append(wallet.getShopNo()).toString());
			account = accountDao.getAccountByIdLock(account.getId());
			_infoLogger.info(new StringBuffer("账户被锁定SHOPNO:").append(wallet.getShopNo()).toString());
			
			if(null == account){
				_infoLogger.info("账户为空，无法提现");
				throw new PaymentException("账户为空，无法提现");
			}
			if(null == account.getWithdrawCommission() || account.getWithdrawCommission().longValue() < wallet.getTotalPrice().longValue()){
				_infoLogger.info(new StringBuffer("佣金不足，无法提现,当前佣金：").append(account.getWithdrawCommission())
						.append("提取佣金：").append(wallet.getTotalPrice()).toString());
				throw new PaymentException("佣金不足，无法提现");
			}
			//扣减佣金
			accountDao.minusAccountCommission(account.getId(), wallet.getTotalPrice());
			//累加余额
			accountDao.sumAccountAmount(account.getId(), wallet.getTotalPrice());
			Account accountAfter = accountDao.getAccountByRemoteId(new Account(wallet.getShopNo(), Account.REMOTE_TYPE_SHOP));
			//添加操作记录
			String serialNumber = GenerateCode.getWalletSN();
			Date date = new Date();
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setSerialNumber(serialNumber);
			aor.setFromAccountId(account.getId());
			aor.setToAccountId(account.getId());
			aor.setOperationAmount(wallet.getTotalPrice());
			aor.setFromAmountBefore(account.getWithdrawAmount());
			aor.setFromCommissionBefore(account.getWithdrawCommission());
			aor.setFromAmountAfter(accountAfter.getWithdrawAmount());
			aor.setFromCommissionAfter(accountAfter.getWithdrawCommission());
			aor.setToAmountBefore(account.getWithdrawAmount());
			aor.setToCommissionBefore(account.getWithdrawCommission());
			aor.setToAmountAfter(accountAfter.getWithdrawAmount());
			aor.setToCommissionAfter(accountAfter.getWithdrawCommission());
			aor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_WITHDRAW_COMMISSION);
			aor.setWithdrawalStates(null);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(aor);
			_infoLogger.info("佣金提取完成");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e).append("\",\"err_data\":").append(JSONObject.fromObject(wallet).toString()).append("}"));
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean withdrawCommission(Long accountId, String serialNumber, Long price) throws PaymentException {
		try {
			_infoLogger.info(new StringBuffer("佣金提现开始,参数为：").append(accountId).toString());
			Account account = accountDao.getAccountById(accountId);
			//锁定账户
			_infoLogger.info(new StringBuffer("锁定账户:").append(accountId).toString());
			account = accountDao.getAccountByIdLock(account.getId());
			_infoLogger.info(new StringBuffer("账户被锁定:").append(accountId).toString());
			
			if(null == account){
				_infoLogger.info("账户为空，无法提现");
				throw new PaymentException("账户为空，无法提现");
			}
			Long commission = account.getWithdrawCommission();
			if(null == commission || price > commission){
				_infoLogger.info(new StringBuffer("佣金不足，无法提现,当前佣金：").append(commission)
						.append("提取佣金：").append(price).toString());
				throw new PaymentException("佣金不足，无法提现");
			}
			//扣减佣金
			accountDao.minusAccountCommission(account.getId(), price);
			//累加余额
			accountDao.sumAccountAmount(account.getId(), price);
			Account accountAfter = accountDao.getAccountById(accountId);
			//添加操作记录
			Date date = new Date();
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setSerialNumber(serialNumber);
			aor.setFromAccountId(account.getId());
			aor.setToAccountId(account.getId());
			aor.setOperationAmount(price);
			aor.setFromAmountBefore(account.getWithdrawAmount());
			aor.setFromCommissionBefore(account.getWithdrawCommission());
			aor.setFromAmountAfter(accountAfter.getWithdrawAmount());
			aor.setFromCommissionAfter(accountAfter.getWithdrawCommission());
			aor.setToAmountBefore(account.getWithdrawAmount());
			aor.setToCommissionBefore(account.getWithdrawCommission());
			aor.setToAmountAfter(accountAfter.getWithdrawAmount());
			aor.setToCommissionAfter(accountAfter.getWithdrawCommission());
			aor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_WITHDRAW_COMMISSION);
			aor.setWithdrawalStates(null);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(aor);
			_infoLogger.info("佣金提取完成");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e).append("\",\"err_data\":").append(accountId).append("}"));
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	public boolean receivables(Receivables receivables) throws PaymentException {
		try {
			_infoLogger.info(JSONObject.fromObject(receivables).toString());
			_infoLogger.info(new StringBuffer("银行卡支付收款开始,wallet=").append(JSONObject.fromObject(receivables).toString()).toString());
			//验证参数
			_checkReceivablesParam(receivables);
			//查询账户
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			if(StringUtils.isBlank(jzAccountId)){
				throw new PaymentException("无法从配置中获取到连连账户编号");
			}
			_infoLogger.info(new StringBuffer("平台账户编号:").append(jzAccountId).toString());
			String llAccountId = null;
			Integer payWay = null;
			//区分渠道
			if(1 == receivables.getChannel().intValue()){
				//连连渠道
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_LIANLIAN_ACCOUNT_ID);
			} else if(2 == receivables.getChannel().intValue()){
				//捷蓝渠道绑定支付
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JIELAN_ACCOUNT_ID);
			} else if(3 == receivables.getChannel().intValue()){
				//支付宝支付
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_ALIPAY_ACCOUNT_ID);
				payWay = ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_ZFB;
			} else if(6 == receivables.getChannel().intValue()){
				//拉卡拉支付
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_LAKALA_ACCOUNT_ID);
			}else {
				throw new PaymentException("支付渠道错误");
			}
			if(StringUtils.isBlank(llAccountId)){
				throw new PaymentException("无法从配置中获取到连连账户编号");
			}
			_infoLogger.info(new StringBuffer("接口账户编号:").append(llAccountId).toString());
			//景正收款
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			if(null == jzAccount){
				throw new PaymentException("查询平台账户失败");
			}
			accountDao.sumAccountAmount(jzAccount.getId(), receivables.getAmount());
			Account jzAccountAfter = accountDao.getAccountById(jzAccount.getId());
			//收取手续费
			Account llAccount = accountDao.getAccountById(Long.valueOf(llAccountId));
			if(null == llAccount){
				throw new PaymentException("查询接口账户失败");
			}
			accountDao.sumAccountAmount(llAccount.getId(), receivables.getFee());
			Account llAccountAfter = accountDao.getAccountById(llAccount.getId());
			
			//添加平台账户操作记录
			Date date = new Date();
			AccountOperationRecord jzaor = new AccountOperationRecord();
			jzaor.setSerialNumber(receivables.getTransactionSerialNumber());
			jzaor.setToAccountId(jzAccount.getId());
			jzaor.setOperationAmount(receivables.getAmount());
			jzaor.setToAmountBefore(jzAccount.getWithdrawAmount());
			jzaor.setToCommissionBefore(jzAccount.getWithdrawCommission());
			jzaor.setToAmountAfter(jzAccountAfter.getWithdrawAmount());
			jzaor.setToCommissionAfter(jzAccountAfter.getWithdrawCommission());
			jzaor.setBankCardId(receivables.getBankCardId());
			jzaor.setBankCard(receivables.getBankCard());
			jzaor.setBankName(receivables.getBankName());
			jzaor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_BANKCARD_PAY);
			jzaor.setCreateTime(date);
			jzaor.setUpdateTime(date);
			jzaor.setPayWayFlag(payWay);
			jzaor.setRemark(receivables.getRemark());
			accountOperationRecordDao.addOperationRecord(jzaor);
			//添加连连账户操作记录
			AccountOperationRecord llaor = new AccountOperationRecord();
			llaor.setSerialNumber(receivables.getTransactionSerialNumber());
			llaor.setToAccountId(llAccount.getId());
			llaor.setOperationAmount(receivables.getFee());
			llaor.setToAmountBefore(llAccount.getWithdrawAmount());
			llaor.setToCommissionBefore(llAccount.getWithdrawCommission());
			llaor.setToAmountAfter(llAccountAfter.getWithdrawAmount());
			llaor.setToCommissionAfter(llAccountAfter.getWithdrawCommission());
			llaor.setBankCardId(receivables.getBankCardId());
			llaor.setBankCard(receivables.getBankCard());
			llaor.setBankName(receivables.getBankName());
			llaor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_FEE);
			jzaor.setPayWayFlag(payWay);
			jzaor.setRemark(receivables.getRemark());
			llaor.setCreateTime(date);
			llaor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(llaor);
			return true;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data1\":").append(JSONObject.fromObject(receivables).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	public boolean receivablesWechat(Receivables receivables) throws PaymentException {
		//验证参数
		Assert.notNull(receivables,"参数为空");
		Assert.notNull(receivables.getAmount(),"付款金额为空");
		Assert.notNull(receivables.getFee(),"手续费为空");
		Assert.notNull(receivables.getOpenId(),"OPEN_ID为空");
		Assert.notNull(receivables.getTransactionSerialNumber(),"交易流水号为空");
		_infoLogger.info(new StringBuffer("微信支付收款开始,wallet=").append(JSONObject.fromObject(receivables).toString()).toString());
		try {
			//查询账户
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			if(StringUtils.isBlank(jzAccountId)){
				throw new PaymentException("无法从配置中获取到连连账户编号");
			}
			_infoLogger.info(new StringBuffer("平台账户编号:").append(jzAccountId).toString());
			String wechatAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_WECHAT_ACCOUNT_ID);
			if(StringUtils.isBlank(wechatAccountId)){
				throw new PaymentException("无法从配置中获取到微信账户编号");
			}
			_infoLogger.info(new StringBuffer("微信账户编号:").append(wechatAccountId).toString());
			//景正收款
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			if(null == jzAccount){
				throw new PaymentException("查询平台账户失败");
			}
			accountDao.sumAccountAmount(jzAccount.getId(), receivables.getAmount());
			Account jzAccountAfter = accountDao.getAccountById(jzAccount.getId());
			//微信收取手续费
			Account wechatAccount = accountDao.getAccountById(Long.valueOf(wechatAccountId));
			if(null == wechatAccount){
				throw new PaymentException("查询微信账户失败");
			}
			accountDao.sumAccountAmount(wechatAccount.getId(), receivables.getFee());
			Account wechatAccountAfter = accountDao.getAccountById(wechatAccount.getId());
			
			//添加平台账户操作记录
			Date date = new Date();
			AccountOperationRecord jzaor = new AccountOperationRecord();
			jzaor.setSerialNumber(receivables.getTransactionSerialNumber());
			jzaor.setToAccountId(jzAccount.getId());
			jzaor.setOperationAmount(receivables.getAmount());
			jzaor.setToAmountBefore(jzAccount.getWithdrawAmount());
			jzaor.setToCommissionBefore(jzAccount.getWithdrawCommission());
			jzaor.setToAmountAfter(jzAccountAfter.getWithdrawAmount());
			jzaor.setToCommissionAfter(jzAccountAfter.getWithdrawCommission());
			jzaor.setOpenId(receivables.getOpenId());
			jzaor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_BANKCARD_PAY);
			jzaor.setCreateTime(date);
			jzaor.setUpdateTime(date);
			jzaor.setPayWayFlag(ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_WECHAT);
			jzaor.setRemark(receivables.getRemark());
			accountOperationRecordDao.addOperationRecord(jzaor);
			//添加微信账户操作记录
			AccountOperationRecord wechataor = new AccountOperationRecord();
			wechataor.setSerialNumber(receivables.getTransactionSerialNumber());
			wechataor.setToAccountId(wechatAccount.getId());
			wechataor.setOperationAmount(receivables.getFee());
			wechataor.setToAmountBefore(wechatAccount.getWithdrawAmount());
			wechataor.setToCommissionBefore(wechatAccount.getWithdrawCommission());
			wechataor.setToAmountAfter(wechatAccountAfter.getWithdrawAmount());
			wechataor.setToCommissionAfter(wechatAccountAfter.getWithdrawCommission());
			wechataor.setOpenId(receivables.getOpenId());
			wechataor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_FEE);
			wechataor.setCreateTime(date);
			wechataor.setUpdateTime(date);
			jzaor.setPayWayFlag(ConstantsUtil.ACCOUNT_TYPE_PAY_WAY_WECHAT);
			jzaor.setRemark(receivables.getRemark());
			accountOperationRecordDao.addOperationRecord(wechataor);
			return true;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data1\":").append(JSONObject.fromObject(receivables).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public boolean bindFeeReceivables(Receivables receivables)
			throws PaymentException {
		Assert.notNull(receivables.getChannel(),"渠道参数为空");
		Assert.notNull(receivables.getFee(),"手续费参数为空");
		Assert.notNull(receivables.getTransactionSerialNumber(), "交易流水号为空");
		try {
			_infoLogger.info(new StringBuffer("接口渠道手续费收款,channel：").append(receivables.getChannel()).append(" fee:").append(receivables.getFee()).toString());
			String llAccountId = null;
			//区分渠道
			if(1 == receivables.getChannel().intValue()){
				//连连渠道
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_LIANLIAN_ACCOUNT_ID);
			} else if(2 == receivables.getChannel().intValue()){
				//捷蓝渠道绑定支付
				llAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JIELAN_ACCOUNT_ID);
			} else {
				throw new PaymentException("支付渠道错误");
			}
			if(StringUtils.isBlank(llAccountId)){
				throw new PaymentException("无法从配置中获取到连连账户编号");
			}
			_infoLogger.info(new StringBuffer("接口账户编号:").append(llAccountId).toString());
			//收取手续费
			Account llAccount = accountDao.getAccountById(Long.valueOf(llAccountId));
			if(null == llAccount){
				throw new PaymentException("查询接口账户失败");
			}
			Date date = new Date();
			accountDao.sumAccountAmount(llAccount.getId(), receivables.getFee());
			Account llAccountAfter = accountDao.getAccountById(llAccount.getId());
			AccountOperationRecord llaor = new AccountOperationRecord();
			llaor.setSerialNumber(receivables.getTransactionSerialNumber());
			llaor.setToAccountId(llAccount.getId());
			llaor.setOperationAmount(receivables.getFee());
			llaor.setToAmountBefore(llAccount.getWithdrawAmount());
			llaor.setToCommissionBefore(llAccount.getWithdrawCommission());
			llaor.setToAmountAfter(llAccountAfter.getWithdrawAmount());
			llaor.setToCommissionAfter(llAccountAfter.getWithdrawCommission());
			llaor.setBankCardId(receivables.getBankCardId());
			llaor.setBankCard(receivables.getBankCard());
			llaor.setBankName(receivables.getBankName());
			llaor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_FEE);
			llaor.setCreateTime(date);
			llaor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(llaor);
			return true;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(receivables).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}

	@Override
	public WapPayRsp applyBindBankCard(BankCardInfo bankCardInfo)
			throws PaymentException {
		_infoLogger.info(new StringBuffer("银行卡签约申请开始,参数为：").append(JSONObject.fromObject(bankCardInfo).toString()).toString());
		String applyCardBindUrl = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_APPLY_CARD_BIND_URL);
		if(StringUtils.isBlank(applyCardBindUrl)){
			new PaymentException("未读取到银行卡签约申请地址");
		}
		//参数校验
		_checkApplyBindParam(bankCardInfo);
		//创建请求参数
		Map<String,String> params = _createApplyBindParam(bankCardInfo);
		//对参数排序，用于生成签名数据
		String plainText = SortParamUtil.sortParamForSign(params);
		_infoLogger.info(new StringBuffer("签名信息明文：").append(plainText).toString());
		//生成签名参数
		String sign = this._encryptByParams(plainText);
		if (StringUtils.isBlank(sign)) {
			throw new PaymentException("生成签名参数失败，无法完成【银行卡签约申请】操作!");
		}
		_infoLogger.info(new StringBuffer("签名信息密文：").append(sign).toString());
		params.put("sign", sign);
		JSONObject object = JSONObject.fromObject(params);
		_infoLogger.info(new StringBuffer("银行卡签约申请请求连连参数：").append(object.toString()).toString());
		try {
			String rspData = HttpClientUtils.getInstances().doSSLPost(applyCardBindUrl, "utf-8", object.toString());
			if(StringUtils.isBlank(rspData)){
				throw new PaymentException("请求失败，无法完成【银行卡签约申请】操作");
			}
			JSONObject jsonObject = JSONObject.fromObject(rspData);
			if(null == jsonObject){
				throw new PaymentException("参数转换失败，无法完成【银行卡签约申请】操作");
			}
			WapPayRsp wapPayRspVo = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			if(null == wapPayRspVo){
				throw new PaymentException("参数转换失败，无法完成【银行卡签约申请】操作");
			}
			if(!"0000".equals(wapPayRspVo.getRet_code())){
				String message = ApplyBindBankError.getMsgByCode(wapPayRspVo.getRet_code());
				if (StringUtils.isNotBlank(message)) {
					throw new PaymentException(message);
				}
				_errorLogger.error(new StringBuffer("【银行卡签约申请】失败，Ret_code:").append(wapPayRspVo.getRet_code())
						.append(",Ret_msg:").append(wapPayRspVo.getRet_msg()).toString());
				throw new PaymentException(wapPayRspVo.getRet_msg());
			}
			return wapPayRspVo;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(bankCardInfo).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}

	@Override
	public WapPayRsp valiBindBankCard(BankCardInfo bankCardInfo)
			throws PaymentException {
		_infoLogger.info(new StringBuffer("银行卡签约验证开始,参数为：").append(JSONObject.fromObject(bankCardInfo).toString()).toString());
		String valiCardBindUrl = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_VALI_CARD_BIND_URL);
		if(StringUtils.isBlank(valiCardBindUrl)){
			throw new PaymentException("未读取到银行卡签约验证地址");
		}
		//参数校验
		_checkValiBindParam(bankCardInfo);
		//创建请求参数
		Map<String,String> params = _createValiBindParam(bankCardInfo);
		//对参数排序，用于生成签名数据
		String plainText = SortParamUtil.sortParamForSign(params);
		_infoLogger.info(new StringBuffer("签名信息明文：").append(plainText).toString());
		//生成签名参数
		String sign = this._encryptByParams(plainText);
		if (StringUtils.isBlank(sign)) {
			throw new PaymentException("生成签名参数失败，无法完成【银行卡签约验证】操作");
		}
		_infoLogger.info(new StringBuffer("签名信息明文：").append(sign).toString());
		params.put("sign", sign);
		JSONObject object = JSONObject.fromObject(params);
		_infoLogger.info(new StringBuffer("银行卡签约验证请求连连参数：").append(object.toString()).toString());
		try {
			String rspData = HttpClientUtils.getInstances().doSSLPost(valiCardBindUrl, "utf-8", object.toString());
			if(StringUtils.isBlank(rspData)){
				throw new PaymentException("请求失败，无法完成【银行卡签约申请】操作");
			}
			JSONObject jsonObject = JSONObject.fromObject(rspData);
			if(null == jsonObject){
				throw new PaymentException("参数转换失败，无法完成【银行卡签约验证】操作");
			}
			WapPayRsp wapPayRspVo = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			if(null == wapPayRspVo){
				throw new PaymentException("参数转换失败，无法完成【银行卡签约验证】操作");
			}
			if(!"0000".equals(wapPayRspVo.getRet_code())){
				_errorLogger.error(new StringBuffer("【银行卡签约验证】失败，Ret_code:").append(wapPayRspVo.getRet_code())
						.append(",Ret_msg:").append(wapPayRspVo.getRet_msg()).toString());
				throw new PaymentException(wapPayRspVo.getRet_msg());
			}
			return wapPayRspVo;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(bankCardInfo).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}

	@Override
	public WapPayRsp unbindBankCard(BankCardInfo bankCardInfo)
			throws PaymentException {
		_infoLogger.info(new StringBuffer("银行卡解约开始,参数为：").append(JSONObject.fromObject(bankCardInfo).toString()).toString());
		String unbindCardUrl = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_UNBIND_CARD_BIND_URL);
		if(StringUtils.isBlank(unbindCardUrl)){
			throw new PaymentException("未读取到银行卡解约地址");
		}
		//参数校验
		_checkUnbindParam(bankCardInfo);
		//创建请求参数
		Map<String,String> params = _createUnbindParam(bankCardInfo);
		//对参数排序，用于生成签名数据
		String plainText = SortParamUtil.sortParamForSign(params);
		_infoLogger.info(new StringBuffer("签名信息明文：").append(plainText).toString());
		//生成签名参数
		String sign = this._encryptByParams(plainText);
		if (StringUtils.isBlank(sign)) {
			throw new PaymentException("生成签名参数失败，无法完成【银行卡解约】操作!");
		}
		_infoLogger.info(new StringBuffer("签名信息密文：").append(sign).toString());
		params.put("sign", sign);
		JSONObject object = JSONObject.fromObject(params);
		_infoLogger.info(new StringBuffer("银行卡解约请求连连参数：").append(object.toString()).toString());
		try {
			String rspData = HttpClientUtils.getInstances().doSSLPost(unbindCardUrl, "utf-8", object.toString());
			if(StringUtils.isBlank(rspData)){
				throw new PaymentException("请求失败,无法完成【银行卡解约】操作");
			}
			JSONObject jsonObject = JSONObject.fromObject(rspData);
			if(null == jsonObject){
				throw new PaymentException("参数转换失败，无法完成【银行卡解约】操作");
			}
			WapPayRsp wapPayRspVo = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			if(null == wapPayRspVo){
				throw new PaymentException("参数转换失败，无法完成【银行卡解约】操作");
			}
			if(!"0000".equals(wapPayRspVo.getRet_code())){
				_errorLogger.error(new StringBuffer("【银行卡解约】失败，Ret_code:").append(wapPayRspVo.getRet_code())
						.append(",Ret_msg:").append(wapPayRspVo.getRet_msg()).toString());
				throw new PaymentException(wapPayRspVo.getRet_msg());
			}
			return wapPayRspVo;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(bankCardInfo).toString()).append("}"));
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean sumConsumeAmount(Long userId,Long consumeAmount) throws PaymentException{
		_infoLogger.info(new StringBuffer("累计消费额参数{\"userId\":\"").append(userId)
				.append("\",\"consumeAmount\":\"").append(consumeAmount).append("\"}").toString());
		try {
			Account account = accountDao.getAccountByRemoteId(new Account(String.valueOf(userId), Account.REMOTE_TYPE_USER));
			if(null == account){
				throw new PaymentException("用户账户不存在");
			}
			_infoLogger.info(new StringBuffer("锁定账户USERID:").append(userId));
			account = accountDao.getAccountByIdLock(account.getId());
			_infoLogger.info(new StringBuffer("账户被锁USERID:").append(userId));
			Account newAccount = new Account();
			newAccount.setId(account.getId());
			newAccount.setConsumeAmount(consumeAmount);
			accountDao.sumConsumeAmount(newAccount);
			return true;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e).append("\",\"err_data\":{\"userId\":\"").append(userId)
					.append("\",\"consumeAmount\":\"").append(consumeAmount).append("\"}}"));
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean lotteryWinner(Wallet wallet)throws PaymentException{
		_infoLogger.info(new StringBuffer("彩票中奖参数：").append(JSONObject.fromObject(wallet).toString()).toString());
		try {
			_checkLotteryWinnerParam(wallet);
			Account account = new Account(String.valueOf(wallet.getUserId()), Account.REMOTE_TYPE_USER);
			Account userAccount = accountDao.getAccountByRemoteId(account);
//			Account userAccount = accountDao.getAccountByUserId(Long.valueOf(wallet.getUserId()));
			//锁定用户账号
			_infoLogger.info(new StringBuffer("锁定账户USERID:").append(wallet.getUserId()));
			userAccount = accountDao.getAccountByIdLock(userAccount.getId());
			_infoLogger.info(new StringBuffer("账户被锁USERID:").append(wallet.getUserId()));
			
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			_infoLogger.info(new StringBuffer("jzAccountId:").append(jzAccountId).toString());
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号,无法使用钱包支付");
				throw new PaymentException("无法从配置中获取到平台账户编号,无法使用钱包支付");
			}
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			_transferAccountToAmount(jzAccount,userAccount,wallet.getTotalPrice(),wallet.getTransactionSerialNumber(),
					ConstantsUtil.ACCOUNT_OPERATION_LOTTERY_WINNING,ConstantsUtil.TRANSFER_TO_AMOUNT);
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(wallet).toString()).append("}"));
			throw new PaymentException(e.getMessage(),e);
		}
		return true;
	}
	
	@Override
	public boolean WalletRefund(Wallet wallet)throws PaymentException{
		_infoLogger.info(new StringBuffer("钱包退款参数：").append(JSONObject.fromObject(wallet).toString()).toString());
		try {
			_checkLotteryWinnerParam(wallet);
			Account account = new Account(String.valueOf(wallet.getUserId()), Account.REMOTE_TYPE_USER);
			Account userAccount = accountDao.getAccountByRemoteId(account);
//			Account userAccount = accountDao.getAccountByUserId(Long.valueOf(wallet.getUserId()));
			//锁定用户账号
			_infoLogger.info(new StringBuffer("锁定账户USERID:").append(wallet.getUserId()));
			userAccount = accountDao.getAccountByIdLock(userAccount.getId());
			_infoLogger.info(new StringBuffer("账户被锁USERID:").append(wallet.getUserId()));
			
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			_infoLogger.info(new StringBuffer("jzAccountId:").append(jzAccountId).toString());
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号,无法使用钱包支付");
				throw new PaymentException("无法从配置中获取到平台账户编号,无法使用钱包支付");
			}
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			_transferAccountToAmount(jzAccount,userAccount,wallet.getTotalPrice(),wallet.getTransactionSerialNumber(),
					ConstantsUtil.ACCOUNT_OPERATION_REFUND,ConstantsUtil.TRANSFER_TO_AMOUNT);
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(wallet).toString()).append("}"));
			throw new PaymentException(e.getMessage(),e);
		}
		return true;
	}
	
	@Override
	public WapPayRsp paymentQueryForLianLian(WapPayReq wapPayReq) throws PaymentException{
		_infoLogger.info(new StringBuffer("查询连连支付状态参数：").append(JSONObject.fromObject(wapPayReq).toString()).toString());
		if(null == wapPayReq || StringUtils.isBlank(wapPayReq.getNo_order())){
			throw new PaymentException("查询参数为空");
		}
		String paymentQueryUrl = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_PAYMENT_QUERY_URL);
		if(StringUtils.isBlank(paymentQueryUrl)){
			throw new PaymentException("未读取到查询支付状态地址");
		}
		Map<String,String> params = _createPaymentQueryParam(wapPayReq);
		//对参数排序，用于生成签名数据
		String plainText = SortParamUtil.sortParamForSign(params);
		_infoLogger.info(new StringBuffer("签名信息明文：").append(plainText).toString());
		//生成签名参数
		String sign = this._encryptByParams(plainText);
		if (StringUtils.isBlank(sign)) {
			throw new PaymentException("生成签名参数失败，无法完成【支付状态查询】操作!");
		}
		_infoLogger.info(new StringBuffer("签名信息密文：").append(sign).toString());
		params.put("sign", sign);
		JSONObject object = JSONObject.fromObject(params);
		_infoLogger.info(new StringBuffer("支付状态查询请求连连参数：").append(object.toString()).toString());
		try {
			String rspData = HttpClientUtils.getInstances().doSSLPost(paymentQueryUrl, "utf-8", object.toString());
			if(StringUtils.isBlank(rspData)){
				throw new PaymentException("请求失败，无法完成【支付状态查询】操作");
			}
			JSONObject jsonObject = JSONObject.fromObject(rspData);
			if(null == jsonObject){
				throw new PaymentException("参数转换失败，无法完成【支付状态查询】操作");
			}
			WapPayRsp wapPayRspVo = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			if(null == wapPayRspVo){
				throw new PaymentException("参数转换失败，无法完成【支付状态查询】操作");
			}
			
			return wapPayRspVo;
		} catch (Exception e) {
			_errorLogger.error(new StringBuffer("{\"err_info\":\"").append(e)
					.append("\",\"err_data\":").append(JSONObject.fromObject(wapPayReq).toString()).append("}").toString());
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	/**
	 * 彩票中奖和钱包退款验证参数
	 * @Title: _checkLotteryWinnerParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wallet
	 * @throws PaymentException
	 * @date 2014年8月21日 下午8:55:02  
	 * @author Administrator
	 */
	private void _checkLotteryWinnerParam(Wallet wallet)throws PaymentException{
		if(null == wallet){
			throw new PaymentException("参数对象为空");
		}
		if(StringUtils.isBlank(wallet.getUserId())){
			throw new PaymentException("用户编号为空");
		}
		if(StringUtils.isBlank(wallet.getTransactionSerialNumber())){
			throw new PaymentException("交易流水号为空");
		}
		if(null == wallet.getTotalPrice()){
			throw new PaymentException("金额为空");
		}
	}
	
	/**
	 * 校验生产支付参数
	 * @Title: _checkReqParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wapPayReqVo
	 * @throws PaymentException
	 * @date 2014年7月10日 下午5:17:34  
	 * @author Administrator
	 */
	private void _checkReqParam(WapPayReq wapPayReq) throws PaymentException{
		if(null == wapPayReq){
			throw new PaymentException("请求参数为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getUser_id())){
			throw new PaymentException("用户编号为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getNo_order())){
			throw new PaymentException("交易流水号为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getName_goods())){
			throw new PaymentException("商品名称为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getMoney_order())){
			throw new PaymentException("支付金额为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getNotify_url())){
			throw new PaymentException("异步回调地址为空，无法生产支付");
		}
		if(StringUtils.isBlank(wapPayReq.getUrl_return())){
			throw new PaymentException("同步回调地址为空，无法生产支付");
		}
	}
	
	/**
	 * 生成参数
	 * @Title: _createParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wapPayReqVo
	 * @return
	 * @date 2014年7月11日 上午10:51:01  
	 * @author Administrator
	 */
	private Map<String,String> _createReqParam(WapPayReq wapPayReq) throws PaymentException{
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		if(StringUtils.isBlank(signType)){
			throw new PaymentException("未读取到加密方式signType参数");
		}
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		if(StringUtils.isBlank(oidPartner)){
			throw new PaymentException("未读取到商户编号oidPartner");
		}
		Map<String,String> param = new HashMap<String, String>();
		param.put("version", ConstantsUtil.LL_PAY_VERSION);
		param.put("oid_partner", oidPartner);
		param.put("user_id", wapPayReq.getUser_id());
		param.put("app_request", ConstantsUtil.APP_REQUEST_WAP);
		param.put("sign_type", signType);
		param.put("busi_partner", ConstantsUtil.BUSI_PARTNER_PHYSICAL);//当前账户只支持实物订单
		param.put("no_order", wapPayReq.getNo_order());
		param.put("dt_order", DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
		param.put("name_goods", wapPayReq.getName_goods());
		param.put("money_order", wapPayReq.getMoney_order());
		param.put("notify_url", wapPayReq.getNotify_url());
		param.put("url_return", wapPayReq.getUrl_return());
		param.put("valid_order", "10080");//7天有效期
		
		//可选参数
		if(StringUtils.isNotBlank(wapPayReq.getInfo_order())){
			param.put("info_order", wapPayReq.getInfo_order());
		}
		if(StringUtils.isNotBlank(wapPayReq.getPay_type())){
			param.put("pay_type", wapPayReq.getPay_type());
		}
		if(StringUtils.isNotBlank(wapPayReq.getNo_agree())){
			param.put("no_agree", wapPayReq.getNo_agree());
		}
		if(StringUtils.isNotBlank(wapPayReq.getBank_code())){
			param.put("bank_code", wapPayReq.getBank_code());
		}
		if(StringUtils.isNotBlank(wapPayReq.getId_type())){
			param.put("id_type", wapPayReq.getId_type());
		}
		if(StringUtils.isNotBlank(wapPayReq.getId_no())){
			param.put("id_no", wapPayReq.getId_no());
		}
		if(StringUtils.isNotBlank(wapPayReq.getAcct_name())){
			param.put("acct_name", wapPayReq.getAcct_name());
		}
		if(StringUtils.isNotBlank(wapPayReq.getFlag_modify())){
			param.put("flag_modify", wapPayReq.getFlag_modify());
		}
		if(StringUtils.isNotBlank(wapPayReq.getCard_no())){
			param.put("card_no", wapPayReq.getCard_no());
		}
		if(StringUtils.isNotBlank(wapPayReq.getRisk_item())){
			param.put("risk_item", wapPayReq.getRisk_item());
		}
		return param;
	}
	
	/**
	 * 校验消费支付参数
	 * @Title: _checkRspParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wapPayRspVo
	 * @throws PaymentException
	 * @date 2014年7月11日 上午11:31:51  
	 * @author Administrator
	 */
	private void _checkRspParam(WapPayRsp wapPayRsp) throws PaymentException{
		if (StringUtils.isBlank(wapPayRsp.getSign()) || StringUtils.isBlank(wapPayRsp.getSign_type())){
			throw new PaymentException("加密参数为空,系统无法完成支付消费");
		}
		if (StringUtils.isBlank(wapPayRsp.getOid_partner())){
			throw new PaymentException("商户编号为空,系统无法完成支付消费");
		}
		if (StringUtils.isBlank(wapPayRsp.getNo_order()) || StringUtils.isBlank(wapPayRsp.getOid_paybill())){
			throw new PaymentException("流水号为空,系统无法完成支付消费");
		}
		if (StringUtils.isBlank(wapPayRsp.getResult_pay())||StringUtils.isBlank(wapPayRsp.getMoney_order())){
			throw new PaymentException("支付结果参数为空，系统无法完成支付消费");
		}
		if (StringUtils.isBlank(wapPayRsp.getDt_order())){
			throw new PaymentException("支付时间为空，系统无法完成支付消费");
		}
		if (!"SUCCESS".equals(wapPayRsp.getResult_pay())){
			throw new PaymentException("支付状态异常，系统无法完成支付消费");
		}
		if (StringUtils.isBlank(wapPayRsp.getSettle_date())){
			throw new PaymentException("清算时间为空，系统无法完成支付消费");
		}
		Long moneyOrder = CalculateUtils.converYuantoPenny(wapPayRsp.getMoney_order());
		if (0 >= moneyOrder.longValue()){
			throw new PaymentException("支付金额异常，系统无法完成支付消费");
		}
	}
	
	/**
	 * 验证钱包支付参数
	 * @Title: _checkWalletPayParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param walletPayDto
	 * @throws PaymentException
	 * @date 2014年7月21日 下午8:11:05  
	 * @author Administrator
	 */
	private void _checkWalletPayParam(Wallet walletPay) throws PaymentException{
		if(null == walletPay){
			throw new PaymentException("参数为空,无法完成钱包支付");
		}
		if(StringUtils.isBlank(walletPay.getUserId())){
			throw new PaymentException("用户编号为空,无法完成钱包支付");
		}
		if(StringUtils.isBlank(walletPay.getPaymendCode())){
			throw new PaymentException("支付密码为空,无法完成钱包支付");
		}
		if(null == walletPay.getTotalPrice() || walletPay.getTotalPrice().longValue() <= 0){
			throw new PaymentException("支付价格异常,无法完成钱包支付");
		}
		if(StringUtils.isBlank(walletPay.getTransactionSerialNumber())){
			throw new PaymentException("交易流水号为空,无法完成钱包支付");
		}
	}
	
	/**
	 * 申请银行卡签约验证
	 * @Title: _checkApplyBindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @throws PaymentException
	 * @date 2014年7月22日 上午11:08:37  
	 * @author Administrator
	 */
	private void _checkApplyBindParam(BankCardInfo bankCardInfo) throws PaymentException{
		if(null == bankCardInfo){
			throw new PaymentException("参数为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getUser_id())){
			throw new PaymentException("用户编号为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getCard_no())){
			throw new PaymentException("银行卡号为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getAcct_name())){
			throw new PaymentException("银行账户姓名为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getBind_mob())){
			throw new PaymentException("绑定手机号为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getId_type())){
			throw new PaymentException("证件类型为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getId_no())){
			throw new PaymentException("证件号码为空,无法申请银行卡签约操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getBankcard_type())){
			throw new PaymentException("卡类型异常(信用卡还是借记卡),无法申请银行卡签约操作");
		}
		if(ConstantsUtil.BANKCARD_TYPE_CREDIT.equals(bankCardInfo.getBankcard_type()) 
				&& StringUtils.isBlank(bankCardInfo.getVali_date()) ){
			throw new PaymentException("信用卡有效期为空,无法申请银行卡签约操作");
		}
		if(ConstantsUtil.BANKCARD_TYPE_CREDIT.equals(bankCardInfo.getBankcard_type()) 
				&& StringUtils.isBlank(bankCardInfo.getCvv2()) ){
			throw new PaymentException("信用卡CVV2为空,无法申请银行卡签约操作");
		}
	}
	
	/**
	 * 创建申请绑卡参数
	 * @Title: _createApplyBindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @return
	 * @date 2014年7月22日 上午11:37:13  
	 * @author Administrator
	 */
	private Map<String,String> _createApplyBindParam(BankCardInfo bankCardInfo)throws PaymentException{
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		if(StringUtils.isBlank(signType)){
			throw new PaymentException("未读取到加密方式signType参数");
		}
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		if(StringUtils.isBlank(oidPartner)){
			throw new PaymentException("未读取到商户编号oidPartner");
		}
		Map<String,String> param = new HashMap<String, String>();
//		param.put("platform", "");
		param.put("user_id", bankCardInfo.getUser_id());
		param.put("oid_partner", oidPartner);
		param.put("sign_type",signType);
		param.put("api_version", ConstantsUtil.LL_BIND_VERSION);
//		param.put("risk_item", "");
		param.put("card_no", bankCardInfo.getCard_no());
		param.put("acct_name", bankCardInfo.getAcct_name());
		param.put("bind_mob", bankCardInfo.getBind_mob());
		if(ConstantsUtil.BANKCARD_TYPE_CREDIT.equals(bankCardInfo.getBankcard_type()) ){
			param.put("vali_date",bankCardInfo.getVali_date());
			param.put("cvv2", bankCardInfo.getCvv2());
		}
		param.put("id_type", bankCardInfo.getId_type());
		param.put("id_no",bankCardInfo.getId_no());
		return param;
	}
	
	/**
	 * 验证 【签约银行卡验证】 参数
	 * @Title: _checVildBindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @throws PaymentException
	 * @date 2014年7月22日 上午11:37:50  
	 * @author Administrator
	 */
	private void _checkValiBindParam(BankCardInfo bankCardInfo) throws PaymentException{
		if(null == bankCardInfo){
			throw new PaymentException("参数为空,无法进行【签约银行卡验证】操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getToken())){
			throw new PaymentException("授权码为空,无法进行【签约银行卡验证】操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getUser_id())){
			throw new PaymentException("用户编号为空,无法进行【签约银行卡验证】操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getVerify_code())){
			throw new PaymentException("短信验证码为空,无法进行【签约银行卡验证】操作");
		}
	}
	/**
	 * 创建 【签约银行卡验证】请求参数
	 * @Title: _createVildBindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @return
	 * @date 2014年7月22日 上午11:42:29  
	 * @author Administrator
	 */
	private Map<String,String> _createValiBindParam(BankCardInfo bankCardInfo)throws PaymentException{
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		if(StringUtils.isBlank(signType)){
			throw new PaymentException("未读取到加密方式signType参数");
		}
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		if(StringUtils.isBlank(oidPartner)){
			throw new PaymentException("未读取到商户编号oidPartner");
		}
		Map<String,String> param = new HashMap<String, String>();
		param.put("oid_partner", oidPartner);
		param.put("token", bankCardInfo.getToken());
		param.put("sign_type",signType);
		param.put("user_id", bankCardInfo.getUser_id());
		param.put("verify_code", bankCardInfo.getVerify_code());
		return param;
	}
	
	/**
	 * 验证 【银行卡解约】 参数
	 * @Title: _checkUnbindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @throws PaymentException
	 * @date 2014年7月22日 上午11:52:19  
	 * @author Administrator
	 */
	private void _checkUnbindParam(BankCardInfo bankCardInfo) throws PaymentException{
		if(null == bankCardInfo){
			throw new PaymentException("参数为空,无法进行【银行卡解约】操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getUser_id())){
			throw new PaymentException("用户编号为空,无法进行【银行卡解约】操作");
		}
		if(StringUtils.isBlank(bankCardInfo.getNo_agree())){
			throw new PaymentException("协议号为空,无法进行【银行卡解约】操作");
		}
	}
	
	/**
	 * 创建【银行卡解约】 请求参数
	 * @Title: _createUnbindParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param bankCardInfoDto
	 * @return
	 * @date 2014年7月22日 上午11:52:51  
	 * @author Administrator
	 */
	private Map<String,String> _createUnbindParam(BankCardInfo bankCardInfo) throws PaymentException{
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		if(StringUtils.isBlank(signType)){
			throw new PaymentException("未读取到加密方式signType参数");
		}
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		if(StringUtils.isBlank(oidPartner)){
			throw new PaymentException("未读取到商户编号oidPartner");
		}
		Map<String,String> param = new HashMap<String, String>();
		param.put("oid_partner", oidPartner);
		param.put("user_id", bankCardInfo.getUser_id());
		param.put("sign_type",signType);
		param.put("no_agree", bankCardInfo.getNo_agree());
		return param;
	}
	
	/**
	 * 校验佣金分成参数
	 * @Title: _checkDivideCommissionParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param divideCommissionDto
	 * @throws PaymentException
	 * @date 2014年7月22日 下午4:06:10  
	 * @author Administrator
	 */
	private void _checkDivideCommissionParam(DivideCommission divideCommission) throws PaymentException{
		if(null == divideCommission){
			throw new PaymentException("佣金分成参数为空");
		}
		if(StringUtils.isBlank(divideCommission.getShopNo())
				|| divideCommission.getShopIncome() < 0){
			throw new PaymentException(new StringBuffer("商户编号或商户利益异常,shopNo=").append(divideCommission.getShopNo())
								.append("shopIncome=").append(divideCommission.getShopIncome()).toString());
		}
		if(divideCommission.getIsPlatformPublicsh() == 2 
				&& ( StringUtils.isBlank(divideCommission.getInvitationCode())
					|| divideCommission.getRefereeDivide() < 0	)){
			throw new PaymentException(new StringBuffer("推荐人邀请码或推荐人佣金金额异常,invitationCode=")
								.append(divideCommission.getInvitationCode())
								.append("refereeDivide=")
								.append(divideCommission.getRefereeDivide()).toString());
		}
		if(StringUtils.isNotBlank(divideCommission.getOnInvitationCode())
				&& divideCommission.getOnRefereeDivide() < 0){
			throw new PaymentException("上家佣金金额异常");
		}
	}
	/**
	 * 验证收益到账参数
	 * @Title: _checkDivideCommissionParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param divideCommissions
	 * @throws PaymentException
	 * @date 2015年1月20日 下午5:30:15  
	 * @author phb
	 */
	private void _checkDivideCommissionParam(List<DivideCommissions> divideCommissions) throws PaymentException{
		if(null == divideCommissions){
			throw new PaymentException("参数为空");
		}
		for (DivideCommissions dc : divideCommissions) {
			if( null == dc.getAccountId() ){
				throw new PaymentException("店铺钱包参数为空");
			}
			if(StringUtils.isBlank(dc.getTransactionSerialNumber())){
				throw new PaymentException("交易流水号为空");
			}
			if(null == dc.getType()){
				throw new PaymentException("收益类型参数为空");
			}
			if(0 > dc.getMoney().longValue()){
				throw new PaymentException("金额异常");
			}
		}
	}
	
	/**
	 *  钱包资金流转
	 * @Title: _transferAccountToAmount 
	 * @Description: (从一个账户余额，到另一个账户的余额或者佣金) 
	 * @param from 						扣减账户
	 * @param to						累加账户
	 * @param price						操作金额
	 * @param transationSerialNumber	流水号(一个事务同一个流水)
	 * @param operationType				操作类型
	 * @param transferType				1.累加到余额 2.累加到佣金
	 * @throws PaymentException
	 * @throws AmountException
	 * @date 2014年7月22日 下午4:33:38  
	 * @author Administrator
	 */
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	private void _transferAccountToAmount(Account from,Account to,Long price,String transationSerialNumber,
			Integer operationType,Integer transferType) throws PaymentException,AmountException{
		try {
			if(null == from){
				_infoLogger.info("【钱包资金流转】扣减账户为空");
				throw new PaymentException("【钱包资金流转】扣减账户为空");
			}
			if(null == from.getWithdrawAmount() || from.getWithdrawAmount().longValue() < price.longValue()){
				_infoLogger.info("【钱包资金流转】扣减账户余额不足");
				throw new AmountException("【钱包资金流转】扣减账户余额不足");
			}
			if(null == to){
				_infoLogger.info("【钱包资金流转】累加账户为空");
				throw new PaymentException("【钱包资金流转】累加账户为空");
			}
			accountDao.minusAccountAmount(from.getId(), price);
			//查询操作后的账户信息
			Account fromAfter = accountDao.getAccountById(from.getId());
			
			switch (transferType) {
			case 1:
				_infoLogger.info("【钱包资金流转】当前操作将累加到余额");
				accountDao.sumAccountAmount(to.getId(), price);
				break;
			case 2:
				_infoLogger.info("【钱包资金流转】当前操作将累加到佣金");
				accountDao.sumAccountCommission(to.getId(), price);
				this.withdrawCommission(to.getId(), transationSerialNumber, price);   //TODO
				break;
			default:
				_infoLogger.info("【钱包资金流转】transferType 参数异常");
				throw new PaymentException("【钱包资金流转】transferType 参数异常");
			}
			//查询操作后的账户信息
			Account toAfter = accountDao.getAccountById(to.getId());
			
			Date date = new Date();
			//添加资金流转记录
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setSerialNumber(transationSerialNumber);
			aor.setFromAccountId(from.getId());
			aor.setToAccountId(to.getId());
			aor.setOperationAmount(price);
			aor.setFromAmountBefore(from.getWithdrawAmount());
			aor.setFromCommissionBefore(from.getWithdrawCommission());
			aor.setFromAmountAfter(fromAfter.getWithdrawAmount());
			aor.setFromCommissionAfter(fromAfter.getWithdrawCommission());
			aor.setToAmountBefore(to.getWithdrawAmount());
			aor.setToCommissionBefore(to.getWithdrawCommission());
			aor.setToAmountAfter(toAfter.getWithdrawAmount());
			aor.setToCommissionAfter(toAfter.getWithdrawCommission());
			aor.setOperationType(operationType);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(aor);
		}catch (AmountException ae){
			throw ae;
		}catch (Exception e) {
			throw new PaymentException(e.getMessage(),e);
		}
	}
	/**
	 *  钱包资金流转
	 * @Title: _transferAccountToAmount 
	 * @Description: (从一个账户余额，到另一个账户的余额或者佣金) 
	 * @param from 						扣减账户
	 * @param to						累加账户
	 * @param price						操作金额
	 * @param transationSerialNumber	流水号(一个事务同一个流水)
	 * @param operationType				操作类型
	 * @param transferType				1.累加到余额 2.累加到佣金
	 * @throws PaymentException
	 * @throws AmountException
	 * @date 2014年7月22日 下午4:33:38  
	 * @author Administrator
	 */
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	private void _transferAccountToAmount(Account from,Account to,Long price,String transationSerialNumber,
			Integer operationType,Integer transferType,Integer payWay,String remark) throws PaymentException,AmountException{
		try {
			if(null == from){
				_infoLogger.info("【钱包资金流转】扣减账户为空");
				throw new PaymentException("【钱包资金流转】扣减账户为空");
			}
			if(null == from.getWithdrawAmount() || from.getWithdrawAmount().longValue() < price.longValue()){
				_infoLogger.info("【钱包资金流转】扣减账户余额不足");
				throw new AmountException("【钱包资金流转】扣减账户余额不足");
			}
			if(null == to){
				_infoLogger.info("【钱包资金流转】累加账户为空");
				throw new PaymentException("【钱包资金流转】累加账户为空");
			}
			if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(from.getId())){
				accountDao.minusAccountAmount(from.getId(), price);
			}
			//查询操作后的账户信息
			Account fromAfter = accountDao.getAccountById(from.getId());
			
			switch (transferType) {
			case 1:
				_infoLogger.info("【钱包资金流转】当前操作将累加到余额");
				if(!ConstantsUtil.ACCOUNT_PINGTAI.equals(to.getId())){
					accountDao.sumAccountAmount(to.getId(), price);
				}
				break;
			case 2:
				_infoLogger.info("【钱包资金流转】当前操作将累加到佣金");
				accountDao.sumAccountCommission(to.getId(), price);
				this.withdrawCommission(to.getId(), transationSerialNumber, price);   //TODO
				break;
			default:
				_infoLogger.info("【钱包资金流转】transferType 参数异常");
				throw new PaymentException("【钱包资金流转】transferType 参数异常");
			}
			//查询操作后的账户信息
			Account toAfter = accountDao.getAccountById(to.getId());
			
			Date date = new Date();
			//添加资金流转记录
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setSerialNumber(transationSerialNumber);
			aor.setFromAccountId(from.getId());
			aor.setToAccountId(to.getId());
			aor.setOperationAmount(price);
			aor.setFromAmountBefore(from.getWithdrawAmount());
			aor.setFromCommissionBefore(from.getWithdrawCommission());
			aor.setFromAmountAfter(fromAfter.getWithdrawAmount());
			aor.setFromCommissionAfter(fromAfter.getWithdrawCommission());
			aor.setToAmountBefore(to.getWithdrawAmount());
			aor.setToCommissionBefore(to.getWithdrawCommission());
			aor.setToAmountAfter(toAfter.getWithdrawAmount());
			aor.setToCommissionAfter(toAfter.getWithdrawCommission());
			aor.setOperationType(operationType);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			aor.setPayWayFlag(payWay);
			aor.setRemark(remark);
			if(ConstantsUtil.ACCOUNT_PINGTAI.equals(from.getId()) || ConstantsUtil.ACCOUNT_PINGTAI.equals(to.getId())){
				aor.setDeal(AccountServiceImpl.DEAL_WAIT);
			}else{
				aor.setDeal(AccountServiceImpl.DEAL_NO_REQUIRE);
			}
			accountOperationRecordDao.addOperationRecord(aor);
		}catch (AmountException ae){
			throw ae;
		}catch (Exception e) {
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	/**
	 * 验证收款参数
	 * @Title: _checkReceivablesParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param receivables
	 * @throws PaymentException
	 * @date 2014年7月23日 下午3:50:14  
	 * @author Administrator
	 */
	private void _checkReceivablesParam(Receivables receivables) throws PaymentException{
		if(null == receivables ){
			throw new PaymentException("参数为空");
		}
		if(null == receivables.getAmount() || receivables.getAmount() < 0){
			throw new PaymentException("金额异常");
		}
		if(StringUtils.isBlank(receivables.getTransactionSerialNumber())){
			throw new PaymentException("交易流水号为空");
		}
//		if(null == receivables.getBankCardId()){
//			throw new PaymentException("付款银行卡编号为空");
//		}
//		if(null == receivables.getBankCard()){
//			throw new PaymentException("付款银行卡号为空");
//		}
	}
	
	/**
	 * RSA加密
	 * @Title: _encryptByParams 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param plainText
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月10日 下午4:55:02  
	 * @author Administrator
	 */
	private String _encryptByParams(String plainText) throws PaymentException{
		try {
			String privateKey = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_PRIKEY_DEFAULT);
			if(StringUtils.isBlank(privateKey)){
				throw new PaymentException("未读取到景正私钥privateKey");
			}
			String sign = RSAUtil.sign(privateKey, plainText);
			return sign;
		} catch (Exception e) {
			throw new PaymentException("加密失败!",e);
		}
	}
	
	/**
	 * 签名验证
	 * @Title: _comparableToParams 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param sign
	 * @param obj
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月10日 下午5:02:37  
	 * @author Administrator
	 */
	private boolean _comparableToParams(String sign,Object obj) throws PaymentException{
		Class<? extends Object> clazz = (Class<? extends Object>) obj.getClass();
		Map<String,String> map = new ConcurrentHashMap<String,String>();
		 try {
			 Field[] fs = clazz.getDeclaredFields();
			 for (Field field : fs) {
			     String name = field.getName();
			     if ("sign".equals(name)) continue; 
			     if ("serialVersionUID".equals(name)) continue;
			     String upName = name.substring(0, 1).toUpperCase() + name.substring(1); 
				 Method m = obj.getClass().getMethod("get" + upName);
				 String value = (String) m.invoke(obj);
				 if (StringUtils.isNotBlank(value)) {
					 map.put(name, value);
				}
			 }
			 String plaintext = SortParamUtil.sortParamForSign(map);
			 String publicKey = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_PUBKEY_LIANLIAN);
			 if(StringUtils.isBlank(publicKey)){
				throw new PaymentException("未读取到连连公钥privateKey");
			 }
			 return RSAUtil.checksign(publicKey, plaintext, sign);
		} catch (Exception e) {
			throw new PaymentException("比较明文参数时发生异常,支付失败!",e);
		}
	}
	
	/**
	 * 组装查询支付状态参数
	 * @Title: _createPaymentQueryParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wapPayReq
	 * @return
	 * @throws PaymentException
	 * @date 2014年8月22日 下午5:57:47  
	 * @author Administrator
	 */
	private Map<String,String> _createPaymentQueryParam(WapPayReq wapPayReq) throws PaymentException{
		String signType = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE);
		if(StringUtils.isBlank(signType)){
			throw new PaymentException("未读取到加密方式signType参数");
		}
		String oidPartner = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_OID_PARTNER);
		if(StringUtils.isBlank(oidPartner)){
			throw new PaymentException("未读取到商户编号oidPartner");
		}
		Map<String,String> param = new HashMap<String, String>();
		param.put("query_version", ConstantsUtil.LL_PAY_VERSION);
		param.put("oid_partner", oidPartner);
		param.put("sign_type", signType);
		param.put("no_order", wapPayReq.getNo_order());
		return param;
	}
	
	/***
	 * 拼装异常信息
	 * @Title: getErrorInfo 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param errorInfo
	 * @param errorMsg
	 * @param errorData
	 * @return
	 * @date 2014年8月9日 上午11:52:35  
	 * @author Administrator
	 */
	private String _getErrorInfo(String errorInfo,String errorMsg,Object errorData){
		String msg = new StringBuffer("{\"err_info\":\"").append(errorInfo)
				.append("\",\"err_msg\":\"").append(errorMsg).append("\",\"err_data\":")
				.append(JSONObject.fromObject(errorData).toString()).append("}").toString();
		return msg;
	}
	
	private String _getArrayErrorInfo(String errorInfo,String errorMsg,Object errorData){
		String msg = new StringBuffer("{\"err_info\":\"").append(errorInfo)
				.append("\",\"err_msg\":\"").append(errorMsg).append("\",\"err_data\":")
				.append(JSONArray.fromObject(errorData).toString()).append("}").toString();
		return msg;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean getSubAccountBalance(Long userId, List<String> shopNoList)
			throws PaymentException {
		
		try {
			Assert.notNull(userId, "参数[userId]不能为空");
			Assert.notEmpty(shopNoList, "参数[shopNoList]不能为空");
			
			
		} catch (Exception e) {
			throw new PaymentException(e);
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	public boolean platfromSendAmount(Wallet wallet) throws PaymentException {
		try {
			_infoLogger.info(new StringBuffer("平台赠送金额参数：").append(JSONObject.fromObject(wallet).toString()).toString());
			if(null == wallet || StringUtils.isBlank(wallet.getUserId()) || null == wallet.getTotalPrice()){
				throw new PaymentException("平台赠送金额参数为空");
			}
			//统一流水号
			String serialNumber = GenerateCode.getWalletSN();
			//查询平台账户
			String jzAccountId = PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID);
			if(StringUtils.isBlank(jzAccountId)){
				_infoLogger.info("无法从配置中获取到平台账户编号,无法赠送金额");
				throw new PaymentException("无法从配置中获取到平台账户编号,无法赠送金额");
			}
			Account jzAccount = accountDao.getAccountById(Long.valueOf(jzAccountId));
			if(null == jzAccount){
				_infoLogger.info("未查询到平台账户,无法赠送金额");
				throw new PaymentException("未查询到平台账户,无法赠送金额");
			}
			if(jzAccount.getTosendAmount().longValue() - wallet.getTotalPrice().longValue() < 0){
				_infoLogger.info("赠送金额已送完，当前不能继续赠送");
				return false;
			}
			//锁定平台钱包
			jzAccount = accountDao.getAccountByIdLock(Long.valueOf(jzAccountId));
			_infoLogger.info("为赠送金额，已锁定平台钱包");
			
			Account account = new Account(String.valueOf(wallet.getUserId()), Account.REMOTE_TYPE_USER);
			Account userAccount = accountDao.getAccountByRemoteId(account);
//			Account userAccount = accountDao.getAccountByUserId(Long.valueOf(wallet.getUserId()));
			if(null == userAccount){
				_infoLogger.info("未查询到用户账户,无法赠送金额");
				throw new PaymentException("未查询到用户账户,无法赠送金额");
			}
			//入用户账户
			accountDao.sumAccountAmount(userAccount.getId(), wallet.getTotalPrice());
			
			account = new Account(String.valueOf(wallet.getUserId()), Account.REMOTE_TYPE_USER);
			Account userAccountAfter = accountDao.getAccountByRemoteId(account);
//			Account userAccountAfter =  accountDao.getAccountByUserId(Long.valueOf(wallet.getUserId()));
			
			//扣平台账户(扣余额和可赠送金额)
			accountDao.platfromSendAmount(Long.valueOf(jzAccountId), wallet.getTotalPrice());
			Account jzAccountAfter = accountDao.getAccountById(Long.valueOf(jzAccountId));
			
			Date date = new Date();
			//添加资金流转记录
			AccountOperationRecord aor = new AccountOperationRecord();
			aor.setSerialNumber(serialNumber);
			aor.setFromAccountId(jzAccount.getId());
			aor.setToAccountId(userAccount.getId());
			aor.setOperationAmount(wallet.getTotalPrice());
			aor.setFromAmountBefore(jzAccount.getWithdrawAmount());
			aor.setFromCommissionBefore(jzAccount.getWithdrawCommission());
			aor.setFromAmountAfter(jzAccountAfter.getWithdrawAmount());
			aor.setFromCommissionAfter(jzAccountAfter.getWithdrawCommission());
			aor.setToAmountBefore(userAccount.getWithdrawAmount());
			aor.setToCommissionBefore(userAccount.getWithdrawCommission());
			aor.setToAmountAfter(userAccountAfter.getWithdrawAmount());
			aor.setToCommissionAfter(userAccountAfter.getWithdrawCommission());
			aor.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_PLATFROM_SEND);
			aor.setCreateTime(date);
			aor.setUpdateTime(date);
			accountOperationRecordDao.addOperationRecord(aor);
			return true;
		} catch (Exception e) {
			_errorLogger.error(_getErrorInfo(e.getMessage(),"平台赠送金额失败",wallet));
			throw new PaymentException(e.getMessage(),e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = { "PaymentException" })
	public boolean virtualCommissionTransform(String shopNo, Long commission)
			throws PaymentException {
		try {
			if (StringUtils.isBlank(shopNo)) {
				_errorLogger.error("商铺为空，佣金转入失败。");
				throw new PaymentException("商铺为空，佣金转入失败。");
			}
			if (null == commission || commission <= 0) {
				_errorLogger.error("佣金不合理，佣金转入失败。");
				throw new PaymentException("佣金不合理，佣金转入失败。");
			}
			Date updateTime = new Date();
			//平台账户钱转出
			Account jzAccount = accountDao.getAccountById(Long.valueOf(PropertiesUtil.getContexrtParam(ConstantsUtil.PROPER_JZ_ACCOUNT_ID)));
			if (null == jzAccount) {
				_errorLogger.error("平台账户检索失败");
				throw new PaymentException("平台账户检索失败");
			}
			if (null == jzAccount.getWithdrawAmount() || commission > jzAccount.getWithdrawAmount()) {
				_errorLogger.error("平台余额不足");
				throw new PaymentException("平台余额不足");
			}
			jzAccount.setWithdrawAmount(jzAccount.getWithdrawAmount() - commission);
			jzAccount.setUpdateTime(updateTime);
			accountDao.editAccount(jzAccount);
			//账户金额转入
			Account account = accountDao.getAccountByRemoteId(new Account(shopNo, Account.REMOTE_TYPE_SHOP));
			if (null == account) {
				_errorLogger.error("对应店铺的钱包不存在，佣金转入失败");
				throw new PaymentException("对应店铺的钱包不存在，佣金转入失败");
			}
			account.setWithdrawCommission(account.getWithdrawCommission() + commission);
			account.setUpdateTime(updateTime);
			accountDao.editAccount(account);
			//佣金转入记录生成
			String serialNumber = GenerateCode.getWalletSN();
			AccountOperationRecord operationRecord = new AccountOperationRecord();
			operationRecord.setSerialNumber(serialNumber);
			operationRecord.setFromAccountId(jzAccount.getId());
			operationRecord.setToAccountId(account.getId());
			operationRecord.setOperationAmount(commission);
			operationRecord.setFromAmountBefore(jzAccount.getWithdrawAmount() + commission);
			operationRecord.setFromAmountAfter(jzAccount.getWithdrawAmount());
			operationRecord.setFromCommissionBefore(jzAccount.getWithdrawCommission());
			operationRecord.setFromCommissionAfter(jzAccount.getWithdrawCommission());
			operationRecord.setToAmountBefore(account.getWithdrawAmount());
			operationRecord.setToAmountAfter(account.getWithdrawAmount());
			operationRecord.setToCommissionBefore(account.getWithdrawCommission() - commission);
			operationRecord.setToCommissionAfter(account.getWithdrawCommission());
			//佣金收益
			operationRecord.setOperationType(ConstantsUtil.ACCOUNT_OPERATION_DIVIDE);
			operationRecord.setCreateTime(updateTime);
			operationRecord.setUpdateTime(updateTime);
			accountOperationRecordDao.addOperationRecord(operationRecord);
			
			//佣金自动转出
			this.withdrawCommission(account.getId(), serialNumber, commission);
			return true;
		} catch (Exception e) {
			throw new PaymentException(e);
		}
	}
	
	@Override
	public BankInfo getBankInfoByBankCard(String bankCard) {
		return KabinUtil.get(bankCard);
	}
}
