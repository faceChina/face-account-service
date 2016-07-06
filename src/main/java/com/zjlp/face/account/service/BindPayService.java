package com.zjlp.face.account.service;


public interface BindPayService {
	
	/**
	 * 获取银行卡信息
	 * @Title: cardInfo
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param 				cardNo		: 银行卡号					M
	 * @return String jsonStr
	 * 						flag 		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * 						bankName	: 银行名称
	 * 						bankId		: 银行编号
	 * 						cardType	: 卡类型 2.借记卡|3.信用卡
	 * @author phb
	 * @date 2015年5月27日 下午2:09:49
	 */
	String cardInfo(String cardNo); 

	/***
	 * 发送验证码
	 * @Title: sendCode
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param 				mobile		: 手机号码					M
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * @author phb
	 * @date 2015年5月27日 下午1:44:25
	 */
	String sendCode(String mobile);
	
	/**
	 * 随机付款
	 * @Title: randomPay
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param jsonStr
	 * 						cardNo		: 银行卡号					M
	 * 						accName		: 姓名						M
	 * 						accId		: 身份证						M
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * @author phb
	 * @date 2015年5月27日 下午1:45:08
	 */
	String randomPay(String jsonStr);
	
	/**
	 * 绑定银行卡（随机付款验证）不支持信用卡
	 * @Title: paymentBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param jsonStr
	 * 						cardNo		: 银行卡号					M
	 * 						accName		: 姓名						M
	 * 						accId		: 身份证						M
	 * 						amount		: 随机验证金额				M
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * 						bindId		: 绑定编号
	 * @author phb
	 * @date 2015年5月27日 下午1:50:30
	 */
	String paymentBind(String jsonStr);
	
	/**
	 * 绑定银行卡（卡号+姓名+预留手机号+验证码验证）
	 * @Title: mobileCBindNA
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param jsonStr
	 * 						cardNo		: 银行卡号					M
	 * 						accName		: 姓名						M
	 * 						mobile		: 手机号码					M
	 * 						cardType	: 卡类型 1.借记卡|2.信用卡		M
	 * 						cvn			: cvn安全码					1.O 2.M
	 * 						expire		: 有效期						1.O 2.M
	 * 						code		: 验证码						M
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * 						bindId		: 绑定编号
	 * @author phb
	 * @date 2015年5月27日 下午1:51:46
	 */
	String mobileCBindNA(String jsonStr);
	
	/**
	 * 解绑银行卡
	 * @Title: unbind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param 				bindId		: 绑定号						M
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * @author phb
	 * @date 2015年5月27日 下午1:53:46
	 */
	String unbind(String bindId);
	
	/**
	 * 支付
	 * @Title: singlePay
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param jsonStr
	 * 						serialNumner: 交易流水号					M
	 * 						bindId		: 绑定号						M
	 * 						amount		: 交易金额（分）				M
	 * 						remarks		: 备注						O
	 * @return
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * 						serialNumner: 交易流水号
	 * 						orderStatus : 订单状态 0:已接受, 1:处理中,2:处理成功,3:处理失败
	 * 						processDate : 系统处理日期
	 * @author phb
	 * @date 2015年5月27日 下午1:56:04
	 */
	String singlePay(String jsonStr);
	
	/**
	 * 根据交易流水号查询订单 状态
	 * @Title: queryOrder
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param 				serialNumber: 交易流水号					M
	 * @return
	 * @return String jsonStr
	 * 						flag		: SUCCESS | FAILED
	 * 						desc		: 错误说明
	 * 						serialNumber: 交易流水号
	 * 						orderStatus : 订单状态 0:已接受, 1:处理中,2:处理成功,3:处理失败
	 * 						processDate ：订单系统处理日期
	 * 						remark		：订单备注
	 * @author phb
	 * @date 2015年5月27日 下午1:57:30
	 */
	String queryOrder(String serialNumber);
}
