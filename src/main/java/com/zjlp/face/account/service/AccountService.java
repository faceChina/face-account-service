package com.zjlp.face.account.service;

import java.util.List;

import com.zjlp.face.account.domain.Account;
import com.zjlp.face.account.domain.AccountOperationRecord;
import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.exception.AccountException;
import com.zjlp.face.util.page.Aide;

/**
 * 钱包基础服务
 * 
 * @ClassName: AccountService
 * @Description: (这里用一句话描述这个类的作用)
 * @author lys
 * @date 2015年3月11日 上午9:21:47
 */
public interface AccountService {
	
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
	Long addAccount(String remoteId, Integer remoteType) throws AccountException;
	

	/**
	 * 钱包初始化
	 * 
	 * @Title: addAccount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param shopNo
	 *            店铺编号
	 * @param phone
	 *            手机号码
	 * @param invitationCode
	 *            店铺邀请码
	 * @param email
	 *            电子邮箱
	 * @param margin
	 *            保证金
	 * @return
	 * @throws AccountException
	 * @date 2015年3月17日 下午1:26:46
	 * @author lys
	 */
	Long addAccount(String shopNo, String phone, String invitationCode,
			String email, Long margin) throws AccountException;

	/**
	 * 钱包初始化
	 * 
	 * @Title: addAccount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @param phone
	 *            手机号码
	 * @param email
	 *            电子邮箱
	 * @param margin
	 *            保证金
	 * @return
	 * @throws AccountException
	 * @date 2015年3月17日 下午1:26:16
	 * @author lys
	 */
	Long addAccount(Long userId, String phone, String email, Long margin)
			throws AccountException;

	/**
	 * 获取指定钱包
	 * 
	 * @Title: getAccountById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午9:51:04
	 * @author lys
	 */
	Account getAccountById(Long id) throws AccountException;

	/**
	 * 获取用户钱包
	 * 
	 * @Title: getAccountByUserId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午9:54:23
	 * @author lys
	 */
	Account getAccountByUserId(Long userId) throws AccountException;

	/**
	 * 获取指定钱包
	 * 
	 * @Title: getAccountByRemoteId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键
	 * @param remoteType
	 *            外键类型
	 * @return
	 * @throws AccountException
	 * @date 2015年3月12日 下午1:38:44
	 * @author lys
	 */
	Account getAccountByRemoteId(String remoteId, Integer remoteType)
			throws AccountException;

	/**
	 * 修改指定钱包的对应手机号
	 * 
	 * @Title: editCellById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @param cell
	 *            手机号码
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午10:00:26
	 * @author lys
	 */
	boolean editCellById(Long id, String cell) throws AccountException;

	/**
	 * 修改用户钱包的手机号
	 * 
	 * @Title: editCellByUserId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @param cell
	 *            手机号码
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午10:03:32
	 * @author lys
	 */
	boolean editCellByUserId(Long userId, String cell) throws AccountException;

	/**
	 * 修改指定钱包支付密码
	 * 
	 * <p>
	 * 
	 * 密码需要进行加密
	 * 
	 * @Title: editPaymentCodeById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @param paymentCode
	 *            支付密码
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:18:31
	 * @author lys
	 */
	boolean editPaymentCodeById(Long id, String paymentCode)
			throws AccountException;

	/**
	 * 修改用户钱包支付密码
	 * <p>
	 * 
	 * 密码需要进行加密
	 * 
	 * @Title: editPaymentCodeByUserId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @param paymentCode
	 *            支付密码
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:20:12
	 * @author lys
	 */
	boolean editPaymentCodeByUserId(Long userId, String paymentCode)
			throws AccountException;

	/**
	 * 查询指定钱包可用余额
	 * 
	 * @Title: getWithdrawAmountById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:27:14
	 * @author lys
	 */
	Long getWithdrawAmountById(Long id) throws AccountException;

	/**
	 * 查询用户钱包可用余额
	 * 
	 * @Title: getWithdrawAmountByUserId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:27:47
	 * @author lys
	 */
	Long getWithdrawAmountByUserId(Long userId) throws AccountException;

