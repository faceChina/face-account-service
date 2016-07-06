package com.zjlp.face.account.ctl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.zjlp.face.util.exception.AssertUtil;
import com.zjlp.face.account.service.PeoplePayService;

@Controller
@Scope("prototype")
public class PeoplePayCtl {

	@Autowired
	private PeoplePayService peoplePayService;
	
	@RequestMapping(value="/umspay/createOrder")
	public String createOrder(Model model){
		JSONObject json = new JSONObject();
		json.put("merOrderId", "ABC10011000001001");
		json.put("transAmt", "1");
		json.put("notifyUrl", "http://ruixue1989.wicp.net/umspay/notice.htm");
		json.put("orderDate", "20150522");
		json.put("orderTime", "144900");
		json.put("orderDesc", "测试支付商品");
		String rsp = peoplePayService.createOrder(json.toString());
		JSONObject obj = JSONObject.fromObject(rsp);
		AssertUtil.isTrue("SUCCESS".equals(obj.getString("flag")), obj.getString("data"));
		JSONObject data = obj.getJSONObject("data");
		data.put("mchantUserCode", "AAA1001");
		data.put("url", "http://ruixue1989.wicp.net/umspay/success.htm");
		model.addAttribute("rsp", data);
		return "/umspay";
	}
	
	@RequestMapping(value="/umspay/success")
	public String success(Model model){
		return "/test";
	}
	
	@RequestMapping(value="/umspay/notice")
	public String notice(HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			String respStr = peoplePayService.noticfyMer(request);
			request.setCharacterEncoding("utf-8");
			try {
				PrintWriter writer = response.getWriter();
				System.out.println("params.toString()： " + respStr);
				writer.write(respStr);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/umspay/queryOrder")
	public String queryOrder(Model model){
		peoplePayService.QueryOrder("662015052228360765","ABC10011000001001");
		return "/test";
	}
	
}
