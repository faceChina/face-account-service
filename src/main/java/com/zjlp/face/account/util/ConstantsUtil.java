package com.zjlp.face.account.util;

public class ConstantsUtil {
	/** 平台账号*/
	public final static Long ACCOUNT_PINGTAI=1l;
	/**SUCCESS*/
	public static final String SUCCESS = "SUCCESS";
	
	/**连连支付版本号*/
	public static final String LL_PAY_VERSION = "1.1";
	/**连连绑卡接口版本*/
	public static final String LL_BIND_VERSION = "2.1";
	
	/**
	 * 1-Android 2-ios 3-WAP
	 */
	/**1-Android*/
	public static final String APP_REQUEST_ANDROID = "1";
	/**2-ios*/
	public static final String APP_REQUEST_IOS = "2";
	/**3-WAP*/
	public static final String APP_REQUEST_WAP = "3";
	
	/**
	 * 2.借记卡 3.信用卡
	 */
	/**2.借记卡*/
	public static final String BANKCARD_TYPE_DEBIT = "2";
	/**3.信用卡*/
	public static final String BANKCARD_TYPE_CREDIT = "3";
	
	/**
	 * 利益分成1.累加到余额2.累加到佣金
	 */
	/**1.累加到余额*/
	public static final Integer TRANSFER_TO_AMOUNT = 1;
	/**2.累加到佣金*/
	public static final Integer TRANSFER_TO_COMMISSION = 2;
	
	/**
	 * 虚拟商品销售：101001、实物商品销售：109001、外部账户充值：108001、账户结转：102001、 账户提现：202000、商户付款：107001
	 */
	/**虚拟商品销售：101001*/
	public static final String BUSI_PARTNER_VIRTUAL = "101001";
	/**实物商品销售：109001*/
	public static final String BUSI_PARTNER_PHYSICAL = "109001";
	/**外部账户充值：108001*/
	public static final String BUSI_PARTNER_EXTERNAL = "108001";
	/**账户结转：102001*/
	public static final String BUSI_PARTNER_ACCOUNT_FORWARD = "102001";
	/**账户提现：202000*/
	public static final String BUSI_PARTNER_WITHDRAWAL = "202000";
	/**商户付款：107001*/
	public static final String BUSI_PARTNER_PAYMENT = "107001";
	
	/**
	 * 账户操作: 1银行卡支付 2钱包支付 3余额提现 4佣金提现 11商户收益 12手续费收益 13佣金收益 14彩票中奖
	 */
	/**1银行卡支付*/
	public static final Integer ACCOUNT_OPERATION_BANKCARD_PAY = 1;
	/**2钱包支付*/
	public static final Integer ACCOUNT_OPERATION_WALLET_PAY = 2;
	/**3余额提现*/
	public static final Integer ACCOUNT_OPERATION_WITHDRAW_AMOUNT = 3;
	/**4佣金提现*/
	public static final Integer ACCOUNT_OPERATION_WITHDRAW_COMMISSION = 4;
	/**5提现失败解冻*/
	public static final Integer ACCOUNT_OPERATION_WITHDRAW_RETURN = 5;
	/**6提现成功冻结金额转出*/
	public static final Integer ACCOUNT_OPERATION_WITHDRAW_TRANSFER = 6;
	/**11商户收益*/
	public static final Integer ACCOUNT_OPERATION_SHOP_INCOME = 11;
	/**12手续费收益*/
	public static final Integer ACCOUNT_OPERATION_FEE = 12;
	/**13佣金收益*/
	public static final Integer ACCOUNT_OPERATION_DIVIDE = 13;
	/**14彩票中奖*/
	public static final Integer ACCOUNT_OPERATION_LOTTERY_WINNING = 14;
	/**15一键余额提取*/
	public static final Integer ACCOUNT_OPERATION_BALANCE_EXTRACT = 15;
	/**16卖家退款*/
	public static final Integer ACCOUNT_OPERATION_REFUND = 16;
	/**17平台赠送*/
	public static final Integer ACCOUNT_OPERATION_PLATFROM_SEND = 17;
	/**18分销收益*/
	public static final Integer ACCOUNT_OPERATION_AGENCY = 18;
	/**19全民推广收益*/
	public static final Integer ACCOUNT_OPERATION_NATIONAL_PROMOTION = 19;
	
	public static final Integer ACCOUNT_OPERATION_EMPLOYEE = 20;
	/** BF收益*/
	public static final Integer ACCOUNT_OPERATION_BF = 21;
	
	/** 领取红包流转 */
	public static final Integer ACCOUNT_OPERATION_REDPACKAGE_RECEIVE = 22;
	/** 红包余额返回 */
	public static final Integer ACCOUNT_OPERATION_REDPACKAGE_BACK = 23;
	/** 红包钱包支付 */
	public static final Integer ACCOUNT_OPERATION_REDPACKAGE_PAY = 24;

