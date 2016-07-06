package com.zjlp.face.account.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.zjlp.face.account.cache.LoginSessionFactory;
import com.zjlp.face.account.dao.WithdrawRecordDao;
import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.account.dto.NBBankReturnData;
import com.zjlp.face.account.dto.WithdrawRecordDto;
import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.dto.WithdrawRsp;
import com.zjlp.face.account.dto.WithdrawRsp.QueryInfo;
import com.zjlp.face.account.exception.WithdrawException;
import com.zjlp.face.account.service.WithdrawService;
import com.zjlp.face.account.service.processor.NingBoBankProcessor;
import com.zjlp.face.account.util.ConstantsUtil;
import com.zjlp.face.account.util.GenerateCode;
import com.zjlp.face.util.date.DateUtil;
import com.zjlp.face.util.file.xml.XmlHelper;
import com.zjlp.face.util.page.Pagination;

@Service("withdrawService")
public class WithdrawServiceImpl implements WithdrawService {

	private Logger _info = Logger.getLogger("withdrawInfoLog");
	private Logger _error = Logger.getLogger("withdrawErrorLog");
	@Autowired
	private NingBoBankProcessor ningBoBankProcessor;
	@Autowired
	private LoginSessionFactory loginSessionFactory;
	@Autowired
	private WithdrawRecordDao withdrawRecordDao;

