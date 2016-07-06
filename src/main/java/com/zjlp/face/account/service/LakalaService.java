package com.zjlp.face.account.service;

import java.util.Map;

import com.zjlp.face.account.dto.LakalaReq;


public interface LakalaService {
	Map<String, String> lakalaProducer(LakalaReq lakalaReq);
	Map<String, String> lakalaConsumer(LakalaReq lakalaReq);
	Map<String, String> getSignCode(LakalaReq lakalaReq);
	Map<String, String> getPayCode(LakalaReq lakalaReq);
	Map<String, String> sign(LakalaReq lakalaReq);
	String signCheck(LakalaReq lakalaReq);
}
