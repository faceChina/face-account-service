package com.zjlp.face.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.zjlp.face.account.dto.AlipayReq;
import com.zjlp.face.account.service.AlipayService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-beans.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "jzTransactionManager")
@ActiveProfiles("dev")
public class AlipayServiceTest {

	@Autowired
	private AlipayService alipayService;
	
	@Test
	public void test(){
		AlipayReq alipayReq = new AlipayReq();
		alipayReq.setNotify_url("http://www.baidu.com");
		alipayReq.setOut_trade_no("123456789");
		alipayReq.setReturn_url("http://www.baidu.com");
		alipayReq.setSubject("测试");
		alipayReq.setTotal_fee("1.00");
		
		String flag = alipayService.alipayProducer(alipayReq);
		System.out.println(flag);
	}
}
