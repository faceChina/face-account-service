package com.zjlp.face.account.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.chinaums.pay.api.entities.NoticeEntity;
import com.chinaums.pay.api.entities.OrderEntity;
import com.chinaums.pay.api.entities.QueryEntity;
import com.chinaums.pay.api.impl.DefaultSecurityService;
import com.chinaums.pay.api.impl.UMSPayServiceImpl;
import com.zjlp.face.account.service.PeoplePayService;
import com.zjlp.face.util.exception.AssertUtil;
import com.zjlp.face.util.file.PropertiesUtil;

@Service
public class PeoplePayServiceImpl implements PeoplePayService {

	private static Logger _log = Logger.getLogger(PeoplePayServiceImpl.class);
	
	private static final String NOTICE_PAY = "NoticePay";
	
	public final String PAY_SUCCESS = "00";

	@Override
	public String createOrder(String jsonStr){
		JSONObject json = new JSONObject();
		try {
			_log.info("下单接口调用开始……");
			/**1.1支付是否开启*/
			String peoplePaySwitch = PropertiesUtil.getContexrtParam("PEOPLE_PAY_SWITCH");
			AssertUtil.isTrue("1".equals(peoplePaySwitch), "全民付支付配置未开启");
			_log.info("全民付支付配置开启验证通过");
			/**1.2验证参数*/
			AssertUtil.hasLength(jsonStr, "参数为空");
			JSONObject jsonObject = JSONObject.fromObject(jsonStr);
			AssertUtil.notNull(jsonObject, "参数格式错误");
			JSONObject checkFlag = _checkCreateOrder(jsonObject);
			AssertUtil.isTrue("SUCCESS".equals(checkFlag.getString("flag")), checkFlag.getString("desc"));
			_log.info("请求参数验证完成");
			
			/**读取配置项*/
			//下单地址
			String createOrderUrl = PropertiesUtil.getContexrtParam("PEOPLE_PAY_CREATE_ORDER_URL");
			AssertUtil.hasLength(createOrderUrl, "参数PEOPLE_PAY_CREATE_ORDER_URL未配置");
			//商户号
			String merId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_ID");
			AssertUtil.hasLength(merId, "参数PEOPLE_PAY_MER_ID未配置");
			//终端号
			String merTermId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_TERM_ID");
			AssertUtil.hasLength(merTermId, "参数PEOPLE_PAY_MER_TERM_ID未配置");
			_log.info("配置参数读取完成");
			
			/**2.1安全验证对象*/
			DefaultSecurityService ss = _getDefaultSecurityService();
			/**2.2全民付请求对象*/
			UMSPayServiceImpl service = new UMSPayServiceImpl();
			service.setSecurityService(ss);
			service.setOrderServiceURL(createOrderUrl);
			_log.info("安全对象组装完成");
			
			/**3.1组装订单请求对象*/
			OrderEntity order = new OrderEntity();
			order.setMerId(merId);// 商户号
			order.setMerTermId(merTermId);// 终端号
			order.setMerOrderId(jsonObject.getString("merOrderId"));// 订单号，商户根据自己的规则生成，最长 32 位
			order.setOrderDate(jsonObject.getString("orderDate"));// 订单日期
			order.setOrderTime(jsonObject.getString("orderTime"));// 订单时间
			order.setTransAmt(jsonObject.getString("transAmt"));// 订单金额(单位分)
			order.setOrderDesc(jsonObject.getString("orderDesc"));// 订单描述
			order.setNotifyUrl(jsonObject.getString("notifyUrl"));// 通知商户地址，保证外网能够访问
			order.setTransType(NOTICE_PAY);// 固定值
			order.setEffectiveTime("0");// 订单有效期期限（秒），值小于等于 0 表示订单长期有效
			order.setMerSign(ss.sign(order.buildSignString()));//签名
			_log.info("订单对象组装完成");
			_log.info("====下单接口，请求报文："+order);
			//下单响应结果对象
			OrderEntity respOrder = new OrderEntity();
			//请求下单
			respOrder = service.createOrder(order);
			_log.info("====下单接口，响应报文："+respOrder);
			/**4.接口返回结果参数*/
			JSONObject rspJsonObject = new JSONObject();
			rspJsonObject.put("chrCode", respOrder.getChrCode());
			rspJsonObject.put("merSign", ss.sign(respOrder.getTransId() + respOrder.getChrCode()));
			rspJsonObject.put("tranId", respOrder.getTransId());
			
			json.put("flag", "SUCCESS");
			json.put("desc", rspJsonObject);
			_log.info(json.toString());
			_log.info("下单接口调用结束");
			return json.toString();
		} catch (Exception e) {
			json.put("flag", "FAILED");
			json.put("desc", e.getMessage());
			return json.toString();
		}
	}
	
