package com.zjlp.face.account.service;

import java.util.List;

import com.chinaums.pay.api.PayException;
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

/**
 * 支付服务
 * @ClassName: PaymentService 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年7月10日 上午10:36:32
 */
public interface PaymentService {
	/**
	 * 银行卡签约申请
	 * @Title: applyBindBankCard 
	 * @Description: (用于绑定银行卡提交申请信息) 
	 * @param bankCardInfo
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月23日 上午11:46:52  
	 * @author Administrator
	 */
	WapPayRsp applyBindBankCard(BankCardInfo bankCardInfo)throws PaymentException;
	
	/**
	 * 查询银行卡卡bin
	 * @Title: getCardBin 
	 * @Description: (用于绑卡时 获取银行卡信息) 
	 * @param cardNo
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月22日 上午9:05:56  
	 * @author Administrator
	 */
	WapPayRsp getCardBin(String cardNo) throws PaymentException;
	
	/**
	 * 获取交易流水号
	 * @Title: getTransactionSerialNumber 
	 * @Description: (所有支付 统一获取交易流水号) 
	 * @param userId	用户编号
	 * @param busiPartner	交易类型 1.实物订单 2.话费充值 3.彩票购买
	 * @return 交易流水
	 * @throws PaymentException
	 * @date 2014年7月19日 下午2:33:51  
	 * @author Administrator
	 */
	String getTransactionSerialNumber(String userId,Integer busiPartner) throws PaymentException;
	
	/**
	 * 钱包支付
	 * @Title: paymentByWallet 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wallet
	 * @return
	 * @throws PaymentException
	 * @throws AmountException(余额不足异常)
	 * @date 2014年7月23日 上午11:47:06  
	 * @author Administrator
	 */
	boolean paymentByWallet(Wallet wallet) throws PaymentException,AmountException;
	
	/**
	 * 验证支付消费签名
	 * @Title: checkPaymentSign 
	 * @Description: (验证签名，防止恶意请求) 
	 * @param wapPayRspVo
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月23日 上午11:47:14  
	 * @author Administrator
	 */
	WapPayRsp checkPaymentSign(WapPayRsp wapPayRspVo) throws PaymentException;
	
	/**
	 * 支付生产者
	 * @Title: paymentProducer 
	 * @Description: (生成支付请求参数) 
	 * @param wapPayReq
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月23日 上午11:47:26  
	 * @author Administrator
	 */
	String paymentProducer(WapPayReq wapPayReq) throws PaymentException;
	
	/**
	 * 银行卡解除签约
	 * @Title: unbindBankCard 
	 * @Description: (用于解除银行卡绑定) 
	 * @param bankCardInfo
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月22日 上午10:57:25  
	 * @author Administrator
	 */
	WapPayRsp unbindBankCard(BankCardInfo bankCardInfo) throws PaymentException;
	
	/**
	 * 银行卡签约验证
	 * @Title: valiBindBankCard 
	 * @Description: (提交授权码和短信验证码到连连 验证签约) 
	 * @param bankCardInfo
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月22日 上午10:53:02  
	 * @author Administrator
	 */
	WapPayRsp valiBindBankCard(BankCardInfo bankCardInfo)throws PaymentException;
	
	/**
	 * 佣金分成
	 * @Title: divideCommission 
	 * @Description: (用于确认收货时，将钱从平台账户转到商户余额、推荐人佣金、推荐人上家佣金) 
	 * @param divideCommission
	 * @return
	 * @throws PaymentException
	 * @throws AmountException
	 * @date 2014年7月22日 下午3:16:24  
	 * @author Administrator
	 */
	@Deprecated
	boolean divideCommission(DivideCommission divideCommission) throws PaymentException,AmountException;
	
	/**
	 * 二阶段改
	 * @Title: divideCommission 
	 * @Description: (用于确认收货时，收益到账) 
	 * @param divideCommissions
	 * @return
	 * @throws PaymentException
	 * @throws AmountException
	 * @date 2015年1月20日 下午3:35:52  
	 * @author phb
	 */
	boolean divideCommission(List<DivideCommissions> divideCommissions) throws PaymentException,AmountException;
	
