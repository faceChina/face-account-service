package com.zjlp.face.account.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountOperationRecord implements Serializable {
	private static final long serialVersionUID = -3732357178648428058L;
	/**1银行卡支付*/
	public static final Integer TYPE_PAY_WAY_BANKCARD = 1;
	/**2钱包支付*/
	public static final Integer TYPE_PAY_WAY_WALLET = 2;
	/**3微信支付*/
	public static final Integer TYPE_PAY_WAY_WECHAT = 3;
	/**5支付宝支付*/
	public static final Integer TYPE_PAY_WAY_ZFB = 5;
	/**3余额提现*/
	public static final Integer TYPE_WITHDRAW_AMOUNT = 3;
	/**4佣金提现*/
	public static final Integer TYPE_WITHDRAW_COMMISSION = 4;
	/**5提现失败解冻*/
	public static final Integer TYPE_WITHDRAW_RETURN = 5;
	/**6提现成功冻结金额转出*/
	public static final Integer TYPE_WITHDRAW_TRANSFER = 6;
	/**11商户收益*/
	public static final Integer TYPE_SHOP_INCOME = 11;
	/**12手续费收益*/
	public static final Integer TYPE_FEE = 12;
	/**13佣金收益*/
	public static final Integer TYPE_DIVIDE = 13;
	/**14彩票中奖*/
	public static final Integer TYPE_LOTTERY_WINNING = 14;
	/**15一键余额提取*/
	public static final Integer TYPE_BALANCE_EXTRACT = 15;
	/**16卖家退款*/
	public static final Integer TYPE_REFUND = 16;
	/**17平台赠送*/
	public static final Integer TYPE_PLATFROM_SEND = 17;
	/**18分销收益*/
	public static final Integer TYPE_AGENCY = 18;
	/**19全民推广收益*/
	public static final Integer TYPE_NATIONAL_PROMOTION = 19;
	/**22红包过期退款*/
	public static final Integer TYPE_REDENVELOPE_REFUND = 22;
	/**23红包领取*/
	public static final Integer TYPE_REDENVELOPE_RECIEVE = 23;
	/**24红包发送*/
	public static final Integer TYPE_REDENVELOPE_SEND = 24;
	
	//主键
	private Long id;
	private Long userId;
	//流水号
    private String serialNumber;
    //资金流转来源账户
    private Long fromAccountId;
    //资金流转对象账户
    private Long toAccountId;
    //操作金额（单位：分）
    private Long operationAmount;
    //来源账户操作前的可用余额（单位：分)
    private Long fromAmountBefore;
    //来源账户操作前的可用佣金（单位：分）
    private Long fromCommissionBefore;
    //来源账户操作后的可用余额（单位：分)
    private Long fromAmountAfter;
    //来源账户操作后的可用佣金（单位：分）
    private Long fromCommissionAfter;
    //流入账户操作前可用余额（单位：分）
    private Long toAmountBefore;
    //流入账户操作前可用佣金（单位：分）
    private Long toCommissionBefore;
    //流入账户操作后可用余额（单位：分）
    private Long toAmountAfter;
    //流入账户操作后可用佣金（单位：分）
    private Long toCommissionAfter;
    //银行卡(表)编号
    private Long bankCardId;
    //操作的银行卡卡号
    private String bankCard;
    //银行名称
    private String bankName;
    //微信OPEN_ID
    private String openId;
    //操作类型（1银行卡支付 2钱包支付 3余额提现 4佣金提现 11商户收益 12手续费收益 13佣金收益 14彩票中奖 15一键余额提取 16卖家退款 17赠送平台）
    private Integer operationType;
    //提现状态:1 提取中  2 提取失败  3 提取成功
    private Integer withdrawalStates;
    // 支付方式：2.钱包支付 3.微信支付 5.支付宝支付
    private Integer payWayFlag;
    // 备注
    private String remark;
    /** 定时任务处理标志:0,未处理;1,已处理;-1,无需处理*/
    private Integer deal;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSerialNumber() {
        return serialNumber;
    }

    public Integer getDeal() {
		return deal;
	}

	public void setDeal(Integer deal) {
		this.deal = deal;
	}

	public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber == null ? null : serialNumber.trim();
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Long getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(Long operationAmount) {
        this.operationAmount = operationAmount;
    }

    public Long getFromAmountBefore() {
        return fromAmountBefore;
    }

    public void setFromAmountBefore(Long fromAmountBefore) {
        this.fromAmountBefore = fromAmountBefore;
    }

    public Long getFromCommissionBefore() {
        return fromCommissionBefore;
    }

    public void setFromCommissionBefore(Long fromCommissionBefore) {
        this.fromCommissionBefore = fromCommissionBefore;
    }

    public Long getFromAmountAfter() {
        return fromAmountAfter;
    }

    public void setFromAmountAfter(Long fromAmountAfter) {
        this.fromAmountAfter = fromAmountAfter;
    }

    public Long getFromCommissionAfter() {
        return fromCommissionAfter;
    }

    public void setFromCommissionAfter(Long fromCommissionAfter) {
        this.fromCommissionAfter = fromCommissionAfter;
    }

    public Long getToAmountBefore() {
        return toAmountBefore;
    }

    public void setToAmountBefore(Long toAmountBefore) {
        this.toAmountBefore = toAmountBefore;
    }

    public Long getToCommissionBefore() {
        return toCommissionBefore;
    }

    public void setToCommissionBefore(Long toCommissionBefore) {
        this.toCommissionBefore = toCommissionBefore;
    }

    public Long getToAmountAfter() {
        return toAmountAfter;
    }

    public void setToAmountAfter(Long toAmountAfter) {
        this.toAmountAfter = toAmountAfter;
    }

    public Long getToCommissionAfter() {
        return toCommissionAfter;
    }

    public void setToCommissionAfter(Long toCommissionAfter) {
        this.toCommissionAfter = toCommissionAfter;
    }

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard == null ? null : bankCard.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public Integer getWithdrawalStates() {
        return withdrawalStates;
    }

    public void setWithdrawalStates(Integer withdrawalStates) {
        this.withdrawalStates = withdrawalStates;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	

	public Integer getPayWayFlag() {
		return payWayFlag;
	}

	public void setPayWayFlag(Integer payWayFlag) {
		this.payWayFlag = payWayFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}