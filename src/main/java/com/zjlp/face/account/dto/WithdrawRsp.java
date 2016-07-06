package com.zjlp.face.account.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 提现响应数据
 * 
 * @ClassName: WithdrawRsp
 * @Description: (这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午9:28:17
 */
public class WithdrawRsp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8100268210342918869L;

	// 交易序列号
	private String serialNo;
	// 电子回单号
	private String electronicReceiptNumber;
	// 银行流水号
	private String bankSeriNumber;
	// 交易状态 0 提现成功 -1 提现失败 5提现处理中
	private Integer states;
	// 错误说明
	private String errorMsg;
	// 转账之前的余额
	private String preBalance;
	// 账户余额
	private String balance;
	// 查询数据列表
	private List<QueryInfo> queryInfoList;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("交易序列号").append(serialNo).append(", 电子回单号")
				.append(electronicReceiptNumber).append(", 银行流水号")
				.append(bankSeriNumber)
				.append(", 交易状态  [0 提现成功  -1 提现失败  5提现处理中] ").append(states)
				.append(", 错误说明").append(errorMsg).append(", 转账之前的余额")
				.append(preBalance).append(", 账户余额").append(balance);
		return sb.toString();
	}

	public String getPreBalance() {
		return preBalance;
	}

	public void setPreBalance(String preBalance) {
		this.preBalance = preBalance;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getElectronicReceiptNumber() {
		return electronicReceiptNumber;
	}

	public void setElectronicReceiptNumber(String electronicReceiptNumber) {
		this.electronicReceiptNumber = electronicReceiptNumber;
	}

	public String getBankSeriNumber() {
		return bankSeriNumber;
	}

	public void setBankSeriNumber(String bankSeriNumber) {
		this.bankSeriNumber = bankSeriNumber;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Integer getStates() {
		return states;
	}

	public void setStates(Integer states) {
		this.states = states;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public List<QueryInfo> getQueryInfoList() {
		return queryInfoList;
	}

	public void setQueryInfoList(List<QueryInfo> queryInfoList) {
		this.queryInfoList = queryInfoList;
	}

	public class QueryInfo {
		// 借贷标志
		private String JDBZ;
		// 摘要标志
		private String PATI;
		// 交易金额
		private String JYJE;
		// 历史余额
		private String YUER;
		// 交易日期
		private String JYRQ;
		// 对方账号
		private String DFZH;
		// 对方户名
		private String DFHM;
		// 凭证编号
		private String PZBH;
		// 备注
		private String BEZH;
		// 电子回单号
		private String DZHD;

		public String getJDBZ() {
			return JDBZ;
		}

		public void setJDBZ(String jDBZ) {
			JDBZ = jDBZ;
		}

		public String getPATI() {
			return PATI;
		}

		public void setPATI(String pATI) {
			PATI = pATI;
		}

		public String getJYJE() {
			return JYJE;
		}

		public void setJYJE(String jYJE) {
			JYJE = jYJE;
		}

		public String getYUER() {
			return YUER;
		}

		public void setYUER(String yUER) {
			YUER = yUER;
		}

		public String getJYRQ() {
			return JYRQ;
		}

		public void setJYRQ(String jYRQ) {
			JYRQ = jYRQ;
		}

		public String getDFZH() {
			return DFZH;
		}

		public void setDFZH(String dFZH) {
			DFZH = dFZH;
		}

		public String getDFHM() {
			return DFHM;
		}

		public void setDFHM(String dFHM) {
			DFHM = dFHM;
		}

		public String getPZBH() {
			return PZBH;
		}

		public void setPZBH(String pZBH) {
			PZBH = pZBH;
		}

		public String getBEZH() {
			return BEZH;
		}

		public void setBEZH(String bEZH) {
			BEZH = bEZH;
		}

		public String getDZHD() {
			return DZHD;
		}

		public void setDZHD(String dZHD) {
			DZHD = dZHD;
		}
	}

}