	/**
	 * 佣金提现
	 * @Title: withdrawCommission 
	 * @Description: (将一个账户的钱从佣金转到余额) 
	 * @param walletPay
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月22日 下午3:43:54  
	 * @author Administrator
	 */
	@Deprecated
	boolean withdrawCommission(Wallet wallet) throws PaymentException;
	
	/**
	 * 收款
	 * @Title: receivables 
	 * @Description: (银行卡支付时，连连收取手续费，平台入账) 
	 * @param receivables
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月23日 下午2:18:25  
	 * @author Administrator
	 */
	boolean receivables(Receivables receivables) throws PaymentException;
	
	/***
	 * 微信支付 收款
	 * @Title: receivablesWechat
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param receivables
	 * @return
	 * @throws PaymentException
	 * @return boolean
	 * @author phb
	 * @date 2015年3月18日 上午10:23:54
	 */
	boolean receivablesWechat(Receivables receivables) throws PaymentException;
	
	/**
	 * 第三方钱包收款
	 * @Title: bindFeeReceivables
	 * @Description: (接口在调用时，需要收取手续费的情况，除支付外)
	 * @param receivables
	 * 			channel						渠道				M
	 * 			fee							手续费			M
	 * 			transactionSerialNumber		交易流水			M
	 * @return
	 * @throws PayException
	 * @return boolean
	 * @author phb
	 * @date 2015年6月1日 上午11:55:02
	 */
	boolean bindFeeReceivables(Receivables receivables) throws PaymentException;
	
	/**
	 * 店铺编号列表
	 * 
	 * @Title: getSubAccountBalance 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param userId
	 * @param shopNoList
	 * @return
	 * @throws PaymentException
	 * @date 2015年1月17日 下午1:15:57  
	 * @author lys
	 */
	boolean getSubAccountBalance(Long userId, List<String> shopNoList) throws PaymentException;
	
	/**
	 * 累计消费金额
	 * @Title: sumConsumeAmount 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @return
	 * @date 2014年8月19日 上午11:30:57  
	 * @author Administrator
	 */
	boolean sumConsumeAmount(Long userId,Long consumeAmount) throws PaymentException;
	
	/**
	 * 彩票中奖
	 * @Title: lotteryWinner 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wallet
	 * @return
	 * @throws PaymentException
	 * @date 2014年8月21日 下午8:33:04  
	 * @author Administrator
	 */
	boolean lotteryWinner(Wallet wallet)throws PaymentException;
	
	/**
	 * 钱包退款
	 * @Title: WalletRefund 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wallet
	 * @return
	 * @throws PaymentException
	 * @date 2014年8月21日 下午8:44:15  
	 * @author Administrator
	 */
	boolean WalletRefund(Wallet wallet)throws PaymentException;
	
	/**
	 * 根据交易流水号查询连连交易状态
	 * @Title: paymentQueryForLianLian 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param wapPayReq
	 * @return
	 * @throws PaymentException
	 * @date 2014年8月22日 下午5:50:53  
	 * @author Administrator
	 */
	WapPayRsp paymentQueryForLianLian(WapPayReq wapPayReq) throws PaymentException;
	
	boolean platfromSendAmount(Wallet wallet) throws PaymentException;
	
	/**
	 * 虚拟订单冻结佣金可用
	 * @Title: virtualCommissionTransform 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param shopNo 商铺
	 * @param commission 佣金
	 * @return
	 * @throws PaymentException
	 * @date 2014年10月6日 下午7:59:27  
	 * @author Administrator
	 */
	boolean virtualCommissionTransform(String shopNo, Long commission) throws PaymentException;
	
	/**
	 * @Description: 通过银行卡号获取银行卡信息
	 * @date: 2015年9月10日 下午1:56:15
	 * @author: zyl
	 */
	BankInfo getBankInfoByBankCard(String bankCard);
}