	/**
	 * 验证参数
	 * @Title: _checkCreateOrder
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param jsonStr
	 * @return
	 * @return String
	 * @author phb
	 * @date 2015年5月11日 下午1:37:55
	 */
	private JSONObject _checkCreateOrder(JSONObject jsonObject){
		JSONObject json = new JSONObject();
		try {
			//商户订单号
			String merOrderId = jsonObject.getString("merOrderId");
			AssertUtil.hasLength(merOrderId, "参数merOrderId为空");
			//订单金额（分）
			Long transAmt = jsonObject.getLong("transAmt");
			AssertUtil.notNull(transAmt, "参数transAmt为空");
			//支付结果通知地址
			String notifyUrl = jsonObject.getString("notifyUrl");
			AssertUtil.hasLength(notifyUrl, "参数notifyUrl为空");
			//订单日期
			String orderDate = jsonObject.getString("orderDate");
			AssertUtil.hasLength(orderDate, "参数orderDate为空");
			//订单日期
			String orderTime = jsonObject.getString("orderTime");
			AssertUtil.hasLength(orderTime, "参数orderTime为空");
			//订单描述
			String orderDesc = jsonObject.getString("orderDesc");
			AssertUtil.hasLength(orderDesc, "参数orderDesc为空");
			
			json.put("flag", "SUCCESS");
			json.put("desc", "验证通过");
			return json;
		} catch (Exception e) {
			json.put("flag", "FAILED");
			json.put("desc", e.getMessage());
			return json;
		}
	}

	@Override
	public String noticfyMer(HttpServletRequest httpRequest) {
		JSONObject json = new JSONObject();
		try {
			// 1.读取配置项
			//商户号
			String merId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_ID");
			AssertUtil.hasLength(merId, "参数PEOPLE_PAY_MER_ID未配置");
			//终端号
			String merTermId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_TERM_ID");
			AssertUtil.hasLength(merTermId, "参数PEOPLE_PAY_MER_TERM_ID未配置");
			_log.info("配置参数读取成功");
			
			DefaultSecurityService ss = _getDefaultSecurityService();
			UMSPayServiceImpl service = new UMSPayServiceImpl();
			service.setSecurityService(ss); // 1.银商会传这些参数过来
			// 2.处理银商传过来的参数，例如修改订单号等。
			NoticeEntity noticeEntity = service.parseNoticeEntity(httpRequest); 
			AssertUtil.notNull(noticeEntity, "通知对象解析失败");
			_log.info("====通知接口，请求报文"+noticeEntity);
			
			// 3.验证参数
			AssertUtil.isTrue(merId.equals(noticeEntity.getMerId()), "商户号不匹配");
			AssertUtil.isTrue(merTermId.equals(noticeEntity.getMerTermId()), "终端号不匹配");
			AssertUtil.isTrue(NOTICE_PAY.equals(noticeEntity.getTransType()), "交易类型TransType不匹配");
			_log.info("验证参数成功");
			
			// 4.返回对象(对外暴露参数)
			JSONObject noticeObj = new JSONObject();
			noticeObj.put("transAmt", noticeEntity.getTransAmt());//交易金额（分）
			noticeObj.put("transId", noticeEntity.getTransId());//银商订单号
			noticeObj.put("merOrderId", noticeEntity.getMerOrderId());//商户订单号
			noticeObj.put("transState", noticeEntity.getTransState());//交易状态
			noticeObj.put("account", noticeEntity.getAccount());//支付卡号
			noticeObj.put("liqDate", noticeEntity.getLiqDate());//清算日期
			
			json.put("flag", "SUCCESS");
			json.put("desc", noticeObj);
			_log.info(json.toString());
			_log.info("通知处理接口调用结束");
			return json.toString();
		} catch (Exception e) {
			json.put("flag", "FAILED");
			json.put("desc", e.getMessage());
			return json.toString();
		}
	}
	
