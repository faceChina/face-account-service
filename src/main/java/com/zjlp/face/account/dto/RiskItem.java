package com.zjlp.face.account.dto;

import java.io.Serializable;
/**
 * 风控参数
 * @ClassName: RiskItemVo 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年7月11日 下午5:43:21
 */
public class RiskItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6283506667020053079L;
	
	//用户唯一标示 userId
	private String user_info_mercht_userno;
	//绑定手机
	private String user_info_bind_phone;
	//客户姓名
	private String user_info_full_name;
	//客户证件类型
	private String user_info_id_type;
	//客户证件号码
	private String user_info_id_no;
	
	public String getUser_info_mercht_userno() {
		return user_info_mercht_userno;
	}
	public void setUser_info_mercht_userno(String user_info_mercht_userno) {
		this.user_info_mercht_userno = user_info_mercht_userno;
	}
	public String getUser_info_bind_phone() {
		return user_info_bind_phone;
	}
	public void setUser_info_bind_phone(String user_info_bind_phone) {
		this.user_info_bind_phone = user_info_bind_phone;
	}
	public String getUser_info_full_name() {
		return user_info_full_name;
	}
	public void setUser_info_full_name(String user_info_full_name) {
		this.user_info_full_name = user_info_full_name;
	}
	public String getUser_info_id_type() {
		return user_info_id_type;
	}
	public void setUser_info_id_type(String user_info_id_type) {
		this.user_info_id_type = user_info_id_type;
	}
	public String getUser_info_id_no() {
		return user_info_id_no;
	}
	public void setUser_info_id_no(String user_info_id_no) {
		this.user_info_id_no = user_info_id_no;
	}
}
