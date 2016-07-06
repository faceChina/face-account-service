package com.zjlp.face.account.service.impl;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.zjlp.face.account.service.BindPayService;
import com.zjlp.face.account.util.BindPayUtil;
import com.zjlp.face.util.date.DateUtil;
import com.zjlp.face.util.exception.AssertUtil;
import com.zjlp.face.util.file.PropertiesUtil;
@SuppressWarnings("deprecation")
@Service("bindPayService")
public class BindPayServiceImpl implements BindPayService {

	private static Logger _log = Logger.getLogger(PeoplePayServiceImpl.class);
	
	@Override
	public String cardInfo(String cardNo) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		try {
			Assert.hasLength(cardNo,"参数为空");
		} catch (Exception e1) {
			_log.error("参数为空");
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数为空");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("查询银行卡信息的卡号为：").append(cardNo).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			reqParam.put("merId", this._getMerId());
			reqParam.put("cardNo", cardNo);
			reqParam.put("timestamp", String.valueOf((new Date()).getTime()));
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "获取银行卡信息失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getCardInfoUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("查询银行卡信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject jsonObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(jsonObject, "转换JSON对象失败");
				AssertUtil.isTrue("0000".equals(jsonObject.getString("retCode")), "获取银行卡信息失败");
				retParam.put("flag", BindPayUtil.SUCCESS);
				retParam.put("desc", "成功");
				retParam.put("bankId", jsonObject.getString("bankId"));
				retParam.put("bankName", jsonObject.getString("bankName"));
				retParam.put("cardName", jsonObject.getString("cardName"));
				String cardType = BindPayUtil.DEBIT_CARD.equals(jsonObject.getString("cardType")) ? BindPayUtil.DEBIT_CARD_VALUE:BindPayUtil.CREDIT_CARD_VALUE;
				retParam.put("cardType", cardType);
				return JSONObject.fromObject(retParam).toString();
			}
		} catch (Exception e) {
			_log.error("获取银行卡信息失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "获取银行卡信息失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "获取银行卡信息失败");
		return JSONObject.fromObject(retParam).toString();
	}

	@Override
	public String sendCode(String mobile) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		try {
			Assert.hasLength(mobile,"参数为空");
		} catch (Exception e) {
			_log.error("参数为空",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数为空");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("发送验证码的手机号码为：").append(mobile).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			Date date = new Date();
			reqParam.put("merId", this._getMerId());
			reqParam.put("mobile", mobile);
			reqParam.put("timestamp", String.valueOf((date).getTime()));
			reqParam.put("tranDateTime", DateUtil.DateToString(date, "yyyyMMddHHmmss"));
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "发送验证码失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getSendCodeUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("发送验证码响应信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject jsonObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(jsonObject, "转换JSON对象失败");
				AssertUtil.isTrue("0000".equals(jsonObject.getString("retCode")), jsonObject.getString("retDesc"));
				retParam.put("flag", BindPayUtil.SUCCESS);
				retParam.put("desc", "成功");
				return JSONObject.fromObject(retParam).toString();
			}
		} catch (Exception e) {
			_log.error("发送验证码失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "发送验证码失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "发送验证码失败");
		return JSONObject.fromObject(retParam).toString();
	}

	@Override
	public String randomPay(String jsonStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String paymentBind(String jsonStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mobileCBindNA(String jsonStr) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		JSONObject jsonObject = null;
		try {
			Assert.hasLength(jsonStr,"参数为空");
			jsonObject = JSONObject.fromObject(jsonStr);
			AssertUtil.notNull(jsonObject, "参数转换JSON对象失败");
			AssertUtil.isTrue(null != jsonObject.get("cardNo") && StringUtils.hasLength(jsonObject.getString("cardNo")), "参数【银行卡号】为空");
			AssertUtil.isTrue(null != jsonObject.get("accName") && StringUtils.hasLength(jsonObject.getString("accName")), "参数【姓名】为空");
			AssertUtil.isTrue(null != jsonObject.get("mobile") && StringUtils.hasLength(jsonObject.getString("mobile")), "参数【手机号】为空");
			AssertUtil.isTrue(null != jsonObject.get("cardType") && StringUtils.hasLength(jsonObject.getString("cardType")), "参数【卡类型】为空");
			AssertUtil.isTrue(null != jsonObject.get("transactionSerialNumber") && StringUtils.hasLength(jsonObject.getString("transactionSerialNumber")), "参数【流水号】为空");
			if (BindPayUtil.CREDIT_CARD_VALUE.equals(jsonObject.getString("cardType"))) {
				AssertUtil.isTrue(null != jsonObject.get("cvn") 
						&& StringUtils.hasLength(jsonObject.getString("cvn")), "参数【cvn安全码】为空");
				AssertUtil.isTrue(null != jsonObject.get("expire") 
						&& StringUtils.hasLength(jsonObject.getString("expire")), "参数【有效期】为空");
			}
			AssertUtil.isTrue(null != jsonObject.get("code") && StringUtils.hasLength(jsonObject.getString("code")), "参数【验证码】为空");
		} catch (Exception e) {
			_log.error("参数错误",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数错误");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("绑定银行卡参数为：").append(jsonStr).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			reqParam.put("merId", this._getMerId());
			reqParam.put("orderNo", jsonObject.getString("transactionSerialNumber"));
			reqParam.put("cardNo", jsonObject.getString("cardNo"));
			reqParam.put("accName", URLEncoder.encode(jsonObject.getString("accName")));
			reqParam.put("mobile", jsonObject.getString("mobile"));
			if(BindPayUtil.CREDIT_CARD_VALUE.equals(jsonObject.getString("cardType"))){
				reqParam.put("cvn", jsonObject.getString("cvn"));
				reqParam.put("expire", jsonObject.getString("expire"));
			}
			reqParam.put("code", jsonObject.getString("code"));
			reqParam.put("timestamp", String.valueOf((new Date()).getTime()));
			reqParam.put("tranDateTime", BindPayUtil.getTranDateTime());
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "绑定银行卡失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getMobileCBindNAUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("绑定银行卡响应信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject rspObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(rspObject, "转换JSON对象失败");
				if("0000".equals(rspObject.getString("retCode"))){
					retParam.put("flag", BindPayUtil.SUCCESS);
					retParam.put("desc", "成功");
					retParam.put("bindId", rspObject.getString("bindId"));
					return JSONObject.fromObject(retParam).toString();
				}else{
					retParam.put("flag", BindPayUtil.FAILED);
					retParam.put("desc", rspObject.getString("retDesc"));
					return JSONObject.fromObject(retParam).toString();
				}
			}
		} catch (Exception e) {
			_log.error("绑定银行卡失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "绑定银行卡失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "绑定银行卡失败");
		return JSONObject.fromObject(retParam).toString();
	}

	@Override
	public String unbind(String bindId) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		try {
			Assert.hasLength(bindId,"参数为空");
		} catch (Exception e) {
			_log.error("参数为空",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数为空");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("解绑参数为：").append(bindId).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			reqParam.put("merId", this._getMerId());
			reqParam.put("bindId", bindId);
			reqParam.put("timestamp", String.valueOf((new Date()).getTime()));
			reqParam.put("tranDateTime", BindPayUtil.getTranDateTime());
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "解绑银行卡失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getUnBindUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("解绑银行卡响应信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject jsonObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(jsonObject, "转换JSON对象失败");
				AssertUtil.isTrue("0000".equals(jsonObject.getString("retCode")), jsonObject.getString("retDesc"));
				retParam.put("flag", BindPayUtil.SUCCESS);
				retParam.put("desc", "成功");
				return JSONObject.fromObject(retParam).toString();
			}
		} catch (Exception e) {
			_log.error("解绑银行卡失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "解绑银行卡失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "解绑银行卡失败");
		return JSONObject.fromObject(retParam).toString();
	}

	@Override
	public String singlePay(String jsonStr) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		JSONObject reqJson = null;
		try {
			Assert.hasLength(jsonStr,"参数为空");
			reqJson = JSONObject.fromObject(jsonStr);
			AssertUtil.notNull(reqJson, "参数转换JSON对象失败");
			AssertUtil.isTrue(null != reqJson.get("serialNumner") && StringUtils.hasLength(reqJson.getString("serialNumner")), "参数【交易流水号为空】");
			AssertUtil.isTrue(null != reqJson.get("bindId") && StringUtils.hasLength(reqJson.getString("bindId")), "参数【绑定号为空】");
			AssertUtil.isTrue(null != reqJson.get("amount") && StringUtils.hasLength(reqJson.getString("amount")), "参数【交易金额为空】");
		} catch (Exception e) {
			_log.error("参数为空",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数错误");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("支付参数为：").append(jsonStr).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			reqParam.put("merId", this._getMerId());
			reqParam.put("bindId", reqJson.getString("bindId"));
			reqParam.put("orderNo", reqJson.getString("serialNumner"));
			reqParam.put("amount", reqJson.getString("amount"));
			if(null != reqJson.get("remarks") && StringUtils.hasLength(reqJson.getString("remarks"))){
				reqParam.put("remarks", URLEncoder.encode(reqJson.getString("remarks")));
			}
			reqParam.put("timestamp", String.valueOf((new Date()).getTime()));
			reqParam.put("tranDateTime", BindPayUtil.getTranDateTime());
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "支付请求失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getSinglePayUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("支付响应信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject jsonObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(jsonObject, "转换JSON对象失败");
				if("0000".equals(jsonObject.getString("retCode"))){
					//{"retDesc":"余额不足","orderNo":"5715060315368862","processDate":"20150603","orderStatus":"3","retCode":"1024"}
//					AssertUtil.isTrue("0000".equals(jsonObject.getString("retCode")), jsonObject.getString("retDesc"));
					retParam.put("flag", BindPayUtil.SUCCESS);
					retParam.put("desc", "成功");
					retParam.put("serialNumner", jsonObject.getString("orderNo"));
					retParam.put("orderStatus", jsonObject.getString("orderStatus"));
					retParam.put("processDate", jsonObject.getString("processDate"));
					return JSONObject.fromObject(retParam).toString();
				}else{
					retParam.put("flag", BindPayUtil.FAILED);
					retParam.put("desc", jsonObject.getString("retDesc"));
					return JSONObject.fromObject(retParam).toString();
				}
			}
		} catch (Exception e) {
			_log.error("支付请求失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "支付请求失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "支付请求失败");
		return JSONObject.fromObject(retParam).toString();
	}

	@Override
	public String queryOrder(String serialNumber) {
		Map<String, String> retParam = new HashMap<String, String>();
		/**1.验证参数*/
		try {
			Assert.hasLength(serialNumber,"参数为空");
		} catch (Exception e) {
			_log.error("参数为空",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "参数为空");
			return JSONObject.fromObject(retParam).toString();
		}
		_log.info(new StringBuffer("查询订单参数为：").append(serialNumber).toString());
		/**2.组装请求参数*/
		Map<String,String> reqParam = new HashMap<String, String>();
		try {
			reqParam.put("merId", this._getMerId());
			reqParam.put("orderNo", serialNumber);
			reqParam.put("timestamp", String.valueOf((new Date()).getTime()));
			reqParam.put("tranDateTime", BindPayUtil.getTranDateTime());
			reqParam.put("sign", BindPayUtil.MD5LowerCase(BindPayUtil.sortParamForSign(reqParam) + this._getKey()));
		} catch (Exception e1) {
			_log.error("组装请求参数发生异常",e1);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "查询订单失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**3.请求接口*/
		String rspData = null;
		try {
			String sortParam = BindPayUtil.mapParamsToUrl(reqParam);
			rspData = BindPayUtil.doPost(this._getQueryOrderUrl(), BindPayUtil.ENCODE, sortParam);
			_log.info(new StringBuffer("查询订单响应信息为：").append(rspData).toString());
		} catch (Exception e) {
			_log.error("连接银商失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "连接银商失败");
			return JSONObject.fromObject(retParam).toString();
		}
		/**4.解析响应参数*/
		try {
			if(StringUtils.hasLength(rspData)){
				JSONObject jsonObject = JSONObject.fromObject(rspData);
				AssertUtil.notNull(jsonObject, "转换JSON对象失败");
				AssertUtil.isTrue("0000".equals(jsonObject.getString("retCode")), jsonObject.getString("retDesc"));
				retParam.put("flag", BindPayUtil.SUCCESS);
				retParam.put("desc", "成功");
				retParam.put("serialNumber", jsonObject.getString("orderNo"));
				String orderDetail = jsonObject.getString("orderDetail");
				JSONObject detailJson = JSONObject.fromObject(orderDetail);
				retParam.put("orderStatus", detailJson.getString("orderStatus"));
				retParam.put("processDate", detailJson.getString("processDate"));
				retParam.put("remark", detailJson.getString("remark"));
				return JSONObject.fromObject(retParam).toString();
			}
		} catch (Exception e) {
			_log.error("查询订单失败",e);
			retParam.put("flag", BindPayUtil.FAILED);
			retParam.put("desc", "查询订单失败");
			return JSONObject.fromObject(retParam).toString();
		}
		retParam.put("flag", BindPayUtil.FAILED);
		retParam.put("desc", "查询订单失败");
		return JSONObject.fromObject(retParam).toString();
	}

	private String _getMerId(){
		String merId = PropertiesUtil.getContexrtParam("BIND_PAY_MERID");
		Assert.hasLength(merId,"参数BIND_PAY_MERID未配置");
		return merId;
	}
	
	private String _getKey(){
		String key = PropertiesUtil.getContexrtParam("BIND_PAY_KEY");
		Assert.hasLength(key,"参数BIND_PAY_KEY未配置");
		return key;
	}
	
	private String _getHost(){
		String host = PropertiesUtil.getContexrtParam("BIND_PAY_HOST");
		Assert.hasLength(host,"参数BIND_PAY_HOST未配置");
		return host;
	}
	
	private String _getCardInfoUrl(){
		String cardInfo = PropertiesUtil.getContexrtParam("BIND_PAY_CARD_INFO_URL");
		Assert.hasLength(cardInfo,"参数BIND_PAY_CARD_INFO_URL未配置");
		return new StringBuffer(this._getHost()).append(cardInfo).toString();
	}
	
	private String _getSendCodeUrl(){
		String sendCode = PropertiesUtil.getContexrtParam("BIND_PAY_SEND_CODE_URL");
		Assert.hasLength(sendCode,"参数BIND_PAY_SEND_CODE_URL未配置");
		return new StringBuffer(this._getHost()).append(sendCode).toString();
	}
	
	private String _getMobileCBindNAUrl(){
		String mobileCBindNA = PropertiesUtil.getContexrtParam("BIND_PAY_MOBILE_C_BIND_NA_URL");
		Assert.hasLength(mobileCBindNA,"参数BIND_PAY_MOBILE_C_BIND_NA_URL未配置");
		return new StringBuffer(this._getHost()).append(mobileCBindNA).toString();
	}
	
	private String _getUnBindUrl(){
		String unbind = PropertiesUtil.getContexrtParam("BIND_PAY_UN_BIND_URL");
		Assert.hasLength(unbind,"参数BIND_PAY_UN_BIND_URL未配置");
		return new StringBuffer(this._getHost()).append(unbind).toString();
	}
	
	private String _getSinglePayUrl(){
		String singlePay = PropertiesUtil.getContexrtParam("BIND_PAY_SINGLE_PAY_URL");
		Assert.hasLength(singlePay,"参数BIND_PAY_SINGLE_PAY_URL未配置");
		return new StringBuffer(this._getHost()).append(singlePay).toString();
	}
	
	private String _getQueryOrderUrl(){
		String queryOrder = PropertiesUtil.getContexrtParam("BIND_PAY_QUERY_ORDER_URL");
		Assert.hasLength(queryOrder,"参数BIND_PAY_QUERY_ORDER_URL未配置");
		return new StringBuffer(this._getHost()).append(queryOrder).toString();
	}
}
