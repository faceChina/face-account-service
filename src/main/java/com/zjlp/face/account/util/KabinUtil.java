package com.zjlp.face.account.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.zjlp.face.account.dto.BankInfo;

public class KabinUtil {
	public static Map<String, String> bankNameMap = new HashMap<String, String>();
	public static Map<String, BankInfo> frontNoMap = new HashMap<String, BankInfo>();
	static{
		bankNameMap.put("邮储银行", "01000000");
		/** 中国工商银行 */
		bankNameMap.put("工商银行", "01020000");
		/** 农业银行 */
		bankNameMap.put("农业银行", "01030000");
		/** 中国银行 */
		bankNameMap.put("中国银行", "01040000");
		/** 建设银行 */
		bankNameMap.put("建设银行", "01050000");
		/** 交通银行 */
		bankNameMap.put("交通银行", "03010000");
		/** 中信银行 */
		bankNameMap.put("中信银行", "03020000");
		/** 光大银行 */
		bankNameMap.put("光大银行", "03030000");
		/** 华夏银行 */
		bankNameMap.put("华夏银行", "03040000");
		/** 民生银行 */
		bankNameMap.put("民生银行", "03050000");
		/** 广发银行 */
		bankNameMap.put("广发银行", "03060000");
		/** 招商银行 */
		bankNameMap.put("招商银行", "03080000");
		/** 兴业银行 */
		bankNameMap.put("兴业银行", "03090000");
		/** 平安银行 */
		bankNameMap.put("平安银行", "03070000");
		/** 浦发银行 */
		bankNameMap.put("浦发银行", "03100000");
		/** 北京银行 */
		bankNameMap.put("北京银行", "04031000");
		/** 上海银行 */
		bankNameMap.put("上海银行", "04012900");
		try{
			InputStream is=KabinUtil.class.getResourceAsStream("/kabin.csv"); // CSV文件
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
			String line = br.readLine();
			while((line = br.readLine()) != null){
				if(bankNameMap.get(line.substring(line.indexOf(",")+1, line.lastIndexOf(","))) != null){
					BankInfo bi = new BankInfo();
					bi.setFrontNo(line.substring(0, line.indexOf(",")));
					bi.setBankName(line.substring(line.indexOf(",") + 1, line.lastIndexOf(",")));
					bi.setBankCode(bankNameMap.get(bi.getBankName()));
					bi.setType(line.substring(line.lastIndexOf(",") + 1));
					frontNoMap.put(bi.getFrontNo(), bi);
				}
			}
			br.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static BankInfo get(String bankCard) {
		BankInfo bi = null;
		if(bankCard != null && bankCard.length() > 15){
			for(int i = 5;i < 13;i++){
				bi = frontNoMap.get(bankCard.substring(0, i));
				if(bi != null)
					break;
			}
		}
		return bi;
	}
}
