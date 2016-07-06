package com.zjlp.face.account.service;

import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.WithdrawRecordDto;
import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.dto.WithdrawRsp;
import com.zjlp.face.account.exception.WithdrawException;
import com.zjlp.face.util.page.Pagination;

/**
 * 提现接口
 * 
 * @ClassName: WithdrawService
 * @Description: (这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月3日 下午8:48:34
 */
public interface WithdrawService {

	/**
	 * 申请从平台提现（支持宁波银行的行内转账以及非宁波银行的跨行转账）
	 * <p>
	 * 参数说明：<br>
	 * 一、行内转账（收款账户为宁波银行）<br>
	 * 1.收款账户（非空）<br>
	 * 2.收款账户名（非空）<br>
	 * 3.转账金额（非空） 大于 0.00 小于 9999999999.99 否则提现失败<br>
	 * 4.服务类型（非空） 指定为行内转账<br>
	 * <p>
	 * 二、跨行转账<br>
	 * 1.收款账户（非空）<br>
	 * 2.收款账户名（非空）<br>
	 * 3.转账金额（非空）大于 0.00 小于 9999999999.99 否则提现失败<br>
	 * 4.服务类型（非空）指定为行内转账<br>
	 * 5.收款账号开户行名称（非空）<br>
	 * <p>
	 * 返回值说明：<br>
	 * 1.交易状态 0 提现成功 -1 提现失败 5提现处理中 2.银行流水号：交易成功 3.电子回单号：交易成功 4.交易序列号
	 * 5.错误说明：如果提现不成功时，有值
	 * 
	 * @Title: withdrawFromJz <br>
	 * @Description: (这里用一句话描述这个方法的作用) <br>
	 * @param withdrawReq
	 *            请求参数<br>
	 * @throws WithdrawException
	 *             与代理服务连接失败时，连接异常<br>
	 * @date 2014年9月4日 下午9:21:30 <br>
	 * @author LYS<br>
	 */
	WithdrawRsp withdrawFromJz(WithdrawReq withdrawReq)
			throws WithdrawException;

	/**
	 * 提现明细历史记录查询<br>
	 * <p>
	 * 参数说明：<br>
	 * 1.要查询的账号<br>
	 * 2.起始日期 （非空）yyyyMMdd<br>
	 * 3.终止日期 （非空）yyyyMMdd<br>
	 * 4.最小金额 （非空）>= 0.00 <br>
	 * 5.最大金额 （非空）>=最小金额and <= 9999999999.99<br>
	 * 6.显示页号（非空）<br>
	 * 7.每页显示条数（非空）<br>
	 * 
	 * @Title: findtransferPageList
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param withdrawReq
	 * @return
	 * @throws WithdrawException
	 *             与代理服务连接失败时，连接异常<br>
	 * @date 2014年9月17日 上午9:13:01
	 * @author LYS
	 */
	WithdrawRsp findtransferPageList(WithdrawReq withdrawReq)
			throws WithdrawException;

	/**
	 * 获取提现交易流水号
	 * 
	 * @Title: getWithdrawSN
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @return
	 * @throws WithdrawException
	 * @date 2015年1月6日 下午1:58:48
	 * @author Administrator
	 */
	String getWithdrawSN() throws WithdrawException;

	/**
	 * 转账结果查询<br>
	 * <p>
	 * 参数说明：<br>
	 * 1.要进行查询的流水号（非空）<br>
	 * 2.服务类型 指定为提现结果查询（非空）<br>
	 * 
	 * 返回值说明：<br>
	 * 1.交易状态 0 提现成功 -1 提现失败 5提现处理中 2.银行流水号：交易成功 3.电子回单号：交易成功 4.错误说明：如果提现不成功时，有值
	 * 
	 * @Title: transferResultQuery
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param withdrawReq
	 * @return
	 * @throws WithdrawException
	 * @date 2015年1月7日 上午10:47:25
	 * @author Administrator
	 */
	WithdrawRsp transferResultQuery(WithdrawReq withdrawReq)
			throws WithdrawException;

	/**
	 * 账户余额查询
	 * 
	 * @Title: balanceQuery
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param transferSerino
	 *            流水号（不可以为空）
	 * @return
	 * @throws WithdrawException
	 * @date 2015年2月3日 下午6:35:03
	 * @author lys
	 */
	String balanceQuery(String transferSerino) throws WithdrawException;

	/**
	 * 提现记录设置提现后的余额
	 * 
	 * @Title: editAmountAfterWithdraw
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param transferSerino
	 *            流水号（不可以为空）
	 * @param price
	 *            余额（不可以为空）
	 * @return
	 * @date 2015年2月3日 下午7:04:17
	 * @author lys
	 */
	boolean editAmountAfterWithdraw(String transferSerino, Long price);

	/**
	 * 添加提现记录
	 * 
	 * @Title: addWithdrawRecord
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param WithdrawRecord
	 *            添加提现记录
	 * @return
	 * @date 2015年1月6日 上午11:25:23
	 * @author Administrator
	 */
	Long addWithdrawRecord(WithdrawRecord withdrawRecord);

	/**
	 * 更新提现记录
	 * 
	 * @Title: editWithdrawRecord
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param WithdrawRecord
	 *            修改提现记录
	 * @date 2015年1月6日 上午11:26:02
	 * @author Administrator
	 */
	void editWithdrawRecord(WithdrawRecord withdrawRecord);

	/**
	 * 通过流水号获取提现记录
	 * 
	 * @Title: getWithdrawRecordByNo
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param serialNo
	 *            流水号
	 * @date 2015年1月6日 上午11:26:54
	 * @author Administrator
	 */
	WithdrawRecord getWithdrawRecordByNo(String serialNo);

	/**
	 * 通过主键获取提现记录
	 * 
	 * @Title: getWithdrawRecordById
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param id
	 *            主键
	 * @return
	 * @date 2015年1月6日 下午1:40:17
	 * @author Administrator
	 */
	WithdrawRecord getWithdrawRecordById(Long id);

	/**
	 * 提现记录获取
	 * 
	 * @Title: findRecordPage
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param pagination
	 *            分页信息
	 * @param recordDto
	 *            查询信息
	 * @return
	 * @date 2015年3月17日 下午3:49:26
	 * @author lys
	 */
	Pagination<WithdrawRecordDto> findRecordPage(
			Pagination<WithdrawRecordDto> pagination,
			WithdrawRecordDto recordDto);

	/**
	 * 根据流水号更新状态
	 * 
	 * @Title: editWithdrawRecordBySerilNo
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param serialNo
	 *            流水号
	 * @param states
	 *            修改的状态
	 * @param errorMsg
	 *            错误信息
	 * @date 2015年3月17日 下午3:40:41
	 * @author lys
	 */
	void editWithdrawRecordBySerilNo(String serialNo, Integer states,
			String errorMsg);
}
