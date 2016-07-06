package com.zjlp.face.account;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.zjlp.face.account.dto.WithdrawReq;
import com.zjlp.face.account.service.WithdrawService;
import com.zjlp.face.jredis.RedisInitializeListener;
import com.zjlp.face.util.calcu.CalculateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-beans.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "jzTransactionManager")
@ActiveProfiles("dev")
public class WithdrawServiceTest {

	@Autowired
	private WithdrawService withdrawService;
	
	static {
		new RedisInitializeListener().contextInitialized(null);
	}
	
//	@Test
	public void test0(){
		String str = withdrawService.balanceQuery("71000015073117621122");
		System.out.println(str);
	}
	
	@Test
	public void test1(){
		WithdrawReq withdrawReq = new WithdrawReq();
		withdrawReq.setTransferAmount(31L);
		withdrawReq.setServiceType(3);
		withdrawReq.setTransferSerino("71000015103014690055");
		withdrawService.transferResultQuery(withdrawReq);
	}
	
//	@Test
	public void testArr(){
		WithdrawReq withdrawReq = new WithdrawReq();
		withdrawReq.setTransferAmount(223400L);
		withdrawReq.setServiceType(3);
		withdrawReq.setTransferSerino("7100005434565545");
		withdrawService.transferResultQuery(withdrawReq);
	}
	
//	@Test
	public void test2(){
		WithdrawReq withdrawReq = new WithdrawReq();
		withdrawReq.setQueryAccountNo("71130122000007903");
		withdrawReq.setStartDate("20151026");
		withdrawReq.setEndDate("20151030");
		withdrawReq.setMinAmount("0.00");
		withdrawReq.setMaxAmount("9999999999.99");
		withdrawReq.setShowPage(1); // 显示页数
		withdrawReq.setCountOfPage(100);  //每页显示条数
		withdrawReq.setTransferSerino("71000015452911162346");  //流水号
		withdrawService.findtransferPageList(withdrawReq);
	}
	
//	@Test
	public void test3() throws UnsupportedEncodingException{
		WithdrawReq withdrawReq = new WithdrawReq();
		withdrawReq.setBankName("宁波银行");  //开户银行名称
//		withdrawReq.setBankCode(bankCard.getBankCode());  //开户银行编号
		withdrawReq.setUserName("宁波维科精华集团股份有限公司（总）");  //用户名
		withdrawReq.setReciveAccountNo("82260120102022631");  //账号
		withdrawReq.setTransferAmount(CalculateUtils.converYuantoPenny("1.2"));
		withdrawReq.setTransferSerino("7660056545663455545");
		withdrawReq.setServiceType(2);   //服务编号:外部转账
		withdrawService.withdrawFromJz(withdrawReq);
	}
    
//    private  static void get(){
//		Base64 base64 = new Base64();
//        String str = "宁波银行";
//        byte[] enbytes = null;
//        String encodeStr = null;
//        byte[] debytes = null;
//        String decodeStr = null;
//        try {
//            enbytes = base64.encode(str.getBytes("gb2312"));
//      //      encodeStr = new String(enbytes);
//            debytes = base64.decode(enbytes);
//            decodeStr = new String(debytes,"utf-8");
//        } catch (Exception ex) {
//            System.out.println("编码错误");
//        }
//        System.out.println("编码前:"+str);
//        System.out.println("编码后:"+encodeStr);
//        System.out.println("解码后:"+decodeStr);
//	}
//	public static void main(String[] args)
//	{
//		get();
//	}
	
}
