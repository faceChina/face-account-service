package com.zjlp.face.account.dto;

import java.io.Serializable;

public class AlipayReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 564642268758175577L;

	/**异步通知地址    必须*/
	private String notify_url;
	
	/**同步返回地址    必须*/
	private String return_url;
	
	/**商户网站唯一订单号    必须*/
	private String out_trade_no;
	
	/**商品的标题/交易标题/订单标题/订单关键字等。    必须*/
	private String subject;
	
	/**该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01 ，100000000.00]，精确到小数点后两位。    必须*/
	private String total_fee;
	
	/**描述 选传*/
	private String body;
	
	/**商品展示页面   选传*/
	private String show_url;

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getShow_url() {
		return show_url;
	}

	public void setShow_url(String show_url) {
		this.show_url = show_url;
	}
	
	
}
