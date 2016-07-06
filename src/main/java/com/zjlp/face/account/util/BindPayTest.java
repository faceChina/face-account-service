package com.zjlp.face.account.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class BindPayTest {

	private static HttpClient httpClient = null;
	
	private static final String MERID = "1000000497";
	
	private static final String KEY = "xyXmjNF2dkfsF6hGm8HejrFhyZ8fpeh5";
	
	private static final String ENCODE = "utf-8";
	
	private static final String REGION = "http://115.236.91.116:8081";
	
	/**发生验证码*/
	private static final String SEND_CODE = "/bind/sendCode.do";
	/**随机付款*/
	private static final String RANDOM_PAY = "/bind/randomPay.do";
	
	/**姓名+卡号 （不支持信用卡）*/
	private static final String NAME_BIND_NA = "/bind/nameBindNA.do";
	/**姓名+卡号+预留手机号*/
	private static final String MOBILE_BIND_NA = "/bind/mobileBindNA.do";
	/**姓名+卡号+预留手机号+验证码*/
	private static final String MOBILE_C_BIND_NA = "/bind/mobileCBindNA.do";
	/**姓名+卡号+身份证 （不支持信用卡）*/
	private static final String REAL_NAME_BIND = "/bind/realNameBind.do";
	/**姓名+卡号+身份证+预留手机号*/
	private static final String CARD_BIND_BY_M = "/bind/cardBindByM.do";
	/**姓名+卡号+身份证+预留手机号+验证码*/
	private static final String CARD_BIND_BY_MC = "/bind/cardBindByMC.do";
	/**随机付款验证（不支持信用卡）
	 * 向用户银行卡随机付款一个金额 1元以下的 用户通过回填金额的方式 进行验证
	 * 主要应用于用户银行卡不支持预留手机号码验证的场景 用户通过银行发送的余额变动短信获知 或者在ATM机 网银等处查询
	 */
	private static final String PAYMENT_BIND = "/bind/paymentBind.do";
	
	/**解绑*/
	private static final String UN_BIND = "/bind/unbind.do";
	
	/**扣款*/
	private static final String SINGLE_PAY = "/bind/singlePay.do";
	/**付款*/
	private static final String PAYMENT = "/bind/payment.do";
	
	/**银行卡信息*/
	private static final String CARD_INFO = "/bind/cardInfo.do";
	

	public static void main(String[] args) {
		/**姓名+卡号 */
//		BindPayTest.nameBindNA("2000000000000005","彭红波", "6222081202010702783");
		
		/**姓名+卡号+预留手机号*/
//		BindPayTest.mobileBindNA("2000000000000010","彭红波", "6222081202010702783","13325853121",null,null);
		
		/**姓名+卡号+预留手机号+验证码
		 *  */
//		try {
//			BindPayTest.sendCode("13325853121");
//			System.out.println("请输入验证码：");
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			String code = br.readLine();
//			BindPayTest.mobileCBindNA("2000000000000007","彭红波", "6222081202010702783","13325853121", code,"519","0220");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		/**随机付款验证*/
//		try {
//			BindPayTest.randomPay("2000000000000008", "彭红波", "6222081202010702783", "421122198909180015");
//			System.out.println("请输入随机付款金额：");
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			String amount = br.readLine();
//			BindPayTest.paymentBind("2000000000000009", "彭红波", "6222081202010702783", "421122198909180015", amount);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		/**1.绑卡，2.扣款，3解绑*/
		try {
			System.out.println("绑定银行卡：");
			BindPayTest.mobileBindNA("2000000000000024","彭红波", "6225758339795741","13325853121","519","0220");
			System.out.println("请输入bindId：");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String bindId = br.readLine();
			BindPayTest.singlePay("2000000000000025", bindId, "测试", "1");
			System.out.println("解除绑定：");
			BindPayTest.unBind(bindId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**1.绑卡，2.付款，3解绑*/
//		try {
//			System.out.println("绑定银行卡：");
//			BindPayTest.mobileBindNA("2000000000000020","彭红波", "6222081202010702783","13325853121",null,null);
//			System.out.println("请输入bindId：");
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			String bindId = br.readLine();
//			
//			BindPayTest.singlePay("2000000000000021", bindId, "测试", "1");
//			
//			BindPayTest.payment("2000000000000022", bindId, "测试", "1");
//			System.out.println("解除绑定：");
//			BindPayTest.unBind(bindId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		/**解绑*/
//		BindPayTest.unBind("8b43d6422a028eaf236168cb598b7226");
		
		/**银行卡信息*/
//		BindPayTest.cardInfo("6222081202010702783");
		
//		BindPayTest.sendCode("1332585311");
	}
	
	/**
	 * 卡号+姓名
	 * @Title: nameBindNA
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param name
	 * @param cardNo
	 * @return void
	 * @author phb
	 * @date 2015年5月25日 下午3:50:29
	 */
	public static void nameBindNA(String orderNo,String name, String cardNo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+NAME_BIND_NA, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 卡号+姓名+手机号
	 * @Title: mobileBindNA
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param name
	 * @param cardNo
	 * @param mobile
	 * @return void
	 * @author phb
	 * @date 2015年5月25日 下午3:50:23
	 */
	public static void mobileBindNA(String orderNo,String name, String cardNo,String mobile,String cvn,String expire) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("mobile", mobile);
		if(StringUtils.isNotBlank(cvn)){
			param.put("cvn", cvn);
		}
		if(StringUtils.isNotBlank(expire)){
			param.put("expire", expire);
		}
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+MOBILE_BIND_NA, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卡号+姓名+手机号
	 * @Title: mobileBindNA
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param name
	 * @param cardNo
	 * @param mobile
	 * @return void
	 * @author phb
	 * @date 2015年5月25日 下午3:50:23
	 */
	public static void mobileCBindNA(String orderNo,String name, String cardNo,String mobile,String code,String cvn,String expire) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("mobile", mobile);
		if(StringUtils.isNotBlank(cvn)){
			param.put("cvn", cvn);
		}
		if(StringUtils.isNotBlank(expire)){
			param.put("expire", expire);
		}
		param.put("code", code);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+MOBILE_C_BIND_NA, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卡号+姓名+身份证
	 * @Title: realNameBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param name
	 * @param cardNo
	 * @param accId
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午10:21:22
	 */
	public static void realNameBind(String orderNo,String name, String cardNo,String accId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", accId);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+REAL_NAME_BIND, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卡号+姓名+身份证+手机号
	 * @Title: cardBindByM
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param name
	 * @param cardNo
	 * @param accId
	 * @param mobile
	 * @param cvn
	 * @param expire
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午10:24:29
	 */
	public static void cardBindByM(String orderNo,String name,String cardNo,String accId,String mobile,String cvn,String expire){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", accId);
		param.put("mobile", mobile);
		if(StringUtils.isNotBlank(cvn)){
			param.put("cvn", cvn);
		}
		if(StringUtils.isNotBlank(expire)){
			param.put("expire", expire);
		}
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+CARD_BIND_BY_M, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 卡号+姓名+身份证+手机号+验证码
	 * @Title: cardBindByMC
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param name
	 * @param cardNo
	 * @param accId
	 * @param mobile
	 * @param cvn
	 * @param expire
	 * @param code
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午10:31:58
	 */
	public static void cardBindByMC(String orderNo,String name,String cardNo,String accId,String mobile,String cvn,String expire,String code){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", accId);
		param.put("mobile", mobile);
		if(StringUtils.isNotBlank(cvn)){
			param.put("cvn", cvn);
		}
		if(StringUtils.isNotBlank(expire)){
			param.put("expire", expire);
		}
		param.put("code", code);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+CARD_BIND_BY_MC, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 随机付款验证
	 * @Title: paymentBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param name
	 * @param cardNo
	 * @param accId
	 * @param amount
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午10:42:28
	 */
	public static void paymentBind(String orderNo,String name,String cardNo,String accId,String amount){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", accId);
		param.put("amount", amount);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+PAYMENT_BIND, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 解绑
	 * @Title: unBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param bindId
	 * @return void
	 * @author phb
	 * @date 2015年5月25日 下午5:38:54
	 */
	public static void unBind(String bindId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("bindId", bindId);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+UN_BIND, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 发生验证码
	 * @Title: sendCode
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param mobile
	 * @return void
	 * @author phb
	 * @date 2015年5月25日 下午4:46:47
	 */
	public static void sendCode(String mobile) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("mobile", mobile);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+SEND_CODE, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 随机付款
	 * @Title: randomPay
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param name
	 * @param cardNo
	 * @param accId
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午10:36:30
	 */
	public static void randomPay(String orderNo,String name,String cardNo,String accId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", accId);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+RANDOM_PAY, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取银行卡信息
	 * @Title: cardInfo
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param cardNo
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午11:16:25
	 */
	public static void cardInfo(String cardNo){
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("cardNo", cardNo);
		param.put("timestamp", date.getTime() + "");
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			System.out.println(REGION+CARD_INFO);
			String rspData = doPost(REGION+CARD_INFO, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 扣款 从绑定的银行卡 付款至 我们账户
	 * @Title: singlePay
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param bindId
	 * @param remarks
	 * @param amount
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 上午11:27:03
	 */
	public static void singlePay(String orderNo,String bindId,String remarks,String amount){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("bindId", bindId);
		param.put("remarks", URLEncoder.encode(remarks));
		param.put("amount", amount);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+SINGLE_PAY, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 付款 我们账户 付款至 绑定银行卡
	 * @Title: payment
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param orderNo
	 * @param bindId
	 * @param remarks
	 * @param amount
	 * @return void
	 * @author phb
	 * @date 2015年5月26日 下午1:43:38
	 */
	public static void payment(String orderNo,String bindId,String remarks,String amount){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", orderNo);
		param.put("bindId", bindId);
		param.put("remarks", URLEncoder.encode(remarks));
		param.put("amount", amount);
		param.put("payType", "2");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REGION+PAYMENT, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String doPost(String url, String charset, String jsonString)
			throws Exception {
		try {
			httpClient = new DefaultHttpClient();
			ClientConnectionManager mgr = httpClient.getConnectionManager();
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
					httpClient.getParams(), mgr.getSchemeRegistry()),
					httpClient.getParams());
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);

			HttpPost post = new HttpPost(url);
			StringEntity s = new StringEntity(jsonString.toString());
			s.setContentEncoding(charset);
			s.setContentType("application/form-data");
			post.setEntity(s);
			HttpResponse httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == 200) {
				String returnString = EntityUtils.toString(httpResponse
						.getEntity());
				return returnString;
			} else {
				System.out.println(status);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}
	
	public static void doPostAsArrayByte(String url, String charset,
			String jsonString) throws Exception {
		try {
			httpClient = new DefaultHttpClient();
			ClientConnectionManager mgr = httpClient.getConnectionManager();
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
					httpClient.getParams(), mgr.getSchemeRegistry()),
					httpClient.getParams());
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);

			HttpPost post = new HttpPost(url);
			StringEntity s = new StringEntity(jsonString.toString());
			s.setContentEncoding(charset);
			s.setContentType("application/form-data");
			post.setEntity(s);
			HttpResponse httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == 200) {
				// String returnString =
				// EntityUtils.toString(httpResponse.getEntity());
				byte[] b = EntityUtils.toByteArray(httpResponse.getEntity());
				getFile(b,"d:","20150107.txt");
			} else {
				System.out.println(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static String sortParamForSign(Map<String, String> params) {
		// 按参数名字典排序
		List<String> valList = Arrays.asList(params.keySet().toArray(
				new String[params.size()]));
		Collections.sort(valList);

		StringBuilder sb = new StringBuilder();

		for (String k : valList) {

			// 跳过 不被签名参数
			if (k.equals("sign")) {
				continue;
			}
			sb.append(k).append("=").append(params.get(k)).append("&");
		}
		if (params.size() > 1)
			sb.delete(sb.length() - 1, sb.length()); // 去掉最后一个字符
		return sb.toString();
	}
	
	/**
	 * MD5加密处理
	 * 
	 * @param src
	 * @return
	 */
	private final static String MD5LowerCase(String src) {

		StringBuffer buf = new StringBuffer("");

		try {
			// 获取MD5摘要算法对象
			MessageDigest digest = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			digest.update(src.getBytes(ENCODE));

			// 获取密文
			byte[] b = digest.digest();

			// 将密文转换成16进制的字符串形式
			int i = 0;

			for (int offset = 0; offset < b.length; offset++) {

				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return buf.toString();
	}
	
	private static String mapParamsToUrl(Map<String, String> params) {

		StringBuilder sb = new StringBuilder();

		for (String s : params.keySet()) {

			sb.append(s).append("=").append(params.get(s)).append("&");
		}

		if (params.size() > 1)
			sb.delete(sb.length() - 1, sb.length()); // 去掉最后一个字符

		return sb.toString();
	}
}
