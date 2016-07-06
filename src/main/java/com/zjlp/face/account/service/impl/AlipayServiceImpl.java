package com.zjlp.face.account.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zjlp.face.account.dto.AlipayReq;
import com.zjlp.face.account.exception.PaymentException;
import com.zjlp.face.account.service.AlipayService;
import com.zjlp.face.account.util.RSA;
import com.zjlp.face.util.exception.AssertUtil;
import com.zjlp.face.util.file.PropertiesUtil;

@Service("alipayService")
public class AlipayServiceImpl implements AlipayService {
	private Logger _logger = Logger.getLogger("alipayLog");

	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	
	private static final String SIGN_TYPE = "RSA";
	
	private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
	
	@Override
	public String alipayProducer(AlipayReq alipayReq) {
		Map<String,String> retParam = new HashMap<String, String>();
		try {
			//验证参数
			_checkAlipayReq(alipayReq);
			//组装支付参数
			Map<String,String> params = _createAlipayParam(alipayReq);
			//签名
			String sign = this._encryptByParams(params);
			AssertUtil.hasLength(sign, "RSA加密失败");
			//将签名加入参数
			params.put("sign", sign);
			params.put("sign_type", SIGN_TYPE);
			//请求网关
			String alipay_gateway = PropertiesUtil.getContexrtParam("alipay.gateway.new");
			AssertUtil.hasLength(alipay_gateway, "参数【alipay.gateway.new】未配置");
			params.put("alipay_gateway", alipay_gateway);
			
			params.put("flag", SUCCESS);
			params.put("desc", "参数组装成功，请求时排除 flag、desc、alipay_gateway,其他参数全部作为支付请求参数");
			return JSONObject.fromObject(params).toString();
		} catch (PaymentException pe) {
			_logger.error(pe.getMessage(),pe);
			retParam.put("flag", FAILED);
			retParam.put("desc", pe.getMessage());
			return JSONObject.fromObject(retParam).toString();
		} catch (Exception e) {
			_logger.error(e.getMessage(),e);
			retParam.put("flag", FAILED);
			retParam.put("desc", "支付宝生产支付失败");
			return JSONObject.fromObject(retParam).toString();
		}
	}

	@Override
	public String alipayCheckSign(Map<String, String> params) {
		Map<String,String> retParam = new HashMap<String, String>();
    	try {
    		//通知校验ID验证
			String responseTxt = "true";
			if(params.get("notify_id") != null) {
				String notify_id = params.get("notify_id");
				responseTxt = _verifyResponse(notify_id);
			}
			
			//签名验证
			String sign = "";
			if(params.get("sign") != null) {sign = params.get("sign");}
			boolean isSign = _getSignVeryfy(params, sign);
			
			if (isSign && responseTxt.equals("true")) {
				retParam.put("flag", SUCCESS);
				retParam.put("desc", "验证通过");
				return JSONObject.fromObject(retParam).toString();
			} else {
				retParam.put("flag", FAILED);
				retParam.put("desc", "验证不通过");
				return JSONObject.fromObject(retParam).toString();
			}
		} catch (Exception e) {
			_logger.error(e.getMessage(),e);
			retParam.put("flag", FAILED);
			retParam.put("desc", "验证发生异常");
			return JSONObject.fromObject(retParam).toString();
		}
	}

