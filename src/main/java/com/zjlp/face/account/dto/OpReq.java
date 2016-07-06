package com.zjlp.face.account.dto;

public class OpReq {
	// 交易序列号
	private String serialNo;
	// 客户端请求时间
	private String reqTime;
	//请求参数
	private ReqParam ReqParam;

	public final String getSerialNo() {
		return serialNo;
	}

	public final void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public final String getReqTime() {
		return reqTime;
	}

	public final void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public final ReqParam getReqParam() {
		return ReqParam;
	}
	public final void setReqParam(ReqParam reqParam) {
		this.ReqParam = reqParam;
	}

}
