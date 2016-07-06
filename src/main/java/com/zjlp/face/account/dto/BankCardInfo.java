package com.zjlp.face.account.dto;

import java.io.Serializable;

public class BankCardInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2953593586023665603L;
	/**用户编号*/
	private String user_id;
	/**银行卡号*/
	private String card_no;
	/**银行账户姓名*/
	private String acct_name;
	/**银行预留手机号*/
	private String bind_mob;
	/**信用卡有效期*/
	private String vali_date;
	/**信用卡VCC2*/
	private String cvv2;
	/**证件类型 默认0身份证*/
	private String id_type;
	/**证件号码*/
	private String id_no;
	/**短信验证码*/
	private String verify_code;
	/**协议号*/
	private String no_agree;
	/**2.借记卡 3.信用卡*/
	private String bankcard_type;
	/**授权码*/
	private String token;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getBankcard_type() {
		return bankcard_type;
	}
	public void setBankcard_type(String bankcard_type) {
		this.bankcard_type = bankcard_type;
	}
	public String getNo_agree() {
		return no_agree;
	}
	public void setNo_agree(String no_agree) {
		this.no_agree = no_agree;
	}
	public String getCard_no() {
		return card_no;
	}
	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}
	public String getAcct_name() {
		return acct_name;
	}
	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}
	public String getBind_mob() {
		return bind_mob;
	}
	public void setBind_mob(String bind_mob) {
		this.bind_mob = bind_mob;
	}
	public String getVali_date() {
		return vali_date;
	}
	public void setVali_date(String vali_date) {
		this.vali_date = vali_date;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
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
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getVerify_code() {
		return verify_code;
	}
	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}
}
