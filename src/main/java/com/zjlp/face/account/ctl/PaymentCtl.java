package com.zjlp.face.account.ctl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zjlp.face.account.dto.WapPayReq;
import com.zjlp.face.account.dto.WapPayRsp;
import com.zjlp.face.account.service.PaymentService;

@Controller
@Scope("prototype")
public class PaymentCtl {
	
	@Autowired
	private PaymentService paymentService;

	@RequestMapping(value="/payment/payProducer")
	public String payProducer(Model model){
//		Map<String,String> param = new HashMap<String, String>();
//		param.put("user_id", "1002");
//		param.put("busi_partner", "101001");
//		param.put("no_order", "1000000000000002");
//		param.put("name_goods", "测试支付");
//		param.put("info_order", "ceshizhifu");
//		
//		param.put("bank_code", "01030000");
//		param.put("card_no", "6228480328553206975");
//		param.put("id_type", "0");
//		param.put("id_no", "421122198909180015");
//		param.put("acct_name", "彭红波");
//		param.put("flag_modify", "0");
//		param.put("agreeno", "Ozk4PTk+ODg5Pj4/OTo5OA==");
//		param.put("bind_mob", "13325853121");
		
//		param.put("money_order", "0.01");
//		param.put("notify_url", "http://hongbo1989.eicp.net:52961/payment/payConsumerAsyn.htm");
//		param.put("url_return", "http://hongbo1989.eicp.net:52961/payment/payConsumer.htm");
		//风控参数
//		RiskItemVo riskItemVo = new RiskItemVo();
//		riskItemVo.setUser_info_bind_phone("13325853121");
//		riskItemVo.setUser_info_full_name("彭红波");
//		riskItemVo.setUser_info_id_no("421122198909180015");
//		riskItemVo.setUser_info_id_type("0");
//		riskItemVo.setUser_info_mercht_userno("1001");
//		JSONObject risk = JSONObject.fromObject(riskItemVo);
//		param.put("risk_item", risk.toString());
		
//		JSONObject jsonData = JSONObject.fromObject(param);
////		Map<String,String> param2 = paymentService.paymentProducer(jsonData.toString());
//		JSONObject jsonData2 = JSONObject.fromObject(param2);
//		model.addAttribute("req_data", jsonData2.toString());
		
		WapPayReq vo = new WapPayReq();
		vo.setUser_id("000001007");
		vo.setNo_order("1000000000000007");
		vo.setName_goods("测试支付支付...");
		vo.setInfo_order("ceshizhifu");
		vo.setMoney_order("0.01");
		vo.setPay_type("2");
		vo.setNo_agree("2014081810473840");
//		vo.setBank_code("01030000");
//		vo.setCard_no("6228480328553206975");
//		vo.setId_type("0");
//		vo.setId_no("421122198909180015");
//		vo.setAcct_name("彭红波");
		vo.setNotify_url("http://hongbo1989.eicp.net:52961/payment/payConsumerAsyn.htm");
		vo.setUrl_return("http://hongbo1989.eicp.net:52961/payment/payConsumer.htm");
		String param = paymentService.paymentProducer(vo);
		model.addAttribute("req_data", param);
		return "/confirm";
	}
	
	@RequestMapping(value="/payment/payConsumer")
	public String payConsumer(HttpServletRequest request,Model model){
		System.out.println("同步消费");
		String rspData = request.getParameter("res_data");
		JSONObject jsonObject = JSONObject.fromObject(rspData);
		WapPayRsp wapPayRsp = (WapPayRsp) JSONObject.toBean(jsonObject, WapPayRsp.class);
		wapPayRsp = paymentService.checkPaymentSign(wapPayRsp);
		System.out.println(JSONObject.fromObject(wapPayRsp).toString());
		return "/test";
	}
	
	@RequestMapping(value="/payment/payConsumerAsyn",method = RequestMethod.POST)
	public void payConsumerAsyn(HttpServletRequest request,HttpServletResponse response,Model model){
		System.out.println("异步消费");
		response.setContentType("text/html;charset=utf-8");
	    BufferedReader reader = null;
	    PrintWriter writer = null;
	    try {
	    	writer = response.getWriter();
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null){
			    sb.append(line);
			}
			JSONObject jsonObject = JSONObject.fromObject(sb.toString());
			WapPayRsp wapPayRsp = (WapPayRsp) JSONObject.toBean(jsonObject,WapPayRsp.class);
			wapPayRsp = paymentService.checkPaymentSign(wapPayRsp);
			System.out.println(JSONObject.fromObject(wapPayRsp).toString());
			
			Map<String, String> result = new HashMap<String, String>();
			result.put("ret_code", "0000");
			result.put("retmsg", "交易成功");
			JSONObject jsonObject1 = JSONObject.fromObject(result);
			writer.write(jsonObject1.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String _supplementZero(String userId)throws RuntimeException{
		if (userId.length() < 8) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = userId.length(); i <= 8; i++) {
				stringBuilder.append("0");
				
			}
			return userId = stringBuilder.append(userId).toString();
		}
		return userId;
	}
}
