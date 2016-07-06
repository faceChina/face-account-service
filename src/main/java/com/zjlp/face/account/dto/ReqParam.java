package com.zjlp.face.account.dto;

import java.io.Serializable;

/**
 * 宁波银行业务申请参数列表
 * @ClassName: ReqParam 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author lys
 * @date 2014年8月30日 下午3:13:34
 */
public class ReqParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8203790751245189573L;
	//---------------登陆--------------------
	//登录ID
	private String userID;
	//银企直联密码
	private String userPWD;
	
	//---------------转账--------------------
	//付款账号
	private String FKZH;
	//收款账号
	private String SKZH;
	//收款方户名
	private String SKHM;
	//收款账号开户行名称
	private String SKYH;
	//收款方行所在省
	private String SKSH;
	//收款方行所在市
	private String SKSI;
	//交易金额
	private String JYJE;
	//币种
	private String BIZH;
	//转账类型
	private String ZZLX;
	//转账类别
	private String ZZLB;
	//用途
	private String YOTU;
	//收款行号
	private String SKHH;
	//集团标志
	private String JTBZ;
	
	//------------------------转账结果查询-------------------------
	//原交易序列号
	private String JYXH;
	
	//--------------------账户余额及基本信息查询-------------------------
	private String ZHHA;
	
	//----------------------------------------
	//起始日期
	private String QSRQ;
	
	// <ZZRQ>终止日期</ZZRQ>
	private String ZZRQ;
	
	// <ZXJE>最小金额</ZXJE>
	private String ZXJE;
	
	// <ZDJE>最大金额</ZDJE>
	private String ZDJE;
	
	// <XSYH>显示页号</XSYH>
	private String XSYH;
	
	// <XSTS>每页显示条数</XSTS>
	private String XSTS;

	
	//------------------------登陆---------------------------
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserPWD() {
		return userPWD;
	}
	public void setUserPWD(String userPWD) {
		this.userPWD = userPWD;
	}
	//------------------------转账---------------------------
	public final String getFKZH() {
		return FKZH;
	}
	public final void setFKZH(String fKZH) {
		FKZH = fKZH;
	}
	public final String getSKZH() {
		return SKZH;
	}
	public final void setSKZH(String sKZH) {
		SKZH = sKZH;
	}
	public final String getSKHM() {
		return SKHM;
	}
	public final void setSKHM(String sKHM) {
		SKHM = sKHM;
	}
	public final String getSKYH() {
		return SKYH;
	}
	public final void setSKYH(String sKYH) {
		SKYH = sKYH;
	}
	public final String getSKSH() {
		return SKSH;
	}
	public final void setSKSH(String sKSH) {
		SKSH = sKSH;
	}
	public final String getSKSI() {
		return SKSI;
	}
	public final void setSKSI(String sKSI) {
		SKSI = sKSI;
	}
	public final String getJYJE() {
		return JYJE;
	}
	public final void setJYJE(String jYJE) {
		JYJE = jYJE;
	}
	public final String getBIZH() {
		return BIZH;
	}
	public final void setBIZH(String bIZH) {
		BIZH = bIZH;
	}
	public final String getZZLX() {
		return ZZLX;
	}
	public final void setZZLX(String zZLX) {
		ZZLX = zZLX;
	}
	public final String getZZLB() {
		return ZZLB;
	}
	public final void setZZLB(String zZLB) {
		ZZLB = zZLB;
	}
	public final String getYOTU() {
		return YOTU;
	}
	public final void setYOTU(String yOTU) {
		YOTU = yOTU;
	}
	public final String getSKHH() {
		return SKHH;
	}
	public final void setSKHH(String sKHH) {
		SKHH = sKHH;
	}
	public String getJTBZ() {
		return JTBZ;
	}
	public void setJTBZ(String jTBZ) {
		JTBZ = jTBZ;
	}
	//----------------------转账结果查询------------------------
	public String getJYXH() {
		return JYXH;
	}
	public void setJYXH(String jYXH) {
		JYXH = jYXH;
	}
	
	//---------------------账户余额及基本信息查询--------------------------
	public String getZHHA() {
		return ZHHA;
	}
	public void setZHHA(String zHHA) {
		ZHHA = zHHA;
	}
	
	//----------------------------
	public String getQSRQ() {
		return QSRQ;
	}
	public void setQSRQ(String qSRQ) {
		QSRQ = qSRQ;
	}
	public String getZZRQ() {
		return ZZRQ;
	}
	public void setZZRQ(String zZRQ) {
		ZZRQ = zZRQ;
	}
	public String getZXJE() {
		return ZXJE;
	}
	public void setZXJE(String zXJE) {
		ZXJE = zXJE;
	}
	public String getZDJE() {
		return ZDJE;
	}
	public void setZDJE(String zDJE) {
		ZDJE = zDJE;
	}
	public String getXSYH() {
		return XSYH;
	}
	public void setXSYH(String xSYH) {
		XSYH = xSYH;
	}
	public String getXSTS() {
		return XSTS;
	}
	public void setXSTS(String xSTS) {
		XSTS = xSTS;
	}
	
}
