package com.zjlp.face.account.dto;

import java.io.Serializable;
/**
 * 支付请求参数
 * @ClassName: WapPayReqVo 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年7月10日 下午2:43:02
 */
public class WapPayReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7038745086033542640L;
	//用户编号
	private String user_id;
	//业务类型 101001虚拟商品销售   109001实物商品销售 108001外部账户充值
	private String busi_partner;
	//商户唯一订单号(商户交易流水号)
	private String no_order;
	//商品名称
	private String name_goods;
	//订单描述
	private String info_order;
	//支付方式 2借记卡  3信用卡
	private String pay_type;
	//签约协议号
	private String no_agree;
	//银行编号
	private String bank_code;
	//证件类型 默认0身份证
	private String id_type;
	//证件号码
	private String id_no;
	//银行账号姓名
	private String acct_name;
	//是否可以修改 0可以，1不可以
	private String flag_modify;
	//银行卡号
	private String card_no;
	//支付金额
	private String money_order;
	//异步通知地址
	private String notify_url;
	//支付结果回显地址(同步地址)
	private String url_return;
	//风控参数
	private String risk_item;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getBusi_partner() {
		return busi_partner;
	}

	public void setBusi_partner(String busi_partner) {
		this.busi_partner = busi_partner;
	}

	public String getNo_order() {
		return no_order;
	}

	public void setNo_order(String no_order) {
		this.no_order = no_order;
	}

	public String getName_goods() {
		return name_goods;
	}

	public void setName_goods(String name_goods) {
		this.name_goods = name_goods;
	}

	public String getInfo_order() {
		return info_order;
	}

	public void setInfo_order(String info_order) {
		this.info_order = info_order;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getNo_agree() {
		return no_agree;
	}

	public void setNo_agree(String no_agree) {
		this.no_agree = no_agree;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getAcct_name() {
		return acct_name;
	}

	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}

	public String getFlag_modify() {
		return flag_modify;
	}

	public void setFlag_modify(String flag_modify) {
		this.flag_modify = flag_modify;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getUrl_return() {
		return url_return;
	}

	public void setUrl_return(String url_return) {
		this.url_return = url_return;
	}

	public String getMoney_order() {
		return money_order;
	}

	public void setMoney_order(String money_order) {
		this.money_order = money_order;
	}

	public String getRisk_item() {
		return risk_item;
	}

	public void setRisk_item(String risk_item) {
		this.risk_item = risk_item;
	}
	
	
}