	/**
	 * 账户支付方式:5支付宝支付,2钱包支付,3微信支付
	 */
	/**5支付宝支付*/
	public static final Integer  ACCOUNT_TYPE_PAY_WAY_ZFB = 5;
	/**2钱包支付*/
	public static final Integer  ACCOUNT_TYPE_PAY_WAY_WALLET = 2;
	/**3微信支付*/
	public static final Integer  ACCOUNT_TYPE_PAY_WAY_WECHAT = 3;
	
	/**
	 * 配置文件常量
	 */
	/**加密方式*/
	public static final String PROPER_SWITCH_UNIONPAY_ENCRYPTTYPE = "SWITCH_UNIONPAY_ENCRYPTTYPE";
	/**私钥*/
	public static final String PROPER_PRIKEY_DEFAULT = "PRIKEY_DEFAULT";
	/**公钥*/
	public static final String PROPER_PUBKEY_LIANLIAN = "PUBKEY_LIANLIAN";
	/**商户编号*/
	public static final String PROPER_OID_PARTNER = "OID_PARTNER";
	/**卡bin查询地址*/
	public static final String PROPER_CARD_BIN_URL = "CARD_BIN_URL";
	/**申请银行卡签约地址*/
	public static final String PROPER_APPLY_CARD_BIND_URL = "APPLY_CARD_BIND_URL";
	/**银行卡签约验证地址*/
	public static final String PROPER_VALI_CARD_BIND_URL = "VALI_CARD_BIND_URL";
	/**银行卡解约地址*/
	public static final String PROPER_UNBIND_CARD_BIND_URL = "UNBIND_CARD_BIND_URL";
	
	public static final String PROPER_PAYMENT_QUERY_URL = "PAYMENT_QUERY_URL";
	/**平台账户编号*/
	public static final String PROPER_JZ_ACCOUNT_ID = "JZ_ACCOUNT_ID";
	/**连连账户编号*/
	public static final String PROPER_LIANLIAN_ACCOUNT_ID = "LIANLIAN_ACCOUNT_ID";
	/**微信账户编号*/
	public static final String PROPER_WECHAT_ACCOUNT_ID = "WECHAT_ACCOUNT_ID";
	/**捷蓝绑定支付账户编号*/
	public static final String PROPER_JIELAN_ACCOUNT_ID = "JIELAN_ACCOUNT_ID";
	/**支付宝账户编号*/
	public static final String PROPER_ALIPAY_ACCOUNT_ID = "ALIPAY_ACCOUNT_ID";
	/**拉卡拉账户编号*/
	public static final String PROPER_LAKALA_ACCOUNT_ID = "LAKALA_ACCOUNT_ID";
	
	/** 账户类型  */
	/** 超级管理员 */
	public static final Integer ACCOUNTTYPE_MANAGER = 0;
	/** 普通用户 */
	public static final Integer ACCOUNTTYPE_NORMAL = 1;
	/** 产品账户 */
	public static final Integer ACCOUNTTYPE_PRODUCT = 2;
	
	/***
	 * 手续费
	 */
	/**连连手续费比例*/
	public static final String LIANLIAN_FEE = "LIANLIAN_FEE";
	/**平台手续费比例*/
	public static final String PLATFORM_FEE = "PLATFORM_FEE";
	/**连连手续费最小值*/
	public static final String LIANLIAN_MIN_FEE = "LIANLIAN_MIN_FEE";
	
	
	/**
	 * 提现状态  0 提现成功  -1 提现失败  5提现处理中 
	 */
	/**提现成功*/
	public static final Integer WD_STATE_SUCC = 0;
	/**提现失败*/
	public static final Integer WD_STATE_FAIL = -1;
	/**提现处理中*/
	public static final Integer WD_STATE_DEAL = 5;
	/** 没有该提现结果 */
	public static final Integer WD_STATE_NORECORD = 10;
	
	
	/**
	 * 交易状态  90：交易成功 91：交易失败 99：系统处理中 94：银行已受理  93：被银行取消
	 */
	/** 90：交易成功  */
	public static final String NBJY_STATE_90 = "90";
	/** 91：交易失败 */
	public static final String NBJY_STATE_91 = "91";
	/** 93：被银行取消 */
	public static final String NBJY_STATE_93 = "93";
	/** 94：银行已受理  */
	public static final String NBJY_STATE_94 = "94";
	/** 99：系统处理中 */
	public static final String NBJY_STATE_99 = "99";
	
	/** 成功返回码 */
	public static final String RET_CODE_0000 = "0000";
	
	/** 宁波银行没有对应的提现请求 */
	public static final String NO_RECORD = "EBLN5001";
	
	/**
	 * 服务类型
	 */
	/** 同行转账  */
	public static final Integer SERVICE_PEER_WD = 1;
	/** 跨行转账  */
	public static final Integer SERVICE_OUTER_WD = 2;
	/** 提现结果查询 */
	public static final Integer SERVICE_QUERY_WD = 3;
}