	/**
	 * 编辑用户的可用余额
	 * 
	 * @Title: sumWithdrawAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @param amount
	 *            增量金额（可为负）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:57:35
	 * @author lys
	 */
	boolean sumWithdrawAmount(String remoteId, Integer remoteType, Long amount)
			throws AccountException;

	/**
	 * 编辑指定钱包的可用余额
	 * 
	 * @Title: sumWithdrawAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @param amount
	 *            增量金额（可为负）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午1:20:18
	 * @author lys
	 */
	boolean sumWithdrawAmount(Long id, Long amount) throws AccountException;

	/**
	 * 指定店铺的余额转入指定的用户账户
	 * 
	 * @Title: extractAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @param shopNo
	 *            店铺编号
	 * @param amount
	 *            转入金额
	 * @param serialNo
	 *            流水号
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午1:35:38
	 * @author lys
	 */
	boolean extractAmount(Long userId, String shopNo, Long amount,
			String serialNo,String remark) throws AccountException;

	/**
	 * 指定钱包佣金自动转出
	 * 
	 * @Title: extractCommission
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键
	 * @param remoteType
	 *            外键类型
	 * @param amount
	 *            转出佣金金额
	 * @param serialNo
	 *            流水号
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午1:41:03
	 * @author lys
	 */
	boolean extractCommission(String remoteId, Integer remoteType, Long amount,
			String serialNo) throws AccountException;

	/**
	 * 查询还可以提现的次数
	 * 
	 * @Title: getLastWithdrawCount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午2:02:42
	 * @author lys
	 */
	Integer getLastWithdrawCount(Long userId) throws AccountException;

	/**
	 * 增加提现次数
	 * 
	 * @Title: addLastWithdrawCount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param userId
	 *            用户id
	 * @throws AccountException
	 * @date 2015年3月11日 下午2:03:22
	 * @author lys
	 */
	void addLastWithdrawCount(Long userId) throws AccountException;

	/**
	 * 提现冻结金额转出
	 * 
	 * 1.提现成功时调用
	 * 
	 * 2.将冻结的金额转走
	 * 
	 * @Title: transferredFreeze
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            钱包id
	 * @param amout
	 *            金额
	 * @param serialNo
	 *            流水号
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午2:08:49
	 * @author lys
	 */
	boolean transferredFreeze(Long id, Long amout, String serialNo)
			throws AccountException;

	/**
	 * 提现冻结金额解冻
	 * 
	 * 1.提现失败时调用
	 * 
	 * 2.将冻结的金额返回到钱包
	 * 
	 * @Title: returnFreeze
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            钱包id
	 * @param amout
	 *            金额
	 * @param serialNo
	 *            流水号
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午2:08:29
	 * @author lys
	 */
	boolean returnFreeze(Long id, Long amout, String serialNo)
			throws AccountException;

	/**
	 * 提现时，提现金额冻结
	 * 
	 * @Title: freeze
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            钱包id
	 * @param amout
	 *            金额
	 * @param serialNo
	 *            流水号
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午2:08:10
	 * @author lys
	 */
	boolean freeze(WithdrawReq withdrawReq,Long id, Long amout, String serialNo)
			throws AccountException;

	/**
	 * 查询明细个数
	 * 
	 * @Title: getCount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param fromTypeList
	 *            资金来源类型
	 * @param toTypeList
	 *            资金流转类型
	 * @param aide
	 *            辅助数据（分页，日期）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月12日 下午4:17:02
	 * @author lys
	 */
	Integer getCount(Long accountId, List<Integer> fromTypeList,
			List<Integer> toTypeList, Aide aide) throws AccountException;
	
	/**
	 * 
	 * @Title: getCountWithStatus 
	 * @Description: 通过状态查询明细个数 
	 * @param accountId
	 * @param fromTypeList
	 * @param toTypeList
	 * @param aide
	 * @param status
	 * @param type 1为收入， 2为支出， 3为提现
	 * @return
	 * @throws AccountException
	 * @date 2015年8月3日 下午3:23:48  
	 * @author cbc
	 */
	Integer getCountWithStatus(Long accountId, List<Integer> fromTypeList,
			List<Integer> toTypeList, Aide aide, List<Integer> status, Integer type) throws AccountException;

