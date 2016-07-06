package com.zjlp.face.account.dto;

/**
 * 宁波银行返回数据
 * @ClassName: NBBankReturnData 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年9月5日 上午11:30:38
 */
public class NBBankReturnData {

	//头数据
	private String serialNo;
	private String sessionId;
	private String retCode;
	private String errorMsg;
	
	//转账结果数据
	private String JYXH;  //银行流水号
	private String JYZT;   //交易状态
	private String YDDM;  //交易应答代码
	private String SBYY;  //失败原因
	private String FKYE;  //付款账户余额
	private String DZHD;  //电子回单号
	
	
	//账户余额及基本信息查询
	private String ZHHA;  //账号
	private String HUMI; //户名
	private String BIZH;  //币种
	private String ZHLX; //账户类型
	private String KHWD; //开户网点
	private String KHRZ;  //开户日期
	private String LILV;  //利率
	private String CUQI;  //存期
	private String YUER;  //账面余额
	private String KYER;  //可用余额
	
	//活期账户交易明细查询
//	private String ZHHA;
//	private String HUMI;
//	private String BIZH;
	private String SHMU;  //查询结果总笔数
	private String FYSH; //总共分页数
	private String JDBZ;  //借贷标志
	private String PATI; //摘要标志
	private String JYJE;  //交易金额
//	private String YUER;  //历史余额
	private String JYRQ; //交易日期
	private String DFZH;  //对方账号
	private String DFHM;  //对方户名
	private String PZBH;  //凭证编号
	private String BEZH;  //备注
//	private String DZHD;
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getJYXH() {
		return JYXH;
	}
	public void setJYXH(String jYXH) {
		JYXH = jYXH;
	}
	public String getJYZT() {
		return JYZT;
	}
	public void setJYZT(String jYZT) {
		JYZT = jYZT;
	}
	public String getYDDM() {
		return YDDM;
	}
	public void setYDDM(String yDDM) {
		YDDM = yDDM;
	}
	public String getSBYY() {
		return SBYY;
	}
	public void setSBYY(String sBYY) {
		SBYY = sBYY;
	}
	public String getFKYE() {
		return FKYE;
	}
	public void setFKYE(String fKYE) {
		FKYE = fKYE;
	}
	public String getDZHD() {
		return DZHD;
	}
	public void setDZHD(String dZHD) {
		DZHD = dZHD;
	}
	public String getZHHA() {
		return ZHHA;
	}
	public void setZHHA(String zHHA) {
		ZHHA = zHHA;
	}
	public String getHUMI() {
		return HUMI;
	}
	public void setHUMI(String hUMI) {
		HUMI = hUMI;
	}
	public String getBIZH() {
		return BIZH;
	}
	public void setBIZH(String bIZH) {
		BIZH = bIZH;
	}
	public String getZHLX() {
		return ZHLX;
	}
	public void setZHLX(String zHLX) {
		ZHLX = zHLX;
	}
	public String getKHWD() {
		return KHWD;
	}
	public void setKHWD(String kHWD) {
		KHWD = kHWD;
	}
	public String getKHRZ() {
		return KHRZ;
	}
	public void setKHRZ(String kHRZ) {
		KHRZ = kHRZ;
	}
	public String getLILV() {
		return LILV;
	}
	public void setLILV(String lILV) {
		LILV = lILV;
	}
	public String getCUQI() {
		return CUQI;
	}
	public void setCUQI(String cUQI) {
		CUQI = cUQI;
	}
	public String getYUER() {
		return YUER;
	}
	public void setYUER(String yUER) {
		YUER = yUER;
	}
	public String getKYER() {
		return KYER;
	}
	public void setKYER(String kYER) {
		KYER = kYER;
	}
	public String getSHMU() {
		return SHMU;
	}
	public void setSHMU(String sHMU) {
		SHMU = sHMU;
	}
	public String getFYSH() {
		return FYSH;
	}
	public void setFYSH(String fYSH) {
		FYSH = fYSH;
	}
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
}
