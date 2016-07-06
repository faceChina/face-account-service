package com.zjlp.face.account.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * 新支付接口测试
 * 
 * @ClassName: NewPayTest
 * @Description: (这里用一句话描述这个类的作用)
 * @author phb
 * @date 2014年12月22日 下午3:05:44
 */
@SuppressWarnings("deprecation")
public class NewPayTest {

	private static HttpClient httpClient = null;

	private static final String MERID = "1000000078";

	private static final String KEY = "123456";

	private static final String REAL_NAME_BIND_URL = "http://115.236.91.114:8081/bind/realNameBind.do";

	private static final String DEDUCT_BIND_URL = "http://115.236.91.114:8081/bind/deductBind.do";

	private static final String SINGLE_PAY_URL = "http://115.236.91.114:8081/bind/singlePay.do";

	private static final String QUERY_ORDER_URL = "http://115.236.91.114:8081/bind/queryOrder.do";

	private static final String SEND_CODE_URL = "http://115.236.91.114:8081/bind/sendCode.do";

	private static final String QUERY_BIND_URL = "http://115.236.91.114:8081/bind/queryBind.do";

	private static final String CARD_INFO_URL = "http://115.236.91.114:8081/bind/cardInfo.do";

	private static final String CHECK_ORDERS_URL = "http://115.236.91.114:8081/bind/checkOrders.do";
	
	private static final String RANDOM_PAY_URL = "http://115.236.91.114:8081/bind/randomPay.do";
	
	private static final String PAYMENT_BIND_URL = "http://115.236.91.114:8081/bind/paymentBind.do";
	
	private static final String CARD_BIND_BY_MC_URL = "http://115.236.91.114:8081/bind/cardBindByMC.do"; 

	private static final String ENCODE = "utf-8";

	/**
	 * 姓名加卡号绑定
	 * 
	 * @Title: realNameBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param name
	 * @param cardNo
	 * @date 2015年1月7日 下午4:50:11
	 * @author phb
	 */
	public static void realNameBind(String name, String cardNo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000002");
		param.put("cardNo", cardNo);
		param.put("accName", URLEncoder.encode(name));
		param.put("accId", "421122198909180015");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(REAL_NAME_BIND_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 一分钱扣款绑定
	 * 
	 * @Title: deductBind
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @date 2015年1月7日 下午4:50:23
	 * @author phb
	 */
	public static void deductBind() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000005");
		param.put("cardNo", "6225758102403705");
		param.put("accName", URLEncoder.encode("安辉"));
		param.put("accId", "370921199003155418");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(DEDUCT_BIND_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void singlePay(String bindId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000003");
		param.put("bindId", bindId);
		param.put("remarks", URLEncoder.encode("测试的测试的"));
		param.put("amount", "1");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(SINGLE_PAY_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void queryOrder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000003");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(QUERY_ORDER_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendCode() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("mobile", "13325853121");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(SEND_CODE_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void queryBind(String bindId) {
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
			String rspData = doPost(QUERY_BIND_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cardInfo() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("cardNo", "6225758102403705");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(CARD_INFO_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void checkOrders() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("tranDate", "20141212");
		param.put("timestamp", System.currentTimeMillis() + "");
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			doPostAsArrayByte(CHECK_ORDERS_URL, ENCODE, sortParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void randomPay(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000004");
		param.put("cardNo", "6228480328553206975");
		param.put("accName", URLEncoder.encode("彭红波"));
		param.put("accId", "421122198909180015");
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(RANDOM_PAY_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void paymentBind(String amount){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000005");
		param.put("cardNo", "6228480328553206975");
		param.put("accName", URLEncoder.encode("彭红波"));
		param.put("accId", "421122198909180015");
		param.put("amount", amount);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(PAYMENT_BIND_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cardBindByMC(String code){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merId", MERID);
		param.put("orderNo", "2000000000000007");
		param.put("cardNo", "4381260004428257");
		param.put("accName", URLEncoder.encode("郑建武"));
		param.put("accId", "332624198003194217");
		param.put("mobile", "13325853121");
		
		param.put("cvn", "307");
		param.put("expire", "1217");
		
		param.put("code", code);
		param.put("timestamp", date.getTime() + "");
		param.put("tranDateTime", sdf.format(date));
		param.put("sign", MD5LowerCase(sortParamForSign(param) + KEY));
		String sortParam = mapParamsToUrl(param);
		try {
			String rspData = doPost(CARD_BIND_BY_MC_URL, ENCODE, sortParam);
			System.out.println(rspData);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static String doPost(String url, String charset, String jsonString)
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

	public static String sortParamForSign(Map<String, String> params) {
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
