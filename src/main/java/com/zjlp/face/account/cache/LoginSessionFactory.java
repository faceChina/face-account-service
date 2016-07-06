package com.zjlp.face.account.cache;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zjlp.face.account.service.processor.NingBoBankProcessor;
import com.zjlp.face.account.util.GenerateCode;
import com.zjlp.face.jredis.client.RedisStringHelper;

@Component("loginSessionFactory")
public class LoginSessionFactory {
	
	private static final String SESSION_KEY = "LOGINSESSIONFACTORY_NINGBOBANK_SESSIONID";
	private static final Integer TIMEOUT = 60 * 20;
	private Logger _info = Logger.getLogger("withdrawInfoLog");
	@Autowired
	private RedisStringHelper wgjStringHelper;
	@Autowired
	private NingBoBankProcessor ningBoBankProcessor;
	
	public String getSessionId(){
		
		String sessionId = this.getCacheSessionId();
		if (StringUtils.isBlank(sessionId)) {
			sessionId = ningBoBankProcessor.login(GenerateCode.getWithdrawSN());
			this.cacheSessionId(sessionId);
		}
		return sessionId;
	}
	
	private String getCacheSessionId(){
		try {
			return wgjStringHelper.get(SESSION_KEY);
		} catch (Exception e) {
			_info.info("Get sessionId from redis faild.");
			return null;
		}
	}
	
	private void cacheSessionId(String sessionId){
		try {
			wgjStringHelper.set(SESSION_KEY, TIMEOUT, sessionId);
		} catch (Exception e) {
			_info.info("Cache sessionId to redis faild.");
		}
	}
}
