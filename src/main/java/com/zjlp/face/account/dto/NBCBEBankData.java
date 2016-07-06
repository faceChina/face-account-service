package com.zjlp.face.account.dto;

/**
 * 	跨行汇款（同城/异地）: 请求包格式
 * @author LYS
 *
 */
public class NBCBEBankData {
	
	//登陆session
	private String sessionId;
	//交易ID
	private String serviceId;
	//客户号
	private String customerId;
	//软件识别码
	private String softwareId;
	//功能编号
	private String functionId;
	//功能名称
	private String functionName;
	//请求数据
	private OpReq opReq;
	//签名数据
	private String signData;
	public final String getSessionId() {
		return sessionId;
	}

	public final void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public final String getServiceId() {
		return serviceId;
	}

	public final void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public final String getCustomerId() {
		return customerId;
	}

	public final void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public final String getSoftwareId() {
		return softwareId;
	}

	public final void setSoftwareId(String softwareId) {
		this.softwareId = softwareId;
	}

	public final String getFunctionId() {
		return functionId;
	}

	public final void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public final String getFunctionName() {
		return functionName;
	}

	public final void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public final String getSignData() {
		return signData;
	}

	public final void setSignData(String signData) {
		this.signData = signData;
	}

	public final OpReq getOpReq() {
		return opReq;
	}

	public final void setOpReq(OpReq opReq) {
		this.opReq = opReq;
	}
	
	
}
