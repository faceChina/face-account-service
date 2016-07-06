package com.zjlp.face.account.service.processor;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.zjlp.face.account.dto.NBCBEBankData;
import com.zjlp.face.account.dto.OpReq;
import com.zjlp.face.account.dto.ReqParam;
import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.exception.WithdrawException;
import com.zjlp.face.account.util.GenerateCode;
import com.zjlp.face.account.util.HttpClientUtils;
import com.zjlp.face.account.util.SignUtil;
import com.zjlp.face.util.calcu.CalculateUtils;
import com.zjlp.face.util.date.DateStyle;
import com.zjlp.face.util.date.DateUtil;
import com.zjlp.face.util.file.PropertiesUtil;
import com.zjlp.face.util.file.xml.XmlHelper;

@Component("ningBoBankProcessor")
public class NingBoBankProcessor {

	private static final String SIGN_DATA = "signed_data";
	private static final String CHARSET_UTF_8 = "utf-8";
	private Logger _info = Logger.getLogger("withdrawInfoLog");
	private Logger _error = Logger.getLogger("withdrawErrorLog");

	/**
	 * 获取配置数据
	 * 
	 * @Title: getProties
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param name
	 * @return
	 * @date 2014年9月4日 下午3:23:29
	 * @author Administrator
	 */
	public static String getProties(String name) {
		return (String) PropertiesUtil.getContextProperty(name);
	}

	/**
	 * 签名数据包
	 * 
	 * @Title: getOnloadReq
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @return
	 * @date 2014年9月1日 上午10:45:13
	 * @author Administrator
	 */
	private OpReq getOpReq(ReqParam reqParam, String serialNo) {
		OpReq opReq = new OpReq();
		opReq.setSerialNo(serialNo);
		opReq.setReqTime(DateUtil.DateToString(new Date(),
				DateStyle.YYMMDDHHMMSS));
		opReq.setReqParam(reqParam);
		return opReq;
	}

	/**
	 * 
	 * @Title: getXmlForSign
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param verifyValue
	 * @return
	 * @date 2015年10月27日 上午10:32:47
	 * @author lys
	 * @throws UnsupportedEncodingException
	 */
	public static String getXmlForSign(String verifyValue)
			throws UnsupportedEncodingException {
		return "<?xml version=\"1.0\" encoding=\"gbk\"?>\n" + "<msg>\n"
				+ "<msg_head>\n" + "<msg_type>0</msg_type>\n"
				+ "<msg_id>1005</msg_id>\n" + "<msg_sn>0</msg_sn>\n"
				+ "<version>1</version>\n" + "</msg_head>\n" + "<msg_body>\n"
				+ "<origin_data_len>" + verifyValue.getBytes("gbk").length
				+ "</origin_data_len>\n" + "<origin_data>" + verifyValue
				+ "</origin_data>\n" + "</msg_body>\n" + "</msg>";
	}

	/**
	 * 获取封装交易包
	 * 
	 * @Title: getNBCBEBankData
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param opReq
	 * @param serviceId
	 * @param functionId
	 * @param functionName
	 * @param signData
	 * @param sessionId
	 * @return
	 * @date 2014年9月4日 下午3:23:59
	 * @author Administrator
	 */
	private NBCBEBankData getNBCBEBankData(OpReq opReq, String serviceId,
			String functionId, String functionName, String signData,
			String sessionId) {
		NBCBEBankData bankData = new NBCBEBankData();
		bankData.setSessionId(sessionId);// 登陆session
		bankData.setServiceId(serviceId);// 交易ID
		bankData.setCustomerId(getProties("customer_id"));// 客户号
		bankData.setSoftwareId(getProties("software_id"));// 软件识别码
		bankData.setFunctionId(functionId);// 功能编号
		bankData.setFunctionName(functionName);// 功能名称
		bankData.setOpReq(opReq);// 请求数据
		bankData.setSignData(signData);// 签名数据
		return bankData;
	}