	/**
	 * 店铺资金流转记录查询
	 * 
	 * @Title: findOperationRecordPageForShop
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param fromTypeList
	 *            资金来源类型
	 * @param toTypeList
	 *            资金流转类型
	 * @param aide
	 *            辅助数据（分页，日期）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月12日 下午4:17:02
	 * @author lys
	 */
	List<AccountOperationRecord> findOperationRecordPage(Long accountId,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide)
			throws AccountException;
	
	/**
	 * 
	 * @Title: findUserOperationRecord 
	 * @Description: 收支详情 
	 * @param accountId
	 * @param fromTypeList
	 * @param toTypeList
	 * @param aide
	 * @param status
	 * @return
	 * @throws AccountException
	 * @date 2015年8月3日 下午2:46:30  
	 * @author cbc
	 */
	List<AccountOperationRecord> findUserOperationRecord(Long accountId,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide, List<Integer> status, Integer type)
					throws AccountException;

	/**
	 * 查询明细个数
	 * 
	 * @Title: getCount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键id
	 * @param remoteType
	 *            外键类型
	 * @param fromTypeList
	 *            资金来源类型
	 * @param toTypeList
	 *            资金流转类型
	 * @param aide
	 *            辅助数据（分页，日期）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月18日 下午4:22:07
	 * @author lys
	 */
	Integer getCount(String remoteId, Integer remoteType,
			List<Integer> fromTypeList, List<Integer> toTypeList, Aide aide)
			throws AccountException;
	


	/**
	 * 店铺资金流转记录查询
	 * 
	 * @Title: findOperationRecordPageForShop
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键id
	 * @param remoteType
	 *            外键类型
	 * @param fromTypeList
	 *            资金来源类型
	 * @param toTypeList
	 *            资金流转类型
	 * @param aide
	 *            辅助数据（分页，日期）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月12日 下午4:18:51
	 * @author lys
	 */
	List<AccountOperationRecord> findOperationRecordPage(String remoteId,
			Integer remoteType, List<Integer> fromTypeList,
			List<Integer> toTypeList, Aide aide) throws AccountException;

	/**
	 * 获取有效的钱包
	 * 
	 * @Title: getValidAccountById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @return
	 * @throws AccountException
	 *             如果钱包不存在，则抛出异常
	 * @date 2015年3月11日 下午7:27:07
	 * @author lys
	 */
	Account getValidAccountById(Long accountId) throws AccountException;

	/**
	 * 获取有效的用户钱包
	 * 
	 * @Title: getValidAccountByUserId
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键
	 * @param remoteType
	 *            外键类型
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午7:40:04
	 * @author lys
	 */
	Account getValidAccountByRemoteId(String remoteId, Integer remoteType)
			throws AccountException;

	/**
	 * 累计佣金
	 * 
	 * @Title: sumWithdrawCommission
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param amount
	 *            增加金额
	 * @return
	 * @throws AccountException
	 * @date 2015年3月12日 上午10:07:09
	 * @author lys
	 */
	boolean sumWithdrawCommission(Long accountId, Long amount)
			throws AccountException;

	/**
	 * 累计佣金
	 * 
	 * @Title: sumWithdrawCommission
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键
	 * @param remoteType
	 *            外键类型
	 * @param amount
	 *            增量金额（可为负）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 下午1:26:54
	 * @author lys
	 */
	boolean sumWithdrawCommission(String remoteId, Integer remoteType,
			Long amount) throws AccountException;

	/**
	 * 累计消费金额
	 * 
	 * @Title: sumConsumeAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键
	 * @param remoteType
	 *            外键类型
	 * @param amount
	 *            增量金额（可为负）
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:33:49
	 * @author lys
	 */
	boolean sumConsumeAmount(String remoteId, Integer remoteType, Long amount)
			throws AccountException;

