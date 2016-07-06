package com.zjlp.face.account.service.impl;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lakala.aps.commons.PropertiseUtil;
import com.lakala.pay.service.MerInterfaceService;
import com.lakala.testmer.util.HttpHelper;
import com.zjlp.face.account.dto.LakalaReq;
import com.zjlp.face.account.service.LakalaService;
import com.zjlp.face.util.json.JsonUtil;

@Service("lakalaService")
@SuppressWarnings("all")
public class LakalaServiceImpl implements LakalaService {
	
	private Logger logger=Logger.getLogger("lakalaLog");
	private String ver = "1.0.0";
	private String currency = "CNY";
	private String certType ="01";
	private String merId = PropertiseUtil.getDataFromPropertiseFile("site", "merId");
	private String url = PropertiseUtil.getDataFromPropertiseFile("site", "ppayGateUrl");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public Map<String, String> lakalaProducer(LakalaReq lr) {
		String reqType="B0002";
		lr.setReqType(reqType);
		logger.info("业务参数:"+lr);
		Calendar cal=Calendar.getInstance();
		String time=sdf.format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 2);
		String ts=MerInterfaceService.getTs();
		Date effDate=cal.getTime();
		String effTime=sdf.format(effDate);
		String encKey = MerInterfaceService.getMerEncKey(ts);
		logger.info("时间戳拼接对称密钥hexencKey==="+encKey);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merOrderId", lr.getMerOrderId());
		params.put("currency", currency);
		params.put("orderAmount", lr.getOrderAmount());
		params.put("payeeAmount", lr.getPayeeAmount());
		params.put("clientName", lr.getClientName());
		params.put("certType", certType);
		params.put("clientId", lr.getClientId());
		params.put("cardNo", lr.getCardNo());
		params.put("dateOfExpire", lr.getDateOfExpire());
		params.put("cvv", lr.getCvv());
		params.put("mobile", lr.getMobile());
		params.put("busiCode", "122030");
		params.put("orderSummary", null);
		params.put("orderTime", time);
		params.put("timeZone", "GMT+8");
		params.put("pageUrl", null);
		params.put("bgUrl", "http://www.baidu.com");
		params.put("ext1", null);
		params.put("ext2", null);
		params.put("orderEffTime", effTime);
		String json = JsonUtil.fromMap(params, false, new String[]{});
		logger.info("业务参数原文json==="+json);
		String json1 = MerInterfaceService.getJson1(json);
		logger.info("使用对称密钥加密以后的json1==="+json1);
		//拼接时间戳、业务类型、加密json1、做SHA1,请求方私钥加密，HEX，等MAC
		String macStr1 = merId + ver + ts+reqType+json1;
		String macStr = MerInterfaceService.getMacStr(macStr1);
		String mac = MerInterfaceService.getMac(macStr);
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("ver", ver);
		reqMap.put("merId", merId);
		reqMap.put("ts", ts);
		reqMap.put("reqType", reqType);
		reqMap.put("encKey", encKey);
		reqMap.put("encData", json1);
		reqMap.put("mac", mac);
		String result = null;
		try{
			result=HttpHelper.doHttp(url, HttpHelper.POST, "UTF-8", JsonUtil.fromMap(reqMap,false,new String[]{}), 60000);
		}catch(Exception e){
			logger.error("下单失败,json:"+json);
		}
		logger.info("下单结果:"+result);
		Map<String, String> retMap=null;
		if(result==null){
			retMap=new HashMap<String,String>();
			retMap.put("retCode", "10000");
			retMap.put("retMsg", "下单失败");
			return retMap; 
		}
		Map<String,String> resultMap = JsonUtil.toBean(result, HashMap.class);
		if (!"0000".equals(resultMap.get("retCode"))) {
			return resultMap;
		}
		String reqData = MerInterfaceService.getReqData(resultMap.get("encData"));
		String retCode = resultMap.get("retCode");
		String retMsg = resultMap.get("retMsg");
		retMap = JsonUtil.toBean(reqData, HashMap.class);
		retMap.put("retMsg", retMsg);
		retMap.put("retCode", retCode);
		return retMap;
	}

	@Override
	public Map<String, String> lakalaConsumer(LakalaReq lakalaReq) {
		String reqType = "B0003";//请求业务类型
		lakalaReq.setReqType(reqType);
		logger.info("业务参数:"+lakalaReq);
		String merOrderId = lakalaReq.getMerOrderId();
		String orderAmount = lakalaReq.getOrderAmount();
		String payeeAmount = lakalaReq.getPayeeAmount();
		String msgCode = lakalaReq.getMsgCode();
		String transactionId = lakalaReq.getTransactionId();
		String ext1 = lakalaReq.getExt1();//扩展字段1
		String ext2 = lakalaReq.getExt2();//扩展字段2
		//2.时间戳
		String ts = MerInterfaceService.getTs();
		//logger.info("商户生产时间戳dateStr==="+ts);
		//3.时间戳拼接对称密钥的hex，用响应方公钥加密，生成加密密钥密文，hex编码
		String encKey = MerInterfaceService.getMerEncKey(ts);
		logger.info("时间戳拼接对称密钥hexencKey==="+encKey);
		//4.用对称密钥3DES加密“请求业务json”，生成“加密json1”
		Map<String,String> map = new HashMap<String,String>();
		map.put("currency",currency);
		map.put("merOrderId",merOrderId);
		map.put("orderAmount",orderAmount);
		map.put("payeeAmount",payeeAmount);
		map.put("transactionId",transactionId);
		map.put("msgCode",msgCode);
		map.put("ext1", ext1);
		map.put("ext2", ext2);
		String json = JsonUtil.fromMap(map, false, new String[]{});
		logger.info("业务参数原文json==="+json);
		String json1 = MerInterfaceService.getJson1(json);
		logger.info("使用对称密钥加密以后的json1==="+json1);
		//拼接时间戳、业务类型、加密json1、做SHA1,请求方私钥加密，HEX，等MAC
		String macStr1 = merId + ver + ts + reqType + json1;
		String macStr = MerInterfaceService.getMacStr(macStr1);
		String mac = MerInterfaceService.getMac(macStr);
		logger.info("商户私钥加密以后的mac==="+mac);
		
		Map<String,String> reqMap = new HashMap<String,String>();
		reqMap.put("ver", ver);
		reqMap.put("merId", merId);
		reqMap.put("ts", ts);
		reqMap.put("reqType", reqType);
		reqMap.put("encKey", encKey);
		reqMap.put("encData", json1);
		reqMap.put("mac", mac);
		String result = null;
		try{
			result=HttpHelper.doHttp(url, HttpHelper.POST, "UTF-8", JsonUtil.fromMap(reqMap, false, new String[]{}), 60000);
		}catch(Exception e){
			logger.error("支付失败:"+json);
		}
		logger.info("支付结果:"+result);
		Map<String, String> retMap=null;
		if(result==null){
			retMap=new HashMap<String,String>();
			retMap.put("retCode", "10000");
			retMap.put("retMsg", "付款失败");
			return retMap; 
		}
		Map<String, String> resultMap = JsonUtil.toBean(result, HashMap.class);
		String retEncData = resultMap.get("encData");
		String retCode = resultMap.get("retCode");
		String retMsg = resultMap.get("retMsg");
		if(!"0000".equals(retCode)) {
			return resultMap;
		}
		String reqData = MerInterfaceService.getReqData(retEncData);
		//支付返回信息
		retMap = JsonUtil.toBean(reqData, HashMap.class);
		retMap.put("retCode", retCode);
		retMap.put("retMsg", retMsg);
		return retMap;
	}
	
	public static void main(String[] args) {
		String str="绛剧害楠岃瘉鎴愬姛";
		byte[] bs=str.getBytes();
		String s=new String(bs,Charset.forName("GBK"));
		System.out.println(s);
	}
	@Override
	public Map<String, String> getPayCode(LakalaReq lakalaReq) {
		String reqType = "B0004";//请求业务类型
		lakalaReq.setReqType(reqType);
		logger.info("业务参数:"+lakalaReq);
		String merOrderId = lakalaReq.getMerOrderId();
		String orderAmount = lakalaReq.getOrderAmount();
		String payeeAmount = lakalaReq.getPayeeAmount();
		String msgCode = lakalaReq.getMsgCode();
		String transactionId = lakalaReq.getTransactionId();
		String ext1 = lakalaReq.getExt1();//扩展字段1
		String ext2 = lakalaReq.getExt2();//扩展字段2
		//2.时间戳
		String ts = MerInterfaceService.getTs();
		//logger.info("商户生产时间戳dateStr==="+ts);
		//3.时间戳拼接对称密钥的hex，用响应方公钥加密，生成加密密钥密文，hex编码
		String encKey = MerInterfaceService.getMerEncKey(ts);
		logger.info("时间戳拼接对称密钥hexencKey==="+encKey);
		//4.用对称密钥3DES加密“请求业务json”，生成“加密json1”
		Map<String,String> map = new HashMap<String,String>();
		map.put("currency",currency);
		map.put("merOrderId",merOrderId);
		map.put("orderAmount",orderAmount);
		map.put("payeeAmount",payeeAmount);
		map.put("transactionId",transactionId);
		map.put("msgCode",msgCode);
		map.put("ext1", ext1);
		map.put("ext2", ext2);
		String json = JsonUtil.fromMap(map, false, new String[]{});
		logger.info("业务参数原文json==="+json);
		String json1 = MerInterfaceService.getJson1(json);
		logger.info("使用对称密钥加密以后的json1==="+json1);
		//拼接时间戳、业务类型、加密json1、做SHA1,请求方私钥加密，HEX，等MAC
		String macStr1 = merId + ver + ts + reqType + json1;
		String macStr = MerInterfaceService.getMacStr(macStr1);
		String mac = MerInterfaceService.getMac(macStr);
		logger.info("商户私钥加密以后的mac==="+mac);
		
		Map<String,String> reqMap = new HashMap<String,String>();
		reqMap.put("ver", ver);
		reqMap.put("merId", merId);
		reqMap.put("ts", ts);
		reqMap.put("reqType", reqType);
		reqMap.put("encKey", encKey);
		reqMap.put("encData", json1);
		reqMap.put("mac", mac);
		String result = null;
		try{
			result=HttpHelper.doHttp(url, HttpHelper.POST, "UTF-8", JsonUtil.fromMap(reqMap, false, new String[]{}), 60000);
		}catch(Exception e){
			logger.error("获取支付验证码失败,json:"+json);
		}
		logger.info("获取支付验证码结果:"+result);
		Map<String, String> retMap=null;
		if(result==null){
			retMap=new HashMap<String,String>();
			retMap.put("retCode", "10000");
			retMap.put("retMsg", "获取短信验证码失败");
			return retMap; 
		}
		retMap = JsonUtil.toBean(result, HashMap.class);
		String retEncData = retMap.get("encData");
		String retCode = retMap.get("retCode");
		String retMsg = retMap.get("retMsg");
		if(!"0000".equals(retCode)) {
			return retMap;
		}
		String reqData = MerInterfaceService.getReqData(retEncData);
		//支付返回信息
		Map<String, String> resultMap = JsonUtil.toBean(reqData, HashMap.class);
		resultMap.put("retCode", retCode);
		resultMap.put("retMsg", retMsg);
		return resultMap;
	}

	@Override
	public Map<String,String> getSignCode(LakalaReq lakalaReq) {
		String reqType = "B0010";//请求业务类型
		lakalaReq.setReqType(reqType);
		logger.info("业务参数:"+lakalaReq);
		String ext1 = lakalaReq.getExt1();//扩展字段1
		String ext2 = lakalaReq.getExt2();//扩展字段2
		
		String cardNo = lakalaReq.getCardNo();
		String clientName = lakalaReq.getClientName();
		String clientId = lakalaReq.getClientId(); 
		String dateOfExpire = lakalaReq.getDateOfExpire(); 
		String cvv = lakalaReq.getCvv(); 
		String mobile = lakalaReq.getMobile(); 
		
		//2.时间戳
		String ts = MerInterfaceService.getTs();
		//logger.info("商户生产时间戳dateStr==="+ts);
		//3.时间戳拼接对称密钥的hex，用响应方公钥加密，生成加密密钥密文，hex编码
		String encKey = MerInterfaceService.getMerEncKey(ts);
		logger.info("时间戳拼接对称密钥hexencKey==="+encKey);
		//4.用对称密钥3DES加密“请求业务json”，生成“加密json1”
		Map<String,String> map = new HashMap<String,String>();
		map.put("clientName",clientName);
		map.put("clientId",clientId);
		map.put("certType",certType);
		map.put("cardNo",cardNo);
		map.put("dateOfExpire",dateOfExpire);
		map.put("cvv",cvv);
		map.put("mobile",mobile);
		map.put("ext1", ext1);
		map.put("ext2", ext2);
		String json = JsonUtil.fromMap(map, false, new String[]{});
		logger.info("业务参数原文json==="+json);
		String json1 = MerInterfaceService.getJson1(json);
		logger.info("使用对称密钥加密以后的json1==="+json1);
		//拼接时间戳、业务类型、加密json1、做SHA1,请求方私钥加密，HEX，等MAC
		String macStr1 = merId+ver+ts+reqType+json1;
		String macStr = MerInterfaceService.getMacStr(macStr1);
		String mac = MerInterfaceService.getMac(macStr);
		logger.info("商户私钥加密以后的mac==="+mac);
		
		Map<String,String> reqMap = new HashMap<String,String>();
		reqMap.put("ver", ver);
		reqMap.put("merId", merId);
		reqMap.put("ts", ts);
		reqMap.put("reqType", reqType);
		reqMap.put("encKey", encKey);
		reqMap.put("encData", json1);
		reqMap.put("mac", mac);
		String result = null;
		try{
			result=HttpHelper.doHttp(url, HttpHelper.POST, "UTF-8", JsonUtil.fromMap(reqMap, false, new String[]{}), 60000);
		}catch(Exception e){
			logger.error("获取签约验证码失败:"+json);
		}
		logger.info("获取签约验证码结果:"+result);
		Map<String, String> retMap=null;
		if(result==null){
			retMap=new HashMap<String,String>();
			retMap.put("retCode", "10000");
			retMap.put("retMsg", "发送短信验证码失败");
			return retMap; 
		}
		return JsonUtil.toBean(result, HashMap.class);
	}

	@Override
	public Map<String,String> sign(LakalaReq lakalaReq) {
		String reqType = "B0011";//请求业务类型
		lakalaReq.setReqType(reqType);
		logger.info("业务参数:"+lakalaReq);
		String ext1 = lakalaReq.getExt1();//扩展字段1
		String ext2 = lakalaReq.getExt2();//扩展字段2
		String cardNo = lakalaReq.getCardNo();//签约卡号
		String clientName = lakalaReq.getClientName();
		String clientId = lakalaReq.getClientId();
		String dateOfExpire = lakalaReq.getDateOfExpire();
		String cvv = lakalaReq.getCvv();
		String mobile = lakalaReq.getMobile();
		String msgCode = lakalaReq.getMsgCode();
		//2.时间戳
		String ts = MerInterfaceService.getTs();
		//logger.info("商户生产时间戳dateStr==="+ts);
		//3.时间戳拼接对称密钥的hex，用响应方公钥加密，生成加密密钥密文，hex编码
		String encKey = MerInterfaceService.getMerEncKey(ts);
		logger.info("时间戳拼接对称密钥hexencKey==="+encKey);
		//4.用对称密钥3DES加密“请求业务json”，生成“加密json1”
		Map<String,String> map = new HashMap<String,String>();
		map.put("clientName",clientName);
		map.put("clientId",clientId);
		map.put("certType",certType);
		map.put("cardNo",cardNo);
		map.put("dateOfExpire",dateOfExpire);
		map.put("cvv",cvv);
		map.put("mobile",mobile);
		map.put("smsCode",msgCode);
		map.put("ext1", ext1);
		map.put("ext2", ext2);
		String json = JsonUtil.fromMap(map,false,new String[]{});
		logger.info("业务参数原文json==="+json);
		String json1 = MerInterfaceService.getJson1(json);
		logger.info("使用对称密钥加密以后的json1==="+json1);
		//拼接时间戳、业务类型、加密json1、做SHA1,请求方私钥加密，HEX，等MAC
		String macStr1 = merId+ver+ts+reqType+json1;
		String macStr = MerInterfaceService.getMacStr(macStr1);
		String mac = MerInterfaceService.getMac(macStr);
		logger.info("商户私钥加密以后的mac==="+mac);
		Map<String,String> reqMap = new HashMap<String,String>();
		reqMap.put("ver", ver);
		reqMap.put("merId", merId);
		reqMap.put("ts", ts);
		reqMap.put("reqType", reqType);
		reqMap.put("encKey", encKey);
		reqMap.put("encData", json1);
		reqMap.put("mac", mac);
		String result = null;
		try{
			result=HttpHelper.doHttp(url, HttpHelper.POST, "UTF-8", JsonUtil.fromMap(reqMap,false,new String[]{}), 60000);
		}catch(Exception e){
			logger.error("签约失败:"+json);
		}
		//{"retCode":"1101","retMsg":"签约短信验证失败,签约信息不存在请重新申请签约"}
		logger.info("签约结果:"+result);
		Map<String, String> retMap=null;
		if(result==null){
			retMap=new HashMap<String,String>();
			retMap.put("retCode", "10000");
			retMap.put("retMsg", "签约失败");
			return retMap; 
		}
		return JsonUtil.toBean(result, HashMap.class);
	}

	@Override
	@Deprecated
	public String signCheck(LakalaReq lakalaReq) {
		return null;
	}

}