	/***
	 * 验证支付请求参数
	 * @Title: _checkAlipayReq 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param alipayReq
	 * @throws PaymentException
	 * @author Hongbo Peng
	 */
	private void _checkAlipayReq(AlipayReq alipayReq) throws PaymentException{
		try {
			AssertUtil.notNull(alipayReq, "参数为空");
			AssertUtil.hasLength(alipayReq.getNotify_url(), "参数【异步通知地址】为空");
			AssertUtil.hasLength(alipayReq.getReturn_url(), "参数【同步通知地址】为空");
			AssertUtil.hasLength(alipayReq.getOut_trade_no(), "参数【商户唯一订单号】为空");
			AssertUtil.hasLength(alipayReq.getSubject(), "参数【商品名称】为空");
			AssertUtil.hasLength(alipayReq.getTotal_fee(), "参数【支付金额】为空");
		} catch (Exception e) {
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	/**
	 * 组装支付参数
	 * @Title: _createAlipayParam 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param alipayReq
	 * @return
	 * @throws PaymentException
	 * @author Hongbo Peng
	 */
	private Map<String,String> _createAlipayParam(AlipayReq alipayReq) throws PaymentException{
		try {
			String service = PropertiesUtil.getContexrtParam("alipay.service.topay");
			AssertUtil.isTrue(StringUtils.isNotBlank(service), "参数【alipay.service.topay】未配置");
			String partner = PropertiesUtil.getContexrtParam("alipay.partner");
			AssertUtil.isTrue(StringUtils.isNotBlank(partner), "参数【alipay.partner】未配置");
			String seller_id = partner;
			String input_charset = PropertiesUtil.getContexrtParam("alipay.input.charset");
			AssertUtil.isTrue(StringUtils.isNotBlank(input_charset), "参数【alipay.input.charset】未配置");
			String payment_type = PropertiesUtil.getContexrtParam("alipay.payment.type");
			AssertUtil.isTrue(StringUtils.isNotBlank(payment_type), "参数【alipay.payment.type】未配置");
			String it_b_pay = PropertiesUtil.getContexrtParam("alipay.it.b.pay");
			AssertUtil.isTrue(StringUtils.isNotBlank(it_b_pay), "参数【alipay.it.b.pay】未配置");
			
			Map<String,String> param = new HashMap<String, String>();
			param.put("service", service);
	        param.put("partner", partner);
	        param.put("seller_id", seller_id);
	        param.put("_input_charset", input_charset);
			param.put("payment_type", payment_type);
			param.put("notify_url", alipayReq.getNotify_url());
			param.put("return_url", alipayReq.getReturn_url());
			param.put("out_trade_no", alipayReq.getOut_trade_no());
			param.put("subject", alipayReq.getSubject());
			param.put("total_fee", alipayReq.getTotal_fee());
			param.put("show_url", alipayReq.getShow_url());
			param.put("body", alipayReq.getBody());
			param.put("it_b_pay", it_b_pay);
//			param.put("extern_token", extern_token);
			return param;
		} catch (Exception e) {
			throw new PaymentException(e.getMessage(),e);
		}
	}
	
	/**
	 * RSA加密
	 * @Title: _encryptByParams 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param plainText
	 * @return
	 * @throws PaymentException
	 * @date 2014年7月10日 下午4:55:02  
	 * @author Administrator
	 */
	private String _encryptByParams(Map<String, String> Params) throws PaymentException{
		try {
			String privateKey = PropertiesUtil.getContexrtParam("alipay.owr.private.key");
			AssertUtil.isTrue(StringUtils.isNotBlank(privateKey), "参数【alipay.owr.private.key】未配置");
			String input_charset = PropertiesUtil.getContexrtParam("alipay.input.charset");
			AssertUtil.isTrue(StringUtils.isNotBlank(input_charset), "参数【alipay.input.charset】未配置");
			
			Map<String, String> sParaNew = _paraFilter(Params);
	        //获取待签名字符串
	        String preSignStr = _createLinkString(sParaNew);
			
			String sign = RSA.sign(preSignStr, privateKey, input_charset);
			return sign;
		} catch (Exception e) {
			throw new PaymentException("加密失败!",e);
		}
	}
	
	/**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
	private boolean _getSignVeryfy(Map<String, String> Params, String sign) {
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = _paraFilter(Params);
        //获取待签名字符串
        String preSignStr = _createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        if(SIGN_TYPE.equals("RSA")){
        	//阿里公钥
        	String pubkeyvalue = PropertiesUtil.getContexrtParam("alipay.ali.public.key");
        	AssertUtil.hasLength(pubkeyvalue, "参数【alipay.ali.public.key】未配置");
        	String input_charset = PropertiesUtil.getContexrtParam("alipay.input.charset");
			AssertUtil.isTrue(StringUtils.isNotBlank(input_charset), "参数【alipay.input.charset】未配置");
        	isSign = RSA.verify(preSignStr, sign, pubkeyvalue, input_charset);
        }
        return isSign;
    }

    /**
    * 获取远程服务器ATN结果,验证返回URL
    * @param notify_id 通知校验ID
    * @return 服务器ATN结果
    * 验证结果集：
    * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
    * true 返回正确信息
    * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
    */
    private String _verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
    	String partner = PropertiesUtil.getContexrtParam("alipay.partner");
		AssertUtil.isTrue(StringUtils.isNotBlank(partner), "参数【alipay.partner】未配置");
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;

        return _checkUrl(veryfy_url);
    }

    /**
    * 获取远程服务器ATN结果
    * @param urlvalue 指定URL路径地址
    * @return 服务器ATN结果
    * 验证结果集：
    * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
    * true 返回正确信息
    * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
    */
    private String _checkUrl(String urlvalue) {
        String inputLine = "";

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                .getInputStream()));
            inputLine = in.readLine().toString();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }
	
    private Map<String, String> _paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private String _createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}
