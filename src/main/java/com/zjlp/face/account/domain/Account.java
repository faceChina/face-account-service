package com.zjlp.face.account.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 钱包
 * 
 * @ClassName: Account
 * @Description: (这里用一句话描述这个类的作用)
 * @author lys
 * @date 2015年3月11日 上午9:31:45
 */
public class Account implements Serializable {

	private static final long serialVersionUID = -5996825946840351345L;
	/** 外部ID类型:用户 */
	public static final Integer REMOTE_TYPE_USER = 1;
	/** 外部ID类型:店铺 */
	public static final Integer REMOTE_TYPE_SHOP = 2;
	/** 普通用户钱包 */
	public static final Integer TYPE_USER = 1;
	/** 产品钱包 */
	public static final Integer TYPE_SHOP = 2;
	/** 平台钱包 */
	public static final Integer TYPE_PLATFORM = 3;
	/** 连连钱包 */
	public static final Integer TYPE_LIANLIAN = 4;
	// 主键
	private Long id;
	// 外部ID 标识所属的唯一ID
	private String remoteId;
	// 外部ID类型：1.用户 2店铺
	private Integer remoteType;
	// 1普通用户钱包 3平台钱包 4连连钱包 5微信钱包 6.捷蓝钱包 7.支付宝钱包
	private Integer type;
	// 可提现金额（单位：分）
	private Long withdrawAmount;
	// 可提取佣金（单位：分）
	private Long withdrawCommission;
	// 总消费金额（单位：分）
	private Long consumeAmount;
	// 提现中不可使用的金额（单位：分）
	private Long freezeAmount;
	// 保证金（单位：分）
	private Long margin;
	// 支付密码
	private String paymentCode;
	// 最后一次提现的日期
	private String withdrawDate;
	// 最后一天提现的次数
	private Integer withdrawCount;
	// 刷脸币数量
	private Integer slcoinAmount;
	// 内部系统编码
	private String systemCode;
	// 店铺邀请码
	private String invitationCode;
	// 收款人全名
	private String name;
	// 联系电话
	private String telephone;
	// 移动电话
	private String cell;
	// 电子邮箱
	private String email;
	// 赠送金额
	private Long tosendAmount;
	// 更新时间
	private Date updateTime;
	// 创建时间
	private Date createTime;

	public Account() {
	}

	public Account(String remoteId, Integer remoteType) {
		this.remoteId = remoteId;
		this.remoteType = remoteType;
	}

	public Account(Long id, Long withdrawAmount, Long withdrawCommission,
			Long consumeAmount, Date updateTime) {
		this.id = id;
		this.withdrawAmount = withdrawAmount;
		this.withdrawCommission = withdrawCommission;
		this.consumeAmount = consumeAmount;
		this.updateTime = updateTime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public Integer getRemoteType() {
		return remoteType;
	}

	public void setRemoteType(Integer remoteType) {
		this.remoteType = remoteType;
	}

	public Integer getSlcoinAmount() {
		return slcoinAmount;
	}

	public void setSlcoinAmount(Integer slcoinAmount) {
		this.slcoinAmount = slcoinAmount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getWithdrawAmount() {
		return withdrawAmount;
	}

	public void setWithdrawAmount(Long withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	public Long getWithdrawCommission() {
		return withdrawCommission;
	}

	public void setWithdrawCommission(Long withdrawCommission) {
		this.withdrawCommission = withdrawCommission;
	}

	public Long getConsumeAmount() {
		return consumeAmount;
	}

	public void setConsumeAmount(Long consumeAmount) {
		this.consumeAmount = consumeAmount;
	}

	public Long getFreezeAmount() {
		return freezeAmount;
	}

	public void setFreezeAmount(Long freezeAmount) {
		this.freezeAmount = freezeAmount;
	}

	public Long getMargin() {
		return margin;
	}

	public void setMargin(Long margin) {
		this.margin = margin;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode == null ? null : paymentCode.trim();
	}

	public String getWithdrawDate() {
		return withdrawDate;
	}

	public void setWithdrawDate(String withdrawDate) {
		this.withdrawDate = withdrawDate == null ? null : withdrawDate.trim();
	}

	public Integer getWithdrawCount() {
		return withdrawCount;
	}

	public void setWithdrawCount(Integer withdrawCount) {
		this.withdrawCount = withdrawCount;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode == null ? null : systemCode.trim();
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode == null ? null : invitationCode
				.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone == null ? null : telephone.trim();
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell == null ? null : cell.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public Long getTosendAmount() {
		return tosendAmount;
	}

	public void setTosendAmount(Long tosendAmount) {
		this.tosendAmount = tosendAmount;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return new StringBuilder("Account [id=").append(id)
				.append(", remoteId=").append(remoteId).append(", remoteType=")
				.append(remoteType).append(", type=").append(type)
				.append(", withdrawAmount=").append(withdrawAmount)
				.append(", withdrawCommission=").append(withdrawCommission)
				.append(", consumeAmount=").append(consumeAmount)
				.append(", freezeAmount=").append(freezeAmount)
				.append(", margin=").append(margin).append(", paymentCode=")
				.append(paymentCode).append(", withdrawDate=")
				.append(withdrawDate).append(", withdrawCount=")
				.append(withdrawCount).append(", systemCode=")
				.append(systemCode).append(", invitationCode=")
				.append(invitationCode).append(", name=").append(name)
				.append(", telephone=").append(telephone).append(", cell=")
				.append(cell).append(", email=").append(email)
				.append(", tosendAmount=").append(tosendAmount)
				.append(", updateTime=").append(updateTime)
				.append(", createTime=").append(createTime).append("]")
				.toString();
	}

}