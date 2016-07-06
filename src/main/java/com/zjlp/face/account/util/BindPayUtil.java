package com.zjlp.face.account.util;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.zjlp.face.util.date.DateUtil;

@SuppressWarnings("deprecation")
public class BindPayUtil {

	public static final String ENCODE = "utf-8";
	
	public static final String DEBIT_CARD = "借记卡";
	public static final String CREDIT_CARD = "贷记卡";
	public static final String DEBIT_CARD_VALUE = "2";
	public static final String CREDIT_CARD_VALUE = "3";
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	
	
	public static String doPost(String url, String charset, String jsonString)
			throws Exception {
		HttpClient httpClient = null;
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
	
	/**
	 * MD5加密处理
	 * 
	 * @param src
	 * @return
	 */
	public final static String MD5LowerCase(String src) {

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
	
	public static String mapParamsToUrl(Map<String, String> params) {

		StringBuilder sb = new StringBuilder();

		for (String s : params.keySet()) {

			sb.append(s).append("=").append(params.get(s)).append("&");
		}

		if (params.size() > 1)
			sb.delete(sb.length() - 1, sb.length()); // 去掉最后一个字符

		return sb.toString();
	}
	
	private static String _currentDate(){
		String currentDate = DateUtil.DateToString(new Date(), "yyyyMMddHHmmss");
		currentDate = currentDate.substring(2, 10);
		return currentDate;
	}
	
	public static String getOrderNo() {
		StringBuilder sn = new StringBuilder();
		String currentDate = _currentDate();
		
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
		
		sn.append("10").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getTranDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
}
