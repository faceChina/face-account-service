package com.zjlp.face.account.dto;

import com.zjlp.face.account.domain.WithdrawRecord;
import com.zjlp.face.util.page.Aide;

/**
 * 提现记录表
 * 
 * @ClassName: WithdrawRecordDto
 * @Description: (这里用一句话描述这个类的作用)
 * @author lys
 * @date 2015年1月21日 上午10:00:16
 */
public class WithdrawRecordDto extends WithdrawRecord {

	private static final long serialVersionUID = 1L;

	private static final Integer[] STATES = { 0, -1, 5 }; // -1 失败 0 成功 5 处理中

	private Aide aide = new Aide();
	
	public Aide getAide() {
		return aide == null ? new Aide() : aide;
	}
	public void setAide(Aide aide) {
		this.aide = aide;
	}
	/**
	 * 提现状态
	 * 
	 * @Title: getWithdrawStates
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @return
	 * @date 2015年1月14日 上午10:59:15
	 * @author lys
	 */
	public String getWithdrawalStates() {
		Integer states = super.getStatus();
		if (null == states) {
			return null;
		} else if (STATES[0].equals(states)) {
			return "提现成功";
		} else if (STATES[1].equals(states)) {
			return "提现失败";
		} else if (STATES[2].equals(states)) {
			return "提现中";
		}
		return null;
	}
	
	/**
	 * 银行卡号可视化
	 * 
	 * @Title: getCardForView 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @return
	 * @date 2015年1月21日 上午10:28:57  
	 * @author lys
	 */
	public String getCardForView() {
		
		String bankCard = super.getReciveBankCard();
		if (null == bankCard || "".equals(bankCard)) {
			return null;
		}
		String bankName = null == super.getReciveBankName() 
				? "" : super.getReciveBankName();
		StringBuilder sb = new StringBuilder(bankName);
		int len = bankCard.length();
		sb.append("*********").append(bankCard.substring(len - 3, len));
		return sb.toString();
	}

}