	@Override
	public String noticfyFlagParam(String transId,String merOrderId,String merOrderState){
		try {
			// 1.读取配置项
			//商户号
			String merId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_ID");
			AssertUtil.hasLength(merId, "参数PEOPLE_PAY_MER_ID未配置");
			//终端号
			String merTermId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_TERM_ID");
			AssertUtil.hasLength(merTermId, "参数PEOPLE_PAY_MER_TERM_ID未配置");
			_log.info("配置参数读取成功");
			//日期格式
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss" ,java.util.Locale. US);
			
			//2.安全对象
			DefaultSecurityService ss = _getDefaultSecurityService();
			UMSPayServiceImpl service = new UMSPayServiceImpl();
			service.setSecurityService(ss); // 1.银商会传这些参数过来
			
			//3.返回银商结果对象
			NoticeEntity respEntity = new NoticeEntity();
			respEntity.setMerOrderId(merOrderId);
			respEntity.setMerId(merId);
			respEntity.setMerTermId(merTermId);
			respEntity.setTransId(transId);
			respEntity.setMerPlatTime(sf.format(new Date()));
			respEntity.setMerOrderState(merOrderState);
			respEntity.setMerSign(ss.sign(respEntity.buildSignString()));
			
			String respStr = service.getNoticeRespString(respEntity);
			_log.info("====通知接口，响应报文"+respStr);
			return respStr;
		} catch (Exception e) {
			_log.error("组装响应参数发生异常",e);
			return null;
		}
	}
	
	@Override
	public void QueryOrder(String transId,String merOrderId) {
		/**读取配置项*/
		//下单地址
		String queryOrderUrl = PropertiesUtil.getContexrtParam("PEOPLE_PAY_QUERY_ORDER_URL");
		AssertUtil.hasLength(queryOrderUrl, "参数PEOPLE_PAY_QUERY_ORDER_URL未配置");
		//商户号
		String merId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_ID");
		AssertUtil.hasLength(merId, "参数PEOPLE_PAY_MER_ID未配置");
		//终端号
		String merTermId = PropertiesUtil.getContexrtParam("PEOPLE_PAY_MER_TERM_ID");
		AssertUtil.hasLength(merTermId, "参数PEOPLE_PAY_MER_TERM_ID未配置");
		
		DefaultSecurityService ss = _getDefaultSecurityService();
		UMSPayServiceImpl service = new UMSPayServiceImpl();
		service.setSecurityService(ss);
		service.setQueryServiceURL(queryOrderUrl);
		QueryEntity queryOrder = new QueryEntity();
		queryOrder.setMerId(merId);
		queryOrder.setMerTermId(merTermId);
		queryOrder.setTransId(transId);// 下单返回的 TransId
		queryOrder.setMerOrderId(merOrderId);// 商户的订单号
		queryOrder.setOrderDate("20150522");// 下单日期
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss",
				java.util.Locale.US);
		String curreTime = sf.format(new Date());
		queryOrder.setReqTime(curreTime);
		queryOrder.setMerSign(ss.sign(queryOrder.buildSignString()));
		_log.info("====查询接口，请求报文"+queryOrder);
		QueryEntity respOrder = new QueryEntity();
		try {
			respOrder = service.queryOrder(queryOrder);
			_log.info("====查询接口，响应报文"+respOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 安全对象组装
	 * @Title: _getDefaultSecurityService
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @return
	 * @return DefaultSecurityService
	 * @author phb
	 * @date 2015年5月11日 上午11:19:43
	 */
	private DefaultSecurityService _getDefaultSecurityService(){
		String signKeyMod = PropertiesUtil.getContexrtParam("PEOPLE_PAY_SIGN_KEY_MOD");
		AssertUtil.hasLength(signKeyMod, "全民付参数PEOPLE_PAY_SIGN_KEY_MOD未配置");
		String signKeyExp = PropertiesUtil.getContexrtParam("PEOPLE_PAY_SIGN_KEY_EXP");
		AssertUtil.hasLength(signKeyExp, "全民付参数PEOPLE_PAY_SIGN_KEY_EXP未配置");
		String verifyKeyMod = PropertiesUtil.getContexrtParam("PEOPLE_PAY_VERIFY_KEY_MOD");
		AssertUtil.hasLength(verifyKeyMod, "全民付参数PEOPLE_PAY_VERIFY_KEY_MOD未配置");
		String verifyKeyExp = PropertiesUtil.getContexrtParam("PEOPLE_PAY_VERIFY_KEY_EXP");
		AssertUtil.hasLength(verifyKeyExp, "全民付参数PEOPLE_PAY_VERIFY_KEY_EXP未配置");
		
		// 测试参数
		DefaultSecurityService ss = new DefaultSecurityService();
		// 设置签名的商户私钥，验签的银商公钥
		ss.setSignKeyModHex(signKeyMod);// 签名私钥 Mod
		ss.setSignKeyExpHex(signKeyExp);// 签名私钥 Exp
		ss.setVerifyKeyExpHex(verifyKeyExp);
		ss.setVerifyKeyModHex(verifyKeyMod);
		return ss;
	}
}