	@Override
	public WithdrawRsp withdrawFromJz(WithdrawReq withdrawReq) throws WithdrawException {
		
		Assert.notNull(withdrawReq, "提现请求数据不能为空");
		Assert.notNull(withdrawReq.getServiceType(), "服务类型不能为空");
		Assert.notNull(withdrawReq.getTransferSerino(), "流水号不能为空");
		try {
			
			this.filter(withdrawReq);
			
			//登陆
			String sessionId = loginSessionFactory.getSessionId();
			WithdrawRsp withdrawRsp = new WithdrawRsp();
			//付款账户
			withdrawReq.setPayAccountNo(NingBoBankProcessor.getProties("jz_account_number"));
			withdrawReq.setCurrencyType("01");   //01:人民币
			String withdrawRst = null;
			if (ConstantsUtil.SERVICE_PEER_WD.equals(withdrawReq.getServiceType())) {
				withdrawRst = ningBoBankProcessor.singleInnerTransfer(sessionId, withdrawReq);
			} else if (ConstantsUtil.SERVICE_OUTER_WD.equals(withdrawReq.getServiceType())) {
				withdrawRst = ningBoBankProcessor.singleOuterTransfer(sessionId, withdrawReq);
			} else {
				throw new WithdrawException("请求提现类型错误："+withdrawReq.getServiceType());
			}
			Assert.hasLength(withdrawRst, "提现返回数据为空，提现失败");
			NBBankReturnData returnData = XmlHelper.parseXml(withdrawRst, NBBankReturnData.class);
			//交易失败
			if (!ConstantsUtil.RET_CODE_0000.equals(returnData.getRetCode())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
				withdrawRsp.setErrorMsg(returnData.getErrorMsg());
				_info.info("【提现日志】提现失败，失败原因："+returnData.getErrorMsg());
			} 
			else if (ConstantsUtil.NBJY_STATE_91.equals(returnData.getJYZT())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
				withdrawRsp.setErrorMsg(returnData.getSBYY());
				_info.info("【提现日志】交易状态：91，提现失败，银行流水："+returnData.getJYXH());
			} else if (ConstantsUtil.NBJY_STATE_99.equals(returnData.getJYZT())
					|| ConstantsUtil.NBJY_STATE_94.equals(returnData.getJYZT())) {
				//交易处理中
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_DEAL);
				withdrawRsp.setSerialNo(withdrawReq.getTransferSerino());				
			} else if (ConstantsUtil.NBJY_STATE_90.equals(returnData.getJYZT())) {
				//交易成功
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_SUCC);
				withdrawRsp.setSerialNo(withdrawReq.getTransferSerino());
				withdrawRsp.setBankSeriNumber(returnData.getJYXH());
				withdrawRsp.setBalance(returnData.getFKYE());
				withdrawRsp.setElectronicReceiptNumber(returnData.getDZHD());
			} else if (ConstantsUtil.NO_RECORD.equals(returnData.getRetCode())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
				withdrawRsp.setErrorMsg("发送提现请求失败");
				_info.info("【提现日志】提现失败，失败原因：发送提现请求失败。");
			} else {
				throw new WithdrawException("返回结果状态错误="+returnData.getJYZT());
			}
			//提现之前的余额查询
			return withdrawRsp;
		} catch (Exception e) {
			_error.error("【提现日志】提现失败。", e);
			throw new WithdrawException(e);
		} 
	}
	
	/**
	 * 过滤
	 * @Title: filter 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param withdrawReq
	 * @date 2015年11月4日 下午4:19:53  
	 * @author lys
	 */
	private synchronized void filter(WithdrawReq withdrawReq){
		//过滤重复
		WithdrawRecord withdrawRecord = withdrawRecordDao.getRecordBySeriNum(withdrawReq.getTransferSerino());
		Assert.isNull(withdrawRecord, "流水号="+withdrawReq.getTransferSerino()+"提现任务重复提交");
		//提现记录生成
		this.addWithdrawRecord(withdrawReq);
	}
	
	@Override
	public WithdrawRsp findtransferPageList(WithdrawReq withdrawReq) throws WithdrawException {
		try {
			 String sessionId = loginSessionFactory.getSessionId();
			 String queryInfo = ningBoBankProcessor.accountTradeInfoQuery(sessionId, withdrawReq);
			 List<QueryInfo> queryInfoList = XmlHelper.parseListXml(queryInfo, WithdrawRsp.QueryInfo.class);
			 WithdrawRsp withdrawRsp = new WithdrawRsp();
			 withdrawRsp.setQueryInfoList(queryInfoList);
			return withdrawRsp;
		} catch (Exception e) {
			throw new WithdrawException(e);
		}
	}

	@Override
	public String getWithdrawSN() throws WithdrawException {
		try {
			String serialNo = GenerateCode.getWithdrawSN();
			if (StringUtils.isBlank(serialNo)) {
				throw new WithdrawException("获取提现交易流水号失败");
			}
			return serialNo;
		} catch (Exception e) {
			throw new WithdrawException(e);
		}
	}

	@Override
	public WithdrawRsp transferResultQuery(WithdrawReq withdrawReq) throws WithdrawException {
		Assert.notNull(withdrawReq, "查询提现结果请求数据不能为空");
		Assert.notNull(withdrawReq.getTransferSerino(), "对应查询交易的流水号不能为空");
		Assert.notNull(withdrawReq.getTransferAmount(), "对应查询交易的金额不能为空");
		try {
			if (!ConstantsUtil.SERVICE_QUERY_WD.equals(withdrawReq.getServiceType())) {
				throw new WithdrawException("非查询提现结果请求："+withdrawReq.getServiceType());
			}
			_info.info("【提现日志】提现查询开始执行：流水号："+withdrawReq.getTransferSerino()+"， 交易金额："+withdrawReq.getTransferAmount()+"， 服务编号："+withdrawReq.getServiceType());
			//登陆
			String sessionId = loginSessionFactory.getSessionId();
			_info.info("【提现日志】登陆成功，session id:"+sessionId);
			//查询
			String queryRst = ningBoBankProcessor.transferResultInfoQuery(sessionId, withdrawReq);
			if (StringUtils.isBlank(queryRst)) {
				throw new WithdrawException("提现返回信息为空");
			}
			System.out.println(queryRst);
			_info.info("queryRst: " + queryRst);
			NBBankReturnData returnData = XmlHelper.parseXml(queryRst, NBBankReturnData.class);
			//返回结果处理
			WithdrawRsp withdrawRsp = new WithdrawRsp();
			//交易失败
			if("-1".equals(returnData.getSessionId())) {  //连接失败
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_DEAL);
			} else if (!ConstantsUtil.RET_CODE_0000.equals(returnData.getRetCode())) {
				if (ConstantsUtil.NO_RECORD.equals(returnData.getRetCode())) {
					withdrawRsp.setStates(ConstantsUtil.WD_STATE_NORECORD);
					withdrawRsp.setErrorMsg("没有查询到该记录，需要人工处理");
					_info.info("【提现日志】提现查询没有该记录，需要人工处理！");
				} else {
					withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
					withdrawRsp.setErrorMsg(returnData.getErrorMsg());
					_info.info("【提现日志】提现失败，失败原因："+returnData.getErrorMsg());
				}
			} else if (ConstantsUtil.NBJY_STATE_91.equals(returnData.getJYZT())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
				withdrawRsp.setErrorMsg(returnData.getSBYY());
				_info.info("【提现日志】交易状态：91，提现失败，银行流水："+returnData.getJYXH());
			} else if (ConstantsUtil.NBJY_STATE_99.equals(returnData.getJYZT())
					|| ConstantsUtil.NBJY_STATE_94.equals(returnData.getJYZT())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_DEAL);
			} else if (ConstantsUtil.NBJY_STATE_93.equals(returnData.getJYZT())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_FAIL);
				withdrawRsp.setErrorMsg(returnData.getErrorMsg());
				_info.info("【提现日志】提现失败，失败原因："+returnData.getErrorMsg());
			} else if (ConstantsUtil.NBJY_STATE_90.equals(returnData.getJYZT())) {
				withdrawRsp.setStates(ConstantsUtil.WD_STATE_SUCC);
				withdrawRsp.setSerialNo(returnData.getSerialNo());
				withdrawRsp.setElectronicReceiptNumber(returnData.getDZHD());
			} else {
				throw new WithdrawException("返回结果状态错误="+returnData.getJYZT());
			}
			return withdrawRsp;
		} catch (Exception e) {
			_error.error("【提现日志】查询提现结果失败。", e);
			throw new WithdrawException("查询提现结果失败。", e);
		}
	}
	
	private void addWithdrawRecord(WithdrawReq withdrawReq) throws WithdrawException {
		try {
			//提现结果处理
			Date date = new Date();
			WithdrawRecord withdrawRecord = new WithdrawRecord();
			withdrawRecord.setRemoteId(String.valueOf(withdrawReq.getUserId()));
			withdrawRecord.setRemoteType(WithdrawRecord.REMOTE_TYPE_USER);
			withdrawRecord.setProvince(withdrawReq.getProvince());
			withdrawRecord.setCity(withdrawReq.getCity());
			withdrawRecord.setBankCode(withdrawReq.getBankCode());
			withdrawRecord.setWithdrawPrice(withdrawReq.getTransferAmount());  //单位：分
			withdrawRecord.setCurrencyType(withdrawReq.getCurrencyType());
			withdrawRecord.setPayBankCard(withdrawReq.getPayAccountNo());
			withdrawRecord.setReciveBankCard(withdrawReq.getReciveAccountNo());
			withdrawRecord.setUserName(withdrawReq.getUserName());
			withdrawRecord.setReciveBankName(withdrawReq.getBankName());
			withdrawRecord.setServiceType(withdrawReq.getServiceType());
			withdrawRecord.setWithdrawType("02");
			withdrawRecord.setEmergencyDegree(1);  //普通
			withdrawRecord.setPurpose("提现");
			withdrawRecord.setCreateTime(date);
			withdrawRecord.setUpdateTime(date);
			withdrawRecord.setSettleDate(DateUtil.DateToString(date, "yyyyMMdd"));
			//流水号  银行流水  电子回单号  提现状态  所在省  所在市  币种  提现类型
			withdrawRecord.setSeriNumber(withdrawReq.getTransferSerino());
			//提现成功
			withdrawRecord.setStatus(ConstantsUtil.WD_STATE_DEAL);  //生成
			withdrawRecordDao.addWithdrawRecord(withdrawRecord);
		} catch (Exception e) {
			throw new WithdrawException(e);
		}
	}

	@Override
	public String balanceQuery(String transferSerino) throws WithdrawException {
		try {
			//登陆
			String sessionId = loginSessionFactory.getSessionId();
			//余额查询
			String balanceXml = ningBoBankProcessor.singleAccountInfoQuery(sessionId, transferSerino);
			NBBankReturnData data = XmlHelper.parseXml(balanceXml, NBBankReturnData.class);
			return data.getKYER();
		} catch (Exception e) {
			throw new WithdrawException(e);
		}
	}

	@Override
	public boolean editAmountAfterWithdraw(String transferSerino, Long price) {
		Assert.notNull(transferSerino, "Param [transferSerino] can not be null.");
		Assert.notNull(price, "Param [price] can not be null.");
		try {
			withdrawRecordDao.editRecordBalanceAf(transferSerino, price);
			return true;
		} catch (Exception e) {
			throw new WithdrawException(e);
		}
	}
	
	@Override
	public Long addWithdrawRecord(WithdrawRecord withdrawRecord) {
		withdrawRecordDao.addWithdrawRecord(withdrawRecord);
		Long id = withdrawRecord.getId();
		return id;
	}

	@Override
	public void editWithdrawRecord(WithdrawRecord withdrawRecord) {
		withdrawRecordDao.editRecordStates(withdrawRecord);
	}

	@Override
	public WithdrawRecord getWithdrawRecordByNo(String serialNo) {
		WithdrawRecord record = withdrawRecordDao.getRecordBySeriNum(serialNo);
		return record;
	}

	@Override
	public WithdrawRecord getWithdrawRecordById(Long id) {
		WithdrawRecord record = withdrawRecordDao.getWithdrawRecordById(id);
		return record;
	}

	@Override
	public Pagination<WithdrawRecordDto> findRecordPage(
			Pagination<WithdrawRecordDto> pagination,
			WithdrawRecordDto recordDto) {
		
		Assert.notNull(pagination, "Param [pagination] can not be null.");
		Assert.notNull(recordDto, "Param [recordDto] can not be null.");
		recordDto.getAide().calculationTime();
		Integer totalRow = withdrawRecordDao.getCount(recordDto);
		recordDto.getAide().setStartNum(pagination.getStart());
		recordDto.getAide().setPageSizeNum(pagination.getPageSize());
		List<WithdrawRecordDto> datas = withdrawRecordDao.findPageList(recordDto);
		pagination.setTotalRow(totalRow);
		pagination.setDatas(datas);
		return pagination;
	}

	@Override
	public void editWithdrawRecordBySerilNo(String seriNumber, Integer states, String errorMsg) {
		Date date = new Date();
		WithdrawRecord record = new WithdrawRecord();
		record.setSeriNumber(seriNumber);
		record.setStatus(states);
		if (!StringUtils.isBlank(errorMsg)) {
			record.setWithdrawInfo(errorMsg.substring(0, errorMsg.length() > 64 ? 64 : errorMsg.length()));
		}
		record.setSettleDate(DateUtil.DateToString(date, "yyyyMMdd"));
		record.setUpdateTime(date);
		withdrawRecordDao.editStatesBySerialNo(record);
	}
	
}