	/**
	 * 登陆请求
	 * 
	 * @Title: login
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param 用户id
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月4日 上午10:07:17
	 * @author Administrator
	 */
	public String login(String serialNo) throws WithdrawException {
		try {
			// ----------请求数据-----------
			ReqParam reqParam = new ReqParam();
			reqParam.setUserID(getProties("customer_id")); // 客户号
			reqParam.setUserPWD(getProties("user_pwd")); // 密码
			OpReq opReq = getOpReq(reqParam, serialNo);
			opReq.setSerialNo(GenerateCode.getWithdrawSN());

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("登陆请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】登陆请求代理地址：" + signIp + " : " + signPort);
			String reqXml = replacelineChar(getXmlForSign(replacelineChar(XmlHelper
					.objectToXML(opReq))));
			reqXml = reqXml.replace("OpReq", "opReq");
			_info.info("【提现日志】登陆请求签名的数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			_info.info("【提现日志】登陆签名之后的数据：" + signXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】登陆请求签名数据为空");
			}
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);
			if (StringUtils.isBlank(signData)) {
				throw new WithdrawException("【提现日志】登陆请求签名密文数据为空");
			}

			NBCBEBankData data = getNBCBEBankData(opReq, "srv001_signOn", "01",
					"登陆", signData, "-1");
			String plaintext = replacelineChar(XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data)));
			_info.info("【提现日志】登陆请求数据：" + plaintext);
			String loginData = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】登陆返回数据：" + loginData);
			String sessionId = XmlHelper.getTagContent(loginData, "sessionId");
			if (StringUtils.isBlank(sessionId) || "-1".equals(sessionId)) {
				throw new WithdrawException("获取登陆回话失败，登陆失败");
			}
			return sessionId;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WithdrawException("登陆发生异常", e);
		}
	}

	/**
	 * 行内转账
	 * 
	 * @Title: singleInnerTransfer
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param fkAccount
	 *            付款方账户
	 * @param skAccount
	 *            收款方账户
	 * @param skUserName
	 *            收款方户名
	 * @param jyAmount
	 *            转账金额
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月4日 下午1:49:39
	 * @author Administrator
	 * @param sessionId
	 */
	public String singleInnerTransfer(String sessionId, WithdrawReq withdrawReq)
			throws WithdrawException {
		try {
			if (StringUtils.isBlank(withdrawReq.getPayAccountNo())) {
				throw new WithdrawException("参数【付款账户】不能为空");
			}
			if (StringUtils.isBlank(withdrawReq.getReciveAccountNo())) {
				throw new WithdrawException("参数【收款账户】不能为空");
			}
			if (StringUtils.isBlank(withdrawReq.getUserName())) {
				throw new WithdrawException("参数【收款账户名】不能为空");
			}
			if (null == withdrawReq.getTransferAmount()) {
				throw new WithdrawException("参数【转账金额】不能为空");
			}
			if (null == withdrawReq.getTransferSerino()) {
				throw new WithdrawException("参数【流水号】不能为空");
			}
			// ----------请求数据-----------
			ReqParam reqParam = new ReqParam();
			reqParam.setFKZH(withdrawReq.getPayAccountNo()); // <FKZH>付款账号</FKZH>
			reqParam.setSKZH(withdrawReq.getReciveAccountNo()); // <SKZH>收款账号</SKZH>
			reqParam.setSKHM(withdrawReq.getUserName());// <SKHM>收款方户名</SKHM>
			reqParam.setJYJE(CalculateUtils.converPennytoYuan(withdrawReq
					.getTransferAmount()));// <JYJE>交易金额</JYJE>
			reqParam.setBIZH("01");// <BIZH>币种</BIZH>
			reqParam.setYOTU("提现");// <YOTU>用途</YOTU>
			reqParam.setJTBZ("0");// <SKHH>收款行号</SKHH>
			OpReq opReq = getOpReq(reqParam, withdrawReq.getTransferSerino());

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("【提现日志】行内转账请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】行内转账签名请求代理地址：" + signIp + " : " + signPort);
			String reqXml = getXmlForSign(XmlHelper.objectToXML(opReq));
			_info.info("【提现日志】行内转账签名请求数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】行内转账签名之后的数据为空");
			}
			_info.info("【提现日志】行内转账签名数据：\t\n" + signXml);
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);
			if (StringUtils.isBlank(signData)) {
				throw new WithdrawException("【提现日志】行内转账签名密文数据为空");
			}

			// -----------请求提现--------
			NBCBEBankData data = getNBCBEBankData(opReq,
					"srv006_singleInnerTransfer", "02", "转账", signData,
					sessionId);
			String plaintext = XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data));
			_info.info("【提现日志】同行提现请求数据：" + plaintext);
			String response = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】同行提现返回数据：" + response);
			// 返回数据
			withdrawReq.setTransferSerino(opReq.getSerialNo());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			_error.error("【提现日志】行内转账失败", e);
			throw new WithdrawException("【提现日志】行内转账失败", e);
		}
	}

	/**
	 * 跨行转账
	 * 
	 * @Title: singleOuterTransfer
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param sessionId
	 *            回话id
	 * @param withdrawReq
	 *            请求参数
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月17日 上午11:48:35
	 * @author Administrator
	 */
	public String singleOuterTransfer(String sessionId, WithdrawReq withdrawReq)
			throws WithdrawException {
		Assert.notNull(withdrawReq, "参数为空");
		Assert.notNull(withdrawReq.getPayAccountNo(), "参数【付款账户】不能为空");
		Assert.notNull(withdrawReq.getReciveAccountNo(), "参数【收款账户】不能为空");
		Assert.notNull(withdrawReq.getUserName(), "参数【收款账户名】不能为空");
		Assert.notNull(withdrawReq.getTransferAmount(), "参数【转账金额】不能为空");
		Assert.notNull(withdrawReq.getBankName(), "参数【收款账号开户行名称】不能为空");
		Assert.notNull(withdrawReq.getCurrencyType(), "参数【币种】不能为空");
		Assert.notNull(withdrawReq.getTransferSerino(), "参数【流水号】不能为空");
		try {
			ReqParam reqParam = new ReqParam();
			reqParam.setFKZH(withdrawReq.getPayAccountNo()); // <FKZH>付款账号</FKZH>
			reqParam.setSKZH(withdrawReq.getReciveAccountNo()); // <SKZH>收款账号</SKZH>
			reqParam.setSKHM(withdrawReq.getUserName());// <SKHM>收款方户名</SKHM>
			reqParam.setSKYH(withdrawReq.getBankName());// <SKYH>收款账号开户行名称</SKYH>
			reqParam.setSKSH(withdrawReq.getProvince());// <SKSH>收款方行所在省</SKSH>
			reqParam.setSKSI(withdrawReq.getCity());// <SKSI>收款方行所在市</SKSI>
			reqParam.setJYJE(CalculateUtils.converPennytoYuan(withdrawReq
					.getTransferAmount()));// <JYJE>交易金额</JYJE>
			reqParam.setBIZH("01");// <BIZH>币种</BIZH>
			reqParam.setZZLX("02");// <ZZLX>转账类型</ZZLX> 02 人行大额
			reqParam.setZZLB("0");// <ZZLB>转账类别</ZZLB>
			reqParam.setYOTU("提现");// <YOTU>用途</YOTU>
			reqParam.setSKHH(null);// <SKHH>收款行号</SKHH>
			OpReq opReq = getOpReq(reqParam, withdrawReq.getTransferSerino());

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("【提现日志】跨行转账请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】跨行转账签名请求代理地址：" + signIp + " : " + signPort);
			String reqXml = getXmlForSign(replacelineChar(XmlHelper
					.objectToXML(opReq)));
			reqXml = reqXml.replace("OpReq", "opReq");
			_info.info("【提现日志】跨行转账签名请求数据：" + reqXml);
			System.out.println("【提现日志】跨行转账签名请求数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】跨行转账签名数据为空");
			}
			_info.info("【提现日志】跨行转账签名数据：\t\n" + signXml);
			System.out.println("【提现日志】跨行转账签名数据：\t\n" + signXml);
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);
			if (StringUtils.isBlank(signData)) {
				throw new WithdrawException("【提现日志】跨行转账签名密文数据为空");
			}
			// signData = replaceMiwen(signData);
