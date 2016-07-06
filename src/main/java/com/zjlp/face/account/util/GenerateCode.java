package com.zjlp.face.account.util;

import java.util.Date;
import java.util.Random;

import com.zjlp.face.util.date.DateUtil;


public class GenerateCode {
	
	/**交易流水号
	 * 10 + yyMMddHH + 4位用户ID（向前补0）+ 6位随机数 (加两位系统标示码) ——20位
	 * @param userId用户ID
	 * @date 2014/04/10 15:27:00
	 * @author lys
	 * @return
	 */
//	
//	private static String _userCode(String userId){
//		if(userId.length() > 4){
//			userId = userId.substring(0,4);
//		}else if(userId.length() < 4){
//			for(int i = 0; i < 3; i++ ){
//				if(userId.length() == 4){
//					break;
//				}else{
//					userId = "0" + userId;
//				}
//			}
//		}
//		return userId;
//	}
	
	private static String _currentDate(){
		String currentDate = DateUtil.DateToString(new Date(), "yyyyMMddHHmmss");
		currentDate = currentDate.substring(2, 10);
		return currentDate;
	}
	
	public static String getSN(String userId) {
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
		
//		sn.append("10").append(userId).append(currentDate).append(t);
		sn.append("10").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getPhoneSN(String userId){
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
//		sn.append("21").append(userId).append(currentDate).append(t);
		sn.append("21").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getLotterySN(String userId){
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
//		sn.append("41").append(userId).append(currentDate).append(t);
		sn.append("41").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getReserveSN(String userId){
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
//		sn.append("51").append(userId).append(currentDate).append(t);
		sn.append("51").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getRechargeSN(String userId){
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
//		sn.append("61").append(userId).append(currentDate).append(t);
		sn.append("61").append(currentDate).append(t);
		return sn.toString();
	}
	
	public static String getFeeSN(String userId){
		StringBuilder sn = new StringBuilder();
//		userId = _userCode(userId);
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
		sn.append("81").append(currentDate).append(t);
		return sn.toString();
	}
	
	/**
	 * 钱包资金流转流水号
	 * @Title: getWalletSN 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @return
	 * @date 2014年7月22日 下午8:00:46  
	 * @author Administrator
	 */
	public static String getWalletSN(){
		StringBuilder sn = new StringBuilder();
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
		sn.append("91").append("0000").append(currentDate).append(t);
		return sn.toString();
	}
	
	/**
	 * 提现流水号
	 * @Title: getWithdrawSN 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param userId
	 * @return
	 * @date 2014年9月4日 下午8:17:13  
	 * @author Administrator
	 */
	public static String getWithdrawSN(){
		StringBuilder sn = new StringBuilder();
		String currentDate = _currentDate();
		int t = new Random().nextInt(999999);
		if(t < 100000) t+=100000;
		sn.append("71").append("0000").append(currentDate).append(t);
		return sn.toString();
	}
}
