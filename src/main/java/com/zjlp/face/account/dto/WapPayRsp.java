package com.zjlp.face.account.dto;

import java.io.Serializable;
/**
 * 支付响应参数
 * @ClassName: WapPayRspVo 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年7月10日 下午2:36:22
 */
public class WapPayRsp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1711176683433268535L;
	//签名方式
	private String sign_type;
	//签名
	private String sign;
	//商户编号
	private String oid_partner;
	//商户订单时间
	private String dt_order;
	//商户唯一订单号(商户订单流水号)
	private String no_order;
	//连连支付支付单号(连连订单流水号)
	private String oid_paybill;
	//支付结果
	private String result_pay;
	//交易金额
	private String money_order;
	//清算日期
	private String settle_date;
	//订单描述
	private String info_order;
	//支付方式
	private String pay_type;
	//银行编号
	private String bank_code;
	//银行名称
	private String bank_name;
	//银行卡类型 2借记卡 3信用卡
	private String card_type;
	//返回编码
	private String ret_code;
	//返回说明
	private String ret_msg;
	/**授权码*/
	private String token;
	/**协议号*/
	private String no_agree;
	/** 交易人*/
	private String acct_name;
	/** 身份证 */ 
	private String id_no;
	/** 证件类型 */ 
	private String id_type;
	
	public String getAcct_name() {
		return acct_name;
	}
	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}
	public String getId_no() {
		return id_no;
	}
	public void setId_no(String id_no) {
		this.id_no = id_no;
	}
	public String getId_type() {
		return id_type;
	}
	public void setId_type(String id_type) {
		this.id_type = id_type;
	}
	public String getNo_agree() {
		return no_agree;
	}
	public void setNo_agree(String no_agree) {
		this.no_agree = no_agree;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getOid_partner() {
		return oid_partner;
	}
	public void setOid_partner(String oid_partner) {
		this.oid_partner = oid_partner;
	}
	public String getDt_order() {
		return dt_order;
	}
	public void setDt_order(String dt_order) {
		this.dt_order = dt_order;
	}
	public String getNo_order() {
		return no_order;
	}
	public void setNo_order(String no_order) {
		this.no_order = no_order;
	}
	public String getOid_paybill() {
		return oid_paybill;
	}
	public void setOid_paybill(String oid_paybill) {
		this.oid_paybill = oid_paybill;
	}
	public String getResult_pay() {
		return result_pay;
	}
	public void setResult_pay(String result_pay) {
		this.result_pay = result_pay;
	}
	public String getMoney_order() {
		return money_order;
	}
	public void setMoney_order(String money_order) {
		this.money_order = money_order;
	}
	public String getSettle_date() {
		return settle_date;
	}
	public void setSettle_date(String settle_date) {
		this.settle_date = settle_date;
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
	public String getBank_code() {
		return bank_code;
	}
	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getRet_code() {
		return ret_code;
	}
	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}
	public String getRet_msg() {
		return ret_msg;
	}
	public void setRet_msg(String ret_msg) {
		this.ret_msg = ret_msg;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