//			System.out.println("解密数据为：" + SignUtil.jieMi(signData));
			// -----------请求提现-----------
			NBCBEBankData data = getNBCBEBankData(opReq,
					"srv007_singleOuterTransfer", "02", "提现", signData,
					sessionId);
			String plaintext = XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data));
			_info.info("【提现日志】跨行提现请求数据：" + plaintext);
			System.out.println("【提现日志】跨行提现请求数据：" + plaintext);
			String response = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】跨行提现返回数据：" + response);
			System.out.println("【提现日志】跨行提现返回数据：" + response);
			// 返回数据
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			_error.error("【提现日志】跨行转账失败", e);
			throw new WithdrawException("跨行转账失败", e);
		}
	}

	/**
	 * 转账结果查询
	 * 
	 * @Title: transferResultInfoQuery
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param transferSerino
	 *            交易流水号
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月4日 下午2:25:29
	 * @author Administrator
	 */
	public String transferResultInfoQuery(String sessionId,
			WithdrawReq withdrawReq) throws WithdrawException {
		Assert.notNull(withdrawReq, "参数为空");
		Assert.notNull(withdrawReq.getTransferSerino(), "参数【所要查询的流水】不能为空");
		try {
			// ----------请求数据-----------
			ReqParam reqParam = new ReqParam();
			reqParam.setJYXH(withdrawReq.getTransferSerino());
			OpReq opReq = getOpReq(reqParam, withdrawReq.getTransferSerino());

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("【提现日志】查询提现结果请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】查询提现结果签名请求代理地址：" + signIp + " : " + signPort);
			String reqXml = getXmlForSign(XmlHelper.objectToXML(opReq));
			_info.info("【提现日志】查询提现结果签名请求数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】查询提现结果的签名为空");
			}
			_info.info("【提现日志】查询提现结果签名数据：\t\n" + signXml);
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);
			if (StringUtils.isBlank(signData)) {
				throw new WithdrawException("【提现日志】查询提现结果签名密文数据为空");
			}

			// -----------请求查询-----------
			NBCBEBankData data = getNBCBEBankData(opReq,
					"srv008_transferResultInfoQuery", "03", "提现结果查询", signData,
					sessionId);
			String plaintext = XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data));
			_info.info("【提现日志】提现查询结果请求数据：" + plaintext);
			String response = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】提现查询结果返回数据：" + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			_error.error("【提现日志】查询提现结果失败", e);
			throw new WithdrawException("查询提现结果失败", e);
		}
	}

	/**
	 * 单笔账户信息查询
	 * 
	 * @Title: singleAccountInfoQuery
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param accountNo
	 *            要查询的账号
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月4日 下午2:29:25
	 * @author Administrator
	 */
	public String singleAccountInfoQuery(String sessionId, String serialNo)
			throws WithdrawException {
		Assert.notNull(sessionId, "【提现日志】单笔账户信息查询时，回话id不能为空");
		Assert.notNull(serialNo, "【提现日志】单笔账户信息查询时，流水号不能为空");
		try {
			// ----------请求数据-----------
			ReqParam reqParam = new ReqParam();
			reqParam.setZHHA(getProties("jz_account_number"));
			OpReq opReq = getOpReq(reqParam, serialNo);

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("【提现日志】账户信息查询请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】账户信息查询签名请求代理地址：" + signIp + " : " + signPort);
			String reqXml = getXmlForSign(XmlHelper.objectToXML(opReq));
			_info.info("【提现日志】账户信息查询签名请求数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】账户信息查询签名数据为空");
			}
			_info.info("【提现日志】账户信息查询签名数据：\t\n" + signXml);
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);
			if (StringUtils.isBlank(signData)) {
				throw new WithdrawException("账户信息查询签名密文数据为空");
			}

			// -----------请求查询-----------
			NBCBEBankData data = getNBCBEBankData(opReq,
					"srv003_singleAccountInfoQuery", "04", "账户信息查询", signData,
					sessionId);
			String plaintext = XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data));
			_info.info("【提现日志】账户信息查询请求数据：" + plaintext);
			String response = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】账户信息查询返回数据：" + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			_error.error("【提现日志】账户信息查询失败", e);
			throw new WithdrawException("账户信息查询失败", e);
		}
	}

	/**
	 * 活期账务明细查询
	 * 
	 * @Title: accountTradeInfoQuery
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param withdrawReq
	 * @return
	 * @throws WithdrawException
	 * @date 2014年9月17日 上午9:10:51
	 * @author Administrator
	 */
	public String accountTradeInfoQuery(String sessionId,
			WithdrawReq withdrawReq) throws WithdrawException {
		Assert.notNull(withdrawReq, "活期账务明细查询请求参数不能为空");
		Assert.notNull(withdrawReq.getQueryAccountNo(), "活期账务明细查询【请求账号】不能为空");
		Assert.notNull(withdrawReq.getStartDate(), "活期账务明细查询【开始查询时间】不能为空");
		Assert.notNull(withdrawReq.getEndDate(), "活期账务明细查询【终止查询时间】不能为空");
		Assert.notNull(withdrawReq.getShowPage(), "活期账务明细查询【显示页数】不能为空");
		Assert.notNull(withdrawReq.getCountOfPage(), "活期账务明细查询【每页显示条数】不能为空");
		Assert.notNull(withdrawReq.getTransferSerino(), "活期账务明细查询【流水号】不能为空");
		try {
			// ----------请求数据-----------
			ReqParam reqParam = new ReqParam();
			reqParam.setZHHA(withdrawReq.getQueryAccountNo());
			reqParam.setQSRQ(withdrawReq.getStartDate());
			reqParam.setZZRQ(withdrawReq.getEndDate());
			reqParam.setZXJE(withdrawReq.getMinAmount());
			reqParam.setZDJE(withdrawReq.getMaxAmount());
			reqParam.setXSYH(withdrawReq.getShowPage().toString());
			reqParam.setXSTS(withdrawReq.getCountOfPage().toString());
			OpReq opReq = getOpReq(reqParam, withdrawReq.getTransferSerino());

			// ------------签名----------
			String signIp = getProties("sign_ip");
			String signPort = getProties("sign_port");
			if (StringUtils.isBlank(signIp) || StringUtils.isBlank(signPort)) {
				throw new WithdrawException("【提现日志】活期账户交易明细查询请求签名服务地址配置错误。");
			}
			_info.info("【提现日志】活期账户交易明细查询签名请求代理地址：" + signIp + " : " + signPort);
			String reqXml = getXmlForSign(XmlHelper.objectToXML(opReq));
			_info.info("【提现日志】活期账户交易明细查询签名请求数据：" + reqXml);
			String signXml = SignUtil.getSign(signIp,
					Integer.valueOf(signPort), reqXml);
			if (StringUtils.isBlank(signXml)) {
				throw new WithdrawException("【提现日志】账户信息查询签名数据为空");
			}
			_info.info("【提现日志】活期账户交易明细查询签名数据：\t\n" + signXml);
			String signData = XmlHelper.getTagContent(signXml, SIGN_DATA);

			// -----------请求查询-----------
			NBCBEBankData data = getNBCBEBankData(opReq,
					"srv005_accountTradeInfoQuery", "05", "活期账户交易明细查询",
					signData, sessionId);
			String plaintext = XmlHelper.XMLHEAD
					+ replacelineChar(XmlHelper.objectToXML(data));
			_info.info("【提现日志】活期账户交易明细查询请求数据：" + plaintext);
			String response = HttpClientUtils.getInstances().doPostWithXml(
					getProties("service_url"), CHARSET_UTF_8, plaintext);
			_info.info("【提现日志】活期账户交易明细查询返回数据：" + response);
			System.out.println(response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			_error.error("【提现日志】活期账户交易明细查询失败", e);
			throw new WithdrawException("活期账户交易明细查询失败", e);
		}
	}

	/**
	 * 特殊字符的转换
	 * 
	 * @Title: replacelineChar
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param reqStr
	 * @return
	 * @date 2014年9月26日 下午2:10:12
	 * @author Administrator
	 */
	private static final String replacelineChar(String reqStr) {
		return reqStr.replace("\n", "").replace("\t", "").replace(" ", "");
	}
}