	/**
	 * 累计消费金额
	 * 
	 * @Title: sumConsumeAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @param amount
	 *            单次累计消费金额
	 * @return
	 * @throws AccountException
	 * @date 2015年3月11日 上午11:33:49
	 * @author lys
	 */
	boolean sumConsumeAmount(Long accountId, Long amount)
			throws AccountException;

	/**
	 * 可用余额获取
	 * 
	 * @Title: getWithdrawAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountId
	 *            钱包id
	 * @return
	 * @date 2015年3月12日 上午11:09:51
	 * @author lys
	 */
	Long getWithdrawAmount(Long accountId) throws AccountException;

	/**
	 * 可用余额获取
	 * 
	 * @Title: getWithdrawAmount
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param remoteId
	 *            外键id
	 * @param remoteType
	 *            外键类型
	 * @return
	 * @date 2015年3月12日 上午11:09:55
	 * @author lys
	 */
	Long getWithdrawAmount(String remoteId, Integer remoteType) throws AccountException;
	
	/**
	 * 通过id查询操作记录s
	 * @Title: getOperationRecordById 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param id 主键
	 * @return
	 * @throws AccountException
	 * @date 2015年3月23日 下午3:09:36  
	 * @author lys
	 */
	AccountOperationRecord getOperationRecordById(Long id) throws AccountException;
	
	/**
	 * 从平台账号中进行转账
	 * @Title: transaferAccountLPAsync 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param serialNumber 流水号
	 * @param fromUserId 
	 * @param toUserId 转入者
	 * @param amount 金额
	 * @param businessType 业务类型
	 * @param businessId 
	 * @param remark 备注
	 * @return
	 * @date 2015年10月19日 下午12:03:01  
	 * @author lys
	 */
	boolean transaferAccountAsync(String serialNumber, Long fromUserId, Long toUserId, 
			Long amount, Integer businessType, String businessId, String remark);
	/**
	 *  第三方账户 转入公司账户
	 * @Description: 描述
	 * @param serialNumber
	 * @param fromUserId
	 * @param amount
	 * @param businessType
	 * @param remark
	 * @param payWay TODO
	 * @return
	 * @return boolean
	 * @date: 2015-10-28上午10:18:42
	 * @author: wangzhang
	 */
	boolean transaferThirdPayToPlatform(String serialNumber, Long fromUserId, 
			Long amount, Integer businessType,  String remark, Integer payWay);
	
	/**
	 * @Description: 转账：平台->用户
	 * @ClassName: AccountService.transaferAccountPingtaiToUser
	 * @param serialNumber
	 * @param fromUserId
	 * @param toUserId
	 * @param amount
	 * @param businessType
	 * @param businessId
	 * @param remark TODO
	 * @return
	 * @date: 2015年10月27日 上午11:46:15
	 * @author: zyl
	 */
	boolean transaferAccountPlatformToUser(String serialNumber, Long fromUserId, Long toUserId, Long amount, 
			Integer businessType, String businessId, String remark);
	
	/**
	 * 退款
	 * @Title: refund 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param id
	 * @param money
	 * @throws AccountException
	 * @date 2015年10月20日 下午2:02:23  
	 * @author cbc
	 */
	void refund(Long id, Long money, String serialNumber) throws AccountException;
	
	
	/**
	 * @Description: 用户刷脸币增加
	 * @param userId
	 * @param slcoinAmount
	 * @date: 2015年10月21日 上午9:03:07
	 * @author: zyl
	 */
	void increaseSlcoin(Long userId,Integer slcoinAmount);
	
	/**
	 * @Description: 用户刷脸币减少
	 * @param userId
	 * @param slcoinAmount
	 * @date: 2015年10月21日 上午9:03:07
	 * @author: zyl
	 */
	void decreaseSlcoin(Long userId,Integer slcoinAmount);
	
	/**
	 * @Description: 账户操作记录定时任务
	 * @ClassName: AccountService.operationRecordTask
	 * @return
	 * @date: 2015年10月24日 下午4:38:43
	 * @author: zyl
	 */
	Long operationRecordTask();
}
