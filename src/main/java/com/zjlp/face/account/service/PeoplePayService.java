package com.zjlp.face.account.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 全民付接口
* @ClassName: PeoplePayService 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author phb 
* @date 2015年5月6日 下午3:26:09 
*
 */
public interface PeoplePayService {
	
	/**
	 * 到银商下单
	 * @Title: createOrder
	 * @Description: (支付前，需要到银商下单，获取支付票据)
	 * @param jsonStr
	 * 				merOrderId : 订单号
	 * 				transAmt   ： 订单金额
	 * 				notifyUrl  ： 支付结果通知地址
	 * 				orderDate  ： 订单日期 yyyyMMdd
	 * 				orderTime  : 订单时间 hhmmss
	 * 				orderDesc  : 订单描述（商品名称）
	 * @return String
	 * 				flag : FAILED
	 * 				desc : msg
	 * 				--------------
	 * 				flag : SUCCESS
	 * 				desc : json （支付参数）
	 * 							chrCode : 订单特征号
	 * 							merSign : 签名数据
	 * 							tranId  : 银商订单号
	 * @author phb
	 * @date 2015年5月6日 下午3:30:30
	 */
	String createOrder(String jsonStr );
	
	/**
	 * 处理订单通知
	 * @Title: noticfyMer
	 * @Description: (付款后，银商通知支付结果到商户)
	 * @param httpRequest
	 * @return String
	 * 				flag : FAILED
	 * 				desc : msg
	 * 				--------------
	 * 				flag : SUCCESS
	 * 				desc : json （通知结果参数）
	 * 							transAmt  : 交易金额
	 * 							transId   ：银商订单号
	 * 							merOrderId：商户订单号
	 * 							transState：交易状态
	 * 							account   ：支付卡号
	 * 							liqDate   ：清算日期
	 * @return String
	 * @author phb
	 * @date 2015年5月7日 上午10:29:07
	 */
	String noticfyMer(HttpServletRequest httpRequest);
	
	/**
	 * 获取 响应通知 参数
	 * @Title: noticfyFlagParam
	 * @Description: (银商通知商户后，商户将接收处理结果返回给银商)
	 * @param transId		银商订单号
	 * @param merOrderId	商户订单号
	 * @param merOrderState 处理状态 00 销帐成功
	 * @return
	 * @return String
	 * 				flag : FAILED
	 * 				desc : msg
	 * 				--------------
	 * 				flag : SUCCESS
	 * 				desc : 返回银商结果字符串
	 * @author phb
	 * @date 2015年5月11日 下午3:26:18
	 */
	String noticfyFlagParam(String transId,String merOrderId,String merOrderState);
	
	/**
	 * 查询订单
	 * @Title: QueryOrder
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @return void
	 * @author phb
	 * @date 2015年5月7日 上午11:19:11
	 */
	void QueryOrder(String transId,String merOrderId);
}
