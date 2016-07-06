package com.zjlp.face.account.service;

import java.util.Map;

import com.zjlp.face.account.dto.AlipayReq;

public interface AlipayService {

	/***
	 * 支付宝付款生产者
	 * @Title: alipayProducer 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param alipayReq
	 * @return
	 * @author Hongbo Peng
	 */
	String alipayProducer(AlipayReq alipayReq);
	
	/**
	 * 验证支付宝通知签名
	 * @Title: alipayCheckSign 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param param
	 * @return
	 * @author Hongbo Peng
	 */
	String alipayCheckSign(Map<String,String> param);
}
