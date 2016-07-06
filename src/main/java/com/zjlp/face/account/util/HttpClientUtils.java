package com.zjlp.face.account.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

@SuppressWarnings("deprecation")
public class HttpClientUtils {

	private static HttpClient httpClient = null;
	
	private static HttpClientUtils instance = new HttpClientUtils();

	private Logger _logger = Logger.getLogger(this.getClass());

	private HttpClientUtils(){
	}
	
	public static HttpClientUtils getInstances(){
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), getHttpParams()); 
		return instance;
	}
	
	private static HttpParams getHttpParams(){
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		return params;
	}
	
	public String doGet(String url, String charset) throws Exception {
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(get);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status =" + status);
			if (200 != status) {
				throw new RuntimeException("访问失败！");
			}
			String returnString = EntityUtils.toString(httpResponse.getEntity(),charset);
			return returnString;
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}
	/**
	 * Post发送https请求
	 * @Title: doSSLPost 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url	   url
	 * @param charset  字符编码
	 * @param paramMap 参数集合
	 * @return
	 * @throws Exception
	 * @date 2014年5月8日 下午4:22:22  
	 * @author dzq
	 */
	public String doSSLPost(String url, String charset,Map<String, String> paramMap) throws Exception {
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER ;
		try {
	    	SchemeRegistry registry =  new  SchemeRegistry (); 
	    	SSLSocketFactory socketFactory =  SSLSocketFactory . getSocketFactory (); 
	    	socketFactory.setHostnameVerifier (( X509HostnameVerifier ) hostnameVerifier ); 
	    	registry.register (new Scheme("https",443,socketFactory)); 
	    	httpClient.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET,charset);
	    	BasicClientConnectionManager bccm =  new  BasicClientConnectionManager (registry ); 
	    	DefaultHttpClient client =  new  DefaultHttpClient (bccm, httpClient.getParams()); 
	    	HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvps = converForMap(paramMap);
			post.setEntity(new UrlEncodedFormEntity(nvps, charset));
			HttpResponse httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status =" + status);
			String returnString = EntityUtils
					.toString(httpResponse.getEntity());
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}
	
	/**
	 * Post发送https请求
	 * @Title: doSSLPost 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url	   url
	 * @param charset  字符编码
	 * @param jsonString json参数
	 * @return
	 * @throws Exception
	 * @date 2014年5月8日 下午4:22:22  
	 * @author dzq
	 */
	public String doSSLPost(String url,String charset,String jsonString) throws Exception{
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER ;
	    try {
	    	SchemeRegistry registry =  new  SchemeRegistry (); 
	    	SSLSocketFactory socketFactory =  SSLSocketFactory . getSocketFactory (); 
	    	socketFactory.setHostnameVerifier (( X509HostnameVerifier ) hostnameVerifier ); 
	    	registry.register (new Scheme("https",443,socketFactory)); 
	    	httpClient.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET,charset);
	    	BasicClientConnectionManager bccm =  new  BasicClientConnectionManager (registry ); 
	    	DefaultHttpClient client =  new  DefaultHttpClient (bccm, httpClient.getParams()); 
	    	HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpPost post = new HttpPost(url);
			StringEntity s = new StringEntity(jsonString.toString(),charset);  
            s.setContentEncoding(charset);  
            s.setContentType("application/json");  
	        post.setEntity(s);  
	        post.addHeader("Content-Type","application/json;charset=UTF-8");
			HttpResponse httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status ="+status);
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			System.err.println(returnString);
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
		}
	}
	
	public String doSSLPost302(String url,String charset,String jsonString) throws Exception{
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER ;
	    try {
	    	SchemeRegistry registry =  new  SchemeRegistry (); 
	    	SSLSocketFactory socketFactory =  SSLSocketFactory . getSocketFactory (); 
	    	socketFactory.setHostnameVerifier (( X509HostnameVerifier ) hostnameVerifier ); 
	    	registry.register (new Scheme("https",443,socketFactory)); 
	    	httpClient.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET,charset);
	    	BasicClientConnectionManager bccm =  new  BasicClientConnectionManager (registry ); 
	    	DefaultHttpClient client =  new  DefaultHttpClient (bccm, httpClient.getParams()); 
	    	HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpPost post = new HttpPost(url);
			StringEntity s = new StringEntity(jsonString.toString(),charset);  
            s.setContentEncoding(charset);  
            s.setContentType("application/json");  
	        post.setEntity(s);  
	        post.addHeader("Content-Type","application/json;charset=UTF-8");
			HttpResponse httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status ="+status);
			if(status == 302){
				String redirectUrl = httpResponse.getHeaders("location")[0].getValue();
				return doGet(redirectUrl,"utf-8");
			}
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			System.err.println(returnString);
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
		}
	}
	
	/**
	 * Post发送http请求
	 * @Title: doPost 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url	   url
	 * @param charset  字符编码
	 * @param jsonString json参数
	 * @return
	 * @throws Exception
	 * @date 2014年5月8日 下午4:23:29  
	 * @author dzq
	 */
	public String doPost(String url,String charset,String jsonString) throws Exception{
	    try {
			HttpPost post = new HttpPost(url);
			StringEntity s = new StringEntity(jsonString.toString());  
            s.setContentEncoding(charset);  
            s.setContentType("application/json");  
	        post.setEntity(s);  
			HttpResponse httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status ="+status);
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
		}
	}
	
	
	/**
	 * Post发送http请求    注：因为读取时间的问题，不进行自动关闭，要进行手动关闭
	 * @Title: doPostWithXml 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url
	 * @param charset
	 * @param xmlString
	 * @return
	 * @throws Exception
	 * @date 2014年9月18日 下午2:34:16  
	 * @author LYS
	 */
	public String doPostWithXml(String url,String charset,String xmlString) throws Exception{
		HttpPost post = null;
		HttpResponse httpResponse = null;
	    try {
			post = new HttpPost(url);
			StringEntity s = new StringEntity(xmlString, charset);  
            s.setContentType("text/xml"); 
            s.setContentEncoding(charset);
	        post.setEntity(s);  
	        post.setHeader("User-Agent", "MSIE");
			httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status ="+status);
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			return returnString;
		} catch (Exception e) {
			_logger.error("网络发生异常");;
			throw e;
		} finally {
			post.abort();
			if (null != httpResponse) {
				EntityUtils.consumeQuietly(httpResponse.getEntity());
			}
		}
	}
	
	/**
	 * Post发送http请求    注：因为读取时间的问题，不进行自动关闭，要进行手动关闭
	 * @Title: doPostWithXml 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url
	 * @param charset
	 * @param xmlString
	 * @return
	 * @throws Exception
	 * @date 2014年9月18日 下午2:34:16  
	 * @author LYS
	 */
	public String doPostWithXml1(String url,String charset,String xmlString) throws Exception{
		HttpPost post = null;
		HttpResponse httpResponse = null;
	    try {
			post = new HttpPost(url);
			StringEntity s = new StringEntity(xmlString, charset);  
            s.setContentType("text/xml"); 
            s.setContentEncoding(charset);
	        post.setEntity(s);  
	        post.setHeader("User-Agent", "MSIE");
			httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status ="+status);
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			return returnString;
		} catch (Exception e) {
			_logger.error("网络发生异常");;
			throw e;
		} finally {
//			post.abort();
			if (null != httpResponse) {
				EntityUtils.consumeQuietly(httpResponse.getEntity());
			}
		}
	}
	
	/**
	 * Post发送http请求
	 * @Title: doPost 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url	   url
	 * @param charset  字符编码
	 * @param paramMap 参数集合
	 * @return
	 * @throws Exception
	 * @date 2014年5月8日 下午4:24:03  
	 * @author dzq
	 */
	public String doPost(String url, String charset,Map<String, String> paramMap) throws Exception {
		try {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvps = converForMap(paramMap);
			post.setEntity(new UrlEncodedFormEntity(nvps, charset));
			HttpResponse httpResponse = httpClient.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			_logger.info("status =" + status);
			String returnString = EntityUtils
					.toString(httpResponse.getEntity());
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}
	
	public static void close() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 参数转换接口 Map转换
	 * 
	 * @Title: converForMap
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param signParams
	 * @return
	 * @date 2014年4月8日 上午11:53:09
	 * @author dzq
	 */
	public List<NameValuePair> converForMap(Map<String, String> signParams) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keys = signParams.keySet();
		for (String key : keys) {
			nvps.add(new BasicNameValuePair(key, signParams.get(key)));
		}
		return nvps;
	}
	
}
